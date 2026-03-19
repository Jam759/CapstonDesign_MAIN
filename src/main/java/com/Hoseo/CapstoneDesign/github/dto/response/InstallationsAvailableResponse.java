package com.Hoseo.CapstoneDesign.github.dto.response;

import lombok.Builder;

@Builder
public record InstallationsAvailableResponse(
        boolean installed,
        String installUrl
) {}
