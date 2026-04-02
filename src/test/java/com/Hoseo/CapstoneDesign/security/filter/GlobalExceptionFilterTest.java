package com.Hoseo.CapstoneDesign.security.filter;

import com.Hoseo.CapstoneDesign.global.logging.StructuredHttpLogger;
import com.Hoseo.CapstoneDesign.global.logging.dto.ErrorInfo;
import com.Hoseo.CapstoneDesign.security.exception.AccessTokenBlackListErrorCode;
import com.Hoseo.CapstoneDesign.security.exception.AccessTokenBlackListException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.Map;

import static com.Hoseo.CapstoneDesign.global.logging.support.LoggingConstants.ERROR_ALREADY_LOGGED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionFilterTest {

    @Mock
    private StructuredHttpLogger structuredHttpLogger;

    private GlobalExceptionFilter globalExceptionFilter;

    @BeforeEach
    void setUp() {
        globalExceptionFilter = new GlobalExceptionFilter(structuredHttpLogger);
    }

    @Test
    @DisplayName("필터 단계 인증 예외는 구조화 로그와 JSON 응답으로 처리된다")
    void logsAndWritesAuthFailureResponse() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/projects");
        request.setAttribute("requestStartTime", System.currentTimeMillis() - 50L);
        MockHttpServletResponse response = new MockHttpServletResponse();

        AccessTokenBlackListException exception =
                new AccessTokenBlackListException(AccessTokenBlackListErrorCode.TOKEN_IS_BLACK_LIST);
        ErrorInfo errorInfo = new ErrorInfo(
                AccessTokenBlackListErrorCode.TOKEN_IS_BLACK_LIST.getMessage(),
                AccessTokenBlackListErrorCode.TOKEN_IS_BLACK_LIST.getErrorCode(),
                AccessTokenBlackListErrorCode.TOKEN_IS_BLACK_LIST.getHttpStatus().value()
        );

        when(structuredHttpLogger.resolveHandlerClassName(request, "GlobalExceptionFilter"))
                .thenReturn("GlobalExceptionFilter");
        when(structuredHttpLogger.resolveHandlerMethodName(request, "doFilterInternal"))
                .thenReturn("doFilterInternal");
        when(structuredHttpLogger.resolveDurationMs(request)).thenReturn(50L);
        when(structuredHttpLogger.toErrorInfo(AccessTokenBlackListErrorCode.TOKEN_IS_BLACK_LIST))
                .thenReturn(errorInfo);

        globalExceptionFilter.doFilter(request, response, (req, res) -> {
            throw exception;
        });

        verify(structuredHttpLogger).error(
                eq("HTTP"),
                eq("HTTP_ERROR"),
                eq("GlobalExceptionFilter"),
                eq("doFilterInternal"),
                eq("Authentication failed in filter"),
                eq(Map.of("exception", exception.getClass().getSimpleName())),
                same(request),
                eq(AccessTokenBlackListErrorCode.TOKEN_IS_BLACK_LIST.getHttpStatus().value()),
                eq(50L),
                same(errorInfo)
        );

        assertThat(request.getAttribute(ERROR_ALREADY_LOGGED)).isEqualTo(true);
        assertThat(response.getStatus()).isEqualTo(AccessTokenBlackListErrorCode.TOKEN_IS_BLACK_LIST.getHttpStatus().value());
        assertThat(response.getContentAsString())
                .contains("\"errorCode\":" + AccessTokenBlackListErrorCode.TOKEN_IS_BLACK_LIST.getErrorCode());
    }
}
