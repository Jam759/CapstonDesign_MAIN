package com.Hoseo.CapstoneDesign.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Current user summary response for frontend user store")
public record MyInfoResponse(
        @Schema(description = "Service nickname", example = "DevXP User")
        String nickname,
        @Schema(description = "Current level", example = "5")
        Integer level,
        @Schema(description = "Current XP within the current level", example = "320")
        Long xp,
        @Schema(description = "XP required to reach the next level", example = "500")
        Long maxXp,
        @Schema(description = "Top percentage ranking (lower is better)", example = "15")
        Integer topPercentage,
        @Schema(description = "Total accumulated experience", example = "1520")
        Long totalExp
) {
}
