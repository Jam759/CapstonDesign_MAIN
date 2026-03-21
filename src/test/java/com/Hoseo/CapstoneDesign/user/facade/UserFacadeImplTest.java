package com.Hoseo.CapstoneDesign.user.facade;

import com.Hoseo.CapstoneDesign.gamification.entity.UserBadge;
import com.Hoseo.CapstoneDesign.gamification.service.UserBadgeService;
import com.Hoseo.CapstoneDesign.support.factory.UserProfileUpdateRequestFactory;
import com.Hoseo.CapstoneDesign.support.fixture.UserBadgeFixture;
import com.Hoseo.CapstoneDesign.support.mother.UsersMother;
import com.Hoseo.CapstoneDesign.user.dto.request.UserProfileUpdateRequest;
import com.Hoseo.CapstoneDesign.user.dto.response.UpdateUserInfoResponse;
import com.Hoseo.CapstoneDesign.user.entity.UserInfoUpdateHistory;
import com.Hoseo.CapstoneDesign.user.entity.Users;
import com.Hoseo.CapstoneDesign.user.facade.impl.UserFacadeImpl;
import com.Hoseo.CapstoneDesign.user.service.UserInfoUpdateHistoryService;
import com.Hoseo.CapstoneDesign.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserFacadeImplTest {

    private static final Logger log = LoggerFactory.getLogger(UserFacadeImplTest.class);

    @Mock
    private UserService userService;

    @Mock
    private UserBadgeService userBadgeService;

    @Mock
    private UserInfoUpdateHistoryService historyService;

    @InjectMocks
    private UserFacadeImpl facade;

    @Test
    @DisplayName("유저 정보 수정 유스케이스를 조합해 응답 DTO를 반환한다")
    void updateUserProfileUseCaseSuccess() {
        Users user = UsersMother.withNickname("before-service-nick").updateOauthNickname("before-oauth");
        UserProfileUpdateRequest request = UserProfileUpdateRequestFactory.create("after-service-nick", Set.of(11L, 12L));
        Set<UserBadge> badges = UserBadgeFixture.equippedBadgeSet(11L, 12L);
        UserInfoUpdateHistory savedHistory = UserInfoUpdateHistory.builder()
                .previousNickname("before-service-nick")
                .newNickname("after-service-nick")
                .updatedAt(LocalDateTime.of(2026, 3, 12, 15, 0))
                .build();

        when(userService.updateServiceUserName(user, request.userServiceNickname()))
                .thenAnswer(invocation -> {
                    user.updateServiceNickname(request.userServiceNickname());
                    return user;
                });
        when(userBadgeService.updateUserBadgeEquip(user, request.equippedBadges())).thenReturn(badges);
        when(historyService.save(any())).thenReturn(savedHistory);

        UpdateUserInfoResponse response = facade.updateUserProfile(user, request);

        assertThat(response.serviceNickname()).isEqualTo("after-service-nick");
        assertThat(response.equippedBadges()).containsExactlyInAnyOrder(11L, 12L);
        assertThat(response.updateDate()).isEqualTo(LocalDateTime.of(2026, 3, 12, 15, 0));

        ArgumentCaptor<UserInfoUpdateHistory> captor = ArgumentCaptor.forClass(UserInfoUpdateHistory.class);
        verify(historyService).save(captor.capture());
        assertThat(captor.getValue().getPreviousNickname()).isEqualTo("before-service-nick");
        assertThat(captor.getValue().getNewNickname()).isEqualTo("after-service-nick");
        log.info("[TEST] facade use-case orchestration validated");
    }

    @Test
    @DisplayName("@Facade public 메서드는 @Transactional 경계를 가진다")
    void facadeMethodMustBeTransactional() throws NoSuchMethodException {
        Method method = UserFacadeImpl.class.getMethod(
                "updateUserProfile",
                Users.class,
                UserProfileUpdateRequest.class
        );

        assertThat(method.isAnnotationPresent(Transactional.class)).isTrue();
        log.info("[TEST] facade transaction boundary validated");
    }
}
