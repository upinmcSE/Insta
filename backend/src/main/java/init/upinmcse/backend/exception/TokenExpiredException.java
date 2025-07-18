package init.upinmcse.backend.exception;

import org.springframework.security.oauth2.jwt.JwtException;

public class TokenExpiredException extends JwtException {
    public TokenExpiredException(String message) {
        super(message);
    }
    public TokenExpiredException(String message, Throwable cause) {
        super(message, cause);
    }
}
