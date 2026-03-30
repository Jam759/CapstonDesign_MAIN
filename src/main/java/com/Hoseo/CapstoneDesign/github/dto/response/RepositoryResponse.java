package com.Hoseo.CapstoneDesign.github.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "GitHub 저장소 요약 정보")
@Builder
public record RepositoryResponse(
        @Schema(description = "설치 저장소 ID", example = "3001")
        Long repositoryId,
        @Schema(description = "저장소 전체 이름", example = "Jam759/CapstoneDesign")
        String repositoryFullName,
        @Schema(description = "비공개 저장소 여부", example = "true")
        boolean isPrivate
) {
}
