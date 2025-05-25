package init.upinmcse.backend.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReplyCommentResponse {
    Long replyId;
    Long commentId;
    String content;
    String userId;
    String fullName;
    String avtUrl;
    Date createdAt;
}
