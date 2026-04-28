package com.Hoseo.CapstoneDesign.security.service.impl;

import com.Hoseo.CapstoneDesign.security.cache.service.AccessTokenBlackListCacheService;
import com.Hoseo.CapstoneDesign.security.cache.dto.AuthenticatedUserCacheEntry;
import com.Hoseo.CapstoneDesign.security.dto.cache.AccessTokenBlackListCache;
import com.Hoseo.CapstoneDesign.security.factory.SecurityDtoFactory;
import com.Hoseo.CapstoneDesign.security.service.AccessTokenBlackListService;
import com.Hoseo.CapstoneDesign.security.util.JwtUtil;
import com.Hoseo.CapstoneDesign.global.util.TimeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccessTokenBlackListServiceImpl implements AccessTokenBlackListService {

    private final AccessTokenBlackListCacheService cacheService;
    private final JwtUtil jwtUtil;

    @Override
    public AccessTokenBlackListCache getBlackList(String accessToken) {
        UUID jti = jwtUtil.getJtiFromAccessToken(accessToken);
        return cacheService.findByJti(jti).orElse(null);
    }

    @Override
    public Optional<AccessTokenBlackListCache> findBlackList(String accessToken) {
        UUID jti = jwtUtil.getJtiFromAccessToken(accessToken);
        return cacheService.findByJti(jti);
    }

    @Override
    public void saveBlackList(String accessToken, AuthenticatedUserCacheEntry user) {
        AccessTokenBlackListCache cache =
                SecurityDtoFactory.toAccessTokenBlackListCache(accessToken, user, jwtUtil);
        cacheService.save(accessToken, cache);
    }

    @Override
    public boolean isExistByToken(String accessToken) {
        return cacheService.exists(
                jwtUtil.getJtiFromAccessToken(accessToken),
                TimeUtil.toLocalDateTime(jwtUtil.getExpirationFromAccessToken(accessToken))
        );
    }
}
