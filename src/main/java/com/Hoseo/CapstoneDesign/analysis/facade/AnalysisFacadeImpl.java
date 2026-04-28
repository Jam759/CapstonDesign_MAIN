package com.Hoseo.CapstoneDesign.analysis.facade;

import com.Hoseo.CapstoneDesign.analysis.dto.response.ProjectAnalysisAdviceResponse;
import com.Hoseo.CapstoneDesign.analysis.dto.response.ProjectAnalysisHighlightsResponse;
import com.Hoseo.CapstoneDesign.analysis.dto.response.ProjectAnalysisOverviewResponse;
import com.Hoseo.CapstoneDesign.analysis.dto.response.ProjectAnalysisScorecardResponse;
import com.Hoseo.CapstoneDesign.analysis.dto.application.ProjectAnalysisUserViewResponse;
import com.Hoseo.CapstoneDesign.analysis.dto.response.ProjectRoadMapResponse;
import com.Hoseo.CapstoneDesign.analysis.factory.AnalysisSectionDtoFactory;
import com.Hoseo.CapstoneDesign.analysis.service.AnalysisJobService;
import com.Hoseo.CapstoneDesign.analysis.service.ProjectAnalysisReportService;
import com.Hoseo.CapstoneDesign.analysis.service.ProjectRoadMapService;
import com.Hoseo.CapstoneDesign.global.annotation.Facade;
import com.Hoseo.CapstoneDesign.project.exception.ProjectsErrorCode;
import com.Hoseo.CapstoneDesign.project.exception.ProjectsException;
import com.Hoseo.CapstoneDesign.project.service.ProjectMemberService;
import com.Hoseo.CapstoneDesign.project.service.ProjectService;
import com.Hoseo.CapstoneDesign.security.cache.dto.AuthenticatedUserCacheEntry;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@Facade
@RequiredArgsConstructor
public class AnalysisFacadeImpl implements AnalysisFacade {

    private final ProjectService projectService;
    private final ProjectMemberService projectMemberService;
    private final ProjectAnalysisReportService projectAnalysisReportService;
    private final ProjectRoadMapService projectRoadMapService;
    private final AnalysisJobService analysisJobService;

    @Override
    @Transactional(readOnly = true)
    public ProjectAnalysisOverviewResponse getOverview(AuthenticatedUserCacheEntry user, Long projectId, Integer version) {
        ProjectAnalysisUserViewResponse userView = getValidatedUserView(user, projectId, version);
        return AnalysisSectionDtoFactory.toOverview(userView);
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectAnalysisHighlightsResponse getHighlights(AuthenticatedUserCacheEntry user, Long projectId, Integer version) {
        ProjectAnalysisUserViewResponse userView = getValidatedUserView(user, projectId, version);
        return AnalysisSectionDtoFactory.toHighlights(userView);
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectAnalysisAdviceResponse getAdvice(AuthenticatedUserCacheEntry user, Long projectId, Integer version) {
        ProjectAnalysisUserViewResponse userView = getValidatedUserView(user, projectId, version);
        return AnalysisSectionDtoFactory.toAdvice(userView);
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectAnalysisScorecardResponse getScorecard(AuthenticatedUserCacheEntry user, Long projectId, Integer version) {
        ProjectAnalysisUserViewResponse userView = getValidatedUserView(user, projectId, version);
        return AnalysisSectionDtoFactory.toScorecard(userView);
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectRoadMapResponse getRoadMap(AuthenticatedUserCacheEntry user, Long projectId) {
        return projectRoadMapService.getRoadMap(projectId, user.userId());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isAnalysisReady(AuthenticatedUserCacheEntry user, Long projectId) {
        validateProjectAccess(user, projectId);
        return analysisJobService.hasCompletedAnalysis(projectId);
    }

    private ProjectAnalysisUserViewResponse getValidatedUserView(AuthenticatedUserCacheEntry user, Long projectId, Integer version) {
        validateProjectAccess(user, projectId);
        return projectAnalysisReportService.getUserView(projectId, version);
    }

    private void validateProjectAccess(AuthenticatedUserCacheEntry user, Long projectId) {
        projectService.getById(projectId);

        if (!projectMemberService.isAcceptedMember(projectId, user.userId())) {
            throw new ProjectsException(ProjectsErrorCode.PROJECT_FORBIDDEN);
        }
    }
}
