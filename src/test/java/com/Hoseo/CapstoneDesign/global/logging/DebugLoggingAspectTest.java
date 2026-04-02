package com.Hoseo.CapstoneDesign.global.logging;

import com.Hoseo.CapstoneDesign.global.logging.properties.LoggingProperties;
import org.apache.ibatis.annotations.Mapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(OutputCaptureExtension.class)
class DebugLoggingAspectTest {

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

    @Test
    @DisplayName("민감값은 마스킹되고 일반 문자열은 길이 제한으로 줄여서 로그한다")
    void masksSensitiveValuesAndTruncatesLongStrings(CapturedOutput output) {
        SamplePayload payload = new SamplePayload(
                "api-secret-value",
                "n".repeat(40),
                List.of("first", "second", "third", "fourth")
        );

        proxy.process("refresh-token-value", "a".repeat(30), payload);

        String logs = output.getOut();

        assertThat(logs).contains("ENTER] SampleService.process");
        assertThat(logs).contains("refreshTokenRaw=<redacted>");
        assertThat(logs).contains("note=aaaaaaaaaaaaaaaaaaaa...(len=30)");
        assertThat(logs).contains("apiKey=<redacted>");
        assertThat(logs).contains("comments=[first, second, third, <+1 more>]");
        assertThat(logs).contains("EXIT] SampleService.process");
        assertThat(logs).contains("durationMs=");
        assertThat(logs).contains("accessToken=<redacted>");
    }

    @Test
    @DisplayName("예외가 발생하면 예외 타입과 메시지를 길이 제한으로 남긴다")
    void logsThrownException(CapturedOutput output) {
        assertThatThrownBy(() -> proxy.fail("refresh-token-value"))
                .isInstanceOf(IllegalStateException.class);

        String logs = output.getOut();
        assertThat(logs).contains("ENTER] SampleService.fail");
        assertThat(logs).contains("THROW] SampleService.fail");
        assertThat(logs).contains("exception=IllegalStateException");
        assertThat(logs).contains("xxxxxxxxxxxxxxxxxxxx...(len=45)");
    }

    @Test
    @DisplayName("repository와 mapper 호출도 디버그 aspect가 추적한다")
    void logsRepositoryAndMapperCalls(CapturedOutput output) {
        repositoryProxy.findSecretByToken("refresh-token-value");
        mapperProxy.selectByState("oauth-state-secret");

        String logs = output.getOut();
        assertThat(logs).contains("ENTER] SampleRepository.findSecretByToken");
        assertThat(logs).contains("rawToken=<redacted>");
        assertThat(logs).contains("EXIT] SampleRepository.findSecretByToken");
        assertThat(logs).contains("ENTER] SampleMapper.selectByState");
        assertThat(logs).contains("state=<redacted>");
        assertThat(logs).contains("EXIT] SampleMapper.selectByState");
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
