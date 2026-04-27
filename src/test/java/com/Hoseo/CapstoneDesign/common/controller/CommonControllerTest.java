package com.Hoseo.CapstoneDesign.common.controller;

import com.Hoseo.CapstoneDesign.global.exception.GlobalExceptionHandler;
import com.Hoseo.CapstoneDesign.common.service.CommonGroupDetailService;
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

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommonController.class)
@Import(GlobalExceptionHandler.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class CommonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommonGroupDetailService commonGroupDetailService;

    @MockBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Test
    @DisplayName("공통 기술 스택 코드를 조회한다")
    void getTechStacks() throws Exception {
        when(commonGroupDetailService.getProjectTechStackIds()).thenReturn(List.of("Java", "React"));

        mockMvc.perform(get("/api/v1/common/tech-stacks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("Java"))
                .andExpect(jsonPath("$[1]").value("React"));
    }

    @Test
    @DisplayName("공통 포지션 코드를 조회한다")
    void getPositions() throws Exception {
        when(commonGroupDetailService.getProjectPositionIds()).thenReturn(List.of("Backend", "Frontend"));

        mockMvc.perform(get("/api/v1/common/positions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("Backend"))
                .andExpect(jsonPath("$[1]").value("Frontend"));
    }

    @Test
    @DisplayName("유저 목표 공통 코드를 조회한다")
    void getGoals() throws Exception {
        when(commonGroupDetailService.getUserGoalIds()).thenReturn(List.of("Job", "Leadership"));

        mockMvc.perform(get("/api/v1/common/goals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("Job"))
                .andExpect(jsonPath("$[1]").value("Leadership"));
    }
}
