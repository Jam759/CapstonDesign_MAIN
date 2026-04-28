package com.Hoseo.CapstoneDesign.analysis.facade;

import com.Hoseo.CapstoneDesign.analysis.dto.response.ProjectAnalysisAdviceResponse;
import com.Hoseo.CapstoneDesign.analysis.dto.response.ProjectAnalysisHighlightsResponse;
import com.Hoseo.CapstoneDesign.analysis.dto.response.ProjectAnalysisOverviewResponse;
import com.Hoseo.CapstoneDesign.analysis.dto.response.ProjectAnalysisScorecardResponse;
import com.Hoseo.CapstoneDesign.analysis.dto.response.ProjectRoadMapResponse;
import com.Hoseo.CapstoneDesign.security.cache.dto.AuthenticatedUserCacheEntry;

public interface AnalysisFacade {
    ProjectAnalysisOverviewResponse getOverview(AuthenticatedUserCacheEntry user, Long projectId, Integer version);

    ProjectAnalysisHighlightsResponse getHighlights(AuthenticatedUserCacheEntry user, Long projectId, Integer version);

    ProjectAnalysisAdviceResponse getAdvice(AuthenticatedUserCacheEntry user, Long projectId, Integer version);

    ProjectAnalysisScorecardResponse getScorecard(AuthenticatedUserCacheEntry user, Long projectId, Integer version);

    ProjectRoadMapResponse getRoadMap(AuthenticatedUserCacheEntry user, Long projectId);

    boolean isAnalysisReady(AuthenticatedUserCacheEntry user, Long projectId);
}
