package com.Hoseo.CapstoneDesign.notification.controller;

import com.Hoseo.CapstoneDesign.analysis.enums.AnalysisStatus;
import com.Hoseo.CapstoneDesign.global.aws.properties.SqsProperties;
import com.Hoseo.CapstoneDesign.notification.dto.application.FailMessage;
import com.Hoseo.CapstoneDesign.notification.dto.application.NotificationQueueBaseMessage;
import com.Hoseo.CapstoneDesign.notification.dto.application.SuccessMessage;
import com.fasterxml.jackson.core.TreeNode;
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
    private final SqsProperties properties;

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
                handleSuccess(envelope);
                return;
            }

            if (envelope.getStatus() == AnalysisStatus.FAILED) {
                handleFailed(envelope);
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

    //나중에 클래스만들어서 옮기기

    private void handleSuccess(NotificationQueueBaseMessage envelope) throws Exception {
        SuccessMessage data = objectMapper.convertValue(envelope.getData(), SuccessMessage.class);

        log.info(
                "Analysis success. jobId={}, completeQuestIds={}, newQuestIds={}, newProjectKBid={}, userViewReportId={}",
                envelope.getJobId(),
                data.getCompleteQuestIds(),
                data.getNewQuestIds(),
                data.getNewProjectKBid(),
                data.getUserViewReportId()
        );

        // TODO: 성공 분기 처리
    }

    private void handleFailed(NotificationQueueBaseMessage envelope) throws Exception {
        FailMessage data =
                objectMapper.convertValue(envelope.getData(), FailMessage.class);

        log.warn(
                "Analysis failed. jobId={}, errorCode={}, errorMessage={}, httpStatus={}, retryable={}",
                envelope.getJobId(),
                data.getErrorCode(),
                data.getErrorMessage(),
                data.getHTTPStatus(),
                data.getRetryable()
        );

        // TODO: 실패 분기 처리
    }
}
