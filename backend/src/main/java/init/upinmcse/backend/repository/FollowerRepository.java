package init.upinmcse.backend.repository;

import init.upinmcse.backend.model.UserFollowing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FollowerRepository extends JpaRepository<UserFollowing, Integer> {

    boolean existsByFollowerUserIdAndFollowingUserId(String followerUserId, String followingUserId);

    Optional<UserFollowing> findByFollowerUserIdAndFollowingUserId(String followerUserId, String followingUserId);

    @Query("SELECT uf.followingUserId FROM UserFollowing uf WHERE uf.followerUserId = :followerUserId")
    List<String> findFollowingUserIdsByFollowerUserId(@Param("followerUserId") String followerUserId);


    @Query("SELECT uf.followerUserId FROM UserFollowing uf WHERE uf.followingUserId = :followingUserId")
    List<String> findFollowerUserIdsByFollowingUserId(@Param("followingUserId") String followingUserId);
}
