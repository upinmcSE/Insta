package init.upinmcse.backend.dto.response;

import init.upinmcse.backend.dto.common.UserInfo;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConversationResponse {
    String id;
    String ownerId;
    String ownerName;
    String ownerAvtUrl;
    UserInfo participant;
    Date createdAt;
}
