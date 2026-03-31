package com.Hoseo.CapstoneDesign.gamification.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "배지 응답")
public class BadgeResponse {

    @Schema(description = "배지 ID", example = "501")
    private Long badgeId;

    @Schema(description = "배지 이름", example = "첫 분석 완료")
    private String badgeName;

    @Schema(description = "배지 설명", example = "첫 프로젝트 분석을 완료했습니다.")
    private String badgeDescription;

    @Schema(description = "배지 이미지 URL", example = "https://example.com/badges/first-analysis.png")
    private String badgeImageUrl;

    @Schema(description = "배지 유형", example = "ANALYSIS")
    private String badgeType;

    @Schema(description = "배지 획득 시각", example = "2026-03-10T14:00:00")
    private LocalDateTime acquiredAt;

}
