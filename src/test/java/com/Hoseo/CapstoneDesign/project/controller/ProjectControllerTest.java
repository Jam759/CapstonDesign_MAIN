package com.Hoseo.CapstoneDesign.project.controller;

import com.Hoseo.CapstoneDesign.project.facade.ProjectFacade;
import com.Hoseo.CapstoneDesign.support.fixture.auth.WithMockUserDetail;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProjectController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProjectFacade facade;

    @MockBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Test
    @WithMockUserDetail
    @DisplayName("GET /api/v1/projects 는 프로젝트 썸네일 mock 목록을 반환한다")
    void getMyProjectReturnsMockList() throws Exception {
        mockMvc.perform(get("/api/v1/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].projectId").value(101))
                .andExpect(jsonPath("$[0].title").value("알고리즘 스터디"))
                .andExpect(jsonPath("$[2].description").value("언어 학습용 미니 미션과 실습 코드를 모아둔 프로젝트"));
    }

    @Test
    @WithMockUserDetail
    @DisplayName("POST /api/v1/projects/members 는 초대 mock 응답을 반환한다")
    void inviteProjectReturnsMockResponse() throws Exception {
        mockMvc.perform(post("/api/v1/projects/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "projectId": 201,
                                  "inviteMemberId": 3001
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.projectMemberId").value(23101))
                .andExpect(jsonPath("$.invitedUserId").value(3001))
                .andExpect(jsonPath("$.status").value("INVITED"));
    }

    @Test
    @WithMockUserDetail
    @DisplayName("PATCH /api/v1/projects/member 는 초대 응답 mock 데이터를 반환한다")
    void responseInviteReturnsMockResponse() throws Exception {
        mockMvc.perform(patch("/api/v1/projects/member")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "projectId": 202,
                                  "responseStatus": "DECLINED"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.projectMemberId").value(22201))
                .andExpect(jsonPath("$.invitedUserId").value(2001))
                .andExpect(jsonPath("$.status").value("DECLINED"));
    }

    @Test
    @WithMockUserDetail
    @DisplayName("GET /api/v1/projects/member 는 초대 상태 mock 목록을 반환한다")
    void getMyInvitedListReturnsMockList() throws Exception {
        mockMvc.perform(get("/api/v1/projects/member"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].projectId").value(201))
                .andExpect(jsonPath("$[0].status").value("INVITED"))
                .andExpect(jsonPath("$[2].status").value("DECLINED"));
    }
}
