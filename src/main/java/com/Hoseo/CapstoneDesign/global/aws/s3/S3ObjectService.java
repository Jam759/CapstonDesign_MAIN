package com.Hoseo.CapstoneDesign.global.aws.s3;

import com.Hoseo.CapstoneDesign.global.aws.exception.S3ErrorCode;
import com.Hoseo.CapstoneDesign.global.aws.exception.S3Exception;
import com.Hoseo.CapstoneDesign.global.aws.properties.S3Properties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class S3ObjectService {

    private final S3Client s3Client;
    private final S3Properties s3Properties;
    private final ObjectMapper objectMapper;

    public byte[] getObjectBytes(String objectKey) {
        return getObjectBytes(s3Properties.bucketName(), objectKey);
    }

    public byte[] getObjectBytes(String bucketName, String objectKey) {
        try {
            ResponseBytes<GetObjectResponse> response = s3Client.getObjectAsBytes(buildRequest(bucketName, objectKey));
            return response.asByteArray();
        } catch (NoSuchKeyException e) {
            throw new S3Exception(S3ErrorCode.S3_OBJECT_NOT_FOUND);
        } catch (software.amazon.awssdk.services.s3.model.S3Exception e) {
            throw mapS3Exception(e);
        } catch (SdkException e) {
            throw new S3Exception(S3ErrorCode.S3_IO_ERROR);
        }
    }

    public String getObjectAsString(String objectKey) {
        return getObjectAsString(s3Properties.bucketName(), objectKey);
    }

    public String getObjectAsString(String bucketName, String objectKey) {
        return new String(getObjectBytes(bucketName, objectKey), StandardCharsets.UTF_8);
    }

    public <T> T getObjectAsJson(String objectKey, Class<T> targetType) {
        return getObjectAsJson(s3Properties.bucketName(), objectKey, targetType);
    }

    public <T> T getObjectAsJson(String bucketName, String objectKey, Class<T> targetType) {
        try {
            return objectMapper.readValue(getObjectBytes(bucketName, objectKey), targetType);
        } catch (IOException e) {
            throw new S3Exception(S3ErrorCode.S3_JSON_PARSE_ERROR);
        }
    }

    public <T> T getObjectAsJson(String objectKey, TypeReference<T> targetType) {
        return getObjectAsJson(s3Properties.bucketName(), objectKey, targetType);
    }

    public <T> T getObjectAsJson(String bucketName, String objectKey, TypeReference<T> targetType) {
        try {
            return objectMapper.readValue(getObjectBytes(bucketName, objectKey), targetType);
        } catch (IOException e) {
            throw new S3Exception(S3ErrorCode.S3_JSON_PARSE_ERROR);
        }
    }

    private GetObjectRequest buildRequest(String bucketName, String objectKey) {
        return GetObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .build();
    }

    private S3Exception mapS3Exception(software.amazon.awssdk.services.s3.model.S3Exception exception) {
        String awsErrorCode = exception.awsErrorDetails() == null
                ? null
                : exception.awsErrorDetails().errorCode();

        if (exception.statusCode() == 404 || "NoSuchKey".equals(awsErrorCode)) {
            return new S3Exception(S3ErrorCode.S3_OBJECT_NOT_FOUND);
        }

        if (exception.statusCode() == 403 || "AccessDenied".equals(awsErrorCode)) {
            return new S3Exception(S3ErrorCode.S3_ACCESS_DENIED);
        }

        return new S3Exception(S3ErrorCode.S3_IO_ERROR);
    }
}
