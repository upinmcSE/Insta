package init.upinmcse.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import init.upinmcse.backend.constant.NotificationType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tb_notification")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Notification extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    int id;

    @ManyToOne
    @JoinColumn(name = "from_user_id", nullable = false)
    User fromUser;

    @ManyToOne
    @JoinColumn(name = "to_user_id", nullable = false)
    User toUser;

    @NotNull
    @Enumerated(EnumType.STRING)
    NotificationType notificationType;

    String content;

    boolean isRead = false;


}
