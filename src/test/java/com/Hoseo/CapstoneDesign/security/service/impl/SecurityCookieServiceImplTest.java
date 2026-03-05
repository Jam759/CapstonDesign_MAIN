package com.Hoseo.CapstoneDesign.security.service.impl;

import com.Hoseo.CapstoneDesign.security.properties.JwtProperties;
import com.Hoseo.CapstoneDesign.security.util.JwtUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletResponse;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class SecurityCookieServiceImplTest {

    private final JwtUtil jwtUtil = mock(JwtUtil.class);

    @DisplayName("createRefreshTokenCookie: refresh token 쿠키를 설정한다")
    @Test
    void createRefreshTokenCookie_success() {
        JwtProperties properties = properties();
        SecurityCookieServiceImpl service = new SecurityCookieServiceImpl(jwtUtil, properties);
        MockHttpServletResponse response = new MockHttpServletResponse();

        service.createRefreshTokenCookie(response, "refresh-token");

        String setCookie = response.getHeader("Set-Cookie");
        assertThat(setCookie).contains("REFRESH_TOKEN=refresh-token");
        assertThat(setCookie).contains("Path=/api/v1/auth");
        assertThat(setCookie).contains("HttpOnly");
        assertThat(setCookie).contains("SameSite=Lax");
    }

    @DisplayName("deleteRefreshTokenCookie: max-age 0으로 refresh token 쿠키를 삭제한다")
    @Test
    void deleteRefreshTokenCookie_success() {
        JwtProperties properties = properties();
        SecurityCookieServiceImpl service = new SecurityCookieServiceImpl(jwtUtil, properties);
        MockHttpServletResponse response = new MockHttpServletResponse();

        service.deleteRefreshTokenCookie(response);

        String setCookie = response.getHeader("Set-Cookie");
        assertThat(setCookie).contains("REFRESH_TOKEN=");
        assertThat(setCookie).contains("Max-Age=0");
        assertThat(setCookie).contains("Path=/api/v1/auth");
    }

    private JwtProperties properties() {
        return new JwtProperties(
                "issuer",
                "MDEyMzQ1Njc4OWFiY2RlZjAxMjM0NTY3ODlhYmNkZWY=",
                "MDEyMzQ1Njc4OWFiY2RlZjAxMjM0NTY3ODlhYmNkZWY=",
                15,
                7,
                "REFRESH_TOKEN",
                null,
                false,
                "Lax",
                true,
                URI.create("http://localhost:5500/index.html")
        );
    }
}

