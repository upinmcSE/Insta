package init.upinmcse.backend.model;

import java.util.Date;
import jakarta.persistence.Entity;

import jakarta.persistence.Id;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class TokenRevoked {
    @Id
    String id;

    Date expiryTime;
}
