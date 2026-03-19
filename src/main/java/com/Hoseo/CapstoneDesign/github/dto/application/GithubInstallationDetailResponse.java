package com.Hoseo.CapstoneDesign.github.dto.application;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GithubInstallationDetailResponse(
        Long id,
        Account account
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Account(
            Long id,
            String login,
            String type
    ) {
    }
}