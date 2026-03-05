package com.Hoseo.CapstoneDesign.security.service;

import jakarta.servlet.http.HttpServletResponse;

public interface SecurityCookieService {
    void createRefreshTokenCookie(HttpServletResponse response, String refreshToken);

    void deleteRefreshTokenCookie(HttpServletResponse response);
}
