package com.Hoseo.CapstoneDesign.scenario.auth;

import com.Hoseo.CapstoneDesign.auth.exception.AuthErrorCode;
import com.Hoseo.CapstoneDesign.notification.listener.NotificationQueueListener;
import com.Hoseo.CapstoneDesign.support.factory.UserProfileUpdateRequestFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserAuthenticationScenarioTest {

    private static final Logger log = LoggerFactory.getLogger(UserAuthenticationScenarioTest.class);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private NotificationQueueListener notificationQueueListener;

    @Test
    @DisplayName("인증 없이 사용자 수정 API 호출 시 401 + GlobalExceptionResponse를 반환한다")
    void updateUserWithoutAuthenticationFailsWithGlobalExceptionResponse() throws Exception {
        var request = UserProfileUpdateRequestFactory.create(
                "unauthorized",
                null,
                "Job",
                "Backend",
                List.of("Java")
        );

        mockMvc.perform(patch("/api/v1/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errorCode").value(AuthErrorCode.UNAUTHORIZED.getErrorCode()))
                .andExpect(jsonPath("$.errorMessage").exists())
                .andExpect(jsonPath("$.httpStatus").value("UNAUTHORIZED"));

        log.info("[TEST] authentication failure scenario validated");
    }
}
