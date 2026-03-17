package com.Hoseo.CapstoneDesign.tmp;

import com.Hoseo.CapstoneDesign.tmp.controller.SqsTempController;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SqsReciver {

    @SqsListener("${app.aws.sqs.analysis-queue}")
    public void receiveAnalysisQueue(SqsTempController.TestMessage message) {
        log.info("message: {}", message);
    }

    @SqsListener("${app.aws.sqs.dlq-queue}")
    public void receiveDLQQueue(SqsTempController.TestMessage message) {
        log.info("message: {}", message);
    }

    @SqsListener("${app.aws.sqs.notification-queue}")
    public void receiveNotifyQueue(SqsTempController.TestMessage message) {
        log.info("message: {}", message);
    }

}
