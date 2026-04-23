package com.Hoseo.CapstoneDesign.user.facade.impl;

import com.Hoseo.CapstoneDesign.gamification.entity.LevelRule;
import com.Hoseo.CapstoneDesign.gamification.repository.LevelRuleRepository;
import com.Hoseo.CapstoneDesign.global.annotation.Facade;
import com.Hoseo.CapstoneDesign.user.dto.request.UserProfileUpdateRequest;
import com.Hoseo.CapstoneDesign.user.dto.response.MyInfoResponse;
import com.Hoseo.CapstoneDesign.user.dto.response.UpdateUserInfoResponse;
import com.Hoseo.CapstoneDesign.user.entity.UserInfoUpdateHistory;
import com.Hoseo.CapstoneDesign.user.entity.UserMetaInformation;
import com.Hoseo.CapstoneDesign.user.entity.Users;
import com.Hoseo.CapstoneDesign.user.facade.UserFacade;
import com.Hoseo.CapstoneDesign.user.factory.UserDtoFactory;
import com.Hoseo.CapstoneDesign.user.factory.UserEntityFactory;
import com.Hoseo.CapstoneDesign.user.service.UserInfoUpdateHistoryService;
import com.Hoseo.CapstoneDesign.user.service.UserMetaInformationService;
import com.Hoseo.CapstoneDesign.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@Facade
@RequiredArgsConstructor
public class UserFacadeImpl implements UserFacade {

    private final UserService userService;
    private final UserInfoUpdateHistoryService userInfoUpdateHistoryService;
    private final UserMetaInformationService metaService;
    private final LevelRuleRepository levelRuleRepository;

    @Override
    @Transactional(readOnly = false)
    public UpdateUserInfoResponse updateUserProfile(Users user, UserProfileUpdateRequest request) {
        String previousNickname = user.getServiceNickname();
        String previousOauthNickname = user.getOauthNickname();
        Users updatedUser = userService.updateServiceUserName(user, request.userServiceNickname());

        UserInfoUpdateHistory updateHistory =
                UserEntityFactory.toUserInfoUpdateHistory(updatedUser, previousNickname, previousOauthNickname);
        UserInfoUpdateHistory savedUpdateHistory =
                userInfoUpdateHistoryService.save(updateHistory);

        return UserDtoFactory.toUpdateUserInfoResponse(updatedUser, savedUpdateHistory);
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
}
