package init.upinmcse.backend.repository;

import init.upinmcse.backend.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    // Custom query methods can be defined here if needed
    // For example, find comments by post ID or user ID
}
