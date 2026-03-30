package com.Hoseo.CapstoneDesign.analysis.facade;

import com.Hoseo.CapstoneDesign.analysis.dto.response.ProjectAnalysisAdviceResponse;
import com.Hoseo.CapstoneDesign.analysis.dto.response.ProjectAnalysisHighlightsResponse;
import com.Hoseo.CapstoneDesign.analysis.dto.response.ProjectAnalysisOverviewResponse;
import com.Hoseo.CapstoneDesign.analysis.dto.response.ProjectAnalysisScorecardResponse;
import com.Hoseo.CapstoneDesign.user.entity.Users;

public interface AnalysisFacade {
    ProjectAnalysisOverviewResponse getOverview(Users user, Long projectId, Integer version);

    ProjectAnalysisHighlightsResponse getHighlights(Users user, Long projectId, Integer version);

    ProjectAnalysisAdviceResponse getAdvice(Users user, Long projectId, Integer version);

    ProjectAnalysisScorecardResponse getScorecard(Users user, Long projectId, Integer version);
}
