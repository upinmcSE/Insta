package init.upinmcse.backend.service;

import init.upinmcse.backend.enums.TYPE_TOKEN;
import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;
import java.util.function.Function;

public interface IJwtService {
    String generateToken(String email, TYPE_TOKEN typeToken);

    String extractJit(String token, TYPE_TOKEN typeToken);

    <T> T extractClaims(String token, TYPE_TOKEN typeToken, Function<Claims, T> claimsTFunction);

    Date extractExpiration(String token, TYPE_TOKEN typeToken);

    String extractId(String token, TYPE_TOKEN typeToken);

    String extractEmail(String token, TYPE_TOKEN typeToken);

    Boolean validateToken(String token, TYPE_TOKEN typeToken, UserDetails userDetails);
}
