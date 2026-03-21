package com.Hoseo.CapstoneDesign.github.dto.application;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record GithubInstallationRepositoriesResponse(
        @JsonProperty("total_count")
        int totalCount,
        List<GithubRepositorySummary> repositories
) {}
