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
    ROLE_NOT_FOUND(4007, "Không tìm thấy vai trò", HttpStatus.BAD_REQUEST),
    USER_ALREADY_EXISTS(4008, "Người dùng đã tồn tại", HttpStatus.BAD_REQUEST),
    FILE_NOT_FOUND(4009, "Không tìm thấy file", HttpStatus.NOT_FOUND),
    TOKEN_EXPIRED(4010, "Token đã hết hạn", HttpStatus.UNAUTHORIZED),
    TOKEN_REVOKED(4011, "Token đã bị thu hồi", HttpStatus.UNAUTHORIZED),
    INVALID_TOKEN(4012, "Token không hợp lệ", HttpStatus.UNAUTHORIZED),
    INVALID_PASSWORD(4013, "Mật khẩu không khớp", HttpStatus.UNAUTHORIZED),
    POST_NOT_FOUND(4004, "Không tìm thấy bài viết", HttpStatus.NOT_FOUND),
    ALREADY_FOLLOWING(4004, "Bạn đã theo dõi người dùng này", HttpStatus.BAD_REQUEST),
    NOT_FOLLOWING(4004, "Bạn chưa theo dõi người dùng này", HttpStatus.BAD_REQUEST),
    POST_ALREADY_LIKED(4004, "Bạn đã thích bài viết này", HttpStatus.BAD_REQUEST),
    POST_NOT_LIKED(4004, "Bạn chưa thích bài viết này", HttpStatus.BAD_REQUEST),
    COMMENT_NOT_FOUND(4004, "Không tìm thấy bình luận", HttpStatus.NOT_FOUND),
    REPLY_COMMENT_NOT_FOUND(4004, "Không tìm thấy bình luận trả lời", HttpStatus.NOT_FOUND),
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

