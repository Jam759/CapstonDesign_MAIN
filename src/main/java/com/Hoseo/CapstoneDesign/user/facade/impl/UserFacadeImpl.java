package com.Hoseo.CapstoneDesign.user.facade.impl;

import com.Hoseo.CapstoneDesign.common.entity.CommonGroupDetail;
import com.Hoseo.CapstoneDesign.common.service.CommonGroupDetailService;
import com.Hoseo.CapstoneDesign.gamification.entity.LevelRule;
import com.Hoseo.CapstoneDesign.gamification.repository.LevelRuleRepository;
import com.Hoseo.CapstoneDesign.global.annotation.Facade;
import com.Hoseo.CapstoneDesign.user.dto.request.UserProfileUpdateRequest;
import com.Hoseo.CapstoneDesign.user.dto.response.MyInfoResponse;
import com.Hoseo.CapstoneDesign.user.dto.response.UpdateUserInfoResponse;
import com.Hoseo.CapstoneDesign.user.entity.UserInfoUpdateHistory;
import com.Hoseo.CapstoneDesign.user.entity.UserMetaInformation;
import com.Hoseo.CapstoneDesign.user.entity.UserTechStack;
import com.Hoseo.CapstoneDesign.user.entity.Users;
import com.Hoseo.CapstoneDesign.user.facade.UserFacade;
import com.Hoseo.CapstoneDesign.user.factory.UserDtoFactory;
import com.Hoseo.CapstoneDesign.user.factory.UserEntityFactory;
import com.Hoseo.CapstoneDesign.user.service.UserInfoUpdateHistoryService;
import com.Hoseo.CapstoneDesign.user.service.UserMetaInformationService;
import com.Hoseo.CapstoneDesign.user.service.UserService;
import com.Hoseo.CapstoneDesign.user.service.UserTechStackService;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Facade
@RequiredArgsConstructor
public class UserFacadeImpl implements UserFacade {

    private final UserService userService;
    private final UserInfoUpdateHistoryService userInfoUpdateHistoryService;
    private final UserMetaInformationService metaService;
    private final LevelRuleRepository levelRuleRepository;
    private final CommonGroupDetailService commonGroupDetailService;
    private final UserTechStackService userTechStackService;

    @Override
    @Transactional(readOnly = false)
    public UpdateUserInfoResponse updateUserProfile(Users user, UserProfileUpdateRequest request) {
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

        return UserDtoFactory.toUpdateUserInfoResponse(
                updatedUser,
                savedUpdateHistory,
                resolvedTechStacks.stream()
                        .map(CommonGroupDetail::getCommonGroupDetailId)
                        .toList()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public MyInfoResponse getMyInfo(Users user) {
        UserMetaInformation meta = metaService.getMetaInfo(user);
        int currentLevel = meta.getLevelRule().getLevel();
        long totalExp = meta.getTotalExp();

        long prevLevelExp = levelRuleRepository.findById(currentLevel - 1)
                .map(LevelRule::getRequiredTotalExp)
                .orElse(0L);

        long nextLevelExp = levelRuleRepository.findById(currentLevel + 1)
                .map(LevelRule::getRequiredTotalExp)
                .orElse(totalExp);

        long xp    = totalExp - prevLevelExp;
        long maxXp = nextLevelExp - prevLevelExp;

        long usersAbove = metaService.countUsersAbove(totalExp);
        long total      = metaService.countAll();
        int topPercentage = total == 0 ? 100 : (int) Math.ceil((double) (usersAbove + 1) / total * 100);

        String nickname = (user.getServiceNickname() != null && !user.getServiceNickname().isBlank())
                ? user.getServiceNickname()
                : user.getOauthNickname();

        return new MyInfoResponse(nickname, currentLevel, xp, maxXp, topPercentage, totalExp);
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
