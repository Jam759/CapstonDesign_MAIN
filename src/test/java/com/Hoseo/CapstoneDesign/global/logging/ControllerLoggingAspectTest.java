package com.Hoseo.CapstoneDesign.global.logging;

import com.Hoseo.CapstoneDesign.global.logging.dto.ErrorInfo;
import com.Hoseo.CapstoneDesign.project.exception.ProjectsErrorCode;
import com.Hoseo.CapstoneDesign.project.exception.ProjectsException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;

import static com.Hoseo.CapstoneDesign.global.logging.support.LoggingConstants.ERROR_ALREADY_LOGGED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ControllerLoggingAspectTest {

    @Mock
    private StructuredHttpLogger structuredHttpLogger;

    private SampleController proxy;

    @BeforeEach
    void setUp() {
        AspectJProxyFactory factory = new AspectJProxyFactory(new SampleController());
        factory.addAspect(new ControllerLoggingAspect(structuredHttpLogger));
        proxy = factory.getProxy();
    }

    @AfterEach
    void tearDown() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    @DisplayName("민감 파라미터와 중첩 map 키는 request 로그에서 마스킹된다")
    void redactsSensitiveArgumentsInRequestLog() {
        MockHttpServletRequest request = bindRequest("POST", "/api/v1/auth/reissue");
        when(structuredHttpLogger.resolveDurationMs(request)).thenReturn(25L);

        proxy.reissue(
                "refresh-token-value",
                Map.of("signature256", "sha256=abc", "note", "safe"),
                1
        );

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Map<String, Object>> argsCaptor = ArgumentCaptor.forClass(Map.class);

        verify(structuredHttpLogger).info(
                eq("HTTP"),
                eq("HTTP_REQUEST"),
                eq("SampleController"),
                eq("reissue"),
                eq("Incoming request"),
                argsCaptor.capture(),
                same(request),
                isNull(),
                isNull()
        );

        Map<String, Object> args = argsCaptor.getValue();
        assertThat(args).containsEntry("refreshTokenRaw", "<redacted>");
        assertThat(args).containsEntry("page", 1);
        assertThat(args.get("metadata")).isInstanceOf(Map.class);
        Map<?, ?> metadata = (Map<?, ?>) args.get("metadata");
        assertThat(metadata.get("signature256")).isEqualTo("<redacted>");
        assertThat(metadata.get("note")).isEqualTo("safe");
    }

    @Test
    @DisplayName("SSE 응답은 완료가 아니라 시작으로 기록된다")
    void logsStreamingResponseStarted() {
        MockHttpServletRequest request = bindRequest("GET", "/api/v1/notification/sse/subscribe");
        when(structuredHttpLogger.resolveDurationMs(request)).thenReturn(25L);

        proxy.stream();

        verify(structuredHttpLogger).info(
                eq("HTTP"),
                eq("HTTP_RESPONSE"),
                eq("SampleController"),
                eq("stream"),
                eq("Streaming response started"),
                isNull(),
                same(request),
                eq(200),
                eq(25L)
        );
    }

    @Test
    @DisplayName("비즈니스 예외는 구조화 로그에 실제 에러 코드를 남긴다")
    void logsBusinessExceptionWithNumericErrorCode() {
        MockHttpServletRequest request = bindRequest("GET", "/api/v1/projects/1");
        when(structuredHttpLogger.resolveDurationMs(request)).thenReturn(25L);

        ArgumentCaptor<ErrorInfo> errorCaptor = ArgumentCaptor.forClass(ErrorInfo.class);

        assertThatThrownBy(() -> proxy.fail())
                .isInstanceOf(ProjectsException.class);

        verify(structuredHttpLogger).error(
                eq("HTTP"),
                eq("HTTP_ERROR"),
                eq("SampleController"),
                eq("fail"),
                eq("Request failed"),
                isNull(),
                same(request),
                eq(ProjectsErrorCode.PROJECT_FORBIDDEN.getHttpStatus().value()),
                eq(25L),
                errorCaptor.capture()
        );

        assertThat(errorCaptor.getValue().code()).isEqualTo(ProjectsErrorCode.PROJECT_FORBIDDEN.getErrorCode());
        assertThat(errorCaptor.getValue().httpStatus()).isEqualTo(ProjectsErrorCode.PROJECT_FORBIDDEN.getHttpStatus().value());
        assertThat(request.getAttribute(ERROR_ALREADY_LOGGED)).isEqualTo(true);
    }

    private MockHttpServletRequest bindRequest(String method, String uri) {
        MockHttpServletRequest request = new MockHttpServletRequest(method, uri);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        return request;
    }

    @RestController
    static class SampleController {

        ResponseEntity<String> reissue(String refreshTokenRaw, Map<String, Object> metadata, Integer page) {
            return ResponseEntity.ok("ok");
        }

        SseEmitter stream() {
            return new SseEmitter();
        }

        String fail() {
            throw new ProjectsException(ProjectsErrorCode.PROJECT_FORBIDDEN);
        }
    }
}
