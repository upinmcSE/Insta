package init.upinmcse.backend.repository;

import init.upinmcse.backend.model.RepComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepCommentRepository extends JpaRepository<RepComment, Long> {
    // Custom query methods can be defined here if needed
    // For example, find by post ID or user ID
}
