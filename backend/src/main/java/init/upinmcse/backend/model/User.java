package init.upinmcse.backend.model;


import init.upinmcse.backend.enums.GENDER;
import init.upinmcse.backend.enums.RoleType;
import jakarta.persistence.*;
import lombok.*;


import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users", uniqueConstraints = {@UniqueConstraint(columnNames = "email")})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "dob")
    private LocalDateTime dob;

    @Column(name = "gender")
    @Enumerated(EnumType.STRING)
    private GENDER gender;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private RoleType role;

    @Column(name = "avt_url")
    private String avtUrl;

    @Column(name = "profile_url")
    private String profileUrl;

    @Column(name = "bio")
    private String bio;

    @Column(name = "enabled")
    private boolean enabled;

    @Column(name = "activation_code")
    private String verifyCode;

    @Column(name = "verify_expired")
    private LocalDateTime verifyExpired;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_followers",
            joinColumns = @JoinColumn(name = "follower_id"),
            inverseJoinColumns = @JoinColumn(name = "following_id")
    )
    private Set<User> followers = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_following",
            joinColumns = @JoinColumn(name = "following_id"),
            inverseJoinColumns = @JoinColumn(name = "follower_id")
    )
    private Set<User> following = new HashSet<>();
}
