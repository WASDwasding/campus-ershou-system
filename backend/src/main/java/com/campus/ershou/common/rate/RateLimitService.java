package com.campus.ershou.common.rate;

import com.campus.ershou.common.api.ApiException;
import com.campus.ershou.common.util.RedisKeys;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

@Component
public class RateLimitService {

    private final StringRedisTemplate redis;

    public RateLimitService(StringRedisTemplate redis) {
        this.redis = redis;
    }

    public void check(String rule, String id, int limit, Duration window) {
        long windowId = Instant.now().getEpochSecond() / Math.max(window.getSeconds(), 1);
        String key = RedisKeys.rateLimit(rule, id, String.valueOf(windowId));
        Long count = redis.opsForValue().increment(key);
        if (count != null && count == 1L) {
            redis.expire(key, window.plusSeconds(1));
        }
        if (count != null && count > limit) {
            throw ApiException.rateLimited(window.getSeconds());
        }
    }

    public boolean tryCooldown(String key, Duration ttl) {
        Boolean ok = redis.opsForValue().setIfAbsent(key, "1", ttl);
        return Boolean.TRUE.equals(ok);
    }
}
