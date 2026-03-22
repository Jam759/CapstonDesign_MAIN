package com.Hoseo.CapstoneDesign.global.aws.sqs;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import org.springframework.context.annotation.Bean;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

public class SqsConfig {

    @Bean
    public SqsTemplate sqsTemplate(SqsAsyncClient client, ObjectMapper objectMapper) {
        return SqsTemplate.builder()
                .sqsAsyncClient(client)
                .configureDefaultConverter(converter -> {
                    converter.setObjectMapper(objectMapper);
                })
                .build();
    }

}
