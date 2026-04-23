package com.Hoseo.CapstoneDesign.question.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Question create request")
public record QuestionCreateRequest(
        @Schema(description = "Question title", example = "How should I structure Spring services?")
        String title,
        @Schema(description = "Question body", example = "I want to separate facade and service responsibilities.")
        String content,
        @Schema(description = "Question tags", example = "[\"spring\", \"architecture\"]")
        List<String> tags
) {
}
