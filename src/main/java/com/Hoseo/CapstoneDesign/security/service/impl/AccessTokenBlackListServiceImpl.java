package com.Hoseo.CapstoneDesign.security.service.impl;

import com.Hoseo.CapstoneDesign.security.dto.cache.AccessTokenBlackListCache;
import com.Hoseo.CapstoneDesign.security.factory.SecurityDtoFactory;
import com.Hoseo.CapstoneDesign.security.repository.cache.AccessTokenBlackListCacheStore;
import com.Hoseo.CapstoneDesign.security.service.AccessTokenBlackListService;
import com.Hoseo.CapstoneDesign.security.util.JwtUtil;
import com.Hoseo.CapstoneDesign.user.entity.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccessTokenBlackListServiceImpl implements AccessTokenBlackListService {

    private final AccessTokenBlackListCacheStore cache;
    private final JwtUtil jwtUtil;

    @Override
    public AccessTokenBlackListCache getBlackList(String accessToken) {
        UUID jti = jwtUtil.getJtiFromAccessToken(accessToken);
        return cache.getByJTI(jti);
    }

    @Override
    public Optional<AccessTokenBlackListCache> findBlackList(String accessToken) {
        UUID jti = jwtUtil.getJtiFromAccessToken(accessToken);
        AccessTokenBlackListCache blackList = cache.getByJTI(jti);
        return Optional.of(blackList);
    }

    @Override
    public void saveBlackList(String accessToken, Users user) {
        cache.setAccessTokenBlackListCache(
                SecurityDtoFactory.toAccessTokenBlackListCache(accessToken, user, jwtUtil)
        );
    }

    @Override
    public boolean isExistByToken(String accessToken) {
        return cache.isExist(jwtUtil.getJtiFromAccessToken(accessToken));
    }
}
