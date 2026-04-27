package com.Hoseo.CapstoneDesign.auth.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Access token reissue response")
public record AccessTokenReissueResponse(
        @Schema(description = "Reissued access token", example = "eyJhbGciOiJIUzI1NiJ9...")
        String accessToken,
        @Schema(description = "Access token expiration time", example = "2026-03-30T15:45:00")
        LocalDateTime accessTokenExpiredAt,
        @Schema(description = "Refresh token expiration time", example = "2026-04-06T15:30:00")
        LocalDateTime refreshTokenExpiredAt,
        @Schema(description = "Whether the user still needs to finish profile setup", example = "false")
        boolean needsProfileSetup,
        @Schema(description = "Whether the user already linked a GitHub App installation", example = "true")
        boolean githubInstalled
) {
}
