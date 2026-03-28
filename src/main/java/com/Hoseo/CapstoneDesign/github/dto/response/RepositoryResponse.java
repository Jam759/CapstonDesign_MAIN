package com.Hoseo.CapstoneDesign.github.dto.response;

import lombok.Builder;

@Builder
public record RepositoryResponse(
        Long repositoryId,
        String repositoryFullName,
        boolean isPrivate
) {
}
