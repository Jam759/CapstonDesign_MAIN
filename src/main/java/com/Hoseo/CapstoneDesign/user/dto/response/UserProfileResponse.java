package com.Hoseo.CapstoneDesign.user.dto.response;

public record UserProfileResponse(
        String tier,
        Short level,
        Long totalExp,
        String gitHubId,
        String userServiceNickname
) {
}
