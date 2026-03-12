package com.Hoseo.CapstoneDesign.security.filter;

import com.Hoseo.CapstoneDesign.global.exception.GlobalErrorCode;
import com.Hoseo.CapstoneDesign.global.exception.GlobalExceptionResponse;
import com.Hoseo.CapstoneDesign.security.exception.AuthBaseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
public class GlobalExceptionFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper = new ObjectMapper();

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
            log.debug("[GLOBAL_FILTER_EXCEPTION] AuthBaseException message -> {}" +
                    "\n Stack trace: {}", ex.getMessage(), ex.getStackTrace());
            handleAuthException(response, ex.getErrorCode());
        }

    }

    private void handleAuthException(HttpServletResponse response, GlobalErrorCode errorCode) throws IOException {
        response.setStatus(errorCode.getHttpStatus().value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        GlobalExceptionResponse body = new GlobalExceptionResponse(errorCode);
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }

}
