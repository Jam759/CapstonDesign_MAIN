package com.Hoseo.CapstoneDesign.security.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        response.setContentType("application/json;charset=UTF-8");

        log.error("EntryPoint exception : uri={}, method={}, ex={}",
                request.getRequestURI(), request.getMethod(), authException.getClass().getName());
        // 기본 인증 실패
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("""
                    {
                        "code": 401,
                        "error": "UNAUTHORIZED",
                        "message": "인증이 필요합니다."
                    }
                """);

    }
}
