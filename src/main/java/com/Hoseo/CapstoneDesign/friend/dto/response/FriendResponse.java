package com.Hoseo.CapstoneDesign.friend.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Friend response")
public record FriendResponse(
        @Schema(description = "Friend user id", example = "2")
        Long id,
        @Schema(description = "Friend display name", example = "commit-master")
        String serviceNickname
) {
}