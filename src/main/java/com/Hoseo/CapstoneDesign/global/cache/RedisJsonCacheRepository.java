package com.Hoseo.CapstoneDesign.global.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RedisJsonCacheRepository {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public <T> Optional<T> get(String key, Class<T> type) {
        Assert.hasText(key, "Redis cache key must not be blank");
        Assert.notNull(type, "Redis cache value type must not be null");

        String value = redisTemplate.opsForValue().get(key);
        if (!StringUtils.hasText(value)) {
            return Optional.empty();
        }

        try {
            return Optional.of(objectMapper.readValue(value, type));
        } catch (JsonProcessingException e) {
            throw new CacheOperationException("Failed to deserialize Redis cache value. key=" + key, e);
        }
    }

    public <T> Optional<List<T>> getList(String key, Class<T> elementType) {
        Assert.hasText(key, "Redis cache key must not be blank");
        Assert.notNull(elementType, "Redis cache list element type must not be null");

        String value = redisTemplate.opsForValue().get(key);
        if (!StringUtils.hasText(value)) {
            return Optional.empty();
        }

        JavaType type = objectMapper.getTypeFactory().constructCollectionType(List.class, elementType);
        try {
            return Optional.of(objectMapper.readValue(value, type));
        } catch (JsonProcessingException e) {
            throw new CacheOperationException("Failed to deserialize Redis cache list value. key=" + key, e);
        }
    }

    public void set(String key, Object value, Duration ttl) {
        Assert.hasText(key, "Redis cache key must not be blank");
        Assert.notNull(value, "Redis cache value must not be null");

        try {
            String serialized = objectMapper.writeValueAsString(value);
            if (ttl == null || ttl.isZero() || ttl.isNegative()) {
                redisTemplate.opsForValue().set(key, serialized);
                return;
            }
            redisTemplate.opsForValue().set(key, serialized, ttl);
        } catch (JsonProcessingException e) {
            throw new CacheOperationException("Failed to serialize Redis cache value. key=" + key, e);
        }
    }

    public void delete(String key) {
        Assert.hasText(key, "Redis cache key must not be blank");
        redisTemplate.delete(key);
    }

    public void deleteAll(List<String> keys) {
        if (keys == null || keys.isEmpty()) {
            return;
        }
        redisTemplate.delete(keys);
    }

    public boolean exists(String key) {
        Assert.hasText(key, "Redis cache key must not be blank");
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
}
