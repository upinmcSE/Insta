package init.upinmcse.backend.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import init.upinmcse.backend.config.init.MessageQueueConfig;
import init.upinmcse.backend.dto.event.PostCreatedEvent;
import init.upinmcse.backend.repository.db.FollowerRepository;
import init.upinmcse.backend.repository.db.PostRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FollowerUpdateNewFeed {

    FollowerRepository followerRepository;
    RedisTemplate<String, Object> redisTemplate;
    ObjectMapper objectMapper;

    @RabbitListener(queues = MessageQueueConfig.POST_FOLLOWERS_FEED_QUEUE)
    public void updateNewFeed(PostCreatedEvent event) {

        // lấy tất cả userID của những người theo dõi userId
        List<String> followerUserIds = followerRepository.findFollowerUserIdsByFollowingUserId(event.getUserId());

        log.info("xxx: ",followerUserIds.size());

        if (followerUserIds.isEmpty()) {
            return;
        }

        // thêm post mới vào cache của những người theo dõi userId
        for (String followerUserId : followerUserIds) {
            String key = "new_feed:" + followerUserId;
            try {
                String postJson = objectMapper.writeValueAsString(event);
                redisTemplate.opsForList().leftPush(key, postJson);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            redisTemplate.opsForList().trim(key, 0, 20);
        }
    }
}
