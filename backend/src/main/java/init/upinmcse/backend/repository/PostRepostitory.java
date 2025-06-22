package init.upinmcse.backend.repository;

import init.upinmcse.backend.enums.Status;
import init.upinmcse.backend.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepostitory extends JpaRepository<Post, Long> {
    Optional<Post> findById(Long postId);
    boolean existsById(Long postId);
    // lấy tất cả bài viết của tất cả người dùng với trạng thái cụ thể
    Page<Post> findAllByStatus(Status status, Pageable pageable);

    // lấy tất cả bài viết theo list id
    @Query("SELECT p FROM Post p WHERE p.id IN :postIds AND p.status = init.upinmcse.backend.enums.Status.ACTIVE")
    List<Post> findAllByIdInAndStatus(@Param("postIds") List<Long> postIds, @Param("status") Status status);

    // lây tất cả bài viết của những nguoi mà userId theo dõi
    @Query("SELECT p.id FROM Post p " +
            "JOIN UserFollowing f ON p.user.id = f.followingUserId " +
            "WHERE f.followerUserId = :followerUserId " +
            "AND p.status = init.upinmcse.backend.enums.Status.ACTIVE")
    List<Long> findPostIdsByFollowerUserId(@Param("followerUserId") String followerUserId);

}
