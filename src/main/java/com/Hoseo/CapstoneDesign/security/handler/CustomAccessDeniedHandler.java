package com.Hoseo.CapstoneDesign.security.handler;

import com.Hoseo.CapstoneDesign.auth.exception.AuthErrorCode;
import com.Hoseo.CapstoneDesign.global.exception.GlobalErrorCode;
import com.Hoseo.CapstoneDesign.global.exception.GlobalExceptionResponse;
import com.Hoseo.CapstoneDesign.global.logging.StructuredHttpLogger;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import static com.Hoseo.CapstoneDesign.global.logging.support.HttpEventType.HTTP_ERROR;
import static com.Hoseo.CapstoneDesign.global.logging.support.LogCategory.HTTP;
import static com.Hoseo.CapstoneDesign.global.logging.support.LoggingConstants.ERROR_ALREADY_LOGGED;

@Component
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final StructuredHttpLogger structuredHttpLogger;

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException
    ) throws IOException {
        logAccessDenied(request, accessDeniedException, AuthErrorCode.FORBIDDEN);
        handleAccessDenied(response, AuthErrorCode.FORBIDDEN);
    }

    private void logAccessDenied(
            HttpServletRequest request,
            AccessDeniedException accessDeniedException,
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
                structuredHttpLogger.resolveHandlerMethodName(request, "handle"),
                "Access denied handler invoked",
                Map.of("exception", accessDeniedException.getClass().getSimpleName()),
                request,
                errorCode.getHttpStatus().value(),
                structuredHttpLogger.resolveDurationMs(request),
                structuredHttpLogger.toErrorInfo(errorCode)
        );
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
