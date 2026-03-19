package com.Hoseo.CapstoneDesign.github.util;

import com.Hoseo.CapstoneDesign.github.exception.GitHubErrorCode;
import com.Hoseo.CapstoneDesign.github.exception.GitHubException;
import com.Hoseo.CapstoneDesign.github.dto.application.StatePayload;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;

@Component
public class StateUtil {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${github.app.state-secret}")
    private String secret;

    private static final long EXPIRE_SECONDS = 300; // 5분

    public String createState(UUID userIdentityId) {
        try {
            StatePayload payload = new StatePayload(
                    userIdentityId,
                    UUID.randomUUID().toString(),
                    Instant.now().getEpochSecond() + EXPIRE_SECONDS
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

            return payload.userIdentityId();

        } catch (Exception e) {
            throw new GitHubException(GitHubErrorCode.GIT_HUB_APP_INVALID);
        }
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
        if (a.length() != b.length()) return false;
        int result = 0;
        for (int i = 0; i < a.length(); i++) {
            result |= a.charAt(i) ^ b.charAt(i);
        }
        return result == 0;
    }
}