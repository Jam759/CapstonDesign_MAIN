package com.Hoseo.CapstoneDesign.security.cache.service;

import com.Hoseo.CapstoneDesign.global.cache.CacheOperationException;
import com.Hoseo.CapstoneDesign.security.cache.dto.AccessTokenBlackListCacheEntry;
import com.Hoseo.CapstoneDesign.security.cache.repository.AccessTokenBlackListCacheRepository;
import com.Hoseo.CapstoneDesign.security.dto.cache.AccessTokenBlackListCache;
import com.Hoseo.CapstoneDesign.security.exception.AccessTokenBlackListErrorCode;
import com.Hoseo.CapstoneDesign.security.exception.AccessTokenBlackListException;
import com.Hoseo.CapstoneDesign.security.repository.AccessTokenBlackListRepository;
import com.Hoseo.CapstoneDesign.user.entity.Users;
import com.Hoseo.CapstoneDesign.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessResourceFailureException;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccessTokenBlackListCacheServiceTest {

    @Mock
    private AccessTokenBlackListCacheRepository cacheRepository;

    @Mock
    private AccessTokenBlackListRepository dbRepository;

    @Mock
    private UserService userService;

    private AccessTokenBlackListCacheService service;

    @BeforeEach
    void setUp() {
        service = new AccessTokenBlackListCacheService(cacheRepository, dbRepository, userService);
    }

    @Test
    @DisplayName("returns true without DB lookup when Redis blacklist has the JTI")
    void existsReturnsTrueFromRedisHit() {
        UUID jti = UUID.randomUUID();
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(10);

        when(cacheRepository.existsBlacklisted(jti)).thenReturn(true);

        boolean result = service.exists(jti, expiresAt);

        assertThat(result).isTrue();
        verify(dbRepository, never()).existsByJtiAndExpiresAtAfter(eq(jti), any());
    }

    @Test
    @DisplayName("falls back to DB when Redis lookup fails")
    void existsFallsBackToDbWhenRedisFails() {
        UUID jti = UUID.randomUUID();
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(10);

        when(cacheRepository.existsBlacklisted(jti))
                .thenThrow(new DataAccessResourceFailureException("redis down"));
        when(dbRepository.existsByJtiAndExpiresAtAfter(eq(jti), any())).thenReturn(true);

        boolean result = service.exists(jti, expiresAt);

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("falls back to DB when Redis cache value handling fails")
    void existsFallsBackToDbWhenCacheOperationFails() {
        UUID jti = UUID.randomUUID();
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(10);

        when(cacheRepository.existsBlacklisted(jti))
                .thenThrow(new CacheOperationException("cache deserialize failed", new RuntimeException()));
        when(dbRepository.existsByJtiAndExpiresAtAfter(eq(jti), any())).thenReturn(false);

        boolean result = service.exists(jti, expiresAt);

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("ignores Redis cache value handling failure after DB save")
    void saveIgnoresCacheOperationFailureAfterDbSave() {
        UUID jti = UUID.randomUUID();
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(10);
        Users user = Users.builder()
                .userId(1L)
                .build();
        AccessTokenBlackListCache cache = AccessTokenBlackListCache.builder()
                .jti(jti)
                .encryptedToken("access-token")
                .expiryDate(expiresAt)
                .logoutTime(LocalDateTime.now())
                .userId(user.getUserId())
                .build();
        when(userService.getReferenceById(user.getUserId())).thenReturn(user);

        doThrow(new CacheOperationException("cache delete failed", new RuntimeException()))
                .when(cacheRepository)
                .deleteNegative(jti);

        service.save("access-token", cache);

        verify(dbRepository).save(any());
    }

    @Test
    @DisplayName("fails closed when Redis and DB blacklist checks both fail")
    void existsFailsClosedWhenRedisAndDbFail() {
        UUID jti = UUID.randomUUID();
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(10);

        when(cacheRepository.existsBlacklisted(jti))
                .thenThrow(new DataAccessResourceFailureException("redis down"));
        when(dbRepository.existsByJtiAndExpiresAtAfter(eq(jti), any()))
                .thenThrow(new DataAccessResourceFailureException("db down"));

        assertThatThrownBy(() -> service.exists(jti, expiresAt))
                .isInstanceOf(AccessTokenBlackListException.class)
                .extracting("errorCode")
                .isEqualTo(AccessTokenBlackListErrorCode.TOKEN_BLACKLIST_CHECK_FAILED);
    }

    @Test
    @DisplayName("stores blacklist in DB first and then Redis")
    void savePersistsDbAndRedis() {
        UUID jti = UUID.randomUUID();
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(10);
        Users user = Users.builder()
                .userId(1L)
                .build();
        AccessTokenBlackListCache cache = AccessTokenBlackListCache.builder()
                .jti(jti)
                .encryptedToken("access-token")
                .expiryDate(expiresAt)
                .logoutTime(LocalDateTime.now())
                .userId(user.getUserId())
                .build();
        when(userService.getReferenceById(user.getUserId())).thenReturn(user);

        service.save("access-token", cache);

        verify(dbRepository).save(any());
        verify(cacheRepository).deleteNegative(jti);
        verify(cacheRepository).saveBlacklisted(any(AccessTokenBlackListCacheEntry.class), any());
    }
}
