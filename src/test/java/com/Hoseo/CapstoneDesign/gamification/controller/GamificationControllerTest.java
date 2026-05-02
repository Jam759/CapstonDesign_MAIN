package com.Hoseo.CapstoneDesign.gamification.controller;

import com.Hoseo.CapstoneDesign.gamification.dto.response.QuestResponse;
import com.Hoseo.CapstoneDesign.gamification.dto.response.RankingResponse;
import com.Hoseo.CapstoneDesign.gamification.entity.enums.AiQuestProgressStatus;
import com.Hoseo.CapstoneDesign.gamification.facade.GamificationFacade;
import com.Hoseo.CapstoneDesign.support.fixture.auth.WithMockUserDetail;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GamificationController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class GamificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GamificationFacade facade;

    @MockBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Test
    @WithMockUserDetail
    @DisplayName("GET /api/v1/gamification/xp/ranking returns ranking list")
    void getRankingReturnsMockList() throws Exception {
        when(facade.getRanking(eq(1), eq(3))).thenReturn(List.of(
                RankingResponse.builder()
                        .rank(1L)
                        .serviceNickname("commit-master")
                        .totalExp(4820L)
                        .build(),
                RankingResponse.builder()
                        .rank(2L)
                        .serviceNickname("refactor-ace")
                        .totalExp(4210L)
                        .build(),
                RankingResponse.builder()
                        .rank(3L)
                        .serviceNickname("test-runner")
                        .totalExp(4010L)
                        .build()
        ));

        mockMvc.perform(get("/api/v1/gamification/xp/ranking")
                        .param("page", "1")
                        .param("size", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].rank").value(1))
                .andExpect(jsonPath("$[0].serviceNickname").value("commit-master"))
                .andExpect(jsonPath("$[2].totalExp").value(4010));
    }

    @Test
    @WithMockUserDetail
    @DisplayName("GET /api/v1/gamification/xp returns my rank")
    void getMyRankReturnsMockResponse() throws Exception {
        when(facade.getMyRank(any())).thenReturn(RankingResponse.builder()
                .rank(7L)
                .serviceNickname("service-user")
                .totalExp(1280L)
                .build());

        mockMvc.perform(get("/api/v1/gamification/xp"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rank").value(7))
                .andExpect(jsonPath("$.serviceNickname").value("service-user"))
                .andExpect(jsonPath("$.totalExp").value(1280));
    }

    @Test
    @WithMockUserDetail
    @DisplayName("GET /api/v1/gamification/quests returns quests filtered by status")
    void getQuestResponseReturnsFilteredMockList() throws Exception {
        when(facade.getMyQuest(any(), eq(1L), eq(AiQuestProgressStatus.ACTIVE), isNull(), eq(1), eq(8)))
                .thenReturn(List.of(QuestResponse.builder()
                        .progressStatus(AiQuestProgressStatus.ACTIVE)
                        .rewardExp((short) 120)
                        .build()));

        mockMvc.perform(get("/api/v1/gamification/quests")
                        .param("projectId", "1")
                        .param("progressStatus", "ACTIVE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].progressStatus").value("ACTIVE"))
                .andExpect(jsonPath("$[0].rewardExp").value(120));
    }
}
