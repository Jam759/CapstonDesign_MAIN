package com.Hoseo.CapstoneDesign.github.dto.application;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GithubBranchDto(
    String name,
    @JsonProperty("protected")
    boolean protectedBranch
) {}
