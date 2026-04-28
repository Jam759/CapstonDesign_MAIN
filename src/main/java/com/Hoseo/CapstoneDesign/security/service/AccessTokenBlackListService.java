package com.Hoseo.CapstoneDesign.security.service;

import com.Hoseo.CapstoneDesign.security.cache.dto.AuthenticatedUserCacheEntry;
import com.Hoseo.CapstoneDesign.security.dto.cache.AccessTokenBlackListCache;

import java.util.Optional;

public interface AccessTokenBlackListService {
    AccessTokenBlackListCache getBlackList(String accessToken);

    Optional<AccessTokenBlackListCache> findBlackList(String accessToken);

    void saveBlackList(String accessToken, AuthenticatedUserCacheEntry user);

    boolean isExistByToken(String accessToken);
}
