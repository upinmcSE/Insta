package init.upinmcse.backend.exception;

public class TokenExpiredException extends ErrorException {
    public TokenExpiredException(){
        super(ErrorCode.TOKEN_EXPIRED);
    }
}
