package com.Hoseo.CapstoneDesign.user.controller;

import com.Hoseo.CapstoneDesign.global.exception.GlobalExceptionHandler;
import com.Hoseo.CapstoneDesign.support.factory.UserProfileUpdateRequestFactory;
import com.Hoseo.CapstoneDesign.support.fixture.auth.WithMockUserDetail;
import com.Hoseo.CapstoneDesign.user.dto.response.UpdateUserInfoResponse;
import com.Hoseo.CapstoneDesign.user.exception.CustomUserException;
import com.Hoseo.CapstoneDesign.user.exception.UserErrorCode;
import com.Hoseo.CapstoneDesign.user.facade.UserFacade;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Set;

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

    @MockBean
    private UserFacade userFacade;

    @MockBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Test
    @WithMockUserDetail
    @DisplayName("PATCH /api/v1/users/me 성공 시 200과 응답 바디를 반환한다")
    void updateUserProfileSuccess() throws Exception {
        UpdateUserInfoResponse response = new UpdateUserInfoResponse(
                "new-service-nick",
                Set.of(101L, 102L),
                LocalDateTime.of(2026, 3, 12, 12, 0)
        );

        when(userFacade.updateUserProfile(any(), any())).thenReturn(response);

        mockMvc.perform(patch("/api/v1/users/me")
                        .param("userServiceNickname", "new-service-nick")
                        .param("equippedBadges", "101", "102"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.serviceNickname").value("new-service-nick"))
                .andExpect(jsonPath("$.equippedBadges.length()").value(2))
                .andExpect(jsonPath("$.updateDate").exists());

        verify(userFacade).updateUserProfile(any(), any());
        log.info("[TEST] controller success contract validated");
    }

    @Test
    @WithMockUserDetail
    @DisplayName("PATCH /api/v1/users/me 실패 시 GlobalExceptionResponse 포맷을 반환한다")
    void updateUserProfileFailureReturnsGlobalExceptionResponse() throws Exception {
        when(userFacade.updateUserProfile(any(), any()))
                .thenThrow(new CustomUserException(UserErrorCode.USER_NOT_FOUND_ERROR));

        mockMvc.perform(patch("/api/v1/users/me")
                        .param("userServiceNickname", "any")
                        .param("equippedBadges", "1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value(UserErrorCode.USER_NOT_FOUND_ERROR.getErrorCode()))
                .andExpect(jsonPath("$.errorMessage").exists())
                .andExpect(jsonPath("$.httpStatus").value(UserErrorCode.USER_NOT_FOUND_ERROR.getHttpStatus().name()));

        log.info("[TEST] controller error contract validated with GlobalExceptionResponse");
    }

    @Test
    @WithMockUserDetail
    @DisplayName("ModelAttribute 바인딩으로 facade에 요청 DTO가 전달된다")
    void modelAttributeBindingContract() throws Exception {
        when(userFacade.updateUserProfile(any(), any()))
                .thenReturn(new UpdateUserInfoResponse("bound", Set.of(1L), LocalDateTime.now()));

        mockMvc.perform(patch("/api/v1/users/me")
                        .param("userServiceNickname", UserProfileUpdateRequestFactory.create("bound", Set.of(1L)).userServiceNickname())
                        .param("equippedBadges", "1"))
                .andExpect(status().isOk());

        verify(userFacade).updateUserProfile(any(), any());
        log.info("[TEST] model attribute binding validated");
    }
}
