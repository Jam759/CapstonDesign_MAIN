package com.Hoseo.CapstoneDesign.project.controller;

import com.Hoseo.CapstoneDesign.project.dto.response.ProjectInviteStatusResponse;
import com.Hoseo.CapstoneDesign.project.dto.response.ProjectInviteResponse;
import com.Hoseo.CapstoneDesign.project.dto.response.ProjectThumbnailResponse;
import com.Hoseo.CapstoneDesign.project.entity.enums.ProjectInviteStatus;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
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
    @DisplayName("GET /api/v1/projects returns project thumbnails from facade")
    void getMyProjectReturnsFacadeList() throws Exception {
        when(facade.getMyProject(any())).thenReturn(List.of(
                ProjectThumbnailResponse.builder()
                        .projectId(101L)
                        .id(101L)
                        .title("Algorithm Study")
                        .name("Algorithm Study")
                        .description("Project for tracking weekly algorithm practice")
                        .build(),
                ProjectThumbnailResponse.builder()
                        .projectId(102L)
                        .id(102L)
                        .title("Capstone Design")
                        .name("Capstone Design")
                        .description("Main GitHub analysis project")
                        .build(),
                ProjectThumbnailResponse.builder()
                        .projectId(103L)
                        .id(103L)
                        .title("TypeScript Practice")
                        .name("TypeScript Practice")
                        .description("Personal project for TypeScript exercises")
                        .build()
        ));

        mockMvc.perform(get("/api/v1/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].projectId").value(101))
                .andExpect(jsonPath("$[0].id").value(101))
                .andExpect(jsonPath("$[0].name").value("Algorithm Study"))
                .andExpect(jsonPath("$[2].description").value("Personal project for TypeScript exercises"));
    }

    @Test
    @WithMockUserDetail
    @DisplayName("POST /api/v1/projects/members returns invite response from facade")
    void inviteProjectReturnsFacadeResponse() throws Exception {
        when(facade.inviteProject(any(), any())).thenReturn(List.of(
                new ProjectInviteResponse(1L, 3001L, ProjectInviteStatus.INVITED)
        ));

        mockMvc.perform(post("/api/v1/projects/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "projectId": 201,
                                  "friendIds": [3001]
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].projectMemberId").value(1))
                .andExpect(jsonPath("$[0].invitedUserId").value(3001))
                .andExpect(jsonPath("$[0].status").value("INVITED"));
    }

    @Test
    @WithMockUserDetail
    @DisplayName("PATCH /api/v1/projects/member returns invite decision from facade")
    void responseInviteReturnsFacadeResponse() throws Exception {
        when(facade.responseInvite(any(), any())).thenReturn(
                new ProjectInviteResponse(2L, 2001L, ProjectInviteStatus.DECLINED)
        );

        mockMvc.perform(patch("/api/v1/projects/member")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "inviteId": 2,
                                  "accepted": false
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.projectMemberId").value(2))
                .andExpect(jsonPath("$.invitedUserId").value(2001))
                .andExpect(jsonPath("$.status").value("DECLINED"));
    }

    @Test
    @WithMockUserDetail
    @DisplayName("GET /api/v1/projects/member returns invite list from facade")
    void getMyInvitedListReturnsFacadeList() throws Exception {
        when(facade.getMyInvitedList(any())).thenReturn(List.of(
                ProjectInviteStatusResponse.builder()
                        .id(201L)
                        .from("Alice")
                        .projectName("Algorithm Study")
                        .status("INVITED")
                        .build(),
                ProjectInviteStatusResponse.builder()
                        .id(202L)
                        .from("Bob")
                        .projectName("Capstone Design")
                        .status("ACCEPTED")
                        .build(),
                ProjectInviteStatusResponse.builder()
                        .id(203L)
                        .from("Charlie")
                        .projectName("TypeScript Practice")
                        .status("DECLINED")
                        .build()
        ));

        mockMvc.perform(get("/api/v1/projects/member"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].id").value(201))
                .andExpect(jsonPath("$[0].projectName").value("Algorithm Study"))
                .andExpect(jsonPath("$[0].status").value("INVITED"))
                .andExpect(jsonPath("$[2].status").value("DECLINED"));
    }
}
