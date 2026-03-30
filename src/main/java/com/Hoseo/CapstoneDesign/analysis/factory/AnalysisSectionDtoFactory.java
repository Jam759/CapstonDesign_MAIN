package com.Hoseo.CapstoneDesign.analysis.factory;

import com.Hoseo.CapstoneDesign.analysis.dto.response.ProjectAnalysisAdviceResponse;
import com.Hoseo.CapstoneDesign.analysis.dto.response.ProjectAnalysisHighlightsResponse;
import com.Hoseo.CapstoneDesign.analysis.dto.response.ProjectAnalysisOverviewResponse;
import com.Hoseo.CapstoneDesign.analysis.dto.response.ProjectAnalysisScorecardResponse;
import com.Hoseo.CapstoneDesign.analysis.dto.application.ProjectAnalysisUserViewResponse;

public final class AnalysisSectionDtoFactory {

    private AnalysisSectionDtoFactory() {
    }

    public static ProjectAnalysisOverviewResponse toOverview(ProjectAnalysisUserViewResponse userView) {
        return new ProjectAnalysisOverviewResponse(
                userView.schemaVersion(),
                userView.generatedAt(),
                userView.scope(),
                userView.headline(),
                userView.summary()
        );
    }

    public static ProjectAnalysisHighlightsResponse toHighlights(ProjectAnalysisUserViewResponse userView) {
        return new ProjectAnalysisHighlightsResponse(
                userView.strengths(),
                userView.risks()
        );
    }

    public static ProjectAnalysisAdviceResponse toAdvice(ProjectAnalysisUserViewResponse userView) {
        return new ProjectAnalysisAdviceResponse(userView.advice());
    }

    public static ProjectAnalysisScorecardResponse toScorecard(ProjectAnalysisUserViewResponse userView) {
        return new ProjectAnalysisScorecardResponse(userView.scorecard());
    }
}
