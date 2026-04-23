package com.Hoseo.CapstoneDesign.friend.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Friend response")
public record FriendResponse(
        @Schema(description = "Friend user id", example = "2")
        Long id,
        @Schema(description = "Friend display name", example = "commit-master")
        String serviceNickname,
        @Schema(description = "Frontend status: online or offline", example = "online")
        String status,
        @Schema(description = "Last seen datetime", example = "2026-04-22T10:15:00")
        LocalDateTime lastSeen
) {
}
