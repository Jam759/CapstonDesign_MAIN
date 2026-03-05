package com.Hoseo.CapstoneDesign.security.service.impl;

import com.Hoseo.CapstoneDesign.security.entity.RefreshToken;
import com.Hoseo.CapstoneDesign.security.exception.RefreshTokenErrorCode;
import com.Hoseo.CapstoneDesign.security.exception.RefreshTokenException;
import com.Hoseo.CapstoneDesign.security.repository.RefreshTokenRepository;
import com.Hoseo.CapstoneDesign.security.util.JwtUtil;
import com.Hoseo.CapstoneDesign.user.entity.Users;
import com.Hoseo.CapstoneDesign.user.entity.enums.OauthType;
import com.Hoseo.CapstoneDesign.user.entity.enums.SystemRole;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceImplTest {

    @Mock
    private RefreshTokenRepository repository;

    @Mock
    private JwtUtil jwtUtil;

    @Captor
    private ArgumentCaptor<RefreshToken> refreshTokenCaptor;

    private RefreshTokenServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new RefreshTokenServiceImpl(repository, jwtUtil);
    }

    @DisplayName("rotate: 정상 토큰이면 old 토큰을 used 처리하고 새 refresh token을 저장한다")
    @Test
    void rotate_success() {
        Users user = users(1L);
        String oldRawToken = "old-refresh-token";
        String oldHash = DigestUtils.sha256Hex(oldRawToken);

        RefreshToken old = RefreshToken.builder()
                .id(UUID.randomUUID())
                .tokenHash(oldHash)
                .expiresAt(LocalDateTime.now().plusDays(1))
                .familyId(UUID.randomUUID())
                .user(user)
                .build();

        when(repository.findByTokenHash(oldHash)).thenReturn(Optional.of(old));
        when(jwtUtil.createRefreshToken(user)).thenReturn("new-refresh-token");
        when(jwtUtil.getExpirationFromRefreshToken("new-refresh-token"))
                .thenReturn(toDate(LocalDateTime.now().plusDays(7)));

        String result = service.rotate(user, oldRawToken);

        assertThat(result).isEqualTo("new-refresh-token");
        assertThat(old.isUsed()).isTrue();
        assertThat(old.getReplacedByTokenId()).isNotNull();

        InOrder inOrder = inOrder(repository);
        inOrder.verify(repository).save(old);
        inOrder.verify(repository).save(refreshTokenCaptor.capture());

        RefreshToken next = refreshTokenCaptor.getValue();
        assertThat(next.getFamilyId()).isEqualTo(old.getFamilyId());
        assertThat(next.getUser()).isEqualTo(user);
        assertThat(next.getTokenHash()).isEqualTo(DigestUtils.sha256Hex("new-refresh-token"));
    }

    @DisplayName("rotate: 이미 사용된 토큰이면 user의 active refresh token 전체를 revoke하고 예외를 던진다")
    @Test
    void rotate_reuseDetected() {
        Users user = users(1L);
        String oldRawToken = "old-refresh-token";
        String oldHash = DigestUtils.sha256Hex(oldRawToken);
        RefreshToken old = RefreshToken.builder()
                .id(UUID.randomUUID())
                .tokenHash(oldHash)
                .expiresAt(LocalDateTime.now().plusDays(1))
                .familyId(UUID.randomUUID())
                .usedAt(LocalDateTime.now().minusMinutes(1))
                .user(user)
                .build();

        when(repository.findByTokenHash(oldHash)).thenReturn(Optional.of(old));

        assertThatThrownBy(() -> service.rotate(user, oldRawToken))
                .isInstanceOf(RefreshTokenException.class)
                .extracting("errorCode")
                .isEqualTo(RefreshTokenErrorCode.REFRESH_TOKEN_REUSE_DETECTED);

        verify(repository).revokeAllActiveByUser(eq(user), any(LocalDateTime.class));
        verify(repository, never()).save(any(RefreshToken.class));
    }

    @DisplayName("revokeAndSoftDeleteByFamily: refresh token으로 family를 찾고 revoke+soft delete 쿼리를 수행한다")
    @Test
    void revokeAndSoftDeleteByFamily_success() {
        Users user = users(1L);
        String rawRefreshToken = "refresh-token";
        String refreshTokenHash = DigestUtils.sha256Hex(rawRefreshToken);
        UUID familyId = UUID.randomUUID();

        RefreshToken token = RefreshToken.builder()
                .id(UUID.randomUUID())
                .tokenHash(refreshTokenHash)
                .expiresAt(LocalDateTime.now().plusDays(1))
                .familyId(familyId)
                .user(user)
                .build();

        when(repository.findByTokenHash(refreshTokenHash)).thenReturn(Optional.of(token));

        service.revokeAndSoftDeleteByFamily(user, rawRefreshToken);

        verify(repository).revokeAndSoftDeleteByFamilyId(eq(user), eq(familyId), any(LocalDateTime.class));
    }

    @DisplayName("revokeAndSoftDeleteByFamily: refresh token 소유자가 다르면 mismatch 예외를 던진다")
    @Test
    void revokeAndSoftDeleteByFamily_userMismatch() {
        Users owner = users(1L);
        Users requester = users(2L);
        String rawRefreshToken = "refresh-token";
        String refreshTokenHash = DigestUtils.sha256Hex(rawRefreshToken);

        RefreshToken token = RefreshToken.builder()
                .id(UUID.randomUUID())
                .tokenHash(refreshTokenHash)
                .expiresAt(LocalDateTime.now().plusDays(1))
                .familyId(UUID.randomUUID())
                .user(owner)
                .build();

        when(repository.findByTokenHash(refreshTokenHash)).thenReturn(Optional.of(token));

        assertThatThrownBy(() -> service.revokeAndSoftDeleteByFamily(requester, rawRefreshToken))
                .isInstanceOf(RefreshTokenException.class)
                .extracting("errorCode")
                .isEqualTo(RefreshTokenErrorCode.REFRESH_TOKEN_USER_MISMATCH);

        verify(repository, never()).revokeAndSoftDeleteByFamilyId(any(), any(), any());
        verifyNoMoreInteractions(repository);
    }

    private Users users(Long userId) {
        return Users.builder()
                .userId(userId)
                .identityId(UUID.randomUUID())
                .systemRole(SystemRole.USER)
                .oauthType(OauthType.GITHUB)
                .oauthProviderId("provider-id-" + userId)
                .oauthNickname("oauth-nickname-" + userId)
                .build();
    }

    private Date toDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }
}

