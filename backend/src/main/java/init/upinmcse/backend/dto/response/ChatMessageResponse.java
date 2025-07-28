package init.upinmcse.backend.dto.response;

import init.upinmcse.backend.dto.common.UserInfo;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level=AccessLevel.PRIVATE)
public class ChatMessageResponse {
    String id;
    String conversationId;
    boolean sender;
    String message;
    UserInfo senderInfo;
    Date createdAt;
}
