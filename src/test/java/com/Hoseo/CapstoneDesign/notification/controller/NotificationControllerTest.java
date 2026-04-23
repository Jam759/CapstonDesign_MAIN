package com.Hoseo.CapstoneDesign.notification.controller;

import com.Hoseo.CapstoneDesign.notification.dto.response.NotificationResponse;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.http.MediaType.APPLICATION_JSON;

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
    @DisplayName("GET /api/v1/notification returns notifications from facade")
    void getNotificationListReturnsFacadeList() throws Exception {
        when(facade.getNotification(any(), eq(1), eq(2))).thenReturn(List.of(
                NotificationResponse.builder()
                        .id(9001L)
                        .message("Latest analysis is complete.")
                        .type("PROJECT")
                        .linkId("102")
                        .build(),
                NotificationResponse.builder()
                        .id(9002L)
                        .message("New AI quests were created.")
                        .type("QUEST")
                        .linkId("3201")
                        .build()
        ));

        mockMvc.perform(get("/api/v1/notification")
                        .param("page", "1")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(9001))
                .andExpect(jsonPath("$[1].message").value("New AI quests were created."));
    }

    @Test
    @WithMockUserDetail
    @DisplayName("GET /api/v1/notification/unread returns unread notifications from facade")
    void getUnreadNotificationReturnsFacadeList() throws Exception {
        when(facade.getUnReadNotification(any())).thenReturn(List.of(
                NotificationResponse.builder()
                        .id(9001L)
                        .message("Latest analysis is complete.")
                        .type("PROJECT")
                        .linkId("102")
                        .build(),
                NotificationResponse.builder()
                        .id(9002L)
                        .message("New AI quests were created.")
                        .type("QUEST")
                        .linkId("3201")
                        .build()
        ));

        mockMvc.perform(get("/api/v1/notification/unread"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].type").value("PROJECT"))
                .andExpect(jsonPath("$[1].linkId").value("3201"));
    }

    @Test
    @WithMockUserDetail
    @DisplayName("PATCH /api/v1/notification delegates read request to facade")
    void markAsReadReturnsOk() throws Exception {
        mockMvc.perform(patch("/api/v1/notification")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "ids": [9001]
                                }
                                """))
                .andExpect(status().isOk());

        verify(facade).readNotifications(any(), eq(List.of(9001L)));
    }
}
