package com.Hoseo.CapstoneDesign.github.util;

import com.Hoseo.CapstoneDesign.github.exception.GitHubErrorCode;
import com.Hoseo.CapstoneDesign.github.exception.GitHubException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@Component
public class GitHubWebhookUtil {

    @Value("${github.webhook.secret}")
    private String secret;

    public void verify(String signature256, String rawBody) {
        if (signature256 == null || signature256.isBlank()) {
            throw new GitHubException(GitHubErrorCode.GIT_HUB_WEBHOOK_SIGNATURE_MISSING);
        }

        String expected = "sha256=" + hmacSha256(rawBody, secret);

        boolean matches = MessageDigest.isEqual(
                expected.getBytes(StandardCharsets.UTF_8),
                signature256.getBytes(StandardCharsets.UTF_8)
        );

        if (!matches) {
            throw new GitHubException(GitHubErrorCode.GIT_HUB_WEBHOOK_SIGNATURE_INVALID);
        }
    }

    private String hmacSha256(String body, String secret) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec keySpec = new SecretKeySpec(
                    secret.getBytes(StandardCharsets.UTF_8),
                    "HmacSHA256"
            );
            mac.init(keySpec);

            byte[] hash = mac.doFinal(body.getBytes(StandardCharsets.UTF_8));

            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();

        } catch (Exception e) {
            throw new GitHubException(GitHubErrorCode.GIT_HUB_WEBHOOK_SIGNATURE_CREATE_ERROR);
        }
    }
}