package init.upinmcse.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import init.upinmcse.backend.enums.NotificationType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tb_notification")
public class Notification extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "from_user_id", nullable = false)
    User fromUser;

    @ManyToOne
    @JoinColumn(name = "to_user_id", nullable = false)
    User toUser;

    @NotNull
    @Enumerated(EnumType.STRING)
    NotificationType notificationType;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    @JsonIgnore
    private Post post;


}
