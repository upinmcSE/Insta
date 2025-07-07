package init.upinmcse.backend.repository.cache.impl;

import init.upinmcse.backend.repository.cache.RedisClient;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
@Slf4j(topic = "TokenRedis")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TokenRedis implements RedisClient {

    RedisTemplate<String, Object> redisTemplate;

    @Override
    public void set(String key, String value, long expirationInSeconds) {
        String redisKey = "refresh_token:" + key;
        log.info("Setting Redis key: {}, value: {}, expiration: {} seconds", redisKey, value, expirationInSeconds);
        redisTemplate.opsForValue().set(redisKey, value, expirationInSeconds, TimeUnit.SECONDS);
    }

    @Override
    public String get(String key) {
        String redisKey = "refresh_token:" + key;
        Object value = redisTemplate.opsForValue().get(redisKey);
        return (value != null) ? value.toString() : null;
    }

    @Override
    public void delete(String key) {
        String redisKey = "refresh_token:" + key;
        redisTemplate.delete(redisKey);
    }
}
