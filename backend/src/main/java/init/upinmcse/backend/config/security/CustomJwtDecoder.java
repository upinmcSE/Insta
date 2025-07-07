package init.upinmcse.backend.config.security;

import java.text.ParseException;
import java.util.Date;
import java.util.Objects;
import javax.crypto.spec.SecretKeySpec;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.SignedJWT;
import init.upinmcse.backend.exception.ErrorCode;
import init.upinmcse.backend.exception.ErrorException;
import init.upinmcse.backend.repository.db.TokenRevokedRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
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
        try {
            validateToken(token);
        } catch (ErrorException e) {
            // Throw OAuth2AuthenticationException with specific OAuth2Error
            OAuth2Error error = new OAuth2Error(
                    String.valueOf(e.getErrorCode().getCode()),
                    e.getErrorCode().getMessage(),
                    null
            );
            throw new OAuth2AuthenticationException(error);
        } catch (JOSEException | ParseException e) {
            OAuth2Error error = new OAuth2Error(
                    String.valueOf(ErrorCode.INVALID_TOKEN.getCode()),
                    ErrorCode.INVALID_TOKEN.getMessage(),
                    null
            );
            throw new OAuth2AuthenticationException(error);
        }

        if (Objects.isNull(nimbusJwtDecoder)) {
            SecretKeySpec secretKeySpec = new SecretKeySpec(SIGNER_KEY.getBytes(), "HS512");
            nimbusJwtDecoder = NimbusJwtDecoder.withSecretKey(secretKeySpec)
                    .macAlgorithm(MacAlgorithm.HS512)
                    .build();
        }
        return nimbusJwtDecoder.decode(token);
    }

    private void validateToken(String token) throws JOSEException, ParseException {
        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());
        SignedJWT signedJWT = SignedJWT.parse(token);
        // Check signature
        if (!signedJWT.verify(verifier)) {
            throw new ErrorException(ErrorCode.UNAUTHENTICATED);
        }

        // Check expiration time
        Date expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();
        if (expirationTime.before(new Date())) {
            throw new ErrorException(ErrorCode.TOKEN_EXPIRED);
        }

        // Check token revoked
        String jwtId = signedJWT.getJWTClaimsSet().getJWTID();
        if (tokenRevokedRepository.existsById(jwtId)) {
            throw new ErrorException(ErrorCode.UNAUTHENTICATED);
        }
    }
}