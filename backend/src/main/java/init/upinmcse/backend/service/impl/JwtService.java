package init.upinmcse.backend.service.impl;

import init.upinmcse.backend.enums.TYPE_TOKEN;
import init.upinmcse.backend.model.User;
import init.upinmcse.backend.repository.UserRepository;
import init.upinmcse.backend.service.IJwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.security.Key;
import java.util.*;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j(topic = "JwtService")
public class JwtService implements IJwtService {
    UserRepository userRepository;

    @NonFinal
    @Value("${jwt.accessKey}")
    String ACCESS_KEY;

    @NonFinal
    @Value("${jwt.refreshKey}")
    String REFRESH_KEY;

    @NonFinal
    @Value("${jwt.accessExpiryMinutes}")
    int ACCESS_EXPIRY_MINUTES;

    @NonFinal
    @Value("${jwt.refreshExpiryMinutes}")
    int REFRESH_EXPIRY_MINUTES;

    @Override
    public String generateToken(String email, TYPE_TOKEN typeToken) {
        Map<String, Object> claims = new HashMap<>();
        User user = userRepository.findByEmail(email).orElseThrow();
        claims.put("id", UUID.randomUUID().toString());
        claims.put("fullName", user.getFullName());
        claims.put("enabled", user.isEnabled());
        claims.put("scope", buildScope(user));
        return createToken(claims, email, typeToken);
    }

    private String buildScope(User user) {
        StringJoiner stringJoiner = new StringJoiner(" ");

        if (!CollectionUtils.isEmpty(user.getRoles()))
            user.getRoles().forEach(role -> {
                stringJoiner.add("ROLE_" + role.getName());
            });

        return stringJoiner.toString();
    }

    private String createToken(Map<String, Object> claims, String email, TYPE_TOKEN typeToken) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuer("U-Poops")
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + (typeToken.equals(TYPE_TOKEN.ACCESS_TOKEN) ? ACCESS_EXPIRY_MINUTES : REFRESH_EXPIRY_MINUTES)))
                .signWith(SignatureAlgorithm.HS512, getSigneKey(typeToken))
                .compact();
    }

    private Key getSigneKey(TYPE_TOKEN typeToken) {
        byte[] keyByte = Decoders.BASE64.decode(typeToken.equals(TYPE_TOKEN.ACCESS_TOKEN) ? ACCESS_KEY : REFRESH_KEY);
        return Keys.hmacShaKeyFor(keyByte);
    }

    @Override
    public String extractJit(String token, TYPE_TOKEN typeToken) {
        return Jwts.parser()
                .setSigningKey(getSigneKey(typeToken))
                .parseClaimsJws(token)
                .getBody()
                .getId();
    }

    private Claims extractAllClaims(String token, TYPE_TOKEN typeToken) {
        return Jwts.parser()
                .setSigningKey(getSigneKey(typeToken))
                .parseClaimsJws(token)
                .getBody();
    }


    public <T> T extractClaims(String token, TYPE_TOKEN typeToken, Function<Claims, T> claimsTFunction) {
        final Claims claims = extractAllClaims(token, typeToken);
        return claimsTFunction.apply(claims);
    }


    public Date extractExpiration(String token, TYPE_TOKEN typeToken) {
        return extractClaims(token, typeToken, Claims::getExpiration);
    }


    public String extractId(String token, TYPE_TOKEN typeToken) {
        return extractClaims(token, typeToken, claims -> claims.get("id", String.class));
    }


    public String extractEmail(String token, TYPE_TOKEN typeToken) {
        return extractClaims(token, typeToken, Claims::getSubject);
    }

    private Boolean isTokenExpired(String token, TYPE_TOKEN typeToken) {
        return extractExpiration(token, typeToken).before(new Date());
    }


    public Boolean validateToken(String token, TYPE_TOKEN typeToken, UserDetails userDetails) {
        final String username = extractEmail(token, typeToken);
        return (
                username.equals(userDetails.getUsername()) && !isTokenExpired(token, typeToken));
    }
}
