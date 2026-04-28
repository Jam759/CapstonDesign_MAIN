package com.Hoseo.CapstoneDesign.notification.facade;

import com.Hoseo.CapstoneDesign.analysis.entity.AnalysisJob;
import com.Hoseo.CapstoneDesign.analysis.entity.ProjectAnalysisReport;
import com.Hoseo.CapstoneDesign.analysis.entity.enums.AnalysisJobStatus;
import com.Hoseo.CapstoneDesign.analysis.event.ProjectAnalysisReportPublishedEvent;
import com.Hoseo.CapstoneDesign.analysis.service.AnalysisJobService;
import com.Hoseo.CapstoneDesign.analysis.service.ProjectAnalysisReportService;
import com.Hoseo.CapstoneDesign.common.entity.CommonGroupDetail;
import com.Hoseo.CapstoneDesign.common.service.CommonGroupDetailService;
import com.Hoseo.CapstoneDesign.global.annotation.Facade;
import com.Hoseo.CapstoneDesign.notification.dto.application.FailMessage;
import com.Hoseo.CapstoneDesign.notification.dto.application.NotificationQueueBaseMessage;
import com.Hoseo.CapstoneDesign.notification.dto.application.SseBaseResponse;
import com.Hoseo.CapstoneDesign.notification.dto.application.SuccessMessage;
import com.Hoseo.CapstoneDesign.notification.dto.response.NotificationResponse;
import com.Hoseo.CapstoneDesign.notification.factory.NotificationDtoFactory;
import com.Hoseo.CapstoneDesign.notification.exception.NotificationErrorCode;
import com.Hoseo.CapstoneDesign.notification.exception.NotificationException;
import com.Hoseo.CapstoneDesign.notification.factory.NotificationEntityFactory;
import com.Hoseo.CapstoneDesign.notification.service.NotificationService;
import com.Hoseo.CapstoneDesign.user.entity.Users;
import com.Hoseo.CapstoneDesign.user.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Facade
@RequiredArgsConstructor
public class NotificationFacadeImpl implements NotificationFacade {

    private static final short MAX_RETRY_COUNT = 3;
    private static final String PROJECT_LINK_TYPE = "PROJECT";

    private final NotificationService notificationService;
    private final AnalysisJobService analysisJobService;
    private final CommonGroupDetailService commonGroupDetailService;
    private final UserService userService;
    private final ObjectMapper objectMapper;
    private final ProjectAnalysisReportService projectAnalysisReportService;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getNotification(Long userId, Integer page, Integer size) {
        return notificationService.getNotifications(userId, page, size).stream()
                .map(NotificationDtoFactory::toNotificationResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getUnReadNotification(Long userId) {
        return notificationService.getUnreadNotifications(userId).stream()
                .map(NotificationDtoFactory::toNotificationResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = false)
    public void readNotification(Long userId, Long notificationId) {
        notificationService.readNotification(userId, notificationId);
    }

    @Override
    @Transactional(readOnly = false)
    public void readNotifications(Long userId, List<Long> ids) {
        notificationService.readNotifications(userId, ids);
    }

    @Override
    @Transactional(readOnly = false)
    public void successHandle(NotificationQueueBaseMessage envelope) {
        SuccessMessage data = objectMapper.convertValue(envelope.getData(), SuccessMessage.class);
        AnalysisJob job = analysisJobService.getById(envelope.getJobId());
        job.updateJobStatus(AnalysisJobStatus.NOTIFICATION_COMPLETED);
        publishReportCacheInvalidation(data);

        SseBaseResponse response = NotificationDtoFactory.toAnalysisSuccessSseResponse(envelope, data);
        notificationService.createAndDispatch(
                NotificationEntityFactory.toAnalysisSuccessNotification(
                        resolveNotificationUser(envelope, job),
                        job,
                        resolveProjectLinkType(),
                        serializeSsePayload(response)
                ),
                response
        );
    }

    @Override
    @Transactional(readOnly = false)
    public void failedHandle(NotificationQueueBaseMessage envelope) {
        FailMessage data = objectMapper.convertValue(envelope.getData(), FailMessage.class);
        AnalysisJob job = analysisJobService.getById(envelope.getJobId());

        if (shouldRetry(job, data)) {
            job.incrementRetryCount();
            job.updateJobStatus(AnalysisJobStatus.PENDING);
            analysisJobService.requestDispatch(job.getAnalysisJobId());
            return;
        }

        job.updateJobStatus(AnalysisJobStatus.FAILED);

        SseBaseResponse response = NotificationDtoFactory.toAnalysisFailureSseResponse(envelope, job, data);
        notificationService.createAndDispatch(
                NotificationEntityFactory.toAnalysisFailureNotification(
                        resolveNotificationUser(envelope, job),
                        job,
                        resolveProjectLinkType(),
                        serializeSsePayload(response)
                ),
                response
        );

    }

    private boolean shouldRetry(AnalysisJob job, FailMessage data) {
        return Boolean.TRUE.equals(data.getRetryable()) && job.getRetryCount() < MAX_RETRY_COUNT;
    }

    private Users resolveNotificationUser(NotificationQueueBaseMessage envelope, AnalysisJob job) {
        if (job.getUser() != null) {
            return job.getUser();
        }

        if (envelope.getUserId() == null) {
            throw new NotificationException(NotificationErrorCode.NOTIFICATION_USER_RESOLVE_FAILED);
        }

        return userService.getReferenceById(envelope.getUserId());
    }

    private CommonGroupDetail resolveProjectLinkType() {
        return commonGroupDetailService.getReferenceById(PROJECT_LINK_TYPE);
    }

    private String serializeSsePayload(SseBaseResponse response) {
        try {
            return objectMapper.writeValueAsString(response.getData());
        } catch (JsonProcessingException e) {
            log.error("SSE payload serialization failed", e);
            throw new NotificationException(NotificationErrorCode.NOTIFICATION_SERIALIZE_FAILED);
        }
    }

    private void publishReportCacheInvalidation(SuccessMessage data) {
        if (data.getUserViewReportId() == null) {
            return;
        }

        ProjectAnalysisReport report = projectAnalysisReportService.getById(data.getUserViewReportId());
        applicationEventPublisher.publishEvent(
                new ProjectAnalysisReportPublishedEvent(
                        report.getProject().getProjectId(),
                        report.getVersion(),
                        report.getProjectAnalysisReportId()
                )
        );
    }
}
