package com.Hoseo.CapstoneDesign.analysis.controller;

import com.Hoseo.CapstoneDesign.analysis.dto.response.ProjectAnalysisHighlightsResponse;
import com.Hoseo.CapstoneDesign.analysis.dto.response.ProjectAnalysisOverviewResponse;
import com.Hoseo.CapstoneDesign.analysis.dto.application.ProjectAnalysisUserViewResponse;
import com.Hoseo.CapstoneDesign.analysis.facade.AnalysisFacade;
import com.Hoseo.CapstoneDesign.global.exception.GlobalExceptionHandler;
import com.Hoseo.CapstoneDesign.project.exception.ProjectsErrorCode;
import com.Hoseo.CapstoneDesign.project.exception.ProjectsException;
import com.Hoseo.CapstoneDesign.support.fixture.auth.WithMockUserDetail;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AnalysisController.class)
@Import(GlobalExceptionHandler.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class AnalysisControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AnalysisFacade analysisFacade;

    @MockBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Test
    @WithMockUserDetail
    @DisplayName("GET /api/v1/analysis/{projectId}/overview returns the overview section")
    void getOverviewSuccess() throws Exception {
        when(analysisFacade.getOverview(any(), eq(1L), isNull()))
                .thenReturn(sampleOverviewResponse());

        mockMvc.perform(get("/api/v1/analysis/1/overview"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.headline").value("headline"))
                .andExpect(jsonPath("$.summary").value("summary"));

        verify(analysisFacade).getOverview(any(), eq(1L), isNull());
    }

    @Test
    @WithMockUserDetail
    @DisplayName("GET /api/v1/analysis/{projectId}/highlights?version=2 returns the merged highlights section")
    void getHighlightsByVersionSuccess() throws Exception {
        when(analysisFacade.getHighlights(any(), eq(1L), eq(2)))
                .thenReturn(sampleHighlightsResponse());

        mockMvc.perform(get("/api/v1/analysis/1/highlights").param("version", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.strengths[0]").value("strength"))
                .andExpect(jsonPath("$.risks[0]").value("risk"));

        verify(analysisFacade).getHighlights(any(), eq(1L), eq(2));
    }

    @Test
    @WithMockUserDetail
    @DisplayName("GET /api/v1/analysis/{projectId}/overview returns GlobalExceptionResponse when the user is not a member")
    void getOverviewForbidden() throws Exception {
        when(analysisFacade.getOverview(any(), eq(1L), isNull()))
                .thenThrow(new ProjectsException(ProjectsErrorCode.PROJECT_FORBIDDEN));

        mockMvc.perform(get("/api/v1/analysis/1/overview"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorCode").value(ProjectsErrorCode.PROJECT_FORBIDDEN.getErrorCode()))
                .andExpect(jsonPath("$.httpStatus").value(ProjectsErrorCode.PROJECT_FORBIDDEN.getHttpStatus().name()));
    }

    private ProjectAnalysisOverviewResponse sampleOverviewResponse() {
        return new ProjectAnalysisOverviewResponse(
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
                "summary"
        );
    }

    private ProjectAnalysisHighlightsResponse sampleHighlightsResponse() {
        return new ProjectAnalysisHighlightsResponse(
                List.of("strength"),
                List.of("risk")
        );
    }
}
