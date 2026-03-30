package com.Hoseo.CapstoneDesign.global.aws.properties;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app.aws.s3")
public record S3Properties(
        @NotBlank String bucketName
) {}
