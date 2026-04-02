package com.Hoseo.CapstoneDesign.notification.listener;

import com.Hoseo.CapstoneDesign.notification.facade.NotificationFacade;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

import static com.Hoseo.CapstoneDesign.global.logging.support.LoggingConstants.TRACE_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class NotificationQueueListenerTest {

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    @Test
    @DisplayName("알림 큐 소비 시 envelope traceId를 MDC에 바인딩하고 처리 후 정리한다")
    void bindsTraceIdFromEnvelope() {
        NotificationFacade notificationFacade = mock(NotificationFacade.class);
        NotificationQueueListener listener = new NotificationQueueListener(new ObjectMapper(), notificationFacade);

        doAnswer(invocation -> {
            assertThat(MDC.get(TRACE_ID)).isEqualTo("delivery-123");
            return null;
        }).when(notificationFacade).successHandle(any());

        String body = """
                {
                  "traceId": "delivery-123",
                  "jobId": "job-1",
                  "eventType": "NORMAL_ANALYSIS_REQUEST",
                  "status": "SUCCESS",
                  "data": {
                    "completeQuestIds": [],
                    "newQuestIds": [],
                    "newProjectKBid": 10,
                    "userViewReportId": 20
                  }
                }
                """;

        listener.listen(body);

        verify(notificationFacade).successHandle(any());
        assertThat(MDC.get(TRACE_ID)).isNull();
    }
}
