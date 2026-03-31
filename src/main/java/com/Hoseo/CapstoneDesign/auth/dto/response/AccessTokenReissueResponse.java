package com.Hoseo.CapstoneDesign.auth.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Access Token 재발급 응답")
public record AccessTokenReissueResponse(
        @Schema(description = "재발급된 Access Token", example = "eyJhbGciOiJIUzI1NiJ9...")
        String accessToken,
        @Schema(description = "Access Token 만료 시각", example = "2026-03-30T15:45:00")
        LocalDateTime accessTokenExpiredAt,
        @Schema(description = "Refresh Token 만료 시각", example = "2026-04-06T15:30:00")
        LocalDateTime refreshTokenExpiredAt,
        @Schema(description = "첫 로그인 사용자 여부", example = "false")
        boolean isNewUser
) {
}
