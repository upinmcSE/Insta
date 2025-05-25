package init.upinmcse.backend.model;

import init.upinmcse.backend.enums.FileType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tb_file")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class File {
    @Id
    String id;
    String contentType;
    String path;
    String url;
    long size;
    String md5Checksum;
    Long postId;
    String userId;
    @Enumerated(EnumType.STRING)
    FileType fileType;
}
