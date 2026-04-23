package com.Hoseo.CapstoneDesign.support.factory;

import com.Hoseo.CapstoneDesign.user.dto.request.UserProfileUpdateRequest;

public final class UserProfileUpdateRequestFactory {

    private UserProfileUpdateRequestFactory() {
    }

    public static UserProfileUpdateRequest create(String nickname) {
        return new UserProfileUpdateRequest(nickname);
    }
}
