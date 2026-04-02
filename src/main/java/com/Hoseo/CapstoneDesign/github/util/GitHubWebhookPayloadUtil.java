package com.Hoseo.CapstoneDesign.github.util;

import com.Hoseo.CapstoneDesign.github.exception.GitHubErrorCode;
import com.Hoseo.CapstoneDesign.github.exception.GitHubException;
import com.fasterxml.jackson.databind.JsonNode;

public final class GitHubWebhookPayloadUtil {

    private static final String BRANCH_REF_PREFIX = "refs/heads/";

    private GitHubWebhookPayloadUtil() {
    }

    public static long requireLong(JsonNode payload, String... pathSegments) {
        return requireNode(payload, pathSegments).asLong();
    }

    public static String requireText(JsonNode payload, String... pathSegments) {
        return requireNode(payload, pathSegments).asText();
    }

    public static Long optionalLong(JsonNode payload, String... pathSegments) {
        JsonNode node = getNode(payload, pathSegments);
        if (node.isMissingNode() || node.isNull()) {
            return null;
        }
        return node.asLong();
    }

    public static String optionalText(JsonNode payload, String... pathSegments) {
        JsonNode node = getNode(payload, pathSegments);
        if (node.isMissingNode() || node.isNull()) {
            return null;
        }
        return node.asText(null);
    }

    public static String extractBranchName(String ref) {
        if (ref == null || !ref.startsWith(BRANCH_REF_PREFIX)) {
            throw new GitHubException(GitHubErrorCode.GIT_HUB_WEBHOOK_UNSUPPORTED_ERROR);
        }
        return ref.substring(BRANCH_REF_PREFIX.length());
    }

    private static JsonNode requireNode(JsonNode payload, String... pathSegments) {
        JsonNode node = getNode(payload, pathSegments);
        if (node.isMissingNode() || node.isNull()) {
            throw new GitHubException(GitHubErrorCode.GIT_HUB_WEBHOOK_UNSUPPORTED_ERROR);
        }
        return node;
    }

    private static JsonNode getNode(JsonNode payload, String... pathSegments) {
        JsonNode current = payload;
        for (String pathSegment : pathSegments) {
            current = current.path(pathSegment);
        }
        return current;
    }
}
