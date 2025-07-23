package init.upinmcse.backend.service;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.SignedJWT;
import init.upinmcse.backend.constant.TokenType;

import java.text.ParseException;
import java.util.Date;

public interface IJwtService {
    String generateToken(String email, TokenType typeToken);

    String extractJwtId(String token, TokenType typeToken) throws ParseException;

    SignedJWT validateToken(String token, boolean isRefresh) throws JOSEException, ParseException;

    Date extractExpiration(String token, TokenType typeToken) throws ParseException;
}
