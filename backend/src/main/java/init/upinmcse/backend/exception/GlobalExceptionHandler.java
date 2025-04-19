package init.upinmcse.backend.exception;

import init.upinmcse.backend.dto.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
@Slf4j(topic = "GlobalExceptionHandler")
public class GlobalExceptionHandler {

    // Xử lý lỗi đã xác định
    @ExceptionHandler(value = ErrorException.class)
    ResponseEntity<BaseResponse> handlingErrorException(ErrorException exception){
        ErrorCode errorCode = exception.getErrorCode();

        BaseResponse successResponse = new BaseResponse<>();
        successResponse.setCode(errorCode.getCode());
        successResponse.setMessage(errorCode.getMessage());

        return ResponseEntity.status(errorCode.getStatusCode()).body(successResponse);
    }

    // Xử lý lỗi phân quyền
    @ExceptionHandler(value = AccessDeniedException.class)
    ResponseEntity<BaseResponse> handleAuthorizationException(AccessDeniedException exception){
        ErrorCode errorCode = ErrorCode.UNAUTHORIZED;
        BaseResponse response = new BaseResponse<>();
        response.setCode(errorCode.getCode());
        response.setMessage(errorCode.getMessage());

        log.error("Authorization error: {}", exception.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    // Xử lý lỗi validator
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<BaseResponse> handleValidationException(MethodArgumentNotValidException exception) {
        BaseResponse response = new BaseResponse<>();

        // Trích xuất thông tin lỗi validation
        String errorMessage = exception.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .reduce((a, b) -> a + "; " + b)
                .orElse("Dữ liệu không hợp lệ");

        response.setMessage(errorMessage);

        log.error("Validation error: {}", errorMessage);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Xử lý các exception khác
    @ExceptionHandler(Exception.class)
    ResponseEntity<BaseResponse> handleGenericException(Exception exception) {
        BaseResponse response = new BaseResponse<>();
        response.setMessage("Đã xảy ra lỗi hệ thống");

        // Log lỗi chi tiết để debug
        log.error("Unexpected error", exception);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
