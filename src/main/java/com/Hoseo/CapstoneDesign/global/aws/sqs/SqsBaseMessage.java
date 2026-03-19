package com.Hoseo.CapstoneDesign.global.aws.sqs;

public abstract class SqsBaseMessage<T> {
    private String jobId;
    private String type;// commonTable 에서 불러올것
    private T data;
}
