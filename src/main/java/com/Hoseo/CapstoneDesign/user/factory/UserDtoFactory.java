package com.Hoseo.CapstoneDesign.user.factory;

import com.Hoseo.CapstoneDesign.gamification.entity.UserBadge;
import com.Hoseo.CapstoneDesign.user.dto.response.UpdateUserInfoResponse;
import com.Hoseo.CapstoneDesign.user.entity.UserInfoUpdateHistory;
import com.Hoseo.CapstoneDesign.user.entity.Users;

import java.util.Set;
import java.util.stream.Collectors;

public class UserDtoFactory {

    public static UpdateUserInfoResponse toUpdateUserInfoResponse(Users updatedUser, Set<UserBadge> updatedUserBages, UserInfoUpdateHistory savedUpdateHistory) {
        return new UpdateUserInfoResponse(
                updatedUser.getServiceNickname(),
                updatedUserBages.stream()
                        .map(UserBadge::getUserBadgeId)
                        .collect(Collectors.toSet()),
                savedUpdateHistory.getUpdatedAt());
    }

}
