package com.Hoseo.CapstoneDesign.question.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Question answer response")
public record AnswerResponse(
        @Schema(description = "Answer id", example = "1")
        Long id,
        @Schema(description = "Answer author display name", example = "commit-master")
        String author,
        @Schema(description = "Answer created datetime", example = "2026-04-22T10:15:00")
        LocalDateTime createdAt,
        @Schema(description = "Whether this answer is selected as best", example = "false")
        boolean best,
        @Schema(description = "Answer body")
        String content
) {
}
