package init.upinmcse.backend.repository;

import init.upinmcse.backend.enums.Status;
import init.upinmcse.backend.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostRepostitory extends JpaRepository<Post, Long> {
    Optional<Post> findById(Long postId);
    boolean existsById(Long postId);
    Page<Post> findAllByUserIdAndStatus(String userId, Status status, Pageable pageable);

    // lấy tất cả bài viết của tất cả người dùng với trạng thái cụ thể
    Page<Post> findAllByStatus(Status status, Pageable pageable);

    // lấy tất cả bài viết mà của người dùng mà người dùng theo dõi
    //Page<Post> findAllByUserIdInAndStatus(Iterable<String> userIds, Status status, Pageable pageable);

}
