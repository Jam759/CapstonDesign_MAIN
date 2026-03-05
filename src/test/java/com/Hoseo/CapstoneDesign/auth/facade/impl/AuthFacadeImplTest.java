package com.Hoseo.CapstoneDesign.auth.facade.impl;

import com.Hoseo.CapstoneDesign.security.exception.JwtUtilErrorCode;
import com.Hoseo.CapstoneDesign.security.exception.JwtUtilException;
import com.Hoseo.CapstoneDesign.security.service.AccessTokenBlackListService;
import com.Hoseo.CapstoneDesign.security.service.RefreshTokenService;
import com.Hoseo.CapstoneDesign.security.util.JwtUtil;
import com.Hoseo.CapstoneDesign.user.entity.Users;
import com.Hoseo.CapstoneDesign.user.entity.enums.OauthType;
import com.Hoseo.CapstoneDesign.user.entity.enums.SystemRole;
import com.Hoseo.CapstoneDesign.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class AuthFacadeImplTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private AccessTokenBlackListService accessTokenBlackListService;

    private AuthFacadeImpl authFacade;

    @BeforeEach
    void setUp() {
        authFacade = new AuthFacadeImpl(userService, jwtUtil, refreshTokenService, accessTokenBlackListService);
    }

    @DisplayName("logout: access token이 비어있으면 TOKEN_IS_NULL 예외를 던진다")
    @Test
    void logout_fail_whenAccessTokenIsNull() {
        Users user = users(1L);

        assertThatThrownBy(() -> authFacade.logout(user, null, "refresh-token"))
                .isInstanceOf(JwtUtilException.class)
                .extracting("errorCode")
                .isEqualTo(JwtUtilErrorCode.TOKEN_IS_NULL);

        verifyNoInteractions(accessTokenBlackListService, refreshTokenService);
    }

    @DisplayName("logout: access token 블랙리스트 등록 후 refresh family revoke/soft delete를 수행한다")
    @Test
    void logout_success() {
        Users user = users(1L);
        String accessToken = "access-token";
        String refreshToken = "refresh-token";

        authFacade.logout(user, accessToken, refreshToken);

        verify(accessTokenBlackListService).saveBlackList(accessToken, user);
        verify(refreshTokenService).revokeAndSoftDeleteByFamily(user, refreshToken);
    }

    private Users users(Long userId) {
        return Users.builder()
                .userId(userId)
                .identityId(UUID.randomUUID())
                .systemRole(SystemRole.USER)
                .oauthType(OauthType.GITHUB)
                .oauthProviderId("provider-id")
                .oauthNickname("oauth-nickname")
                .build();
    }
}

