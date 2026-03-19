package com.Hoseo.CapstoneDesign.security.handler;

import com.Hoseo.CapstoneDesign.github.exception.GitHubErrorCode;
import com.Hoseo.CapstoneDesign.github.exception.GitHubException;
import com.Hoseo.CapstoneDesign.global.util.TimeUtil;
import com.Hoseo.CapstoneDesign.security.properties.JwtProperties;
import com.Hoseo.CapstoneDesign.security.service.RefreshTokenService;
import com.Hoseo.CapstoneDesign.security.service.SecurityCookieService;
import com.Hoseo.CapstoneDesign.security.util.JwtUtil;
import com.Hoseo.CapstoneDesign.user.entity.Users;
import com.Hoseo.CapstoneDesign.user.entity.enums.OauthType;
import com.Hoseo.CapstoneDesign.user.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.Duration;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class GithubOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final JwtProperties jwtProperties;
    private final UserService userService;
    private final RefreshTokenService refreshTokenService;
    private final SecurityCookieService securityCookieService;

    @Override
    @Transactional(readOnly = false)
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        // GitHub 기본 userInfo(/user)에서 오는 키: id, login 등
        Object idObj = oAuth2User.getAttribute("id");
        Object loginObj = oAuth2User.getAttribute("login");

        if (idObj == null || loginObj == null) {
            GitHubErrorCode ec = GitHubErrorCode.GIT_HUB_NOT_FOUND_USER;
            throw new GitHubException(ec);
        }

        String githubProviderId = String.valueOf(idObj); // oauth_provider_id (문자열)
        String githubLogin = String.valueOf(loginObj);   // oauth_nickname

        Users user = userService.getOrCreateOauthUser(OauthType.GITHUB, githubProviderId, githubLogin);
        String refreshToken = refreshTokenService.createAndSaveInitial(user);
        Date refreshTokenExpDate = jwtUtil.getExpirationFromRefreshToken(refreshToken);
        Duration refreshTokenExpDuration = TimeUtil.toDuration(refreshTokenExpDate);

        ResponseCookie.ResponseCookieBuilder cb = ResponseCookie.from(jwtProperties.cookieName(), refreshToken)
                .httpOnly(true)
                .secure(jwtProperties.cookieSecure())
                .path("/api/v1/auth/refresh")
                .sameSite(jwtProperties.cookieSamesite())
                .maxAge(refreshTokenExpDuration);

        if (jwtProperties.cookieDomain() != null && !jwtProperties.cookieDomain().isBlank()) {
            cb.domain(jwtProperties.cookieDomain());
        }
        securityCookieService.createRefreshTokenCookie(response, refreshToken);
        response.sendRedirect(jwtProperties.frontRedirectUrl().toString());
    }
}
