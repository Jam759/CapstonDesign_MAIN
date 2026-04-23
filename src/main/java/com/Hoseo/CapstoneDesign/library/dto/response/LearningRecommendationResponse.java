package com.Hoseo.CapstoneDesign.library.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Learning recommendation response")
public record LearningRecommendationResponse(
        @Schema(description = "Recommendation id", example = "1")
        Long id,
        @Schema(description = "Content type badge", example = "COURSE")
        String type,
        @Schema(description = "Content title", example = "Zustand를 활용한 안전한 전역 상태 관리")
        String title,
        @Schema(description = "Short description", example = "React 프로젝트에서 Zustand로 전역 상태를 모델링하는 방법")
        String description,
        @Schema(description = "Content source", example = "Inlearn")
        String source,
        @Schema(description = "Human-readable duration", example = "15분 소요")
        String duration,
        @Schema(description = "External resource URL", example = "https://example.com/course")
        String url,
        @Schema(description = "Thumbnail image URL", example = "https://example.com/thumb.png")
        String thumbnailUrl
) {
}
