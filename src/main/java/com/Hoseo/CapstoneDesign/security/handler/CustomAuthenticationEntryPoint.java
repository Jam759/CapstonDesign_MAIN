package com.Hoseo.CapstoneDesign.security.handler;

import com.Hoseo.CapstoneDesign.auth.exception.AuthErrorCode;
import com.Hoseo.CapstoneDesign.global.exception.GlobalErrorCode;
import com.Hoseo.CapstoneDesign.global.exception.GlobalExceptionResponse;
import com.Hoseo.CapstoneDesign.global.logging.StructuredHttpLogger;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import static com.Hoseo.CapstoneDesign.global.logging.support.HttpEventType.HTTP_ERROR;
import static com.Hoseo.CapstoneDesign.global.logging.support.LogCategory.HTTP;
import static com.Hoseo.CapstoneDesign.global.logging.support.LoggingConstants.ERROR_ALREADY_LOGGED;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final StructuredHttpLogger structuredHttpLogger;

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException {
        logAuthFailure(request, authException, AuthErrorCode.UNAUTHORIZED);
        handleAuthException(response, AuthErrorCode.UNAUTHORIZED);
    }

    private void logAuthFailure(
            HttpServletRequest request,
            AuthenticationException authException,
            GlobalErrorCode errorCode
    ) {
        if (Boolean.TRUE.equals(request.getAttribute(ERROR_ALREADY_LOGGED))) {
            return;
        }

        request.setAttribute(ERROR_ALREADY_LOGGED, true);
        structuredHttpLogger.error(
                HTTP.name(),
                HTTP_ERROR.name(),
                structuredHttpLogger.resolveHandlerClassName(request, getClass().getSimpleName()),
                structuredHttpLogger.resolveHandlerMethodName(request, "commence"),
                "Authentication entry point invoked",
                Map.of("exception", authException.getClass().getSimpleName()),
                request,
                errorCode.getHttpStatus().value(),
                structuredHttpLogger.resolveDurationMs(request),
                structuredHttpLogger.toErrorInfo(errorCode)
        );
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
