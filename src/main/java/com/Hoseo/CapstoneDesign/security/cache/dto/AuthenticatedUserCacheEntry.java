package com.Hoseo.CapstoneDesign.security.cache.dto;

import com.Hoseo.CapstoneDesign.user.entity.enums.OauthType;
import com.Hoseo.CapstoneDesign.user.entity.enums.SystemRole;

import java.util.UUID;

public record AuthenticatedUserCacheEntry(
        Long userId,
        UUID identityId,
        String serviceNickname,
        SystemRole systemRole,
        OauthType oauthType,
        String oauthProviderId,
        String oauthNickname,
        boolean profileComplete
) {
}
