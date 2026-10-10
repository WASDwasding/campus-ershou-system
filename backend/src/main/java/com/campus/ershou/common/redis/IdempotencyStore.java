package com.campus.ershou.common.redis;

import com.campus.ershou.common.api.ApiException;
import com.campus.ershou.common.api.ErrorCode;
import com.campus.ershou.common.api.FieldErrorItem;
import com.campus.ershou.common.util.RedisKeys;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@Component
public class IdempotencyStore {

    private static final TypeReference<Map<String, Object>> MAP = new TypeReference<>() {
    };

    private final StringRedisTemplate redis;
    private final ObjectMapper objectMapper;

    public IdempotencyStore(StringRedisTemplate redis, ObjectMapper objectMapper) {
        this.redis = redis;
        this.objectMapper = objectMapper;
    }

    public Map<String, Object> run(long userId, String key, Supplier<Map<String, Object>> action) {
        if (key == null || key.isBlank()) {
            return action.get();
        }
        String trimmed = key.trim();
        if (trimmed.length() < 8 || trimmed.length() > 64 || !trimmed.matches("[A-Za-z0-9_-]+")) {
            throw ApiException.validation(List.of(new FieldErrorItem("Idempotency-Key", "长度为 8–64 位")));
        }
        String redisKey = RedisKeys.idempotency(userId, trimmed);
        Boolean created = redis.opsForValue().setIfAbsent(redisKey, "PROCESSING", Duration.ofMinutes(10));
        if (!Boolean.TRUE.equals(created)) {
            String existing = redis.opsForValue().get(redisKey);
            if (existing == null || "PROCESSING".equals(existing)) {
                throw ApiException.of(ErrorCode.CONFLICT);
            }
            try {
                return objectMapper.readValue(existing, MAP);
            } catch (Exception ex) {
                throw ApiException.of(ErrorCode.CONFLICT);
            }
        }
        try {
            Map<String, Object> result = action.get();
            redis.opsForValue().set(redisKey, objectMapper.writeValueAsString(result), Duration.ofMinutes(10));
            return result;
        } catch (RuntimeException ex) {
            redis.delete(redisKey);
            throw ex;
        } catch (Exception ex) {
            redis.delete(redisKey);
            throw ApiException.of(ErrorCode.INTERNAL_ERROR);
        }
    }
}
