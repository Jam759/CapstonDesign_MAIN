package com.Hoseo.CapstoneDesign.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "User profile update response")
public record UpdateUserInfoResponse(
        @Schema(description = "Updated service nickname", example = "new-service-nick")
        String serviceNickname,
        @Schema(description = "Updated user bio", example = "Backend developer interested in distributed systems.")
        String bio,
        @Schema(description = "Updated goal common code", example = "Job")
        String goal,
        @Schema(description = "Updated position common code", example = "Backend")
        String position,
        @Schema(description = "Updated tech stack common codes", example = "[\"Java\", \"Spring\", \"React\"]")
        List<String> techStacks,
        @Schema(description = "Whether the profile is complete", example = "true")
        boolean profileComplete,
        @Schema(description = "Last updated date time", example = "2026-03-12T12:00:00")
        LocalDateTime updateDate
) {
}
