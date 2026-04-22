package com.Hoseo.CapstoneDesign.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "User profile update request")
public record UserProfileUpdateRequest(
        @Schema(description = "Service nickname", example = "commit-master")
        String userServiceNickname
) {
}
