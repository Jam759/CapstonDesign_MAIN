package com.Hoseo.CapstoneDesign.support.factory;

import com.Hoseo.CapstoneDesign.user.dto.request.UserProfileUpdateRequest;

import java.util.List;

public final class UserProfileUpdateRequestFactory {

    private UserProfileUpdateRequestFactory() {
    }

    public static UserProfileUpdateRequest create(String nickname) {
        return new UserProfileUpdateRequest(nickname, null, null, null, null);
    }

    public static UserProfileUpdateRequest create(String nickname, String bio) {
        return new UserProfileUpdateRequest(nickname, bio, null, null, null);
    }

    public static UserProfileUpdateRequest create(
            String nickname,
            String bio,
            String goal,
            String position,
            List<String> techStacks
    ) {
        return new UserProfileUpdateRequest(nickname, bio, goal, position, techStacks);
    }
}
