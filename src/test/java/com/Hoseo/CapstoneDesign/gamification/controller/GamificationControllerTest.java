package com.Hoseo.CapstoneDesign.gamification.controller;

import com.Hoseo.CapstoneDesign.gamification.facade.GamificationFacadeImpl;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GamificationController.class)
@Import(GamificationFacadeImpl.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class GamificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Test
    @WithMockUserDetail
    @DisplayName("GET /api/v1/gamification/xp/ranking 은 mock 랭킹 목록을 반환한다")
    void getRankingReturnsMockList() throws Exception {
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
    @DisplayName("GET /api/v1/gamification/xp 는 내 랭킹 mock 응답을 반환한다")
    void getMyRankReturnsMockResponse() throws Exception {
        mockMvc.perform(get("/api/v1/gamification/xp"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rank").value(7))
                .andExpect(jsonPath("$.serviceNickname").value("service-user"))
                .andExpect(jsonPath("$.totalExp").value(1280));
    }

    @Test
    @WithMockUserDetail
    @DisplayName("GET /api/v1/gamification/quests 는 상태별 mock 퀘스트를 반환한다")
    void getQuestResponseReturnsFilteredMockList() throws Exception {
        mockMvc.perform(get("/api/v1/gamification/quests")
                        .param("progressStatus", "ACTIVE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].progressStatus").value("ACTIVE"))
                .andExpect(jsonPath("$[0].rewardExp").value(120));
    }

    @Test
    @WithMockUserDetail
    @DisplayName("GET /api/v1/gamification/badges 는 mock 뱃지 목록을 반환한다")
    void getMyBadgesReturnsMockList() throws Exception {
        mockMvc.perform(get("/api/v1/gamification/badges"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].badgeName").value("첫 분석 완료"))
                .andExpect(jsonPath("$[1].badgeType").value("COLLABORATION"));
    }
}
