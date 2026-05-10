package com.Hoseo.CapstoneDesign.friend.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Friend response")
public record FriendResponse(
        @Schema(description = "Friend user id (Users 테이블의 PK)", example = "2")
        Long id,
        @Schema(description = "Friend display name", example = "commit-master")
        String serviceNickname,
        @Schema(description = "OAuth nickname", example = "Jam759")
        String oauthNickname
) {
}