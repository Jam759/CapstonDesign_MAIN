package com.Hoseo.CapstoneDesign.security.factory;

import com.Hoseo.CapstoneDesign.global.util.TimeUtil;
import com.Hoseo.CapstoneDesign.global.util.UuidUtil;
import com.Hoseo.CapstoneDesign.security.dto.cache.AccessTokenBlackListCache;
import com.Hoseo.CapstoneDesign.security.entity.AccessTokenBlackList;
import com.Hoseo.CapstoneDesign.security.entity.RefreshToken;
import com.Hoseo.CapstoneDesign.security.util.JwtUtil;
import com.Hoseo.CapstoneDesign.user.entity.Users;
import org.apache.commons.codec.digest.DigestUtils;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

public class SecurityEntityFactory {

    public static RefreshToken toRefreshToken(
            JwtUtil jwtUtil,
            String rawNewToken,
            UUID familyId,
            Users user) {
        String tokenHash =  DigestUtils.sha256Hex(rawNewToken);
        Date expiresAtDate = jwtUtil.getExpirationFromRefreshToken(rawNewToken);
        LocalDateTime expiresAt = TimeUtil.toLocalDateTime(expiresAtDate);
        return RefreshToken.builder()
                .id(UuidUtil.getUuidv7())
                .user(user)
                .tokenHash(tokenHash)
                .familyId(familyId)
                .expiresAt(expiresAt)
                .build();

    }

    public static AccessTokenBlackList toAccessTokenBlackList(
            String rawAccessToken,
            AccessTokenBlackListCache cache,
            Users user
    ) {
        return AccessTokenBlackList.builder()
                .jti(cache.getJti())
                .user(user)
                .tokenHash(DigestUtils.sha256Hex(rawAccessToken))
                .expiresAt(cache.getExpiryDate())
                .build();
    }

}
