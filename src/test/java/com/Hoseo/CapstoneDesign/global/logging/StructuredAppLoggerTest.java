package com.Hoseo.CapstoneDesign.global.logging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.Hoseo.CapstoneDesign.global.logging.dto.AppErrorInfo;
import com.Hoseo.CapstoneDesign.global.logging.properties.LoggingProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class StructuredAppLoggerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private StructuredAppLogger structuredAppLogger;
    private ListAppender<ILoggingEvent> appender;

    @BeforeEach
    void setUp() {
        LoggingProperties properties = new LoggingProperties();
        Logger logger = (Logger) LoggerFactory.getLogger("STRUCTURED_APP");
        logger.detachAndStopAllAppenders();
        logger.setAdditive(false);
        logger.setLevel(Level.TRACE);

        appender = new ListAppender<>();
        appender.start();
        logger.addAppender(appender);

        structuredAppLogger = new StructuredAppLogger(objectMapper, properties);
    }

    @Test
    void writesAllSupportedLevelsAsStructuredJson() throws IOException {
        structuredAppLogger.trace("TRACE_EVENT", "SampleClass", "traceMethod", "trace", Map.of("k", "v"), 1L, null, null);
        structuredAppLogger.debug("DEBUG_EVENT", "SampleClass", "debugMethod", "debug", null, 2L, null, null);
        structuredAppLogger.info("INFO_EVENT", "SampleClass", "infoMethod", "info", null, 3L, Map.of("ok", true), null);
        structuredAppLogger.warn("WARN_EVENT", "SampleClass", "warnMethod", "warn", null, 4L, null, null);
        structuredAppLogger.error(
                "ERROR_EVENT",
                "SampleClass",
                "errorMethod",
                "error",
                null,
                5L,
                null,
                new AppErrorInfo("IllegalStateException", "boom")
        );

        List<JsonNode> logs = readStructuredLogs();

        assertThat(logs).hasSize(5);
        assertThat(logs.get(0).get("level").asText()).isEqualTo("TRACE");
        assertThat(logs.get(1).get("level").asText()).isEqualTo("DEBUG");
        assertThat(logs.get(2).get("level").asText()).isEqualTo("INFO");
        assertThat(logs.get(2).at("/result/ok").asBoolean()).isTrue();
        assertThat(logs.get(3).get("level").asText()).isEqualTo("WARN");
        assertThat(logs.get(4).get("level").asText()).isEqualTo("ERROR");
        assertThat(logs.get(4).at("/error/type").asText()).isEqualTo("IllegalStateException");
        assertThat(logs.get(4).at("/error/message").asText()).isEqualTo("boom");
    }

    private List<JsonNode> readStructuredLogs() throws IOException {
        return appender.list.stream()
                .map(ILoggingEvent::getFormattedMessage)
                .map(this::readJson)
                .toList();
    }

    private JsonNode readJson(String message) {
        try {
            return objectMapper.readTree(message);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
