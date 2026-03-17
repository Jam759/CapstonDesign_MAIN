package com.Hoseo.CapstoneDesign.tmp.controller;

import com.Hoseo.CapstoneDesign.global.aws.properties.SqsProperties;
import com.Hoseo.CapstoneDesign.global.aws.sqs.SqsMessageSender;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class SqsTempController {

    private final SqsMessageSender messageSender;
    private final SqsProperties sqsProperties;
    @PostConstruct
    void init() {
        log.info("analysisQueue={}", sqsProperties.analysisQueue());
        log.info("notificationQueue={}", sqsProperties.notificationQueue());
        log.info("dlqQueue={}", sqsProperties.dlqQueue());
    }
    @PostMapping("/sqs/test/notification")
    public String testNotify() {
        TestMessage message = new TestMessage("알림큐","notify");
        messageSender.send(sqsProperties.notificationQueue(),message);
        return "test";
    }

    @PostMapping("/sqs/test/analysis")
    public String testAnalysis() {
        TestMessage message = new TestMessage("분석큐","analysis");
        messageSender.send(sqsProperties.analysisQueue(),message);
        return "test";
    }

    @PostMapping("/sqs/test/dlq")
    public String testDlq() {
        TestMessage message = new TestMessage("사망큐","dlq");
        messageSender.send(sqsProperties.dlqQueue(),message);
        return "test";
    }

    @AllArgsConstructor
    public static class TestMessage {
        public String message;
        public String type;


    }

}
