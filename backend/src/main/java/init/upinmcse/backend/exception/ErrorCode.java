package init.upinmcse.backend.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Lỗi chưa phân loại", HttpStatus.INTERNAL_SERVER_ERROR),
    UNAUTHORIZED(401, "Bạn không có quyền truy cập tài nguyên này", HttpStatus.UNAUTHORIZED),
    UNAUTHENTICATED(4001, "Tài khoản hoặc mật khẩu chưa đúng", HttpStatus.UNAUTHORIZED),
    USERNAME_EXISTS(4002, "Tài khoản đã tồn tại", HttpStatus.BAD_REQUEST),
    EMAIL_EXISTS(4003, "Email đã tồn tại", HttpStatus.BAD_REQUEST),
    NOT_FOUND_USER(4004, "Không tìm thấy người dùng", HttpStatus.NOT_FOUND),
    ACTIVATION_CODE_NOT_MATCH(4005, "Mã kích hoạt không đúng", HttpStatus.BAD_REQUEST),
    USER_NOT_ACTIVATED(4006, "Tài khoản chưa được kích hoạt hoặc đã bị khóa", HttpStatus.BAD_REQUEST),
    ;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;
}

