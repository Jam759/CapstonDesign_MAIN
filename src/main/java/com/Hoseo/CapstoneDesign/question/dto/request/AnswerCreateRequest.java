package com.Hoseo.CapstoneDesign.question.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Answer create request")
public record AnswerCreateRequest(
        @Schema(description = "Answer body", example = "Keep transaction orchestration in the facade and persistence operations in services.")
        String content
) {
}
