package com.Hoseo.CapstoneDesign.github.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class RepositoryBranchesResponse {
    private Long installationId;
    private Long repositoryId;
    private String repositoryFullName;
    private List<BranchItem> branches;

    @Getter
    @Builder
    @AllArgsConstructor
    public static class BranchItem {
        private String name;
        private boolean protectedBranch;
    }
}
