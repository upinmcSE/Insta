package init.upinmcse.backend.model;

import init.upinmcse.backend.constant.ConversationType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tb_conversation")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Conversation extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Enumerated(EnumType.STRING)
    ConversationType type;

    @Column(unique = true)
    String hashConversation;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "conversation_user",
            joinColumns = @JoinColumn(name = "conversation_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    Set<User> participants = new HashSet<>();
}
