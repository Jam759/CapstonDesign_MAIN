package com.Hoseo.CapstoneDesign.analysis.dto.response;

import com.Hoseo.CapstoneDesign.analysis.dto.application.ProjectAnalysisUserViewResponse;

public record ProjectAnalysisOverviewResponse(
        String schemaVersion,
        String generatedAt,
        ProjectAnalysisUserViewResponse.Scope scope,
        String headline,
        String summary
) {}
