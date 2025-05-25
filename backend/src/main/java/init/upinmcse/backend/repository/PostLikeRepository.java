package init.upinmcse.backend.repository;

import init.upinmcse.backend.model.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    boolean existsByPostIdAndUserId(Long postId, String userId);

    void deleteByPostIdAndUserId(Long postId, String userId);

    List<PostLike> findAllByPostId(Long postId);
}
