package com.Hoseo.CapstoneDesign.security.factory;

import com.Hoseo.CapstoneDesign.auth.dto.application.TokenPair;
import com.Hoseo.CapstoneDesign.global.util.TimeUtil;
import com.Hoseo.CapstoneDesign.security.dto.cache.AccessTokenBlackListCache;
import com.Hoseo.CapstoneDesign.security.util.JwtUtil;
import com.Hoseo.CapstoneDesign.user.entity.Users;

import java.time.LocalDateTime;
import java.util.Date;

public class SecurityDtoFactory {
    public static AccessTokenBlackListCache toAccessTokenBlackListCache(String accessToken, Users user, JwtUtil jwtUtil) {
        return AccessTokenBlackListCache.builder()
                .jti(jwtUtil.getJtiFromAccessToken(accessToken))
                .encryptedToken(accessToken)
                .expiryDate(TimeUtil.toLocalDateTime(jwtUtil.getExpirationFromAccessToken(accessToken)))
                .logoutTime(TimeUtil.getNowSeoulLocalDateTime())
                .userId(user.getUserId())
                .build();
    }

    public static TokenPair toTokenPair(String rawAccessToken, String rawRefreshToken, JwtUtil jwtUtil, Users user) {
        Date accessTokenExpiration = jwtUtil.getExpirationFromAccessToken(rawAccessToken);
        Date refreshTokenExpiration = jwtUtil.getExpirationFromRefreshToken(rawRefreshToken);
        LocalDateTime accessTokenExpiryDate = TimeUtil.toLocalDateTime(accessTokenExpiration);
        LocalDateTime refreshTokenExpiryDate = TimeUtil.toLocalDateTime(refreshTokenExpiration);

        return new TokenPair(
                rawAccessToken,
                rawRefreshToken,
                accessTokenExpiryDate,
                refreshTokenExpiryDate,
                user.getServiceNickname() == null
        );
    }
}
