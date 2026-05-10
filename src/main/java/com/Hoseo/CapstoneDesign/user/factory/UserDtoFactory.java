package com.Hoseo.CapstoneDesign.user.factory;

import com.Hoseo.CapstoneDesign.user.dto.response.UpdateUserInfoResponse;
import com.Hoseo.CapstoneDesign.user.dto.response.UserProfileThumbnail;
import com.Hoseo.CapstoneDesign.user.entity.UserInfoUpdateHistory;
import com.Hoseo.CapstoneDesign.user.entity.Users;

import java.util.List;

public class UserDtoFactory {

    public static UpdateUserInfoResponse toUpdateUserInfoResponse(
            Users updatedUser,
            UserInfoUpdateHistory savedUpdateHistory,
            List<String> techStackIds
    ) {
        return new UpdateUserInfoResponse(
                updatedUser.getServiceNickname(),
                updatedUser.getBio(),
                updatedUser.getUserGoal() != null ? updatedUser.getUserGoal().getCommonGroupDetailId() : null,
                updatedUser.getUserMainPosition() != null ? updatedUser.getUserMainPosition().getCommonGroupDetailId() : null,
                techStackIds,
                updatedUser.isProfileComplete(),
                savedUpdateHistory.getUpdatedAt()
        );
    }

    public static UserProfileThumbnail toUserProfileThumbnail(Users user) {
        return new UserProfileThumbnail(
                user.getUserId(),
                user.getServiceNickname(),
                user.getOauthType().name(),
                user.getOauthNickname()
        );
    }
}
