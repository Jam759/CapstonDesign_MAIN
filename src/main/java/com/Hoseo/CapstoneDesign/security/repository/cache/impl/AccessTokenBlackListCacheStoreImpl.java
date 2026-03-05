package com.Hoseo.CapstoneDesign.security.repository.cache.impl;

import com.Hoseo.CapstoneDesign.security.dto.cache.AccessTokenBlackListCache;
import com.Hoseo.CapstoneDesign.security.repository.cache.AccessTokenBlackListCacheStore;
import com.Hoseo.CapstoneDesign.security.util.JwtUtil;
import com.github.benmanes.caffeine.cache.Cache;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class AccessTokenBlackListCacheStoreImpl implements AccessTokenBlackListCacheStore {

    private final JwtUtil jwtUtil;
    private final Cache<String, AccessTokenBlackListCache> accessTokenCache;
    private final String CACHE_ID = "BL";

    @Override
    public void setAccessTokenBlackListCache(AccessTokenBlackListCache cache) {
        // 1) JWT 만료시간 기반 TTL 계산
        long expireMillis = jwtUtil.getExpirationFromAccessToken(cache.getEncryptedToken()).getTime();
        long nowMillis = System.currentTimeMillis();
        long ttl = Math.max(expireMillis - nowMillis, 0);
        // 2) 객체에 TTL 저장
        cache.setTtlMillis(ttl);
        // 3) 캐시에 저장
        accessTokenCache.put(CACHE_ID + cache.getJti().toString(), cache);
    }

    @Override
    public boolean isExist(UUID jti) {
        return accessTokenCache.getIfPresent(CACHE_ID + jti) != null;
    }

    @Override
    public AccessTokenBlackListCache getByJTI(UUID jti) {
        return accessTokenCache.getIfPresent(CACHE_ID + jti);
    }

    @Override
    public void removeBlackList(UUID jti) {
        accessTokenCache.invalidate(CACHE_ID + jti);
    }
}
