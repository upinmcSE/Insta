package init.upinmcse.backend.model;

import init.upinmcse.backend.enums.GENDER;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Slf4j(topic = "UserProfileEntity")
@Entity
@Table(name = "tb_user_profile")
public class UserProfile {
    @Id
    private String id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "dob")
    private LocalDate dob;

    @Column(name = "gender")
    @Enumerated(EnumType.STRING)
    private GENDER gender;

    @Column(name = "avt_url")
    private String avtUrl;

    @Column(name = "bio", length = 500)
    private String bio;
}
