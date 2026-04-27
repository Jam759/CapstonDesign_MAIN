package com.Hoseo.CapstoneDesign.user.controller;

import com.Hoseo.CapstoneDesign.global.exception.GlobalExceptionHandler;
import com.Hoseo.CapstoneDesign.support.factory.UserProfileUpdateRequestFactory;
import com.Hoseo.CapstoneDesign.support.fixture.auth.WithMockUserDetail;
import com.Hoseo.CapstoneDesign.user.dto.response.UpdateUserInfoResponse;
import com.Hoseo.CapstoneDesign.user.exception.CustomUserException;
import com.Hoseo.CapstoneDesign.user.exception.UserErrorCode;
import com.Hoseo.CapstoneDesign.user.facade.UserFacade;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import(GlobalExceptionHandler.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class UserControllerTest {

    private static final Logger log = LoggerFactory.getLogger(UserControllerTest.class);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserFacade userFacade;

    @MockBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Test
    @WithMockUserDetail
    @DisplayName("PATCH /api/v1/users/me succeeds and returns response body")
    void updateUserProfileSuccess() throws Exception {
        UpdateUserInfoResponse response = new UpdateUserInfoResponse(
                "new-service-nick",
                "Job",
                "Backend",
                List.of("Java", "React"),
                true,
                LocalDateTime.of(2026, 3, 12, 12, 0)
        );

        when(userFacade.updateUserProfile(any(), any())).thenReturn(response);

        var request = UserProfileUpdateRequestFactory.create(
                "new-service-nick",
                "Job",
                "Backend",
                List.of("Java", "React")
        );

        mockMvc.perform(patch("/api/v1/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.serviceNickname").value("new-service-nick"))
                .andExpect(jsonPath("$.goal").value("Job"))
                .andExpect(jsonPath("$.position").value("Backend"))
                .andExpect(jsonPath("$.techStacks[0]").value("Java"))
                .andExpect(jsonPath("$.profileComplete").value(true))
                .andExpect(jsonPath("$.updateDate").exists());

        verify(userFacade).updateUserProfile(any(), any());
        log.info("[TEST] controller success contract validated");
    }

    @Test
    @WithMockUserDetail
    @DisplayName("PATCH /api/v1/users/me failure returns GlobalExceptionResponse")
    void updateUserProfileFailureReturnsGlobalExceptionResponse() throws Exception {
        when(userFacade.updateUserProfile(any(), any()))
                .thenThrow(new CustomUserException(UserErrorCode.USER_NOT_FOUND_ERROR));

        var request = UserProfileUpdateRequestFactory.create("any", "Job", "Backend", List.of("Java"));

        mockMvc.perform(patch("/api/v1/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value(UserErrorCode.USER_NOT_FOUND_ERROR.getErrorCode()))
                .andExpect(jsonPath("$.errorMessage").exists())
                .andExpect(jsonPath("$.httpStatus").value(UserErrorCode.USER_NOT_FOUND_ERROR.getHttpStatus().name()));

        log.info("[TEST] controller error contract validated with GlobalExceptionResponse");
    }

    @Test
    @WithMockUserDetail
    @DisplayName("RequestBody binding passes request DTO to facade")
    void requestBodyBindingContract() throws Exception {
        when(userFacade.updateUserProfile(any(), any()))
                .thenReturn(new UpdateUserInfoResponse("bound", "Job", "Backend", List.of("Java"), true, LocalDateTime.now()));

        var request = UserProfileUpdateRequestFactory.create("bound", "Job", "Backend", List.of("Java"));

        mockMvc.perform(patch("/api/v1/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(userFacade).updateUserProfile(any(), any());
        log.info("[TEST] request body binding validated");
    }
}
