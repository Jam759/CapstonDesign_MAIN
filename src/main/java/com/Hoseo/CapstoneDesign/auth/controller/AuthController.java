package com.Hoseo.CapstoneDesign.auth.controller;

import com.Hoseo.CapstoneDesign.auth.dto.application.TokenPair;
import com.Hoseo.CapstoneDesign.auth.dto.response.AccessTokenReissueResponse;
import com.Hoseo.CapstoneDesign.auth.facade.AuthFacade;
import com.Hoseo.CapstoneDesign.auth.factory.AuthDtoFactory;
import com.Hoseo.CapstoneDesign.security.service.SecurityCookieService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class AuthController {

    private final AuthFacade authFacade;
    private final SecurityCookieService securityCookieService;

    @PostMapping("/auth/reissue")
    public ResponseEntity<AccessTokenReissueResponse> accessTokenReissue(
            @CookieValue(name = "REFRESH_TOKEN", required = false) String refreshTokenRaw,
            HttpServletResponse response
    ) {
        TokenPair tokenPair = authFacade.accessTokenReissue(refreshTokenRaw);
        securityCookieService.createRefreshTokenCookie(response, tokenPair.refreshToken());
        AccessTokenReissueResponse responseDto = AuthDtoFactory.toAccessTokenReissueResponse(tokenPair);
        return ResponseEntity.ok(responseDto);
    }

}
