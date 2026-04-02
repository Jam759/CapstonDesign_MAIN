package com.Hoseo.CapstoneDesign.security.filter;

import com.Hoseo.CapstoneDesign.global.exception.GlobalErrorCode;
import com.Hoseo.CapstoneDesign.global.exception.GlobalExceptionResponse;
import com.Hoseo.CapstoneDesign.global.logging.StructuredHttpLogger;
import com.Hoseo.CapstoneDesign.security.exception.AuthBaseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;

import static com.Hoseo.CapstoneDesign.global.logging.support.HttpEventType.HTTP_ERROR;
import static com.Hoseo.CapstoneDesign.global.logging.support.LogCategory.HTTP;
import static com.Hoseo.CapstoneDesign.global.logging.support.LoggingConstants.ERROR_ALREADY_LOGGED;

@Slf4j
public class GlobalExceptionFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final StructuredHttpLogger structuredHttpLogger;

    public GlobalExceptionFilter(StructuredHttpLogger structuredHttpLogger) {
        this.structuredHttpLogger = structuredHttpLogger;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        try {
            // 다음 필터/컨트롤러 실행
            filterChain.doFilter(request, response);
        } catch (AuthBaseException ex) {
            // 커스텀 인증 예외 처리
            log.debug("[GLOBAL_FILTER_EXCEPTION] AuthBaseException message -> {}", ex.getMessage(), ex);
            handleAuthException(request, response, ex);
        }

    }

    private void handleAuthException(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthBaseException exception
    ) throws IOException {
        GlobalErrorCode errorCode = exception.getErrorCode();
        request.setAttribute(ERROR_ALREADY_LOGGED, true);
        structuredHttpLogger.error(
                HTTP.name(),
                HTTP_ERROR.name(),
                structuredHttpLogger.resolveHandlerClassName(request, getClass().getSimpleName()),
                structuredHttpLogger.resolveHandlerMethodName(request, "doFilterInternal"),
                "Authentication failed in filter",
                Map.of("exception", exception.getClass().getSimpleName()),
                request,
                errorCode.getHttpStatus().value(),
                structuredHttpLogger.resolveDurationMs(request),
                structuredHttpLogger.toErrorInfo(errorCode)
        );

        response.setStatus(errorCode.getHttpStatus().value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        GlobalExceptionResponse body = new GlobalExceptionResponse(errorCode);
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }

}
