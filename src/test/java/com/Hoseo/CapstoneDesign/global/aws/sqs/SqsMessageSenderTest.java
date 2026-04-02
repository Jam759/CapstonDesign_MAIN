package com.Hoseo.CapstoneDesign.global.aws.sqs;

import io.awspring.cloud.sqs.operations.SqsTemplate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.slf4j.MDC;

import static com.Hoseo.CapstoneDesign.global.logging.support.LoggingConstants.TRACE_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class SqsMessageSenderTest {

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    @Test
    @DisplayName("현재 MDC traceId를 SQS envelope에 자동 주입한다")
    void injectsTraceIdFromMdc() {
        SqsTemplate sqsTemplate = mock(SqsTemplate.class);
        SqsMessageSender sender = new SqsMessageSender(sqsTemplate);
        MDC.put(TRACE_ID, "trace-123");

        SqsBaseMessage message = SqsBaseMessage.builder()
                .jobId("job-1")
                .type("NORMAL_ANALYSIS_REQUEST")
                .data("payload")
                .build();

        sender.send("analysis-queue", message);

        ArgumentCaptor<SqsBaseMessage> captor = ArgumentCaptor.forClass(SqsBaseMessage.class);
        verify(sqsTemplate).send(org.mockito.ArgumentMatchers.eq("analysis-queue"), captor.capture());

        assertThat(captor.getValue().getTraceId()).isEqualTo("trace-123");
        assertThat(captor.getValue().getJobId()).isEqualTo("job-1");
    }

    @Test
    @DisplayName("이미 traceId가 있는 메시지는 그대로 보낸다")
    void preservesExistingTraceId() {
        SqsTemplate sqsTemplate = mock(SqsTemplate.class);
        SqsMessageSender sender = new SqsMessageSender(sqsTemplate);
        MDC.put(TRACE_ID, "trace-from-mdc");

        SqsBaseMessage message = SqsBaseMessage.builder()
                .traceId("trace-from-message")
                .jobId("job-1")
                .type("NORMAL_ANALYSIS_REQUEST")
                .data("payload")
                .build();

        sender.send("analysis-queue", message);

        ArgumentCaptor<SqsBaseMessage> captor = ArgumentCaptor.forClass(SqsBaseMessage.class);
        verify(sqsTemplate).send(org.mockito.ArgumentMatchers.eq("analysis-queue"), captor.capture());

        assertThat(captor.getValue().getTraceId()).isEqualTo("trace-from-message");
    }
}
