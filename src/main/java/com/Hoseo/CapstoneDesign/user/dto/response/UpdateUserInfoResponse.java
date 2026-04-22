package com.Hoseo.CapstoneDesign.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "User profile update response")
public record UpdateUserInfoResponse(
        @Schema(description = "Updated service nickname", example = "new-service-nick")
        String serviceNickname,
        @Schema(description = "Last updated date time", example = "2026-03-12T12:00:00")
        LocalDateTime updateDate
) {
}
