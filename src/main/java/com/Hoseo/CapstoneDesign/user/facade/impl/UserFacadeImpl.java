package com.Hoseo.CapstoneDesign.user.facade.impl;

import com.Hoseo.CapstoneDesign.common.entity.CommonGroupDetail;
import com.Hoseo.CapstoneDesign.common.service.CommonGroupDetailService;
import com.Hoseo.CapstoneDesign.global.annotation.Facade;
import com.Hoseo.CapstoneDesign.security.cache.dto.AuthenticatedUserCacheEntry;
import com.Hoseo.CapstoneDesign.user.cache.service.UserResponseCacheService;
import com.Hoseo.CapstoneDesign.user.dto.request.UserProfileUpdateRequest;
import com.Hoseo.CapstoneDesign.user.dto.response.MyInfoResponse;
import com.Hoseo.CapstoneDesign.user.dto.response.UpdateUserInfoResponse;
import com.Hoseo.CapstoneDesign.user.entity.UserInfoUpdateHistory;
import com.Hoseo.CapstoneDesign.user.entity.UserMetaInformation;
import com.Hoseo.CapstoneDesign.user.entity.UserTechStack;
import com.Hoseo.CapstoneDesign.user.entity.Users;
import com.Hoseo.CapstoneDesign.user.event.UserProfileChangedEvent;
import com.Hoseo.CapstoneDesign.user.facade.UserFacade;
import com.Hoseo.CapstoneDesign.user.factory.UserDtoFactory;
import com.Hoseo.CapstoneDesign.user.factory.UserEntityFactory;
import com.Hoseo.CapstoneDesign.user.service.UserInfoUpdateHistoryService;
import com.Hoseo.CapstoneDesign.user.service.UserMetaInformationService;
import com.Hoseo.CapstoneDesign.user.service.UserService;
import com.Hoseo.CapstoneDesign.user.service.UserTechStackService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Facade
@RequiredArgsConstructor
public class UserFacadeImpl implements UserFacade {

    private final UserService userService;
    private final UserInfoUpdateHistoryService userInfoUpdateHistoryService;
    private final UserMetaInformationService metaService;
    private final CommonGroupDetailService commonGroupDetailService;
    private final UserTechStackService userTechStackService;
    private final UserResponseCacheService userResponseCacheService;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    @Transactional(readOnly = false)
    public UpdateUserInfoResponse updateUserProfile(AuthenticatedUserCacheEntry authenticatedUser, UserProfileUpdateRequest request) {
        Users user = userService.getById(authenticatedUser.userId());
        String previousNickname = user.getServiceNickname();
        String previousOauthNickname = user.getOauthNickname();
        String resolvedNickname = resolveNickname(user, request);
        CommonGroupDetail resolvedGoal = resolveGoal(user, request);
        CommonGroupDetail resolvedPosition = resolvePosition(user, request);
        List<CommonGroupDetail> resolvedTechStacks = resolveTechStacks(user, request);
        boolean profileComplete =
                StringUtils.hasText(resolvedNickname)
                        && resolvedGoal != null
                        && resolvedPosition != null
                        && !resolvedTechStacks.isEmpty();

        Users updatedUser = userService.updateUserProfile(
                user,
                resolvedNickname,
                resolvedGoal,
                resolvedPosition,
                profileComplete
        );
        userTechStackService.replaceUserTechStacks(updatedUser, resolvedTechStacks);

        UserInfoUpdateHistory updateHistory =
                UserEntityFactory.toUserInfoUpdateHistory(updatedUser, previousNickname, previousOauthNickname);
        UserInfoUpdateHistory savedUpdateHistory =
                userInfoUpdateHistoryService.save(updateHistory);

        applicationEventPublisher.publishEvent(new UserProfileChangedEvent(
                updatedUser.getUserId(),
                updatedUser.getIdentityId()
        ));
        return UserDtoFactory.toUpdateUserInfoResponse(
                updatedUser,
                savedUpdateHistory,
                resolvedTechStacks.stream()
                        .map(CommonGroupDetail::getCommonGroupDetailId)
                        .toList()
        );
    }

    @Override
    @Transactional(readOnly = false)
    public MyInfoResponse getMyInfo(AuthenticatedUserCacheEntry user) {
        return userResponseCacheService.findMyInfo(user.userId())
                .orElseGet(() -> loadMyInfo(user));
    }

    private MyInfoResponse loadMyInfo(AuthenticatedUserCacheEntry user) {
        UserMetaInformation meta = metaService.getMetaInfo(user.userId());
        int currentLevel = meta.getCurrentLevel();
        long totalExp = meta.getTotalExp();

        long prevLevelExp = commonGroupDetailService.findLevelRequiredExp(currentLevel - 1)
                .orElse(0L);
        long nextLevelExp = commonGroupDetailService.findLevelRequiredExp(currentLevel + 1)
                .orElse(totalExp);

        long xp    = totalExp - prevLevelExp;
        long maxXp = nextLevelExp - prevLevelExp;

        int topPercentage = metaService.calculateTopPercentage(totalExp);
        String nickname = (user.serviceNickname() != null && !user.serviceNickname().isBlank())
                ? user.serviceNickname()
                : user.oauthNickname();

        MyInfoResponse response = new MyInfoResponse(nickname, currentLevel, xp, maxXp, topPercentage, totalExp);
        userResponseCacheService.saveMyInfo(user.userId(), response);
        return response;
    }

    private String resolveNickname(Users user, UserProfileUpdateRequest request) {
        if (request.userServiceNickname() == null) {
            return user.getServiceNickname();
        }
        return request.userServiceNickname();
    }

    private CommonGroupDetail resolveGoal(Users user, UserProfileUpdateRequest request) {
        if (request.goal() == null) {
            return user.getUserGoal();
        }
        if (request.goal().isBlank()) {
            return null;
        }
        return commonGroupDetailService.getRequiredReferenceByGroupAndId(
                CommonGroupDetailService.USER_GOAL_GROUP_ID,
                request.goal()
        );
    }

    private CommonGroupDetail resolvePosition(Users user, UserProfileUpdateRequest request) {
        if (request.position() == null) {
            return user.getUserMainPosition();
        }
        if (request.position().isBlank()) {
            return null;
        }
        return commonGroupDetailService.getRequiredReferenceByGroupAndId(
                CommonGroupDetailService.PROJECT_POSITION_GROUP_ID,
                request.position()
        );
    }

    private List<CommonGroupDetail> resolveTechStacks(Users user, UserProfileUpdateRequest request) {
        if (request.techStacks() == null) {
            return userTechStackService.getByUser(user).stream()
                    .map(UserTechStack::getUserTechStack)
                    .toList();
        }
        return commonGroupDetailService.getRequiredReferencesByGroupAndIds(
                CommonGroupDetailService.PROJECT_TECH_STACK_GROUP_ID,
                request.techStacks()
        );
    }
}
