package com.Hoseo.CapstoneDesign.github.util;

import com.Hoseo.CapstoneDesign.github.exception.GitHubErrorCode;
import com.Hoseo.CapstoneDesign.github.exception.GitHubException;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.StringReader;
import java.security.PrivateKey;
import java.time.Instant;
import java.util.Date;

// github app과 통신을 위한 jwt 생성 유틸
@Slf4j
@Component
public class GithubJwtUtil {

    @Value("${github.app.app-id}")
    private String appId;

    @Value("${github.app.private-key}")
    private String privateKeyPem;

    private volatile PrivateKey privateKey;

    @PostConstruct
    public void init() {
        this.privateKey = parsePrivateKey();
    }

    public String createAppJwt() {
        try {
            Instant now = Instant.now();

            return Jwts.builder()
                    .issuer(appId)
                    .issuedAt(Date.from(now.minusSeconds(30)))
                    .expiration(Date.from(now.plusSeconds(540)))
                    .signWith(getOrLoadPrivateKey(), Jwts.SIG.RS256)
                    .compact();
        } catch (GitHubException e) {
            throw e;
        } catch (Exception e) {
            throw new GitHubException(GitHubErrorCode.GIT_HUB_APP_JWT_CREATE_ERROR);
        }
    }

    private PrivateKey getOrLoadPrivateKey() {
        if (privateKey != null) {
            return privateKey;
        }

        synchronized (this) {
            if (privateKey != null) {
                return privateKey;
            }
            privateKey = parsePrivateKey();
            return privateKey;
        }
    }

    private PrivateKey parsePrivateKey() {
        try {
            if (privateKeyPem == null || privateKeyPem.isBlank()) {
                throw new GitHubException(GitHubErrorCode.GIT_HUB_PRIVATE_KEY_EMPTY);
            }

            String normalizedPem = rebuildPemFromBrokenSingleLine(privateKeyPem);

            try (PEMParser pemParser = new PEMParser(new StringReader(normalizedPem))) {
                Object pemObject = pemParser.readObject();

                if (pemObject == null) {
                    throw new GitHubException(GitHubErrorCode.GIT_HUB_PRIVATE_KEY_INVALID_FORMAT);
                }

                JcaPEMKeyConverter converter = new JcaPEMKeyConverter();

                if (pemObject instanceof PEMKeyPair pemKeyPair) {
                    return converter.getKeyPair(pemKeyPair).getPrivate();
                }

                if (pemObject instanceof PrivateKeyInfo privateKeyInfo) {
                    return converter.getPrivateKey(privateKeyInfo);
                }

                throw new GitHubException(GitHubErrorCode.GIT_HUB_PRIVATE_KEY_INVALID_FORMAT);
            }

        } catch (GitHubException e) {
            throw e;
        } catch (Exception e) {
            throw new GitHubException(GitHubErrorCode.GIT_HUB_PRIVATE_KEY_LOAD_ERROR);
        }
    }

    private String rebuildPemFromBrokenSingleLine(String rawPem) {
        String value = rawPem.trim()
                .replace("\uFEFF", "")
                .replace("\"", "")
                .replace("\\r", "")
                .replace("\r", "")
                .replace("\\\\n", "\n")
                .replace("\\n", "\n");

        final String beginRsa = "-----BEGIN RSA PRIVATE KEY-----";
        final String endRsa = "-----END RSA PRIVATE KEY-----";
        final String beginPkcs8 = "-----BEGIN PRIVATE KEY-----";
        final String endPkcs8 = "-----END PRIVATE KEY-----";

        String beginMarker;
        String endMarker;

        if (value.contains(beginRsa) && value.contains(endRsa)) {
            beginMarker = beginRsa;
            endMarker = endRsa;
        } else if (value.contains(beginPkcs8) && value.contains(endPkcs8)) {
            beginMarker = beginPkcs8;
            endMarker = endPkcs8;
        } else {
            throw new GitHubException(GitHubErrorCode.GIT_HUB_PRIVATE_KEY_INVALID_FORMAT);
        }

        int beginIndex = value.indexOf(beginMarker);
        int endIndex = value.indexOf(endMarker);

        if (beginIndex < 0 || endIndex < 0 || endIndex <= beginIndex) {
            throw new GitHubException(GitHubErrorCode.GIT_HUB_PRIVATE_KEY_INVALID_FORMAT);
        }

        String base64Body = value.substring(beginIndex + beginMarker.length(), endIndex)
                .replaceAll("\\s+", "");

        if (base64Body.isBlank()) {
            throw new GitHubException(GitHubErrorCode.GIT_HUB_PRIVATE_KEY_INVALID_FORMAT);
        }

        StringBuilder sb = new StringBuilder();
        sb.append(beginMarker).append('\n');

        for (int i = 0; i < base64Body.length(); i += 64) {
            int end = Math.min(i + 64, base64Body.length());
            sb.append(base64Body, i, end).append('\n');
        }

        sb.append(endMarker);

        return sb.toString();
    }
}