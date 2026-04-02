package com.Hoseo.CapstoneDesign.global.aws.sqs;

import io.awspring.cloud.sqs.operations.SqsTemplate;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import static com.Hoseo.CapstoneDesign.global.logging.support.LoggingConstants.TRACE_ID;

@Component
@RequiredArgsConstructor
public class SqsMessageSender {
    private final SqsTemplate sqsTemplate;

    public <T> void send(String queueName, T payload) {
        sqsTemplate.send(queueName, enrichTraceId(payload));
    }

    @SuppressWarnings("unchecked")
    private <T> T enrichTraceId(T payload) {
        if (!(payload instanceof SqsBaseMessage message)) {
            return payload;
        }

        if (StringUtils.hasText(message.getTraceId())) {
            return payload;
        }

        String traceId = MDC.get(TRACE_ID);
        if (!StringUtils.hasText(traceId)) {
            return payload;
        }

        return (T) message.toBuilder()
                .traceId(traceId)
                .build();
    }
}
