package com.Hoseo.CapstoneDesign.analysis.dto.response;

import com.Hoseo.CapstoneDesign.analysis.dto.application.ProjectAnalysisUserViewResponse;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "프로젝트 분석 개선 조언 응답")
public record ProjectAnalysisAdviceResponse(
        @ArraySchema(schema = @Schema(implementation = ProjectAnalysisUserViewResponse.Advice.class))
        List<ProjectAnalysisUserViewResponse.Advice> advice
) {}
