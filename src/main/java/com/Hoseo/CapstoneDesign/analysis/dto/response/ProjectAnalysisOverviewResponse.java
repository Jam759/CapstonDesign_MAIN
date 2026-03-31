package com.Hoseo.CapstoneDesign.analysis.dto.response;

import com.Hoseo.CapstoneDesign.analysis.dto.application.ProjectAnalysisUserViewResponse;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "프로젝트 분석 개요 응답")
public record ProjectAnalysisOverviewResponse(
        @Schema(description = "리포트 스키마 버전", example = "1.0.0")
        String schemaVersion,
        @Schema(description = "리포트 생성 시각", example = "2026-03-27T16:45:03.585Z")
        String generatedAt,
        @Schema(description = "분석 대상 범위")
        ProjectAnalysisUserViewResponse.Scope scope,
        @Schema(description = "한 줄 핵심 요약", example = "최근 커밋 품질이 안정적으로 개선되고 있습니다.")
        String headline,
        @Schema(description = "상세 요약", example = "테스트와 예외 처리 품질은 양호하지만 문서화와 협업 패턴 보강이 필요합니다.")
        String summary
) {}
