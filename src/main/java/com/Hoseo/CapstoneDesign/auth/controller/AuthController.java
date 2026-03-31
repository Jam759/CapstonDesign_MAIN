package com.Hoseo.CapstoneDesign.auth.controller;

import com.Hoseo.CapstoneDesign.auth.dto.application.TokenPair;
import com.Hoseo.CapstoneDesign.auth.dto.response.AccessTokenReissueResponse;
import com.Hoseo.CapstoneDesign.auth.facade.AuthFacade;
import com.Hoseo.CapstoneDesign.auth.factory.AuthDtoFactory;
import com.Hoseo.CapstoneDesign.global.exception.GlobalExceptionResponse;
import com.Hoseo.CapstoneDesign.security.entity.UserDetailImpl;
import com.Hoseo.CapstoneDesign.security.service.SecurityCookieService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Tag(name = "Auth", description = "인증 관련 API")
public class AuthController {

    private final AuthFacade authFacade;
    private final SecurityCookieService securityCookieService;

    @PostMapping("/auth/reissue")
    @Operation(summary = "Access Token 재발급", description = "REFRESH_TOKEN 쿠키를 이용해 Access Token을 재발급합니다.")
    @SecurityRequirements
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "재발급 성공",
                    content = @Content(schema = @Schema(implementation = AccessTokenReissueResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Refresh Token이 없거나 유효하지 않음",
                    content = @Content(schema = @Schema(implementation = GlobalExceptionResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Refresh Token 저장 정보를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = GlobalExceptionResponse.class))
            )
    })
    public ResponseEntity<AccessTokenReissueResponse> accessTokenReissue(
            @Parameter(description = "Refresh Token 쿠키 값", example = "eyJhbGciOiJIUzI1NiJ9...")
            @CookieValue(name = "REFRESH_TOKEN", required = false) String refreshTokenRaw,
            @Parameter(hidden = true)
            HttpServletResponse response
    ) {
        TokenPair tokenPair = authFacade.accessTokenReissue(refreshTokenRaw);
        securityCookieService.createRefreshTokenCookie(response, tokenPair.refreshToken());
        AccessTokenReissueResponse responseDto = AuthDtoFactory.toAccessTokenReissueResponse(tokenPair);
        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/auth/logout")
    @Operation(summary = "로그아웃", description = "Access Token 블랙리스트 처리와 Refresh Token 쿠키 삭제를 수행합니다.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "로그아웃 성공"),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(schema = @Schema(implementation = GlobalExceptionResponse.class))
            )
    })
    public ResponseEntity<Void> logout(
            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetailImpl userDetail,
            @Parameter(hidden = true)
            Authentication authentication,
            @Parameter(description = "Refresh Token 쿠키 값")
            @CookieValue(name = "REFRESH_TOKEN", required = false) String refreshTokenRaw,
            @Parameter(hidden = true)
            HttpServletResponse response
    ) {
        String rawAccessToken = authentication == null
                ? null
                : String.valueOf(authentication.getCredentials());
        authFacade.logout(userDetail.getUser(), rawAccessToken, refreshTokenRaw);
        securityCookieService.deleteRefreshTokenCookie(response);
        return ResponseEntity.noContent().build();
    }
}
