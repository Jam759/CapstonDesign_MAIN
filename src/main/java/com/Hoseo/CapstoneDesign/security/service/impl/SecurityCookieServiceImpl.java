package com.Hoseo.CapstoneDesign.security.service.impl;

import com.Hoseo.CapstoneDesign.security.properties.JwtProperties;
import com.Hoseo.CapstoneDesign.security.service.SecurityCookieService;
import com.Hoseo.CapstoneDesign.security.util.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class SecurityCookieServiceImpl implements SecurityCookieService {

    private final JwtUtil jwtUtil;
    private final JwtProperties jwtProperties;

    @Override
    public void createRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        Duration ttl = jwtProperties.refreshTokenTtl();
        int maxAgeSeconds = (int) (ttl == null ? 0 : Math.max(0, ttl.getSeconds()));
        ResponseCookie cookie = ResponseCookie.from(jwtProperties.cookieName(), refreshToken)
                .httpOnly(jwtProperties.cookieHttpOnly())
                .secure(jwtProperties.cookieSecure())
                .path("/api/v1/auth")
                .maxAge(maxAgeSeconds)
                .sameSite(jwtProperties.cookieSamesite()) // 상황에 따라 "None" 또는 "Strict" 로 변경
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    @Override
    public void deleteRefreshTokenCookie(HttpServletResponse response) {
        // ResponseCookie 방식으로 삭제 (SameSite 포함)
        ResponseCookie cookie = ResponseCookie.from(jwtProperties.cookieName(), "")
                .httpOnly(jwtProperties.cookieHttpOnly())
                .secure(jwtProperties.cookieSecure())
                .path("/api/v1/auth")
                .maxAge(0)
                .sameSite(jwtProperties.cookieSamesite()) // 상황에 따라 "None" 또는 "Strict" 로 변경
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}
