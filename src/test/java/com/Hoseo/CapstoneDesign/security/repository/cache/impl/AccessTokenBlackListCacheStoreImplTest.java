package com.Hoseo.CapstoneDesign.security.repository.cache.impl;

import com.Hoseo.CapstoneDesign.security.dto.cache.AccessTokenBlackListCache;
import com.Hoseo.CapstoneDesign.security.util.JwtUtil;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccessTokenBlackListCacheStoreImplTest {

    @Mock
    private JwtUtil jwtUtil;

    private Cache<String, AccessTokenBlackListCache> cache;
    private AccessTokenBlackListCacheStoreImpl store;

    @BeforeEach
    void setUp() {
        cache = Caffeine.newBuilder().maximumSize(1000).build();
        store = new AccessTokenBlackListCacheStoreImpl(jwtUtil, cache);
    }

    @DisplayName("setAccessTokenBlackListCache: ttl을 계산해서 캐시에 저장한다")
    @Test
    void setAccessTokenBlackListCache_success() {
        UUID jti = UUID.randomUUID();
        String token = "access-token";
        AccessTokenBlackListCache input = AccessTokenBlackListCache.builder()
                .jti(jti)
                .encryptedToken(token)
                .logoutTime(LocalDateTime.now())
                .build();
        when(jwtUtil.getExpirationFromAccessToken(token))
                .thenReturn(new Date(System.currentTimeMillis() + 60_000));

        store.setAccessTokenBlackListCache(input);

        AccessTokenBlackListCache cached = store.getByJTI(jti);
        assertThat(cached).isNotNull();
        assertThat(cached.getTtlMillis()).isNotNull();
        assertThat(cached.getTtlMillis()).isGreaterThanOrEqualTo(0);
    }

    @DisplayName("isExist/removeBlackList: 저장 후 존재하고, 삭제 후 없어야 한다")
    @Test
    void existsAndRemove_success() {
        UUID jti = UUID.randomUUID();
        String token = "access-token";
        AccessTokenBlackListCache input = AccessTokenBlackListCache.builder()
                .jti(jti)
                .encryptedToken(token)
                .logoutTime(LocalDateTime.now())
                .build();
        when(jwtUtil.getExpirationFromAccessToken(token))
                .thenReturn(new Date(System.currentTimeMillis() + 60_000));

        store.setAccessTokenBlackListCache(input);
        assertThat(store.isExist(jti)).isTrue();

        store.removeBlackList(jti);
        assertThat(store.isExist(jti)).isFalse();
    }
}

