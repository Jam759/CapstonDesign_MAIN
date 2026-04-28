package com.Hoseo.CapstoneDesign.security.cache.factory;

import com.Hoseo.CapstoneDesign.global.util.TimeUtil;
import com.Hoseo.CapstoneDesign.security.cache.dto.AccessTokenBlackListCacheEntry;
import com.Hoseo.CapstoneDesign.security.dto.cache.AccessTokenBlackListCache;
import com.Hoseo.CapstoneDesign.security.entity.AccessTokenBlackList;
import org.apache.commons.codec.digest.DigestUtils;

public final class SecurityCacheFactory {

    private SecurityCacheFactory() {
    }

    public static AccessTokenBlackListCacheEntry toAccessTokenBlackListCacheEntry(
            String rawAccessToken,
            AccessTokenBlackListCache cache
    ) {
        return new AccessTokenBlackListCacheEntry(
                cache.getJti(),
                DigestUtils.sha256Hex(rawAccessToken),
                cache.getLogoutTime(),
                cache.getExpiryDate(),
                cache.getUserId()
        );
    }

    public static AccessTokenBlackListCacheEntry toAccessTokenBlackListCacheEntry(
            AccessTokenBlackList entity
    ) {
        return new AccessTokenBlackListCacheEntry(
                entity.getJti(),
                entity.getTokenHash(),
                entity.getCreatedAt(),
                entity.getExpiresAt(),
                entity.getUser().getUserId()
        );
    }

    public static AccessTokenBlackListCache toAccessTokenBlackListCache(
            AccessTokenBlackListCacheEntry entry
    ) {
        return AccessTokenBlackListCache.builder()
                .jti(entry.jti())
                .logoutTime(entry.logoutTime())
                .expiryDate(entry.expiresAt())
                .userId(entry.userId())
                .ttlMillis(TimeUtil.toEpochMilli(entry.expiresAt()) - System.currentTimeMillis())
                .build();
    }
}
