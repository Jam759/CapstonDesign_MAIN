package com.Hoseo.CapstoneDesign.analysis.facade;

import com.Hoseo.CapstoneDesign.analysis.dto.response.ProjectAnalysisAdviceResponse;
import com.Hoseo.CapstoneDesign.analysis.dto.response.ProjectAnalysisHighlightsResponse;
import com.Hoseo.CapstoneDesign.analysis.dto.response.ProjectAnalysisOverviewResponse;
import com.Hoseo.CapstoneDesign.analysis.dto.response.ProjectAnalysisScorecardResponse;
import com.Hoseo.CapstoneDesign.analysis.dto.application.ProjectAnalysisUserViewResponse;
import com.Hoseo.CapstoneDesign.analysis.factory.AnalysisSectionDtoFactory;
import com.Hoseo.CapstoneDesign.analysis.service.ProjectAnalysisReportService;
import com.Hoseo.CapstoneDesign.global.annotation.Facade;
import com.Hoseo.CapstoneDesign.project.exception.ProjectsErrorCode;
import com.Hoseo.CapstoneDesign.project.exception.ProjectsException;
import com.Hoseo.CapstoneDesign.project.service.ProjectMemberService;
import com.Hoseo.CapstoneDesign.project.service.ProjectService;
import com.Hoseo.CapstoneDesign.user.entity.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@Facade
@RequiredArgsConstructor
public class AnalysisFacadeImpl implements AnalysisFacade {

    private final ProjectService projectService;
    private final ProjectMemberService projectMemberService;
    private final ProjectAnalysisReportService projectAnalysisReportService;

    @Override
    @Transactional(readOnly = true)
    public ProjectAnalysisOverviewResponse getOverview(Users user, Long projectId, Integer version) {
        ProjectAnalysisUserViewResponse userView = getValidatedUserView(user, projectId, version);
        return AnalysisSectionDtoFactory.toOverview(userView);
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectAnalysisHighlightsResponse getHighlights(Users user, Long projectId, Integer version) {
        ProjectAnalysisUserViewResponse userView = getValidatedUserView(user, projectId, version);
        return AnalysisSectionDtoFactory.toHighlights(userView);
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectAnalysisAdviceResponse getAdvice(Users user, Long projectId, Integer version) {
        ProjectAnalysisUserViewResponse userView = getValidatedUserView(user, projectId, version);
        return AnalysisSectionDtoFactory.toAdvice(userView);
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectAnalysisScorecardResponse getScorecard(Users user, Long projectId, Integer version) {
        ProjectAnalysisUserViewResponse userView = getValidatedUserView(user, projectId, version);
        return AnalysisSectionDtoFactory.toScorecard(userView);
    }

    private ProjectAnalysisUserViewResponse getValidatedUserView(Users user, Long projectId, Integer version) {
        validateProjectAccess(user, projectId);
        return projectAnalysisReportService.getUserView(projectId, version);
    }

    private void validateProjectAccess(Users user, Long projectId) {
        projectService.getById(projectId);

        if (!projectMemberService.isAcceptedMember(projectId, user.getUserId())) {
            throw new ProjectsException(ProjectsErrorCode.PROJECT_FORBIDDEN);
        }
    }
}
