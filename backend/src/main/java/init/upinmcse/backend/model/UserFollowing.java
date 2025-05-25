package init.upinmcse.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tb_following", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "followerUserId", "followingUserId" }) })
public class UserFollowing extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    private String followerUserId;
    private String followingUserId;
}
