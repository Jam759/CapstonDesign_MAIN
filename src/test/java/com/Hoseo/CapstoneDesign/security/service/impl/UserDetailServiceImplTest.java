package com.Hoseo.CapstoneDesign.security.service.impl;

import com.Hoseo.CapstoneDesign.security.entity.UserDetailImpl;
import com.Hoseo.CapstoneDesign.security.exception.JwtUtilErrorCode;
import com.Hoseo.CapstoneDesign.security.exception.JwtUtilException;
import com.Hoseo.CapstoneDesign.user.entity.Users;
import com.Hoseo.CapstoneDesign.user.entity.enums.OauthType;
import com.Hoseo.CapstoneDesign.user.entity.enums.SystemRole;
import com.Hoseo.CapstoneDesign.user.exception.CustomUserException;
import com.Hoseo.CapstoneDesign.user.exception.UserErrorCode;
import com.Hoseo.CapstoneDesign.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserDetailServiceImplTest {

    @Mock
    private UserService userService;

    private UserDetailServiceImpl userDetailService;

    @BeforeEach
    void setUp() {
        userDetailService = new UserDetailServiceImpl(userService);
    }

    @DisplayName("loadUserByUsername: identityId 문자열로 사용자를 조회해 UserDetails를 반환한다")
    @Test
    void loadUserByUsername_success() {
        UUID identityId = UUID.randomUUID();
        Users user = Users.builder()
                .userId(1L)
                .identityId(identityId)
                .oauthType(OauthType.GITHUB)
                .oauthProviderId("provider")
                .oauthNickname("nickname")
                .systemRole(SystemRole.USER)
                .build();
        when(userService.getByIdentityId(identityId)).thenReturn(user);

        UserDetails result = userDetailService.loadUserByUsername(identityId.toString());

        assertThat(result).isInstanceOf(UserDetailImpl.class);
        assertThat(result.getUsername()).isEqualTo(identityId.toString());
    }

    @DisplayName("loadUserByUsername: 사용자 조회 실패면 TOKEN_ILLEGAL_ARGUMENT 예외로 변환한다")
    @Test
    void loadUserByUsername_notFound() {
        UUID identityId = UUID.randomUUID();
        when(userService.getByIdentityId(identityId))
                .thenThrow(new CustomUserException(UserErrorCode.USER_NOT_FOUND_ERROR));

        assertThatThrownBy(() -> userDetailService.loadUserByUsername(identityId.toString()))
                .isInstanceOf(JwtUtilException.class)
                .extracting("errorCode")
                .isEqualTo(JwtUtilErrorCode.TOKEN_ILLEGAL_ARGUMENT);
    }
}

