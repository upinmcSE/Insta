package init.upinmcse.backend.exception;

import lombok.Data;

@Data
public class ErrorException extends RuntimeException{
    private ErrorCode errorCode;
    public ErrorException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
