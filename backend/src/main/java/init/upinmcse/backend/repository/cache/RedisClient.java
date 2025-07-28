package init.upinmcse.backend.repository.cache;

public interface RedisClient {
    void set(String key, String value, long expirationInSeconds);
    String get(String key);
    void delete(String key);
    boolean exists(String key);
}
