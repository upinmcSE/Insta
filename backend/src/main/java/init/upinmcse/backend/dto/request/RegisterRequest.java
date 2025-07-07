package init.upinmcse.backend.dto.request;

import init.upinmcse.backend.validator.PasswordConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegisterRequest {

    @Email(message = "Không đúng định dạng email")
    String email;

    @PasswordConstraint(message = "Mật khẩu phải có ít nhất 8 ký tự, bao gồm chữ hoa, chữ thường, số và ký tự đặc biệt")
    String password;

    @NonNull
    String fullName;
}