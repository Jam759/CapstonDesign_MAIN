package com.Hoseo.CapstoneDesign.global.aws.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class S3StoredUrlUtilTest {

    @Test
    @DisplayName("resolves a plain object key with the fallback bucket")
    void resolvePlainObjectKey() {
        S3StoredUrlUtil.S3Location location = S3StoredUrlUtil.resolveLocation(
                "reports/user-view.json",
                "report-bucket",
                "default-bucket"
        );

        assertThat(location.bucketName()).isEqualTo("report-bucket");
        assertThat(location.objectKey()).isEqualTo("reports/user-view.json");
    }

    @Test
    @DisplayName("resolves s3 scheme urls")
    void resolveS3SchemeUrl() {
        S3StoredUrlUtil.S3Location location = S3StoredUrlUtil.resolveLocation(
                "s3://analysis-bucket/reports/user-view.json",
                null,
                "default-bucket"
        );

        assertThat(location.bucketName()).isEqualTo("analysis-bucket");
        assertThat(location.objectKey()).isEqualTo("reports/user-view.json");
    }

    @Test
    @DisplayName("resolves virtual hosted s3 urls")
    void resolveVirtualHostedS3Url() {
        S3StoredUrlUtil.S3Location location = S3StoredUrlUtil.resolveLocation(
                "https://analysis-bucket.s3.ap-northeast-2.amazonaws.com/reports/user-view.json",
                null,
                "default-bucket"
        );

        assertThat(location.bucketName()).isEqualTo("analysis-bucket");
        assertThat(location.objectKey()).isEqualTo("reports/user-view.json");
    }

    @Test
    @DisplayName("resolves path style s3 urls")
    void resolvePathStyleS3Url() {
        S3StoredUrlUtil.S3Location location = S3StoredUrlUtil.resolveLocation(
                "https://s3.ap-northeast-2.amazonaws.com/analysis-bucket/reports/user-view.json",
                null,
                "default-bucket"
        );

        assertThat(location.bucketName()).isEqualTo("analysis-bucket");
        assertThat(location.objectKey()).isEqualTo("reports/user-view.json");
    }

    @Test
    @DisplayName("throws when the stored url is blank")
    void resolveBlankStoredUrl() {
        assertThatThrownBy(() -> S3StoredUrlUtil.resolveLocation(" ", null, "default-bucket"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
