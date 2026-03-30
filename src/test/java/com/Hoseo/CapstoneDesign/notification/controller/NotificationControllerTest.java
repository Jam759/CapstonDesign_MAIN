package com.Hoseo.CapstoneDesign.notification.controller;

import com.Hoseo.CapstoneDesign.notification.facade.NotificationFacade;
import com.Hoseo.CapstoneDesign.notification.service.NotificationSseService;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NotificationController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationSseService notificationSseService;

    @MockBean
    private NotificationFacade facade;

    @MockBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Test
    @WithMockUserDetail
    @DisplayName("GET /api/notification 는 페이지 크기만큼 mock 알림을 반환한다")
    void getNotificationListReturnsPaginatedMockList() throws Exception {
        mockMvc.perform(get("/api/notification")
                        .param("page", "1")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].notificationId").value(9001))
                .andExpect(jsonPath("$[1].title").value("퀘스트 갱신"));
    }

    @Test
    @WithMockUserDetail
    @DisplayName("GET /api/notification/unread 는 읽지 않은 mock 알림 목록을 반환한다")
    void getUnreadNotificationReturnsMockList() throws Exception {
        mockMvc.perform(get("/api/notification/unread"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].linkType").value("PROJECT"))
                .andExpect(jsonPath("$[1].linkId").value("3201"));
    }

    @Test
    @WithMockUserDetail
    @DisplayName("PATCH /api/notification 는 읽음 처리 mock 응답으로 200을 반환한다")
    void markAsReadReturnsOk() throws Exception {
        mockMvc.perform(patch("/api/notification")
                        .param("notificationId", "9001"))
                .andExpect(status().isOk());
    }
}
