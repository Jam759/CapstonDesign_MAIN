package com.Hoseo.CapstoneDesign.auth.controller;

import com.Hoseo.CapstoneDesign.auth.exception.AuthErrorCode;
import com.Hoseo.CapstoneDesign.auth.facade.AuthFacade;
import com.Hoseo.CapstoneDesign.global.exception.GlobalExceptionHandler;
import com.Hoseo.CapstoneDesign.security.service.SecurityCookieService;
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

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import(GlobalExceptionHandler.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthFacade authFacade;

    @MockBean
    private SecurityCookieService securityCookieService;

    @MockBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Test
    @DisplayName("POST /api/v1/auth/reissue rejects missing refresh token cookie")
    void accessTokenReissueRejectsMissingRefreshTokenCookie() throws Exception {
        mockMvc.perform(post("/api/v1/auth/reissue"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errorCode").value(AuthErrorCode.UNAUTHORIZED.getErrorCode()))
                .andExpect(jsonPath("$.errorMessage").exists())
                .andExpect(jsonPath("$.httpStatus").value(AuthErrorCode.UNAUTHORIZED.getHttpStatus().name()));

        verify(authFacade, never()).accessTokenReissue(anyString());
    }
}
