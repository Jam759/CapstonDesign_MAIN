package com.Hoseo.CapstoneDesign.analysis.dto.response;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "프로젝트 분석 하이라이트 응답")
public record ProjectAnalysisHighlightsResponse(
        @ArraySchema(schema = @Schema(description = "프로젝트 강점", example = "테스트 커버리지가 안정적으로 유지되고 있습니다."))
        List<String> strengths,
        @ArraySchema(schema = @Schema(description = "프로젝트 리스크", example = "README 문서화가 부족해 신규 참여자 온보딩 비용이 큽니다."))
        List<String> risks
) {}
