package com.Hoseo.CapstoneDesign.user.facade.impl;

import com.Hoseo.CapstoneDesign.global.annotation.Facade;
import com.Hoseo.CapstoneDesign.user.dto.request.UserProfileUpdateRequest;
import com.Hoseo.CapstoneDesign.user.dto.response.UpdateUserInfoResponse;
import com.Hoseo.CapstoneDesign.user.entity.UserInfoUpdateHistory;
import com.Hoseo.CapstoneDesign.user.entity.Users;
import com.Hoseo.CapstoneDesign.user.facade.UserFacade;
import com.Hoseo.CapstoneDesign.user.factory.UserDtoFactory;
import com.Hoseo.CapstoneDesign.user.factory.UserEntityFactory;
import com.Hoseo.CapstoneDesign.user.service.UserInfoUpdateHistoryService;
import com.Hoseo.CapstoneDesign.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@Facade
@RequiredArgsConstructor
public class UserFacadeImpl implements UserFacade {

    private final UserService userService;
    private final UserInfoUpdateHistoryService userInfoUpdateHistoryService;

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
}
