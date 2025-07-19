package init.upinmcse.backend.dto.response;

import init.upinmcse.backend.dto.request.UserLoginInfo;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JwtResponse {
    String accessToken;
    UserLoginInfo userLoginInfo;
}
