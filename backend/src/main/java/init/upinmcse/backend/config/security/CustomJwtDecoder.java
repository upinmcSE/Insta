package init.upinmcse.backend.config.security;

import java.time.Instant;
import java.util.Objects;
import javax.crypto.spec.SecretKeySpec;

import init.upinmcse.backend.exception.TokenExpiredException;
import init.upinmcse.backend.repository.db.TokenRevokedRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class CustomJwtDecoder implements JwtDecoder {
    @Value("${jwt.secretKey}")
    @NonFinal
    String SIGNER_KEY;

    @NonFinal
    NimbusJwtDecoder nimbusJwtDecoder = null;

    TokenRevokedRepository tokenRevokedRepository;

    @Override
    public Jwt decode(String token) throws JwtException {
        if (Objects.isNull(nimbusJwtDecoder)) {
            SecretKeySpec secretKeySpec = new SecretKeySpec(SIGNER_KEY.getBytes(), "HS512");
            nimbusJwtDecoder = NimbusJwtDecoder.withSecretKey(secretKeySpec)
                    .macAlgorithm(MacAlgorithm.HS512)
                    .build();
        }

        try {
            Jwt jwt = nimbusJwtDecoder.decode(token);

            // Kiểm tra token hết hạn
            if (jwt.getExpiresAt() != null && jwt.getExpiresAt().isBefore(Instant.now())) {
                throw new TokenExpiredException("Token expired");
            }

            String jwtId = jwt.getId();
            if (jwtId != null && tokenRevokedRepository.existsById(jwtId)) {
                throw new JwtException("Token revoked");
            }
            return jwt;
        } catch (JwtValidationException ex) {
            if (ex.getMessage().contains("expired")) {
                throw new TokenExpiredException("Token expired", ex);
            }
            throw new BadJwtException("Invalid token", ex);
        }
    }
}
