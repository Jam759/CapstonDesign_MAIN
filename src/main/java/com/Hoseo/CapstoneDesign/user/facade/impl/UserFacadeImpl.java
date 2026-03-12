package com.Hoseo.CapstoneDesign.user.facade.impl;

import com.Hoseo.CapstoneDesign.badge.entity.UserBadge;
import com.Hoseo.CapstoneDesign.global.annotation.Facade;
import com.Hoseo.CapstoneDesign.user.dto.request.UserProfileUpdateRequest;
import com.Hoseo.CapstoneDesign.user.dto.response.UpdateUserInfoResponse;
import com.Hoseo.CapstoneDesign.user.dto.response.UserProfileResponse;
import com.Hoseo.CapstoneDesign.user.entity.UserInfoUpdateHistory;
import com.Hoseo.CapstoneDesign.user.entity.Users;
import com.Hoseo.CapstoneDesign.user.facade.UserFacade;
import com.Hoseo.CapstoneDesign.badge.service.UserBadgeService;
import com.Hoseo.CapstoneDesign.user.factory.UserDtoFactory;
import com.Hoseo.CapstoneDesign.user.factory.UserEntityFactory;
import com.Hoseo.CapstoneDesign.user.service.UserInfoUpdateHistoryService;
import com.Hoseo.CapstoneDesign.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Facade
@RequiredArgsConstructor
public class UserFacadeImpl implements UserFacade {

    private final UserService userService;
    private final UserBadgeService userBadgeService;
    private final UserInfoUpdateHistoryService userInfoUpdateHistoryService;

    /*
    *
    * request : nickname, badge
    *
    * 파람의 user를 userservice에 넣어 서비스닉네임 필드 변경
    * 유저가 소유한 뱃지 조회 후
    * 착용한
    */
    @Override
    @Transactional(readOnly = false)
    public UpdateUserInfoResponse updateUserProfile(Users user, UserProfileUpdateRequest request) {
        String previousNickname = user.getServiceNickname();
        String previousOauthNickname = user.getOauthNickname();
        Users updatedUser =
                userService.updateServiceUserName(user,request.userServiceNickname());
        Set<UserBadge> updatedUserBages =
                userBadgeService.updateUserBadgeEquip(user,request.equippedBadges());

        UserInfoUpdateHistory updateHistory =
                UserEntityFactory.toUserInfoUpdateHistory(updatedUser, previousNickname, previousOauthNickname);
        UserInfoUpdateHistory savedUpdateHistory =
                userInfoUpdateHistoryService.save(updateHistory);

        return UserDtoFactory.toUpdateUserInfoResponse(
                updatedUser,updatedUserBages,savedUpdateHistory
        );
    }
}
