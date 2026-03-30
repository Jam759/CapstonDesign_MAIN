package com.Hoseo.CapstoneDesign.analysis.dto.response;

import com.Hoseo.CapstoneDesign.analysis.dto.application.ProjectAnalysisUserViewResponse;

public record ProjectAnalysisScorecardResponse(
        ProjectAnalysisUserViewResponse.Scorecard scorecard
) {}
