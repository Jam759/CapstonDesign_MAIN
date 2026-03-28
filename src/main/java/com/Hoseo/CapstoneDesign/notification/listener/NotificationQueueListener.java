package com.Hoseo.CapstoneDesign.notification.listener;

import com.Hoseo.CapstoneDesign.analysis.enums.AnalysisStatus;
import com.Hoseo.CapstoneDesign.global.aws.properties.SqsProperties;
import com.Hoseo.CapstoneDesign.notification.dto.application.FailMessage;
import com.Hoseo.CapstoneDesign.notification.dto.application.NotificationQueueBaseMessage;
import com.Hoseo.CapstoneDesign.notification.dto.application.SuccessMessage;
import com.Hoseo.CapstoneDesign.notification.facade.NotificationFacade;
import com.Hoseo.CapstoneDesign.notification.facade.NotificationFacadeImpl;
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

            log.info(
                    "SQS analysis result received. jobId={}, eventType={}, status={}",
                    envelope.getJobId(),
                    envelope.getEventType(),
                    envelope.getStatus()
            );

            if (envelope.getStatus() == AnalysisStatus.SUCCESS) {
                notificationFacade.successHandle(envelope);
                return;
            }

            if (envelope.getStatus() == AnalysisStatus.FAILED) {
                notificationFacade.failedHandle(envelope);
                return;
            }
            log.warn(
                    "Unknown analysis status. jobId={}, status={}",
                    envelope.getJobId(),
                    envelope.getStatus()
            );
        } catch (Exception e) {
            log.error("Failed to process SQS message. body={}", messageBody, e);
            throw new RuntimeException("SQS message processing failed", e);
        }
    }

}
