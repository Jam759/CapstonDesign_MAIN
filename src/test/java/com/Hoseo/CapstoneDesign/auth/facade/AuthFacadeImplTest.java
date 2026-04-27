package com.Hoseo.CapstoneDesign.auth.facade;

import com.Hoseo.CapstoneDesign.auth.dto.application.TokenPair;
import com.Hoseo.CapstoneDesign.auth.facade.impl.AuthFacadeImpl;
import com.Hoseo.CapstoneDesign.github.service.GitHubAppInstallationService;
import com.Hoseo.CapstoneDesign.security.service.AccessTokenBlackListService;
import com.Hoseo.CapstoneDesign.security.service.RefreshTokenService;
import com.Hoseo.CapstoneDesign.security.util.JwtUtil;
import com.Hoseo.CapstoneDesign.support.builder.UsersTestBuilder;
import com.Hoseo.CapstoneDesign.user.entity.Users;
import com.Hoseo.CapstoneDesign.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthFacadeImplTest {

    private static final Logger log = LoggerFactory.getLogger(AuthFacadeImplTest.class);

    @Mock
    private UserService userService;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private AccessTokenBlackListService accessTokenBlackListService;

    @Mock
    private GitHubAppInstallationService gitHubAppInstallationService;

    @InjectMocks
    private AuthFacadeImpl authFacade;

    @Test
    @DisplayName("Access token reissue returns profile setup and github installation status")
    void accessTokenReissueReturnsStatusFlags() {
        UUID identityId = UUID.randomUUID();
        Users user = UsersTestBuilder.defaultUser()
                .identityId(identityId)
                .build();

        when(jwtUtil.getSubjectFromRefreshToken("refresh-token")).thenReturn(identityId);
        when(userService.getByIdentityId(identityId)).thenReturn(user);
        when(refreshTokenService.rotate(user, "refresh-token")).thenReturn("rotated-refresh-token");
        when(jwtUtil.createAccessToken(user)).thenReturn("new-access-token");
        when(jwtUtil.getExpirationFromAccessToken("new-access-token")).thenReturn(new Date());
        when(jwtUtil.getExpirationFromRefreshToken("rotated-refresh-token")).thenReturn(new Date());
        when(gitHubAppInstallationService.isInstalledByUser(user)).thenReturn(true);

        TokenPair tokenPair = authFacade.accessTokenReissue("refresh-token");

        assertThat(tokenPair.needsProfileSetup()).isTrue();
        assertThat(tokenPair.githubInstalled()).isTrue();
        log.info("[TEST] auth facade returns status flags from user and github installation link");
    }
}
