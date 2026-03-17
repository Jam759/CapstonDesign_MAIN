package com.Hoseo.CapstoneDesign.global.aws.sqs;

import io.awspring.cloud.sqs.operations.SqsTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SqsMessageSender {
    private final SqsTemplate sqsTemplate;

    public <T> void send(String queueName, T payload) {
        sqsTemplate.send(queueName, payload);
    }
}
