package com.Hoseo.CapstoneDesign.security.cache.service;

import com.Hoseo.CapstoneDesign.global.cache.CacheOperationException;
import com.Hoseo.CapstoneDesign.global.util.TimeUtil;
import com.Hoseo.CapstoneDesign.security.cache.dto.AccessTokenBlackListCacheEntry;
import com.Hoseo.CapstoneDesign.security.cache.factory.SecurityCacheFactory;
import com.Hoseo.CapstoneDesign.security.cache.repository.AccessTokenBlackListCacheRepository;
import com.Hoseo.CapstoneDesign.security.dto.cache.AccessTokenBlackListCache;
import com.Hoseo.CapstoneDesign.security.entity.AccessTokenBlackList;
import com.Hoseo.CapstoneDesign.security.exception.AccessTokenBlackListErrorCode;
import com.Hoseo.CapstoneDesign.security.exception.AccessTokenBlackListException;
import com.Hoseo.CapstoneDesign.security.factory.SecurityEntityFactory;
import com.Hoseo.CapstoneDesign.security.repository.AccessTokenBlackListRepository;
import com.Hoseo.CapstoneDesign.user.entity.Users;
import com.Hoseo.CapstoneDesign.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccessTokenBlackListCacheService {

    private static final Duration NEGATIVE_CACHE_TTL = Duration.ofSeconds(30);

    private final AccessTokenBlackListCacheRepository cacheRepository;
    private final AccessTokenBlackListRepository dbRepository;
    private final UserService userService;

    @Transactional
    public void save(String rawAccessToken, AccessTokenBlackListCache cache) {
        Users user = userService.getReferenceById(cache.getUserId());
        AccessTokenBlackList entity =
                SecurityEntityFactory.toAccessTokenBlackList(rawAccessToken, cache, user);
        dbRepository.save(entity);

        Duration ttl = ttlUntil(cache.getExpiryDate());
        if (ttl.isZero()) {
            return;
        }

        try {
            AccessTokenBlackListCacheEntry entry =
                    SecurityCacheFactory.toAccessTokenBlackListCacheEntry(rawAccessToken, cache);
            cacheRepository.deleteNegative(cache.getJti());
            cacheRepository.saveBlacklisted(entry, ttl);
        } catch (DataAccessException | CacheOperationException ignored) {
        }
    }

    @Transactional(readOnly = true)
    public boolean exists(UUID jti, LocalDateTime expiresAt) {
        try {
            if (cacheRepository.existsBlacklisted(jti)) {
                return true;
            }
            if (cacheRepository.existsNegative(jti)) {
                return false;
            }
        } catch (DataAccessException | CacheOperationException ignored) {
            return existsInDbFailClosed(jti);
        }

        boolean exists = existsInDbFailClosed(jti);
        cacheLookupResult(jti, expiresAt, exists);
        return exists;
    }

    @Transactional(readOnly = true)
    public Optional<AccessTokenBlackListCache> findByJti(UUID jti) {
        try {
            Optional<AccessTokenBlackListCacheEntry> cached = cacheRepository.findBlacklisted(jti);
            if (cached.isPresent()) {
                return cached.map(SecurityCacheFactory::toAccessTokenBlackListCache);
            }
        } catch (DataAccessException | CacheOperationException ignored) {
        }

        try {
            return dbRepository.findByJtiAndExpiresAtAfter(jti, TimeUtil.getNowSeoulLocalDateTime())
                    .map(SecurityCacheFactory::toAccessTokenBlackListCacheEntry)
                    .map(SecurityCacheFactory::toAccessTokenBlackListCache);
        } catch (DataAccessException e) {
            throw new AccessTokenBlackListException(AccessTokenBlackListErrorCode.TOKEN_BLACKLIST_CHECK_FAILED);
        }
    }

    @Transactional
    public void remove(UUID jti) {
        dbRepository.deleteById(jti);
        try {
            cacheRepository.delete(jti);
        } catch (DataAccessException | CacheOperationException ignored) {
        }
    }

    private boolean existsInDbFailClosed(UUID jti) {
        try {
            return dbRepository.existsByJtiAndExpiresAtAfter(jti, TimeUtil.getNowSeoulLocalDateTime());
        } catch (DataAccessException e) {
            throw new AccessTokenBlackListException(AccessTokenBlackListErrorCode.TOKEN_BLACKLIST_CHECK_FAILED);
        }
    }

    private void cacheLookupResult(UUID jti, LocalDateTime expiresAt, boolean exists) {
        try {
            if (exists) {
                dbRepository.findByJtiAndExpiresAtAfter(jti, TimeUtil.getNowSeoulLocalDateTime())
                        .map(SecurityCacheFactory::toAccessTokenBlackListCacheEntry)
                        .ifPresent(entry -> {
                            Duration ttl = ttlUntil(entry.expiresAt());
                            if (!ttl.isZero()) {
                                cacheRepository.saveBlacklisted(entry, ttl);
                            }
                        });
                return;
            }

            Duration ttl = ttlUntil(expiresAt);
            if (ttl.isZero()) {
                return;
            }
            cacheRepository.saveNegative(jti, ttl.compareTo(NEGATIVE_CACHE_TTL) < 0 ? ttl : NEGATIVE_CACHE_TTL);
        } catch (DataAccessException | CacheOperationException ignored) {
        }
    }

    private Duration ttlUntil(LocalDateTime expiresAt) {
        if (expiresAt == null) {
            return Duration.ZERO;
        }

        Duration ttl = Duration.between(TimeUtil.getNowSeoulLocalDateTime(), expiresAt);
        if (ttl.isNegative() || ttl.isZero()) {
            return Duration.ZERO;
        }
        return ttl;
    }
}
