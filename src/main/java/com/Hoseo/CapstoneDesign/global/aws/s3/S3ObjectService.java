package com.Hoseo.CapstoneDesign.global.aws.s3;

import com.Hoseo.CapstoneDesign.global.aws.exception.S3ErrorCode;
import com.Hoseo.CapstoneDesign.global.aws.exception.S3Exception;
import com.Hoseo.CapstoneDesign.global.aws.properties.S3Properties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile; // 💡 [추가] MultipartFile 임포트
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.RequestBody; // 💡 [추가] RequestBody 임포트
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest; // 💡 [추가] PutObjectRequest 임포트

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class S3ObjectService {

    private final S3Client s3Client;
    private final S3Properties s3Properties;
    private final ObjectMapper objectMapper;

    // ==========================================
    // 💡 [새로 추가된 업로드(Upload) 로직 시작]
    // ==========================================

    public void uploadFile(String objectKey, MultipartFile file) {
        // 기존 메서드들처럼 기본 버킷 이름을 가져와서 오버로딩된 메서드를 호출합니다.
        uploadFile(s3Properties.bucketName(), objectKey, file);
    }

    public void uploadFile(String bucketName, String objectKey, MultipartFile file) {
        try {
            // S3에 업로드할 메타데이터(버킷명, 경로명, 파일타입) 세팅
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .contentType(file.getContentType())
                    .build();

            // 실제 파일 스트림을 S3로 전송
            s3Client.putObject(putObjectRequest,
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

        } catch (IOException e) {
            // 파일 읽기 실패 시 기존에 만들어둔 커스텀 예외 던지기
            throw new S3Exception(S3ErrorCode.S3_IO_ERROR);
        } catch (software.amazon.awssdk.services.s3.model.S3Exception e) {
            // S3 서버 에러 발생 시 기존에 만들어둔 맵핑 로직 타기
            throw mapS3Exception(e);
        } catch (SdkException e) {
            throw new S3Exception(S3ErrorCode.S3_IO_ERROR);
        }
    }

    // ==========================================
    // 💡 [새로 추가된 업로드(Upload) 로직 끝]
    // ==========================================


    // 👇 아래부터는 기존 코드와 100% 동일합니다. 건드리지 않았습니다.
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