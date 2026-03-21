package com.Hoseo.CapstoneDesign.github.dto.application;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GithubRepositorySummary(
        Long id,
        String name,
        @JsonProperty("full_name")
        String fullName,
        @JsonProperty("private")
        boolean isPrivate
) {}
