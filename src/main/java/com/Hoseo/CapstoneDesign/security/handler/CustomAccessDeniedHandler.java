package com.Hoseo.CapstoneDesign.security.handler;

import com.Hoseo.CapstoneDesign.auth.exception.AuthErrorCode;
import com.Hoseo.CapstoneDesign.global.exception.GlobalErrorCode;
import com.Hoseo.CapstoneDesign.global.exception.GlobalExceptionResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException
    ) throws IOException {
        handleAccessDenied(response, AuthErrorCode.FORBIDDEN);
    }

    private void handleAccessDenied(HttpServletResponse response, GlobalErrorCode errorCode) throws IOException {
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
