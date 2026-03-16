package com.Hoseo.CapstoneDesign.support.factory;

import com.Hoseo.CapstoneDesign.user.dto.request.UserProfileUpdateRequest;

import java.util.Set;

public final class UserProfileUpdateRequestFactory {

    private UserProfileUpdateRequestFactory() {
    }

    public static UserProfileUpdateRequest create(String nickname, Set<Long> equippedBadges) {
        return new UserProfileUpdateRequest(nickname, equippedBadges);
    }
}
