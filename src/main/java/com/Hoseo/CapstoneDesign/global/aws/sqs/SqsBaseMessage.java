package com.Hoseo.CapstoneDesign.global.aws.sqs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public final class SqsBaseMessage {
    private String traceId;
    private Long userId;
    private String jobId;
    private String type;
    private Object data; //필요시 사용
}
