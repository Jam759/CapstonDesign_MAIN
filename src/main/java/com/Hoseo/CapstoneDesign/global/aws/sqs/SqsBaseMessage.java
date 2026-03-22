package com.Hoseo.CapstoneDesign.global.aws.sqs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public final class SqsBaseMessage {
    private String jobId;
    private String type;// commonTable 에서 불러올것
    private Object data;
}
