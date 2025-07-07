package init.upinmcse.backend.repository.db;

import org.springframework.stereotype.Repository;

@Repository
public class FeedRepository {
//    private static final String FEED_KEY_PREFIX = "feed:";
//
//    @Autowired
//    private RedisTemplate<String, Long> redisTemplate;
//
//    public Long getFeedSize(int userId) {
//        String feedKey = FEED_KEY_PREFIX + userId;
//        return redisTemplate.opsForList().size(feedKey);
//    }
//
//    public void addPostToFeed(int postId, int userId) {
//        String feedKey = FEED_KEY_PREFIX + userId;
//        redisTemplate.opsForList().leftPush(feedKey, (long) postId);
//        // uncomment if need to limit the number of posts in the feed
//        // redisTemplate.opsForList().trim(feedKey, 0, 1000); // Keep only the latest
//        // 1000 posts
//    }
//
//    public List<Long> getFeed(int userId, int limit, int page) {
//        String feedKey = FEED_KEY_PREFIX + userId;
//        int start = (page - 1) * limit;
//        int end = start + limit - 1;
//        return redisTemplate.opsForList().range(feedKey, start, end);
//    }
}
