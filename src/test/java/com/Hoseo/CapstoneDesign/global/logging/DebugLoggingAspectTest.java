package com.Hoseo.CapstoneDesign.global.logging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.Hoseo.CapstoneDesign.global.logging.properties.LoggingProperties;
import org.apache.ibatis.annotations.Mapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.slf4j.LoggerFactory.getLogger;

class DebugLoggingAspectTest {

    private final Logger logger = (Logger) getLogger("DEBUG_ASPECT");
    private ListAppender<ILoggingEvent> appender;
    private SampleService proxy;
    private SampleRepository repositoryProxy;
    private SampleMapper mapperProxy;

    @BeforeEach
    void setUp() {
        LoggingProperties properties = new LoggingProperties();
        properties.getDebugAspect().setEnabled(true);
        properties.getDebugAspect().setMaxStringLength(20);
        properties.getDebugAspect().setMaxCollectionSize(3);
        properties.getDebugAspect().setLogReturnValue(true);

        appender = new ListAppender<>();
        appender.start();

        logger.addAppender(appender);
        logger.setLevel(Level.INFO);

        DebugLoggingAspect aspect = new DebugLoggingAspect(properties);

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

    @AfterEach
    void tearDown() {
        logger.detachAppender(appender);
        appender.stop();
    }

    @Test
    @DisplayName("민감값은 마스킹되고 일반 문자열은 길이 제한으로 줄여서 로그한다")
    void masksSensitiveValuesAndTruncatesLongStrings() {
        SamplePayload payload = new SamplePayload(
                "api-secret-value",
                "n".repeat(40),
                List.of("first", "second", "third", "fourth")
        );

        proxy.process("refresh-token-value", "a".repeat(30), payload);

        List<String> messages = appender.list.stream()
                .map(ILoggingEvent::getFormattedMessage)
                .toList();

        assertThat(messages).hasSize(2);
        assertThat(messages.get(0)).contains("ENTER SampleService.process");
        assertThat(messages.get(0)).contains("refreshTokenRaw=<redacted>");
        assertThat(messages.get(0)).contains("note=aaaaaaaaaaaaaaaaaaaa...(len=30)");
        assertThat(messages.get(0)).contains("apiKey=<redacted>");
        assertThat(messages.get(0)).contains("comments=[first, second, third, <+1 more>]");

        assertThat(messages.get(1)).contains("EXIT SampleService.process");
        assertThat(messages.get(1)).contains("durationMs=");
        assertThat(messages.get(1)).contains("accessToken=<redacted>");
    }

    @Test
    @DisplayName("예외가 발생하면 예외 타입과 메시지를 길이 제한으로 남긴다")
    void logsThrownException() {
        assertThatThrownBy(() -> proxy.fail("refresh-token-value"))
                .isInstanceOf(IllegalStateException.class);

        List<String> messages = appender.list.stream()
                .map(ILoggingEvent::getFormattedMessage)
                .toList();

        assertThat(messages).hasSize(2);
        assertThat(messages.get(0)).contains("ENTER SampleService.fail");
        assertThat(messages.get(1)).contains("THROW SampleService.fail");
        assertThat(messages.get(1)).contains("exception=IllegalStateException");
        assertThat(messages.get(1)).contains("xxxxxxxxxxxxxxxxxxxx...(len=45)");
    }

    @Test
    @DisplayName("repository와 mapper 호출도 디버그 aspect가 추적한다")
    void logsRepositoryAndMapperCalls() {
        repositoryProxy.findSecretByToken("refresh-token-value");
        mapperProxy.selectByState("oauth-state-secret");

        List<String> messages = appender.list.stream()
                .map(ILoggingEvent::getFormattedMessage)
                .toList();

        assertThat(messages).hasSize(4);
        assertThat(messages.get(0)).contains("ENTER SampleRepository.findSecretByToken");
        assertThat(messages.get(0)).contains("rawToken=<redacted>");
        assertThat(messages.get(1)).contains("EXIT SampleRepository.findSecretByToken");
        assertThat(messages.get(2)).contains("ENTER SampleMapper.selectByState");
        assertThat(messages.get(2)).contains("state=<redacted>");
        assertThat(messages.get(3)).contains("EXIT SampleMapper.selectByState");
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
