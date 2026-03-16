package com.Hoseo.CapstoneDesign.user.service;

import com.Hoseo.CapstoneDesign.support.builder.UsersTestBuilder;
import com.Hoseo.CapstoneDesign.support.mother.UsersMother;
import com.Hoseo.CapstoneDesign.user.entity.Users;
import com.Hoseo.CapstoneDesign.user.entity.enums.OauthType;
import com.Hoseo.CapstoneDesign.user.entity.enums.SystemRole;
import com.Hoseo.CapstoneDesign.user.exception.CustomUserException;
import com.Hoseo.CapstoneDesign.user.exception.UserErrorCode;
import com.Hoseo.CapstoneDesign.user.repository.UsersRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private static final Logger log = LoggerFactory.getLogger(UserServiceTest.class);

    @Mock
    private UsersRepository usersRepository;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("identityId로 사용자를 조회한다")
    void getByIdentityIdSuccess() {
        UUID identityId = UUID.randomUUID();
        Users user = UsersMother.defaultUser();

        when(usersRepository.findByIdentityId(identityId)).thenReturn(Optional.of(user));

        Users result = userService.getByIdentityId(identityId);

        assertThat(result).isEqualTo(user);
        log.info("[TEST] getByIdentityId success validated");
    }

    @Test
    @DisplayName("identityId로 조회 실패 시 USER_NOT_FOUND_ERROR를 던진다")
    void getByIdentityIdFail() {
        UUID identityId = UUID.randomUUID();
        when(usersRepository.findByIdentityId(identityId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getByIdentityId(identityId))
                .isInstanceOf(CustomUserException.class)
                .extracting("errorCode")
                .isEqualTo(UserErrorCode.USER_NOT_FOUND_ERROR);

        log.info("[TEST] getByIdentityId fail exception validated");
    }

    @Test
    @DisplayName("기존 OAuth 사용자는 nickname 업데이트 후 저장한다")
    void getOrCreateOauthUserExistingUser() {
        Users existing = UsersMother.withOauth("provider-1", "old-oauth");
        when(usersRepository.findByOauthTypeAndOauthProviderId(OauthType.GITHUB, "provider-1"))
                .thenReturn(Optional.of(existing));
        when(usersRepository.save(existing)).thenReturn(existing);

        Users result = userService.getOrCreateOauthUser(OauthType.GITHUB, "provider-1", "new-oauth");

        assertThat(result.getOauthNickname()).isEqualTo("new-oauth");
        verify(usersRepository).save(existing);
        log.info("[TEST] getOrCreateOauthUser existing-flow validated");
    }

    @ParameterizedTest
    @MethodSource("newOauthUserSamples")
    @DisplayName("신규 OAuth 사용자는 생성 후 저장된다")
    void getOrCreateOauthUserNewUser(String providerId, String oauthNickname) {
        when(usersRepository.findByOauthTypeAndOauthProviderId(OauthType.GITHUB, providerId))
                .thenReturn(Optional.empty());
        when(usersRepository.save(any(Users.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Users result = userService.getOrCreateOauthUser(OauthType.GITHUB, providerId, oauthNickname);

        assertThat(result.getOauthType()).isEqualTo(OauthType.GITHUB);
        assertThat(result.getOauthProviderId()).isEqualTo(providerId);
        assertThat(result.getOauthNickname()).isEqualTo(oauthNickname);
        assertThat(result.getSystemRole()).isEqualTo(SystemRole.USER);
        assertThat(result.getIdentityId()).isNotNull();
        log.info("[TEST] getOrCreateOauthUser new-user sample validated: providerId={}", providerId);
    }

    @ParameterizedTest
    @MethodSource("nicknameBoundarySamples")
    @DisplayName("서비스 닉네임은 전달값으로 변경되어 저장된다")
    void updateServiceUserNameBoundary(String nickname) {
        Users user = UsersTestBuilder.defaultUser().serviceNickname("before").build();
        when(usersRepository.save(user)).thenReturn(user);

        Users result = userService.updateServiceUserName(user, nickname);

        assertThat(result.getServiceNickname()).isEqualTo(nickname);
        verify(usersRepository).save(user);
        log.info("[TEST] nickname boundary validated: length={}", nickname.length());
    }

    private static Stream<Arguments> newOauthUserSamples() {
        return Stream.of(
                Arguments.of("provider-a", "oauth-a"),
                Arguments.of("provider-b", "oauth-b"),
                Arguments.of("provider-c", "oauth-c")
        );
    }

    private static Stream<String> nicknameBoundarySamples() {
        return Stream.of(
                "a",
                "nick-name-100-" + "x".repeat(86),
                "",
                "한글닉네임"
        );
    }
}
