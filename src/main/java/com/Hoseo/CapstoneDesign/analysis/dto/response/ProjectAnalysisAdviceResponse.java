package com.Hoseo.CapstoneDesign.analysis.dto.response;

import com.Hoseo.CapstoneDesign.analysis.dto.application.ProjectAnalysisUserViewResponse;

import java.util.List;

public record ProjectAnalysisAdviceResponse(
        List<ProjectAnalysisUserViewResponse.Advice> advice
) {}
