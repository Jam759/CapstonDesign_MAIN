package com.Hoseo.CapstoneDesign.github.util;

import com.Hoseo.CapstoneDesign.github.dto.application.StatePayload;
import com.Hoseo.CapstoneDesign.github.exception.GitHubErrorCode;
import com.Hoseo.CapstoneDesign.github.exception.GitHubException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;

@Component
public class StateUtil {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${github.app.state-secret}")
    private String secret;

    @Value("${app.frontend.baseUrl}")
    private String frontendBaseUrl;

    private static final long EXPIRE_SECONDS = 300; // 5분

    public String createState(UUID userIdentityId, String returnTo) {
        try {
            StatePayload payload = new StatePayload(
                    userIdentityId,
                    UUID.randomUUID().toString(),
                    Instant.now().getEpochSecond() + EXPIRE_SECONDS,
                    sanitizeReturnTo(returnTo)
            );

            String json = objectMapper.writeValueAsString(payload);
            String encoded = base64UrlEncode(json.getBytes(StandardCharsets.UTF_8));
            String signature = sign(encoded);

            return encoded + "." + signature;

        } catch (Exception e) {
            throw new GitHubException(GitHubErrorCode.GIT_HUB_STATE_CREATE_ERROR);
        }
    }

    public UUID verifyAndExtractStateId(String state) {
        return verifyAndExtractStatePayload(state).userIdentityId();
    }

    public StatePayload verifyAndExtractStatePayload(String state) {
        try {
            String[] parts = state.split("\\.");
            if (parts.length != 2) {
                throw new GitHubException(GitHubErrorCode.GIT_HUB_APP_INVALID);
            }

            String encoded = parts[0];
            String signature = parts[1];

            String expected = sign(encoded);

            if (!constantTimeEquals(signature, expected)) {
                throw new GitHubException(GitHubErrorCode.GIT_HUB_APP_INVALID);
            }

            String json = new String(base64UrlDecode(encoded), StandardCharsets.UTF_8);
            StatePayload payload = objectMapper.readValue(json, StatePayload.class);

            if (payload.exp() < Instant.now().getEpochSecond()) {
                throw new GitHubException(GitHubErrorCode.GIT_HUB_APP_INVALID);
            }

            return new StatePayload(
                    payload.userIdentityId(),
                    payload.nonce(),
                    payload.exp(),
                    sanitizeReturnTo(payload.returnTo())
            );

        } catch (GitHubException e) {
            throw e;
        } catch (Exception e) {
            throw new GitHubException(GitHubErrorCode.GIT_HUB_APP_INVALID);
        }
    }

    public URI buildDefaultRedirectUri() {
        String baseUrl = frontendBaseUrl.endsWith("/")
                ? frontendBaseUrl.substring(0, frontendBaseUrl.length() - 1)
                : frontendBaseUrl;

        return URI.create(baseUrl);
    }

    public URI buildRedirectUri(String state) {
        StatePayload payload = verifyAndExtractStatePayload(state);

        String baseUrl = frontendBaseUrl.endsWith("/")
                ? frontendBaseUrl.substring(0, frontendBaseUrl.length() - 1)
                : frontendBaseUrl;

        String returnTo = payload.returnTo();

        return URI.create(baseUrl + returnTo);
    }

    private String sanitizeReturnTo(String returnTo) {
        if (returnTo == null || returnTo.isBlank()) {
            return "/";
        }

        String trimmed = returnTo.trim();

        if (!trimmed.startsWith("/")) {
            return "/";
        }

        if (trimmed.startsWith("//")) {
            return "/";
        }

        String lower = trimmed.toLowerCase();
        if (lower.startsWith("http://")
                || lower.startsWith("https://")
                || lower.startsWith("javascript:")) {
            return "/";
        }

        return trimmed;
    }

    private String sign(String data) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        byte[] raw = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return base64UrlEncode(raw);
    }

    private String base64UrlEncode(byte[] bytes) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private byte[] base64UrlDecode(String str) {
        return Base64.getUrlDecoder().decode(str);
    }

    private boolean constantTimeEquals(String a, String b) {
        if (a == null || b == null || a.length() != b.length()) {
            return false;
        }

        int result = 0;
        for (int i = 0; i < a.length(); i++) {
            result |= a.charAt(i) ^ b.charAt(i);
        }
        return result == 0;
    }
}