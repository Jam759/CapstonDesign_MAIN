package com.Hoseo.CapstoneDesign.analysis.facade;

import com.Hoseo.CapstoneDesign.analysis.dto.response.ProjectAnalysisHighlightsResponse;
import com.Hoseo.CapstoneDesign.analysis.dto.response.ProjectAnalysisOverviewResponse;
import com.Hoseo.CapstoneDesign.analysis.dto.application.ProjectAnalysisUserViewResponse;
import com.Hoseo.CapstoneDesign.analysis.service.ProjectAnalysisReportService;
import com.Hoseo.CapstoneDesign.project.entity.Projects;
import com.Hoseo.CapstoneDesign.project.exception.ProjectsErrorCode;
import com.Hoseo.CapstoneDesign.project.exception.ProjectsException;
import com.Hoseo.CapstoneDesign.project.service.ProjectMemberService;
import com.Hoseo.CapstoneDesign.project.service.ProjectService;
import com.Hoseo.CapstoneDesign.support.builder.UsersTestBuilder;
import com.Hoseo.CapstoneDesign.user.entity.Users;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnalysisFacadeImplTest {

    @Mock
    private ProjectService projectService;

    @Mock
    private ProjectMemberService projectMemberService;

    @Mock
    private ProjectAnalysisReportService projectAnalysisReportService;

    private AnalysisFacadeImpl facade;

    @BeforeEach
    void setUp() {
        facade = new AnalysisFacadeImpl(projectService, projectMemberService, projectAnalysisReportService);
    }

    @Test
    @DisplayName("returns the overview section when the user is an accepted project member")
    void getOverviewSuccess() {
        Users user = UsersTestBuilder.defaultUser().userId(1L).build();
        when(projectService.getById(1L)).thenReturn(Projects.builder().projectId(1L).build());
        when(projectMemberService.isAcceptedMember(1L, 1L)).thenReturn(true);
        when(projectAnalysisReportService.getUserView(1L, 2)).thenReturn(sampleUserView());

        ProjectAnalysisOverviewResponse response = facade.getOverview(user, 1L, 2);

        assertThat(response.headline()).isEqualTo("headline");
        assertThat(response.summary()).isEqualTo("summary");
    }

    @Test
    @DisplayName("returns merged highlights when the user is an accepted project member")
    void getHighlightsSuccess() {
        Users user = UsersTestBuilder.defaultUser().userId(1L).build();
        when(projectService.getById(1L)).thenReturn(Projects.builder().projectId(1L).build());
        when(projectMemberService.isAcceptedMember(1L, 1L)).thenReturn(true);
        when(projectAnalysisReportService.getUserView(1L, 2)).thenReturn(sampleUserView());

        ProjectAnalysisHighlightsResponse response = facade.getHighlights(user, 1L, 2);

        assertThat(response.strengths()).containsExactly("strength");
        assertThat(response.risks()).containsExactly("risk");
    }

    @Test
    @DisplayName("throws PROJECT_FORBIDDEN when the user does not belong to the project")
    void getOverviewForbidden() {
        Users user = UsersTestBuilder.defaultUser().userId(1L).build();
        when(projectService.getById(1L)).thenReturn(Projects.builder().projectId(1L).build());
        when(projectMemberService.isAcceptedMember(1L, 1L)).thenReturn(false);

        assertThatThrownBy(() -> facade.getOverview(user, 1L, null))
                .isInstanceOf(ProjectsException.class)
                .extracting("errorCode")
                .isEqualTo(ProjectsErrorCode.PROJECT_FORBIDDEN);

        verifyNoInteractions(projectAnalysisReportService);
    }

    private ProjectAnalysisUserViewResponse sampleUserView() {
        return new ProjectAnalysisUserViewResponse(
                "1.0.0",
                "2026-03-27T16:45:03.585Z",
                new ProjectAnalysisUserViewResponse.Scope(
                        1L,
                        1L,
                        "Jam759/CapstonDesign_Worker",
                        "main",
                        "before-hash",
                        "after-hash"
                ),
                "headline",
                "summary",
                List.of("strength"),
                List.of("risk"),
                List.of(
                        new ProjectAnalysisUserViewResponse.Advice(
                                "advice-id",
                                "high",
                                "maintainability",
                                "title",
                                "body",
                                "recommended-action",
                                "expected-impact"
                        )
                ),
                new ProjectAnalysisUserViewResponse.Scorecard(
                        new ProjectAnalysisUserViewResponse.Overall(78, "B", "medium"),
                        List.of(
                                new ProjectAnalysisUserViewResponse.Category(
                                        "architecture",
                                        "structure",
                                        84,
                                        "reason",
                                        List.of("evidence")
                                )
                        )
                )
        );
    }
}
