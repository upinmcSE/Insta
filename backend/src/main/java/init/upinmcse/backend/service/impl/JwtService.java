package init.upinmcse.backend.service.impl;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import init.upinmcse.backend.constant.TokenType;
import init.upinmcse.backend.exception.ErrorCode;
import init.upinmcse.backend.exception.ErrorException;
import init.upinmcse.backend.exception.TokenExpiredException;
import init.upinmcse.backend.model.User;
import init.upinmcse.backend.repository.db.TokenRevokedRepository;
import init.upinmcse.backend.repository.db.UserRepository;
import init.upinmcse.backend.service.IJwtService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j(topic = "JwtService")
public class JwtService implements IJwtService {
    UserRepository userRepository;
    TokenRevokedRepository tokenRevokedRepository;

    @NonFinal
    @Value("${jwt.secretKey}")
    String SIGNER_KEY;

    @NonFinal
    @Value("${jwt.accessExpiryMinutes}")
    long ACCESS_EXPIRY_SECONDS;

    @NonFinal
    @Value("${jwt.refreshExpiryMinutes}")
    long REFRESH_EXPIRY_SECONDS;

    @Override
    public String generateToken(String userId, TokenType typeToken) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        long expiryTimeInSeconds = typeToken == TokenType.ACCESS_TOKEN ? ACCESS_EXPIRY_SECONDS : REFRESH_EXPIRY_SECONDS;

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(userId)
                .issueTime(new Date())
                .expirationTime(new Date(Instant.now().plus(expiryTimeInSeconds, ChronoUnit.SECONDS).toEpochMilli()))
                .jwtID(UUID.randomUUID().toString())
                .claim("scope", buildScope(userRepository.findById(userId).orElseThrow(() -> new ErrorException(ErrorCode.NOT_FOUND_USER))))
                .build();

        Payload payload = new Payload(claimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Cannot create token", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public Date extractExpiration(String token, TokenType typeToken) throws ParseException {
        SignedJWT signedJWT = SignedJWT.parse(token);
        JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();

        if (claimsSet != null && claimsSet.getExpirationTime() != null) {
            return claimsSet.getExpirationTime();
        }

        throw new ErrorException(ErrorCode.INVALID_TOKEN);
    }

    private String buildScope(User user) {
        StringJoiner stringJoiner = new StringJoiner(" ");

        if (!CollectionUtils.isEmpty(user.getRoles()))
            user.getRoles().forEach(role -> {
                stringJoiner.add("ROLE_" + role.getName());
            });

        return stringJoiner.toString();
    }


    @Override
    public String extractJwtId(String token, TokenType typeToken) throws ParseException {
        SignedJWT signedJWT = SignedJWT.parse(token);

        if (signedJWT.getJWTClaimsSet() != null && signedJWT.getJWTClaimsSet().getJWTID() != null) {
            return signedJWT.getJWTClaimsSet().getJWTID();
        }
        return null;
    }

    @Override
    public String extractUserId(String token) throws ParseException {
        SignedJWT signedJWT = SignedJWT.parse(token);

        if (signedJWT.getJWTClaimsSet() != null && signedJWT.getJWTClaimsSet().getSubject() != null) {
            return signedJWT.getJWTClaimsSet().getSubject();
        }

        return null;
    }

    @Override
    public SignedJWT validateToken(String token, boolean isRefresh) throws JOSEException, ParseException {
        if (token == null || token.trim().isEmpty()) {
            throw new ErrorException(ErrorCode.UNAUTHENTICATED);
        }

        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());

        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expiryTime = (isRefresh)
                ? new Date(signedJWT.getJWTClaimsSet()
                .getIssueTime()
                .toInstant()
                .plus(REFRESH_EXPIRY_SECONDS, ChronoUnit.HOURS)
                .toEpochMilli())
                : signedJWT.getJWTClaimsSet().getExpirationTime();

        if (expiryTime.before(new Date())) {
            throw new TokenExpiredException();
        }

        var verified = signedJWT.verify(verifier);
        if (!verified) throw new ErrorException(ErrorCode.INVALID_TOKEN);

        if( tokenRevokedRepository.existsById(token)) {
            throw new ErrorException(ErrorCode.TOKEN_REVOKED);
        }

        return signedJWT;
    }

}
