package init.upinmcse.backend.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostResponse {
    Long postId;
    String userId;
    String fullName;
    String avtUrl;
    String caption;
    List<String> fileUrls;
    List<String> likedUserIds;
}
