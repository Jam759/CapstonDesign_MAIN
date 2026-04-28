package com.Hoseo.CapstoneDesign.security.cache.repository;

import com.Hoseo.CapstoneDesign.global.cache.RedisJsonCacheRepository;
import com.Hoseo.CapstoneDesign.security.cache.dto.AccessTokenBlackListCacheEntry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class AccessTokenBlackListCacheRepository {

    private static final String BLACKLIST_KEY_PREFIX = "auth:blacklist:access-token:";
    private static final String NEGATIVE_KEY_PREFIX = "auth:blacklist:negative:";

    private final RedisJsonCacheRepository redisJsonCacheRepository;

    public void saveBlacklisted(AccessTokenBlackListCacheEntry entry, Duration ttl) {
        redisJsonCacheRepository.set(blacklistedKey(entry.jti()), entry, ttl);
    }

    public Optional<AccessTokenBlackListCacheEntry> findBlacklisted(UUID jti) {
        return redisJsonCacheRepository.get(blacklistedKey(jti), AccessTokenBlackListCacheEntry.class);
    }

    public boolean existsBlacklisted(UUID jti) {
        return redisJsonCacheRepository.exists(blacklistedKey(jti));
    }

    public void saveNegative(UUID jti, Duration ttl) {
        redisJsonCacheRepository.set(negativeKey(jti), Boolean.TRUE, ttl);
    }

    public boolean existsNegative(UUID jti) {
        return redisJsonCacheRepository.exists(negativeKey(jti));
    }

    public void delete(UUID jti) {
        redisJsonCacheRepository.deleteAll(List.of(blacklistedKey(jti), negativeKey(jti)));
    }

    public void deleteNegative(UUID jti) {
        redisJsonCacheRepository.delete(negativeKey(jti));
    }

    private String blacklistedKey(UUID jti) {
        return BLACKLIST_KEY_PREFIX + jti;
    }

    private String negativeKey(UUID jti) {
        return NEGATIVE_KEY_PREFIX + jti;
    }
}
