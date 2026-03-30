package com.Hoseo.CapstoneDesign.analysis.dto.response;

import java.util.List;

public record ProjectAnalysisHighlightsResponse(
        List<String> strengths,
        List<String> risks
) {}
