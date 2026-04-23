package com.Hoseo.CapstoneDesign.friend.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Friend or project invite response")
public record FriendInviteResponse(
        @Schema(description = "Invite id", example = "1")
        Long id,
        @Schema(description = "Sender display name", example = "commit-master")
        String from,
        @Schema(description = "Related project name for project invites", example = "DevXP")
        String projectName,
        @Schema(description = "Frontend status: pending, accepted, declined", example = "pending")
        String status
) {
}
