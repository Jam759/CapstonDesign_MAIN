package com.Hoseo.CapstoneDesign.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "User profile update request")
public record UserProfileUpdateRequest(
        @Schema(description = "Service nickname", example = "commit-master")
        String userServiceNickname,

        @Schema(description = "User goal common code", example = "Job")
        String goal,

        @Schema(description = "User main position common code", example = "Backend")
        String position,

        @Schema(description = "User tech stack common detail ids", example = "[\"Java\", \"Spring\", \"React\"]")
        List<String> techStacks
) {
}
