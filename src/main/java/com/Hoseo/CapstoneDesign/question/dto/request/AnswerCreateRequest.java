package com.Hoseo.CapstoneDesign.question.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank; // 유효성 검증 임포트 추가

@Schema(description = "Answer create request")
public record AnswerCreateRequest(
        @NotBlank(message = "답변 내용은 필수 입력 항목입니다.")
        @Schema(description = "Answer body", example = "Keep transaction orchestration in the facade and persistence operations in services.")
        String content
) {
}