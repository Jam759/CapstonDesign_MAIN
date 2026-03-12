package com.Hoseo.CapstoneDesign.user.dto.request;

import java.util.Set;

public record UserProfileUpdateRequest(
        String userServiceNickname,
        Set<Long> equippedBadges
) {
}
