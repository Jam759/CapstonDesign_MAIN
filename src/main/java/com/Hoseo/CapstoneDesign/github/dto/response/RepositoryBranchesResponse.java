package com.Hoseo.CapstoneDesign.github.dto.response;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "GitHub 저장소 브랜치 목록 응답")
public class RepositoryBranchesResponse {
    @Schema(description = "GitHub App installation 내부 ID", example = "77")
    private Long installationId;

    @Schema(description = "설치 저장소 ID", example = "3001")
    private Long repositoryId;

    @Schema(description = "저장소 전체 이름", example = "Jam759/CapstoneDesign")
    private String repositoryFullName;

    @ArraySchema(schema = @Schema(implementation = BranchItem.class))
    private List<BranchItem> branches;

    @Getter
    @Builder
    @AllArgsConstructor
    @Schema(description = "브랜치 정보")
    public static class BranchItem {
        @Schema(description = "브랜치 이름", example = "main")
        private String name;

        @Schema(description = "보호 브랜치 여부", example = "true")
        private boolean protectedBranch;
    }
}
