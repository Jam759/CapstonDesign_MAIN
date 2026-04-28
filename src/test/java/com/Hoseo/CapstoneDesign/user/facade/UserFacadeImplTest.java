package com.Hoseo.CapstoneDesign.user.facade;

import com.Hoseo.CapstoneDesign.common.entity.CommonGroupDetail;
import com.Hoseo.CapstoneDesign.common.service.CommonGroupDetailService;
import com.Hoseo.CapstoneDesign.gamification.entity.LevelRule;
import com.Hoseo.CapstoneDesign.gamification.repository.LevelRuleRepository;
import com.Hoseo.CapstoneDesign.security.cache.dto.AuthenticatedUserCacheEntry;
import com.Hoseo.CapstoneDesign.security.cache.factory.AuthenticatedUserCacheFactory;
import com.Hoseo.CapstoneDesign.support.builder.UsersTestBuilder;
import com.Hoseo.CapstoneDesign.support.factory.UserProfileUpdateRequestFactory;
import com.Hoseo.CapstoneDesign.user.cache.service.UserResponseCacheService;
import com.Hoseo.CapstoneDesign.user.dto.request.UserProfileUpdateRequest;
import com.Hoseo.CapstoneDesign.user.dto.response.MyInfoResponse;
import com.Hoseo.CapstoneDesign.user.dto.response.UpdateUserInfoResponse;
import com.Hoseo.CapstoneDesign.user.entity.UserInfoUpdateHistory;
import com.Hoseo.CapstoneDesign.user.entity.UserMetaInformation;
import com.Hoseo.CapstoneDesign.user.entity.Users;
import com.Hoseo.CapstoneDesign.user.event.UserProfileChangedEvent;
import com.Hoseo.CapstoneDesign.user.facade.impl.UserFacadeImpl;
import com.Hoseo.CapstoneDesign.user.service.UserInfoUpdateHistoryService;
import com.Hoseo.CapstoneDesign.user.service.UserMetaInformationService;
import com.Hoseo.CapstoneDesign.user.service.UserService;
import com.Hoseo.CapstoneDesign.user.service.UserTechStackService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserFacadeImplTest {

    private static final Logger log = LoggerFactory.getLogger(UserFacadeImplTest.class);

    @Mock
    private UserService userService;

    @Mock
    private UserInfoUpdateHistoryService historyService;

    @Mock
    private UserMetaInformationService metaService;

    @Mock
    private LevelRuleRepository levelRuleRepository;

    @Mock
    private CommonGroupDetailService commonGroupDetailService;

    @Mock
    private UserTechStackService userTechStackService;

    @Mock
    private UserResponseCacheService userResponseCacheService;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @InjectMocks
    private UserFacadeImpl facade;

    @Test
    @DisplayName("User profile update use case returns response DTO")
    void updateUserProfileUseCaseSuccess() {
        Users user = UsersTestBuilder.defaultUser()
                .userId(1L)
                .serviceNickname("before-service-nick")
                .build()
                .updateOauthNickname("before-oauth");
        UserProfileUpdateRequest request = UserProfileUpdateRequestFactory.create(
                "after-service-nick",
                "Job",
                "Backend",
                List.of("Java", "React")
        );
        CommonGroupDetail goal = commonGroupDetail("Job");
        CommonGroupDetail position = commonGroupDetail("Backend");
        CommonGroupDetail javaStack = commonGroupDetail("Java");
        CommonGroupDetail reactStack = commonGroupDetail("React");
        UserInfoUpdateHistory savedHistory = UserInfoUpdateHistory.builder()
                .previousNickname("before-service-nick")
                .newNickname("after-service-nick")
                .updatedAt(LocalDateTime.of(2026, 3, 12, 15, 0))
                .build();

        when(commonGroupDetailService.getRequiredReferenceByGroupAndId(
                CommonGroupDetailService.USER_GOAL_GROUP_ID,
                "Job"
        )).thenReturn(goal);
        when(commonGroupDetailService.getRequiredReferenceByGroupAndId(
                CommonGroupDetailService.PROJECT_POSITION_GROUP_ID,
                "Backend"
        )).thenReturn(position);
        when(commonGroupDetailService.getRequiredReferencesByGroupAndIds(
                CommonGroupDetailService.PROJECT_TECH_STACK_GROUP_ID,
                request.techStacks()
        )).thenReturn(List.of(javaStack, reactStack));
        when(userService.getById(user.getUserId())).thenReturn(user);
        when(userService.updateUserProfile(user, "after-service-nick", goal, position, true))
                .thenAnswer(invocation -> {
                    user.updateServiceNickname(request.userServiceNickname());
                    user.updateUserGoal(goal);
                    user.updateUserMainPosition(position);
                    user.updateProfileComplete(true);
                    return user;
                });
        when(historyService.save(any())).thenReturn(savedHistory);

        UpdateUserInfoResponse response = facade.updateUserProfile(authenticated(user), request);

        assertThat(response.serviceNickname()).isEqualTo("after-service-nick");
        assertThat(response.goal()).isEqualTo("Job");
        assertThat(response.position()).isEqualTo("Backend");
        assertThat(response.techStacks()).containsExactly("Java", "React");
        assertThat(response.profileComplete()).isTrue();
        assertThat(response.updateDate()).isEqualTo(LocalDateTime.of(2026, 3, 12, 15, 0));

        ArgumentCaptor<UserInfoUpdateHistory> captor = ArgumentCaptor.forClass(UserInfoUpdateHistory.class);
        verify(historyService).save(captor.capture());
        verify(userTechStackService).replaceUserTechStacks(user, List.of(javaStack, reactStack));
        ArgumentCaptor<UserProfileChangedEvent> eventCaptor = ArgumentCaptor.forClass(UserProfileChangedEvent.class);
        verify(applicationEventPublisher).publishEvent(eventCaptor.capture());
        assertThat(eventCaptor.getValue().userId()).isEqualTo(user.getUserId());
        assertThat(eventCaptor.getValue().identityId()).isEqualTo(user.getIdentityId());
        assertThat(captor.getValue().getPreviousNickname()).isEqualTo("before-service-nick");
        assertThat(captor.getValue().getNewNickname()).isEqualTo("after-service-nick");
        log.info("[TEST] facade use-case orchestration validated");
    }

    @Test
    @DisplayName("getMyInfo returns cached response without loading meta")
    void getMyInfoReturnsCachedResponse() {
        Users user = UsersTestBuilder.defaultUser()
                .userId(1L)
                .serviceNickname("cached-user")
                .build();
        MyInfoResponse cached = new MyInfoResponse("cached-user", 3, 20L, 100L, 10, 220L);

        when(userResponseCacheService.findMyInfo(user.getUserId())).thenReturn(Optional.of(cached));

        MyInfoResponse response = facade.getMyInfo(authenticated(user));

        assertThat(response).isEqualTo(cached);
        verify(metaService, never()).getMetaInfo(user);
    }

    @Test
    @DisplayName("getMyInfo loads response and saves cache on miss")
    void getMyInfoLoadsAndCachesOnMiss() {
        Users user = UsersTestBuilder.defaultUser()
                .userId(1L)
                .serviceNickname("me")
                .build();
        LevelRule currentLevel = LevelRule.builder()
                .level(2)
                .requiredTotalExp(100L)
                .build();
        UserMetaInformation meta = UserMetaInformation.builder()
                .user(user)
                .totalExp(150L)
                .levelRule(currentLevel)
                .build();

        when(userResponseCacheService.findMyInfo(user.getUserId())).thenReturn(Optional.empty());
        when(metaService.getMetaInfo(user.getUserId())).thenReturn(meta);
        when(levelRuleRepository.findById(1)).thenReturn(Optional.of(LevelRule.builder()
                .level(1)
                .requiredTotalExp(0L)
                .build()));
        when(levelRuleRepository.findById(3)).thenReturn(Optional.of(LevelRule.builder()
                .level(3)
                .requiredTotalExp(300L)
                .build()));
        when(metaService.countUsersAbove(150L)).thenReturn(1L);
        when(metaService.countAll()).thenReturn(10L);

        MyInfoResponse response = facade.getMyInfo(authenticated(user));

        assertThat(response.nickname()).isEqualTo("me");
        assertThat(response.level()).isEqualTo(2);
        assertThat(response.xp()).isEqualTo(150L);
        assertThat(response.maxXp()).isEqualTo(300L);
        assertThat(response.topPercentage()).isEqualTo(20);
        verify(userResponseCacheService).saveMyInfo(user.getUserId(), response);
    }

    @Test
    @DisplayName("@Facade public method has @Transactional boundary")
    void facadeMethodMustBeTransactional() throws NoSuchMethodException {
        Method method = UserFacadeImpl.class.getMethod(
                "updateUserProfile",
                AuthenticatedUserCacheEntry.class,
                UserProfileUpdateRequest.class
        );

        assertThat(method.isAnnotationPresent(Transactional.class)).isTrue();
        log.info("[TEST] facade transaction boundary validated");
    }

    private CommonGroupDetail commonGroupDetail(String id) {
        return CommonGroupDetail.builder()
                .commonGroupDetailId(id)
                .build();
    }

    private AuthenticatedUserCacheEntry authenticated(Users user) {
        return AuthenticatedUserCacheFactory.fromUser(user);
    }
}
