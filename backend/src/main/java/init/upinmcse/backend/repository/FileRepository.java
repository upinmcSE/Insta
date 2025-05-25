package init.upinmcse.backend.repository;

import init.upinmcse.backend.enums.FileType;
import init.upinmcse.backend.model.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileRepository extends JpaRepository<File, String> {

    File findByFileTypeAndUserId(FileType fileType, String userId);

    List<File> findAllByPostId(Long postId);
}
