package com.Hoseo.CapstoneDesign.user.factory;

import com.Hoseo.CapstoneDesign.user.dto.response.UpdateUserInfoResponse;
import com.Hoseo.CapstoneDesign.user.entity.UserInfoUpdateHistory;
import com.Hoseo.CapstoneDesign.user.entity.Users;

public class UserDtoFactory {

    public static UpdateUserInfoResponse toUpdateUserInfoResponse(
            Users updatedUser,
            UserInfoUpdateHistory savedUpdateHistory
    ) {
        return new UpdateUserInfoResponse(
                updatedUser.getServiceNickname(),
                savedUpdateHistory.getUpdatedAt()
        );
    }
}
