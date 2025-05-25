package init.upinmcse.backend.dto.request;

import init.upinmcse.backend.validator.PasswordConstraint;
import jakarta.validation.constraints.Email;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegisterRequest {
    @Email(message = "INVALID_EMAIL")
    String email;

    @PasswordConstraint(message = "INVALID_PASSWORD")
    String password;
}