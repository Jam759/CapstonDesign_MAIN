package com.Hoseo.CapstoneDesign.analysis.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "프로젝트의 첫 AI 분석 완료 여부 (대시보드 진입 가드용)")
public record AnalysisReadyResponse(
        @Schema(description = "NOTIFICATION_COMPLETED 상태의 분석 Job 이 1건이라도 있으면 true", example = "true")
        boolean ready
) {
}
