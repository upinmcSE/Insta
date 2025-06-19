package init.upinmcse.backend.dto.response;

import init.upinmcse.backend.enums.GENDER;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {
    String id;
    String email;
    String fullName;
    LocalDate dob;
    GENDER gender;
    String avtUrl;
    String bio;
    List<String> followers; // List of user IDs who follow this user
    List<String> following; // List of user IDs that this user follows

}
