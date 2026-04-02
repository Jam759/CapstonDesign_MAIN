package com.Hoseo.CapstoneDesign.security.filter;

import com.Hoseo.CapstoneDesign.global.logging.properties.LoggingProperties;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static com.Hoseo.CapstoneDesign.global.logging.support.LoggingConstants.REQUEST_START_TIME;
import static com.Hoseo.CapstoneDesign.global.logging.support.LoggingConstants.TRACE_ID;
import static org.assertj.core.api.Assertions.assertThat;

class TraceMdcFilterTest {

    @Test
    @DisplayName("일반 요청은 traceId와 시작 시각을 설정하고 응답 헤더로 내려준다")
    void setsTraceIdAndStartTimeForRegularRequest() throws Exception {
        LoggingProperties properties = new LoggingProperties();
        TraceMdcFilter filter = new TraceMdcFilter(properties);

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/projects");
        request.addHeader("X-Trace-Id", "trace-123");
        MockHttpServletResponse response = new MockHttpServletResponse();

        AtomicReference<String> traceSeenInChain = new AtomicReference<>();
        AtomicReference<Object> startTimeSeenInChain = new AtomicReference<>();

        filter.doFilter(request, response, (req, res) -> {
            traceSeenInChain.set(MDC.get(TRACE_ID));
            startTimeSeenInChain.set(request.getAttribute(REQUEST_START_TIME));
        });

        assertThat(traceSeenInChain.get()).isEqualTo("trace-123");
        assertThat(startTimeSeenInChain.get()).isInstanceOf(Long.class);
        assertThat(response.getHeader("X-Trace-Id")).isEqualTo("trace-123");
        assertThat(MDC.get(TRACE_ID)).isNull();
    }

    @Test
    @DisplayName("제외 경로는 trace MDC 필터를 건너뛴다")
    void skipsExcludedPaths() throws Exception {
        LoggingProperties properties = new LoggingProperties();
        properties.setExcludePaths(List.of("/actuator/health"));
        TraceMdcFilter filter = new TraceMdcFilter(properties);

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/actuator/health");
        MockHttpServletResponse response = new MockHttpServletResponse();

        AtomicReference<String> traceSeenInChain = new AtomicReference<>();
        AtomicReference<Object> startTimeSeenInChain = new AtomicReference<>();

        filter.doFilter(request, response, (req, res) -> {
            traceSeenInChain.set(MDC.get(TRACE_ID));
            startTimeSeenInChain.set(request.getAttribute(REQUEST_START_TIME));
        });

        assertThat(traceSeenInChain.get()).isNull();
        assertThat(startTimeSeenInChain.get()).isNull();
        assertThat(response.getHeader("X-Trace-Id")).isNull();
    }
}
