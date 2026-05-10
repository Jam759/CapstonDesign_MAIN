package com.Hoseo.CapstoneDesign.question.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

@Schema(description = "Question update request")
public record QuestionUpdateRequest(
        @NotBlank(message = "제목은 필수 입력 항목입니다.")
        @Size(max = 255, message = "제목은 255자를 초과할 수 없습니다.")
        @Schema(description = "Question title", example = "수정된 제목입니다.")
        String title,

        @NotBlank(message = "내용은 필수 입력 항목입니다.")
        @Schema(description = "Question body", example = "수정된 본문 내용입니다. ![imageId:102](https://...)")
        String content,

        @Schema(description = "Question tags")
        List<String> tags
) {
}