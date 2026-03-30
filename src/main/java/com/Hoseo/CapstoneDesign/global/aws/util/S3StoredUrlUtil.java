package com.Hoseo.CapstoneDesign.global.aws.util;

import org.springframework.util.StringUtils;

import java.net.URI;

public final class S3StoredUrlUtil {

    private S3StoredUrlUtil() {
    }

    public static S3Location resolveLocation(
            String storedUrl,
            String reportBucket,
            String defaultBucket
    ) {
        if (!StringUtils.hasText(storedUrl)) {
            throw invalidStorage();
        }

        String fallbackBucket = resolveFallbackBucket(reportBucket, defaultBucket);

        if (!storedUrl.contains("://")) {
            return new S3Location(fallbackBucket, normalizeObjectKey(storedUrl));
        }

        URI uri;
        try {
            uri = URI.create(storedUrl);
        } catch (IllegalArgumentException e) {
            throw invalidStorage();
        }

        if ("s3".equalsIgnoreCase(uri.getScheme())) {
            String bucketName = StringUtils.hasText(uri.getHost()) ? uri.getHost() : fallbackBucket;
            return new S3Location(bucketName, normalizeObjectKey(uri.getPath()));
        }

        String host = uri.getHost();
        String path = normalizeObjectKey(uri.getPath());

        if (isPathStyleS3Host(host)) {
            int delimiterIndex = path.indexOf('/');
            if (delimiterIndex < 0) {
                throw invalidStorage();
            }

            String bucketName = path.substring(0, delimiterIndex);
            String objectKey = path.substring(delimiterIndex + 1);
            if (!StringUtils.hasText(bucketName) || !StringUtils.hasText(objectKey)) {
                throw invalidStorage();
            }

            return new S3Location(bucketName, objectKey);
        }

        if (isVirtualHostedStyleS3Host(host)) {
            int delimiterIndex = host.indexOf(".s3");
            String bucketName = host.substring(0, delimiterIndex);
            if (!StringUtils.hasText(bucketName)) {
                throw invalidStorage();
            }

            return new S3Location(bucketName, path);
        }

        return new S3Location(fallbackBucket, path);
    }

    private static String resolveFallbackBucket(String reportBucket, String defaultBucket) {
        if (StringUtils.hasText(reportBucket)) {
            return reportBucket;
        }

        if (!StringUtils.hasText(defaultBucket)) {
            throw invalidStorage();
        }

        return defaultBucket;
    }

    private static boolean isPathStyleS3Host(String host) {
        return StringUtils.hasText(host)
                && (host.equals("s3.amazonaws.com")
                || host.startsWith("s3.")
                || host.startsWith("s3-"));
    }

    private static boolean isVirtualHostedStyleS3Host(String host) {
        return StringUtils.hasText(host)
                && (host.contains(".s3.")
                || host.endsWith(".s3.amazonaws.com")
                || host.contains(".s3-"));
    }

    private static String normalizeObjectKey(String rawKey) {
        String normalized = rawKey == null ? null : rawKey.trim();
        while (normalized != null && normalized.startsWith("/")) {
            normalized = normalized.substring(1);
        }

        if (!StringUtils.hasText(normalized)) {
            throw invalidStorage();
        }

        return normalized;
    }

    private static IllegalArgumentException invalidStorage() {
        return new IllegalArgumentException("Invalid S3 stored url");
    }

    public record S3Location(String bucketName, String objectKey) {
    }
}
