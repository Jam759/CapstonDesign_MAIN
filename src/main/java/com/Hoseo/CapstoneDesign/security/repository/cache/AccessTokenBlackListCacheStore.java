package com.Hoseo.CapstoneDesign.security.repository.cache;

import com.Hoseo.CapstoneDesign.security.dto.cache.AccessTokenBlackListCache;

import java.util.UUID;

public interface AccessTokenBlackListCacheStore {
    void setAccessTokenBlackListCache(AccessTokenBlackListCache cache);

    boolean isExist(UUID jti);

    AccessTokenBlackListCache getByJTI(UUID jti);

    void removeBlackList(UUID jti);
}

