package init.upinmcse.backend.dto.event;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostCreatedEvent implements Serializable {
    Long postId;
    String userId;
    String fullName;
    String avtUrl;
    String caption;
    List<String> fileUrls;
    List<String> likedUserIds;
    Date createdAt;
}
