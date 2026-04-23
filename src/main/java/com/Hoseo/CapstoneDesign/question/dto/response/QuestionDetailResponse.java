package com.Hoseo.CapstoneDesign.question.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Question detail response")
public record QuestionDetailResponse(
        @Schema(description = "Question id", example = "1")
        Long id,
        @Schema(description = "Question title", example = "How should I structure Spring services?")
        String title,
        @Schema(description = "Question author display name", example = "commit-master")
        String author,
        @Schema(description = "Created datetime", example = "2026-04-22T10:15:00")
        LocalDateTime createdAt,
        @Schema(description = "Question body")
        String content,
        @Schema(description = "Question tags", example = "[\"spring\", \"architecture\"]")
        List<String> tags,
        @Schema(description = "View count", example = "12")
        int views,
        @Schema(description = "Answers")
        List<AnswerResponse> answers
) {
}
