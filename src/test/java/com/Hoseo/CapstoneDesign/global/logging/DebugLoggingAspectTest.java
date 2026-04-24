package com.Hoseo.CapstoneDesign.global.logging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.Hoseo.CapstoneDesign.global.logging.properties.LoggingProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.ibatis.annotations.Mapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DebugLoggingAspectTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private SampleService proxy;
    private SampleRepository repositoryProxy;
    private SampleMapper mapperProxy;
    private ListAppender<ILoggingEvent> appender;

    @BeforeEach
    void setUp() {
        LoggingProperties properties = new LoggingProperties();
        properties.getDebugAspect().setEnabled(true);
        properties.getDebugAspect().setMaxStringLength(20);
        properties.getDebugAspect().setMaxCollectionSize(3);
        properties.getDebugAspect().setLogReturnValue(true);

        Logger logger = (Logger) LoggerFactory.getLogger("STRUCTURED_APP");
        logger.detachAndStopAllAppenders();
        logger.setAdditive(false);
        logger.setLevel(Level.TRACE);

        appender = new ListAppender<>();
        appender.start();
        logger.addAppender(appender);

        StructuredAppLogger structuredAppLogger = new StructuredAppLogger(objectMapper, properties);
        DebugLoggingAspect aspect = new DebugLoggingAspect(properties, structuredAppLogger);

        AspectJProxyFactory serviceFactory = new AspectJProxyFactory(new SampleService());
        serviceFactory.addAspect(aspect);
        proxy = serviceFactory.getProxy();

        AspectJProxyFactory repositoryFactory = new AspectJProxyFactory(new SampleRepository());
        repositoryFactory.addAspect(aspect);
        repositoryProxy = repositoryFactory.getProxy();

        AspectJProxyFactory mapperFactory = new AspectJProxyFactory(new SampleMapper());
        mapperFactory.addAspect(aspect);
        mapperProxy = mapperFactory.getProxy();
    }

    @Test
    @DisplayName("민감값을 마스킹하고 긴 문자열과 컬렉션을 잘라 구조화 로그에 남긴다")
    void masksSensitiveValuesAndTruncatesLongStrings() throws IOException {
        SamplePayload payload = new SamplePayload(
                "api-secret-value",
                "n".repeat(40),
                List.of("first", "second", "third", "fourth")
        );

        proxy.process("refresh-token-value", "a".repeat(30), payload);

        List<JsonNode> logs = readStructuredLogs();

        assertThat(logs).hasSize(2);
        assertThat(logs.get(0).get("eventType").asText()).isEqualTo("METHOD_ENTER");
        assertThat(logs.get(0).get("className").asText()).isEqualTo("SampleService");
        assertThat(logs.get(0).get("method").asText()).isEqualTo("process");
        assertThat(logs.get(0).at("/args/refreshTokenRaw").asText()).isEqualTo("<redacted>");
        assertThat(logs.get(0).at("/args/note").asText()).isEqualTo("aaaaaaaaaaaaaaaaaaaa...(len=30)");
        assertThat(logs.get(0).at("/args/payload/apiKey").asText()).isEqualTo("<redacted>");
        assertThat(logs.get(0).at("/args/payload/comments/3").asText()).isEqualTo("<+1 more>");

        assertThat(logs.get(1).get("eventType").asText()).isEqualTo("METHOD_EXIT");
        assertThat(logs.get(1).get("durationMs").asLong()).isGreaterThanOrEqualTo(0L);
        assertThat(logs.get(1).at("/result/accessToken").asText()).isEqualTo("<redacted>");
    }

    @Test
    @DisplayName("예외 발생 시 타입과 메시지를 구조화 로그에 남긴다")
    void logsThrownException() throws IOException {
        assertThatThrownBy(() -> proxy.fail("refresh-token-value"))
                .isInstanceOf(IllegalStateException.class);

        List<JsonNode> logs = readStructuredLogs();

        assertThat(logs).hasSize(2);
        assertThat(logs.get(0).get("eventType").asText()).isEqualTo("METHOD_ENTER");
        assertThat(logs.get(1).get("eventType").asText()).isEqualTo("METHOD_THROW");
        assertThat(logs.get(1).at("/error/type").asText()).isEqualTo("IllegalStateException");
        assertThat(logs.get(1).at("/error/message").asText()).isEqualTo("xxxxxxxxxxxxxxxxxxxx...(len=45)");
    }

    @Test
    @DisplayName("repository와 mapper 호출도 구조화 로그로 남긴다")
    void logsRepositoryAndMapperCalls() throws IOException {
        repositoryProxy.findSecretByToken("refresh-token-value");
        mapperProxy.selectByState("oauth-state-secret");

        List<JsonNode> logs = readStructuredLogs();

        assertThat(logs).hasSize(4);
        assertThat(logs.get(0).get("className").asText()).isEqualTo("SampleRepository");
        assertThat(logs.get(0).at("/args/rawToken").asText()).isEqualTo("<redacted>");
        assertThat(logs.get(1).get("eventType").asText()).isEqualTo("METHOD_EXIT");
        assertThat(logs.get(2).get("className").asText()).isEqualTo("SampleMapper");
        assertThat(logs.get(2).at("/args/state").asText()).isEqualTo("<redacted>");
        assertThat(logs.get(3).get("eventType").asText()).isEqualTo("METHOD_EXIT");
    }

    private List<JsonNode> readStructuredLogs() throws IOException {
        List<JsonNode> logs = new ArrayList<>();
        for (ILoggingEvent event : appender.list) {
            logs.add(objectMapper.readTree(event.getFormattedMessage()));
        }
        return logs;
    }

    @Service
    static class SampleService {

        SampleResult process(String refreshTokenRaw, String note, SamplePayload payload) {
            return new SampleResult("access-token-value", note, payload);
        }

        void fail(String refreshTokenRaw) {
            throw new IllegalStateException("x".repeat(45));
        }
    }

    @Repository
    static class SampleRepository {

        String findSecretByToken(String rawToken) {
            return "stored-secret-value";
        }
    }

    @Mapper
    static class SampleMapper {

        String selectByState(String state) {
            return "ok";
        }
    }

    static class SamplePayload {

        private final String apiKey;
        private final String note;
        private final List<String> comments;

        SamplePayload(String apiKey, String note, List<String> comments) {
            this.apiKey = apiKey;
            this.note = note;
            this.comments = comments;
        }
    }

    static class SampleResult {

        private final String accessToken;
        private final String summary;
        private final SamplePayload payload;

        SampleResult(String accessToken, String summary, SamplePayload payload) {
            this.accessToken = accessToken;
            this.summary = summary;
            this.payload = payload;
        }
    }
}
