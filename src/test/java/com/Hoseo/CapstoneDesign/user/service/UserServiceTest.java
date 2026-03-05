package com.Hoseo.CapstoneDesign.user.service;

import com.Hoseo.CapstoneDesign.user.entity.Users;
import com.Hoseo.CapstoneDesign.user.entity.enums.OauthType;
import com.Hoseo.CapstoneDesign.user.entity.enums.SystemRole;
import com.Hoseo.CapstoneDesign.user.exception.CustomUserException;
import com.Hoseo.CapstoneDesign.user.exception.UserErrorCode;
import com.Hoseo.CapstoneDesign.user.repository.UsersRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UsersRepository repository;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(repository);
    }

    @DisplayName("getByIdentityId: 사용자를 찾으면 반환한다")
    @Test
    void getByIdentityId_success() {
        UUID identityId = UUID.randomUUID();
        Users user = users(1L, identityId, "old");
        when(repository.findByIdentityId(identityId)).thenReturn(Optional.of(user));

        Users result = userService.getByIdentityId(identityId);

        assertThat(result).isEqualTo(user);
    }

    @DisplayName("getByIdentityId: 사용자가 없으면 USER_NOT_FOUND_ERROR 예외를 던진다")
    @Test
    void getByIdentityId_notFound() {
        UUID identityId = UUID.randomUUID();
        when(repository.findByIdentityId(identityId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getByIdentityId(identityId))
                .isInstanceOf(CustomUserException.class)
                .extracting("errorCode")
                .isEqualTo(UserErrorCode.USER_NOT_FOUND_ERROR);
    }

    @DisplayName("getOrCreateOauthUser: 기존 유저가 있으면 닉네임 업데이트 후 저장한다")
    @Test
    void getOrCreateOauthUser_updateExisting() {
        Users existing = users(1L, UUID.randomUUID(), "before");
        when(repository.findByOauthTypeAndOauthProviderId(OauthType.GITHUB, "provider-id"))
                .thenReturn(Optional.of(existing));
        when(repository.save(existing)).thenReturn(existing);

        Users result = userService.getOrCreateOauthUser(OauthType.GITHUB, "provider-id", "after");

        assertThat(result).isEqualTo(existing);
        assertThat(existing.getOauthNickname()).isEqualTo("after");
        verify(repository).save(existing);
    }

    @DisplayName("getOrCreateOauthUser: 유저가 없으면 신규 생성 후 저장한다")
    @Test
    void getOrCreateOauthUser_createNew() {
        when(repository.findByOauthTypeAndOauthProviderId(OauthType.GITHUB, "provider-id"))
                .thenReturn(Optional.empty());
        when(repository.save(any(Users.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Users result = userService.getOrCreateOauthUser(OauthType.GITHUB, "provider-id", "new-nickname");

        assertThat(result.getUserId()).isNull();
        assertThat(result.getIdentityId()).isNotNull();
        assertThat(result.getOauthType()).isEqualTo(OauthType.GITHUB);
        assertThat(result.getOauthProviderId()).isEqualTo("provider-id");
        assertThat(result.getOauthNickname()).isEqualTo("new-nickname");
        assertThat(result.getSystemRole()).isEqualTo(SystemRole.USER);
        verify(repository).save(any(Users.class));
    }

    private Users users(Long userId, UUID identityId, String oauthNickname) {
        return Users.builder()
                .userId(userId)
                .identityId(identityId)
                .systemRole(SystemRole.USER)
                .oauthType(OauthType.GITHUB)
                .oauthProviderId("provider-id")
                .oauthNickname(oauthNickname)
                .build();
    }
}

