package init.upinmcse.backend.repository.cache.impl;

import init.upinmcse.backend.repository.cache.RedisClient;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;


@Repository
@RequiredArgsConstructor
@Slf4j(topic = "NewFeedRedis")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NewFeedRedis implements RedisClient {

    RedisTemplate<String, Object> redisTemplate;

    @Override
    public void set(String key, String value, long expirationInSeconds) {}

    @Override
    public String get(String key) {
        String redisKey = "new_feed:" + key;
        Object value = redisTemplate.opsForValue().get(redisKey);
        return (value != null) ? value.toString() : null;
    }

    @Override
    public void delete(String key) {}

    @Override
    public boolean exists(String key) {
        String redisKey = "new_feed:" + key;
        return Boolean.TRUE.equals(redisTemplate.hasKey(redisKey));
    }
}
