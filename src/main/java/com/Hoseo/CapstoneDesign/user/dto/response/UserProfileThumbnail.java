package com.Hoseo.CapstoneDesign.user.dto.response;

public record UserProfileThumbnail(
        Long userId,
        String serviceNickname,
        String oauthType,
        String oauthNickname
) {}
