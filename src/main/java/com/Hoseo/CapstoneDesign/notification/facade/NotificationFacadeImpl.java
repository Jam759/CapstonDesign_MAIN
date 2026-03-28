package com.Hoseo.CapstoneDesign.notification.facade;

import com.Hoseo.CapstoneDesign.global.annotation.Facade;
import com.Hoseo.CapstoneDesign.notification.dto.application.FailMessage;
import com.Hoseo.CapstoneDesign.notification.dto.application.NotificationQueueBaseMessage;
import com.Hoseo.CapstoneDesign.notification.dto.application.SuccessMessage;
import com.Hoseo.CapstoneDesign.notification.listener.NotificationEventListener;
import com.Hoseo.CapstoneDesign.notification.service.NotificationService;
import com.Hoseo.CapstoneDesign.notification.service.NotificationSseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

//여기서 트렌젝션 및 SSE송신 처리
@Slf4j
@Facade
@RequiredArgsConstructor
public class NotificationFacadeImpl implements NotificationFacade{

    private final NotificationService notificationService;
    private final NotificationSseService sseService;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional(readOnly = false)
    public void successHandle(NotificationQueueBaseMessage envelope) {
        SuccessMessage data =
                objectMapper.convertValue(envelope.getData(), SuccessMessage.class);
        //나중에 게이미피케이션 되면 구현
        log.info(
                "Analysis success. jobId={}, completeQuestIds={}, newQuestIds={}, newProjectKBid={}, userViewReportId={}",
                envelope.getJobId(),
                data.getCompleteQuestIds(),
                data.getNewQuestIds(),
                data.getNewProjectKBid(),
                data.getUserViewReportId()
        );

    }

    @Override
    @Transactional(readOnly = false)
    public void failedHandle(NotificationQueueBaseMessage envelope) {
        FailMessage data =
                objectMapper.convertValue(envelope.getData(), FailMessage.class);
        //나중에 게이미피케이션 되면 구현
        log.warn(
                "Analysis failed. jobId={}, errorCode={}, errorMessage={}, httpStatus={}, retryable={}",
                envelope.getJobId(),
                data.getErrorCode(),
                data.getErrorMessage(),
                data.getHTTPStatus(),
                data.getRetryable()
        );
    }

}
