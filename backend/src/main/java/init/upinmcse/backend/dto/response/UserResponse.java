package init.upinmcse.backend.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

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
    String avtUrl;
    String bio;
    List<String> followers; // List of user IDs who follow this user
    List<String> following; // List of user IDs that this user follows

}
