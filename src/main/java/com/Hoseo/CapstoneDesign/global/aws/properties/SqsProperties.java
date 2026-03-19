package com.Hoseo.CapstoneDesign.global.aws.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.aws.sqs")
public record SqsProperties(
        String analysisQueue,
        String dlqQueue,
        String notificationQueue
) {}
