package com.Hoseo.CapstoneDesign.security.cache.factory;

import com.Hoseo.CapstoneDesign.security.cache.dto.AuthenticatedUserCacheEntry;
import com.Hoseo.CapstoneDesign.user.entity.Users;

public final class AuthenticatedUserCacheFactory {

    private AuthenticatedUserCacheFactory() {
    }

    public static AuthenticatedUserCacheEntry fromUser(Users user) {
        return new AuthenticatedUserCacheEntry(
                user.getUserId(),
                user.getIdentityId(),
                user.getServiceNickname(),
                user.getSystemRole(),
                user.getOauthType(),
                user.getOauthProviderId(),
                user.getOauthNickname(),
                user.isProfileComplete()
        );
    }
}
