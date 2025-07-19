package init.upinmcse.backend.service;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.SignedJWT;
import init.upinmcse.backend.enums.TYPE_TOKEN;

import java.text.ParseException;
import java.util.Date;

public interface IJwtService {
    String generateToken(String email, TYPE_TOKEN typeToken);

    String extractJwtId(String token, TYPE_TOKEN typeToken) throws ParseException;

    SignedJWT validateToken(String token, boolean isRefresh) throws JOSEException, ParseException;

    Date extractExpiration(String token, TYPE_TOKEN typeToken) throws ParseException;
}
