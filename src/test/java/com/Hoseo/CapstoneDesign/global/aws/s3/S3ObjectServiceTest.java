package com.Hoseo.CapstoneDesign.global.aws.s3;

import com.Hoseo.CapstoneDesign.global.aws.exception.S3ErrorCode;
import com.Hoseo.CapstoneDesign.global.aws.exception.S3Exception;
import com.Hoseo.CapstoneDesign.global.aws.properties.S3Properties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.awscore.exception.AwsErrorDetails;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class S3ObjectServiceTest {

    @Mock
    private S3Client s3Client;

    private S3ObjectService s3ObjectService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final S3Properties s3Properties = new S3Properties("test-bucket");

    @BeforeEach
    void setUp() {
        s3ObjectService = new S3ObjectService(s3Client, s3Properties, objectMapper);
    }

    @Test
    @DisplayName("S3 오브젝트를 문자열로 조회한다")
    void getObjectAsStringSuccess() {
        when(s3Client.getObjectAsBytes(any(GetObjectRequest.class)))
                .thenReturn(ResponseBytes.fromByteArray(
                        GetObjectResponse.builder().build(),
                        "hello-s3".getBytes(StandardCharsets.UTF_8)
                ));

        String result = s3ObjectService.getObjectAsString("reports/result.txt");

        ArgumentCaptor<GetObjectRequest> captor = ArgumentCaptor.forClass(GetObjectRequest.class);
        verify(s3Client).getObjectAsBytes(captor.capture());

        assertThat(result).isEqualTo("hello-s3");
        assertThat(captor.getValue().bucket()).isEqualTo("test-bucket");
        assertThat(captor.getValue().key()).isEqualTo("reports/result.txt");
    }

    @Test
    @DisplayName("S3 오브젝트를 JSON 객체로 역직렬화한다")
    void getObjectAsJsonSuccess() {
        when(s3Client.getObjectAsBytes(any(GetObjectRequest.class)))
                .thenReturn(ResponseBytes.fromByteArray(
                        GetObjectResponse.builder().build(),
                        """
                        {"name":"report","count":3}
                        """.getBytes(StandardCharsets.UTF_8)
                ));

        SamplePayload result = s3ObjectService.getObjectAsJson("reports/result.json", SamplePayload.class);

        assertThat(result.name()).isEqualTo("report");
        assertThat(result.count()).isEqualTo(3);
    }

    @Test
    @DisplayName("S3 오브젝트를 JSON 배열로 역직렬화한다")
    void getObjectAsJsonListSuccess() {
        when(s3Client.getObjectAsBytes(any(GetObjectRequest.class)))
                .thenReturn(ResponseBytes.fromByteArray(
                        GetObjectResponse.builder().build(),
                        """
                        [{"name":"first","count":1},{"name":"second","count":2}]
                        """.getBytes(StandardCharsets.UTF_8)
                ));

        List<SamplePayload> result = s3ObjectService.getObjectAsJson(
                "reports/result-list.json",
                new TypeReference<>() {}
        );

        assertThat(result).hasSize(2);
        assertThat(result.get(0).name()).isEqualTo("first");
        assertThat(result.get(1).count()).isEqualTo(2);
    }

    @Test
    @DisplayName("존재하지 않는 S3 오브젝트 조회 시 OBJECT_NOT_FOUND 예외를 던진다")
    void getObjectBytesObjectNotFound() {
        when(s3Client.getObjectAsBytes(any(GetObjectRequest.class)))
                .thenThrow(NoSuchKeyException.builder().build());

        assertThatThrownBy(() -> s3ObjectService.getObjectBytes("missing.json"))
                .isInstanceOf(S3Exception.class)
                .extracting("errorCode")
                .isEqualTo(S3ErrorCode.S3_OBJECT_NOT_FOUND);
    }

    @Test
    @DisplayName("접근 권한이 없는 S3 오브젝트 조회 시 ACCESS_DENIED 예외를 던진다")
    void getObjectBytesAccessDenied() {
        when(s3Client.getObjectAsBytes(any(GetObjectRequest.class)))
                .thenThrow(software.amazon.awssdk.services.s3.model.S3Exception.builder()
                        .statusCode(403)
                        .awsErrorDetails(AwsErrorDetails.builder().errorCode("AccessDenied").build())
                        .build());

        assertThatThrownBy(() -> s3ObjectService.getObjectBytes("private.json"))
                .isInstanceOf(S3Exception.class)
                .extracting("errorCode")
                .isEqualTo(S3ErrorCode.S3_ACCESS_DENIED);
    }

    @Test
    @DisplayName("JSON 파싱에 실패하면 JSON_PARSE_ERROR 예외를 던진다")
    void getObjectAsJsonParseFail() {
        when(s3Client.getObjectAsBytes(any(GetObjectRequest.class)))
                .thenReturn(ResponseBytes.fromByteArray(
                        GetObjectResponse.builder().build(),
                        "not-json".getBytes(StandardCharsets.UTF_8)
                ));

        assertThatThrownBy(() -> s3ObjectService.getObjectAsJson("invalid.json", SamplePayload.class))
                .isInstanceOf(S3Exception.class)
                .extracting("errorCode")
                .isEqualTo(S3ErrorCode.S3_JSON_PARSE_ERROR);
    }

    @Test
    @DisplayName("AWS SDK 클라이언트 예외는 IO_ERROR로 변환한다")
    void getObjectBytesIoError() {
        when(s3Client.getObjectAsBytes(any(GetObjectRequest.class)))
                .thenThrow(SdkClientException.create("network error"));

        assertThatThrownBy(() -> s3ObjectService.getObjectBytes("error.json"))
                .isInstanceOf(S3Exception.class)
                .extracting("errorCode")
                .isEqualTo(S3ErrorCode.S3_IO_ERROR);
    }

    private record SamplePayload(String name, int count) {
    }
}
