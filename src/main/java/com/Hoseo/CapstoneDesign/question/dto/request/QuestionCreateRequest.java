package com.Hoseo.CapstoneDesign.question.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank; // 유효성 검증 임포트 추가
import jakarta.validation.constraints.Size; // 글자 수 검증 임포트 추가

import java.util.List;

@Schema(description = "Question create request")
public record QuestionCreateRequest(
        // 제목이 비어있거나 공백만 있는 것을 막아줍니다.
        @NotBlank(message = "제목은 필수 입력 항목입니다.")
        // 제목의 최대 길이를 제한합니다.
        @Size(max = 255, message = "제목은 255자를 초과할 수 없습니다.")
        @Schema(description = "Question title", example = "How should I structure Spring services?")
        String title,

        @NotBlank(message = "내용은 필수 입력 항목입니다.")
        @Schema(description = "Question body", example = "I want to separate facade and service. ![imageId:101](https://...)")
        String content,

        @Schema(description = "Question tags", example = "[\"spring\", \"architecture\"]")
        List<String> tags
) {
}