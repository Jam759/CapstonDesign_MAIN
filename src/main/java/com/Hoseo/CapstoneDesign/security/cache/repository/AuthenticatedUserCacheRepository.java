package com.Hoseo.CapstoneDesign.security.cache.repository;

import com.Hoseo.CapstoneDesign.global.cache.RedisJsonCacheRepository;
import com.Hoseo.CapstoneDesign.security.cache.dto.AuthenticatedUserCacheEntry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class AuthenticatedUserCacheRepository {

    private static final String AUTHENTICATED_USER_KEY_PREFIX = "auth:user:";

    private final RedisJsonCacheRepository redisJsonCacheRepository;

    public Optional<AuthenticatedUserCacheEntry> findByIdentityId(UUID identityId) {
        return redisJsonCacheRepository.get(userKey(identityId), AuthenticatedUserCacheEntry.class);
    }

    public void save(AuthenticatedUserCacheEntry entry, Duration ttl) {
        redisJsonCacheRepository.set(userKey(entry.identityId()), entry, ttl);
    }

    public void delete(UUID identityId) {
        redisJsonCacheRepository.delete(userKey(identityId));
    }

    private String userKey(UUID identityId) {
        return AUTHENTICATED_USER_KEY_PREFIX + identityId;
    }
}
