package com.Hoseo.CapstoneDesign.security.handler;

import com.Hoseo.CapstoneDesign.auth.exception.AuthErrorCode;
import com.Hoseo.CapstoneDesign.global.exception.GlobalErrorCode;
import com.Hoseo.CapstoneDesign.global.exception.GlobalExceptionResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;

@Slf4j
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException {
        handleAuthException(response, AuthErrorCode.UNAUTHORIZED);
    }

    private void handleAuthException(HttpServletResponse response, GlobalErrorCode errorCode) throws IOException {
        response.setStatus(errorCode.getHttpStatus().value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        GlobalExceptionResponse body = new GlobalExceptionResponse(errorCode);
        String json = objectMapper.writeValueAsString(body);

        PrintWriter writer = response.getWriter();
        writer.write(json);
        writer.flush();
    }
}
