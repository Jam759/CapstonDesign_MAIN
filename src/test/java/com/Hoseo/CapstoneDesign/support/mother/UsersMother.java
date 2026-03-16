package com.Hoseo.CapstoneDesign.support.mother;

import com.Hoseo.CapstoneDesign.support.builder.UsersTestBuilder;
import com.Hoseo.CapstoneDesign.user.entity.Users;

public final class UsersMother {

    private UsersMother() {
    }

    public static Users defaultUser() {
        return UsersTestBuilder.defaultUser().build();
    }

    public static Users withNickname(String nickname) {
        return UsersTestBuilder.defaultUser()
                .serviceNickname(nickname)
                .build();
    }

    public static Users withOauth(String providerId, String oauthNickname) {
        return UsersTestBuilder.defaultUser()
                .oauthProviderId(providerId)
                .oauthNickname(oauthNickname)
                .build();
    }
}
