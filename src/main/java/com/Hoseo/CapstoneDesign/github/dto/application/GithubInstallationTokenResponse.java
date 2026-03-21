package com.Hoseo.CapstoneDesign.github.dto.application;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GithubInstallationTokenResponse(
        String token,
        @JsonProperty("expires_at")
        String expiresAt
) {
}
