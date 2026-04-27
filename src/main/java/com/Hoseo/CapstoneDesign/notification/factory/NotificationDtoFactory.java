package com.Hoseo.CapstoneDesign.notification.factory;

import com.Hoseo.CapstoneDesign.analysis.entity.AnalysisJob;
import com.Hoseo.CapstoneDesign.analysis.enums.AnalysisEventType;
import com.Hoseo.CapstoneDesign.notification.dto.application.FailMessage;
import com.Hoseo.CapstoneDesign.notification.dto.application.NotificationQueueBaseMessage;
import com.Hoseo.CapstoneDesign.notification.dto.application.SseBaseResponse;
import com.Hoseo.CapstoneDesign.notification.dto.application.SuccessMessage;
import com.Hoseo.CapstoneDesign.notification.dto.response.NotificationResponse;
import com.Hoseo.CapstoneDesign.notification.entity.SseNotification;
import com.Hoseo.CapstoneDesign.notification.exception.NotificationException;
import com.Hoseo.CapstoneDesign.notification.exception.NotificationErrorCode;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class NotificationDtoFactory {

    private static final String ANALYSIS_SUCCESS_EVENT = "analysis-success";
    private static final String ANALYSIS_FAILED_EVENT = "analysis-failed";

    public static SseBaseResponse toSseBaseResponse(String eventType, Object data) {
        return SseBaseResponse.builder()
                .id(UUID.randomUUID())
                .eventType(eventType)
                .eventAt(LocalDateTime.now())
                .data(data)
                .build();
    }

    public static NotificationResponse toNotificationResponse(SseNotification notification) {
        return NotificationResponse.builder()
                .id(notification.getSseNotificationId())
                .type(notification.getLinkType().getCommonGroupDetailId())
                .message(notification.getMessage())
                .read(notification.getIsRead())
                .createdAt(notification.getCreatedAt())
                .linkId(notification.getLinkId() == null ? null : notification.getLinkId().toString())
                .build();
    }

    public static SseBaseResponse toAnalysisSuccessSseResponse(
            NotificationQueueBaseMessage envelope,
            SuccessMessage data
    ) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("jobId", envelope.getJobId());
        payload.put("analysisEventType", resolveEventType(envelope).name());
        payload.put("completeQuestIds", data.getCompleteQuestIds());
        payload.put("newQuestIds", data.getNewQuestIds());
        payload.put("newProjectKBid", data.getNewProjectKBid());
        payload.put("userViewReportId", data.getUserViewReportId());

        return toSseBaseResponse(ANALYSIS_SUCCESS_EVENT, payload);
    }

    public static SseBaseResponse toAnalysisFailureSseResponse(
            NotificationQueueBaseMessage envelope,
            AnalysisJob job,
            FailMessage data
    ) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("jobId", envelope.getJobId());
        payload.put("analysisEventType", resolveEventType(envelope).name());
        payload.put("retryCount", job.getRetryCount());
        payload.put("errorCode", data.getErrorCode());
        payload.put("errorMessage", data.getErrorMessage());
        payload.put("httpStatus", data.getHTTPStatus());
        payload.put("retryable", data.getRetryable());

        return toSseBaseResponse(ANALYSIS_FAILED_EVENT, payload);
    }

    private static AnalysisEventType resolveEventType(NotificationQueueBaseMessage envelope) {
        if (envelope.getEventType() == null) {
            throw new NotificationException(NotificationErrorCode.NOTIFICATION_EVENT_TYPE_NULL);
        }

        return envelope.getEventType();
    }
}
