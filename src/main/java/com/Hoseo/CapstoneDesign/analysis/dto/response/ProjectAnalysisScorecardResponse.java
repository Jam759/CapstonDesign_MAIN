package com.Hoseo.CapstoneDesign.analysis.dto.response;

import com.Hoseo.CapstoneDesign.analysis.dto.application.ProjectAnalysisUserViewResponse;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "프로젝트 분석 점수표 응답")
public record ProjectAnalysisScorecardResponse(
        @Schema(description = "전체 및 카테고리별 점수")
        ProjectAnalysisUserViewResponse.Scorecard scorecard
) {}
