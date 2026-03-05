package com.Hoseo.CapstoneDesign.security.service.impl;

import com.Hoseo.CapstoneDesign.security.dto.cache.AccessTokenBlackListCache;
import com.Hoseo.CapstoneDesign.security.repository.cache.AccessTokenBlackListCacheStore;
import com.Hoseo.CapstoneDesign.security.util.JwtUtil;
import com.Hoseo.CapstoneDesign.user.entity.Users;
import com.Hoseo.CapstoneDesign.user.entity.enums.OauthType;
import com.Hoseo.CapstoneDesign.user.entity.enums.SystemRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccessTokenBlackListServiceImplTest {

    @Mock
    private AccessTokenBlackListCacheStore cacheStore;

    @Mock
    private JwtUtil jwtUtil;

    private AccessTokenBlackListServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new AccessTokenBlackListServiceImpl(cacheStore, jwtUtil);
    }

    @DisplayName("getBlackList: access token의 jti로 캐시 조회를 수행한다")
    @Test
    void getBlackList_success() {
        String accessToken = "access-token";
        UUID jti = UUID.randomUUID();
        AccessTokenBlackListCache cached = AccessTokenBlackListCache.builder()
                .jti(jti)
                .encryptedToken(accessToken)
                .logoutTime(LocalDateTime.now())
                .build();
        when(jwtUtil.getJtiFromAccessToken(accessToken)).thenReturn(jti);
        when(cacheStore.getByJTI(jti)).thenReturn(cached);

        AccessTokenBlackListCache result = service.getBlackList(accessToken);

        assertThat(result).isEqualTo(cached);
    }

    @DisplayName("isExistByToken: jti 존재 여부를 반환한다")
    @Test
    void isExistByToken_success() {
        String accessToken = "access-token";
        UUID jti = UUID.randomUUID();
        when(jwtUtil.getJtiFromAccessToken(accessToken)).thenReturn(jti);
        when(cacheStore.isExist(jti)).thenReturn(true);

        boolean result = service.isExistByToken(accessToken);

        assertThat(result).isTrue();
    }

    @DisplayName("saveBlackList: access token과 user 정보로 블랙리스트 캐시를 저장한다")
    @Test
    void saveBlackList_success() {
        String accessToken = "access-token";
        UUID jti = UUID.randomUUID();
        Users user = Users.builder()
                .userId(1L)
                .identityId(UUID.randomUUID())
                .oauthType(OauthType.GITHUB)
                .oauthProviderId("provider")
                .oauthNickname("nickname")
                .systemRole(SystemRole.USER)
                .build();
        when(jwtUtil.getJtiFromAccessToken(accessToken)).thenReturn(jti);
        when(jwtUtil.getExpirationFromAccessToken(accessToken))
                .thenReturn(java.sql.Timestamp.valueOf(LocalDateTime.now().plusMinutes(10)));

        service.saveBlackList(accessToken, user);

        verify(cacheStore).setAccessTokenBlackListCache(org.mockito.ArgumentMatchers.any(AccessTokenBlackListCache.class));
    }
}

