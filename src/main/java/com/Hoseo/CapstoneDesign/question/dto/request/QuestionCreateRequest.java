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
        List<String> tags,

        // 프론트엔드가 업로드했던 임시 이미지들의 ID 목록을 받습니다.
        @Schema(description = "첨부된 이미지 ID 목록", example = "[1, 2, 3]")
        List<Long> imageIds
) {
}