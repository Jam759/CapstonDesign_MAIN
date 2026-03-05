package com.Hoseo.CapstoneDesign.security.factory;

import com.Hoseo.CapstoneDesign.auth.dto.application.TokenPair;
import com.Hoseo.CapstoneDesign.security.dto.cache.AccessTokenBlackListCache;
import com.Hoseo.CapstoneDesign.security.util.JwtUtil;
import com.Hoseo.CapstoneDesign.user.entity.Users;
import com.Hoseo.CapstoneDesign.user.entity.enums.OauthType;
import com.Hoseo.CapstoneDesign.user.entity.enums.SystemRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SecurityDtoFactoryTest {

    @DisplayName("toAccessTokenBlackListCache: access token과 user를 블랙리스트 DTO로 변환한다")
    @Test
    void toAccessTokenBlackListCache_success() {
        JwtUtil jwtUtil = mock(JwtUtil.class);
        String accessToken = "access-token";
        UUID jti = UUID.randomUUID();
        Users user = user();
        Date expiration = java.sql.Timestamp.valueOf(LocalDateTime.now().plusMinutes(10));

        when(jwtUtil.getJtiFromAccessToken(accessToken)).thenReturn(jti);
        when(jwtUtil.getExpirationFromAccessToken(accessToken)).thenReturn(expiration);

        AccessTokenBlackListCache result = SecurityDtoFactory.toAccessTokenBlackListCache(accessToken, user, jwtUtil);

        assertThat(result.getJti()).isEqualTo(jti);
        assertThat(result.getEncryptedToken()).isEqualTo(accessToken);
        assertThat(result.getUserId()).isEqualTo(user.getUserId());
        assertThat(result.getExpiryDate()).isNotNull();
        assertThat(result.getLogoutTime()).isNotNull();
    }

    @DisplayName("toTokenPair: 토큰 정보와 만료시각을 TokenPair로 변환한다")
    @Test
    void toTokenPair_success() {
        JwtUtil jwtUtil = mock(JwtUtil.class);
        String accessToken = "access-token";
        String refreshToken = "refresh-token";
        Users user = user();
        Date accessExpiration = java.sql.Timestamp.valueOf(LocalDateTime.now().plusMinutes(15));
        Date refreshExpiration = java.sql.Timestamp.valueOf(LocalDateTime.now().plusDays(7));

        when(jwtUtil.getExpirationFromAccessToken(accessToken)).thenReturn(accessExpiration);
        when(jwtUtil.getExpirationFromRefreshToken(refreshToken)).thenReturn(refreshExpiration);

        TokenPair result = SecurityDtoFactory.toTokenPair(accessToken, refreshToken, jwtUtil, user);

        assertThat(result.accessToken()).isEqualTo(accessToken);
        assertThat(result.refreshToken()).isEqualTo(refreshToken);
        assertThat(result.accessTokenExpiredAt()).isNotNull();
        assertThat(result.refreshTokenExpiredAt()).isNotNull();
        assertThat(result.isNewUser()).isTrue();
    }

    private Users user() {
        return Users.builder()
                .userId(1L)
                .identityId(UUID.randomUUID())
                .systemRole(SystemRole.USER)
                .oauthType(OauthType.GITHUB)
                .oauthProviderId("provider")
                .oauthNickname("nickname")
                .serviceNickname(null)
                .build();
    }
}

