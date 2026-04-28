package com.Hoseo.CapstoneDesign.notification.listener;

import com.Hoseo.CapstoneDesign.analysis.enums.AnalysisStatus;
import com.Hoseo.CapstoneDesign.global.logging.support.TraceIdContext;
import com.Hoseo.CapstoneDesign.notification.dto.application.NotificationQueueBaseMessage;
import com.Hoseo.CapstoneDesign.notification.exception.NotificationErrorCode;
import com.Hoseo.CapstoneDesign.notification.exception.NotificationException;
import com.Hoseo.CapstoneDesign.notification.facade.NotificationFacade;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationQueueListener {

    private final ObjectMapper objectMapper;
    private final NotificationFacade notificationFacade;

    @SqsListener("${app.aws.sqs.notification-queue}")
    public void listen(@Payload String messageBody) {
        try {
            NotificationQueueBaseMessage envelope =
                    objectMapper.readValue(messageBody, NotificationQueueBaseMessage.class);

            try (TraceIdContext traceIdContext = TraceIdContext.open(envelope.getTraceId())) {
                String traceId = traceIdContext.traceId();

                if (envelope.getStatus() == AnalysisStatus.SUCCESS) {
                    notificationFacade.successHandle(envelope);
                    return;
                }

                if (envelope.getStatus() == AnalysisStatus.FAILED) {
                    notificationFacade.failedHandle(envelope);
                    return;
                }
            }
        } catch (Exception e) {
            log.error("SQS message processing failed", e);
            throw new NotificationException(NotificationErrorCode.NOTIFICATION_SQS_PROCESS_FAILED);
        }
    }
}
