package com.campus.ershou.common.redis;

import com.campus.ershou.common.util.Randoms;
import com.campus.ershou.common.util.RedisKeys;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;

@Component
public class GoodsLock {

    private static final DefaultRedisScript<Long> UNLOCK = new DefaultRedisScript<>("""
            if redis.call('get', KEYS[1]) == ARGV[1] then
                return redis.call('del', KEYS[1])
            else
                return 0
            end
            """, Long.class);

    private final StringRedisTemplate redis;

    public GoodsLock(StringRedisTemplate redis) {
        this.redis = redis;
    }

    public String tryLock(long goodsId) {
        String requestId = Randoms.hex(16);
        Boolean ok = redis.opsForValue().setIfAbsent(
                RedisKeys.goodsLock(goodsId),
                requestId,
                Duration.ofMillis(5000)
        );
        return Boolean.TRUE.equals(ok) ? requestId : null;
    }

    public void unlock(long goodsId, String requestId) {
        if (requestId == null) {
            return;
        }
        redis.execute(UNLOCK, List.of(RedisKeys.goodsLock(goodsId)), requestId);
    }
}
