package init.upinmcse.backend.dto.request;

import init.upinmcse.backend.validator.PasswordConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ResetPasswordRequest {
    @Email(message = "Sai định dạng email")
    String email;

    @PasswordConstraint(message = "Mật khẩu không hợp lệ")
    String newPassword;

    @Size(min = 4, max = 4, message = "Mã xác nhận không hợp lệ")
    String code;
}
