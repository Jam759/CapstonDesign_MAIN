package com.Hoseo.CapstoneDesign.github.util;

import io.jsonwebtoken.Jwts;
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

//github app과 통신을 위한 jwt생성 유틸
@Slf4j
@Component
public class GithubJwtUtil {
    @Value("${github.app.app-id}")
    private String appId;

    @Value("${github.app.private-key}")
    private String privateKeyPem;

    public String createAppJwt() {
        Instant now = Instant.now();

        return Jwts.builder()
                .issuer(appId)
                .issuedAt(Date.from(now.minusSeconds(30)))
                .expiration(Date.from(now.plusSeconds(540)))
                .signWith(loadPrivateKey(), Jwts.SIG.RS256)
                .compact();
    }

    private PrivateKey loadPrivateKey() {
        try {
            if (privateKeyPem == null || privateKeyPem.isBlank()) {
                throw new IllegalArgumentException("github.app.private-key 값이 비어 있습니다.");
            }

            String normalizedPem = rebuildPemFromBrokenSingleLine(privateKeyPem);

            String[] lines = normalizedPem.split("\n");
            log.info("normalized line count={}", lines.length);
            log.info("normalized first line={}", lines.length > 0 ? lines[0] : "NO_FIRST_LINE");
            log.info("normalized last line={}", lines.length > 0 ? lines[lines.length - 1] : "NO_LAST_LINE");

            try (PEMParser pemParser = new PEMParser(new StringReader(normalizedPem))) {
                Object pemObject = pemParser.readObject();

                log.info("pemObject class={}", pemObject == null ? "null" : pemObject.getClass().getName());

                if (pemObject == null) {
                    throw new IllegalArgumentException("PEMParser가 null을 반환했습니다.");
                }

                JcaPEMKeyConverter converter = new JcaPEMKeyConverter();

                if (pemObject instanceof PEMKeyPair pemKeyPair) {
                    return converter.getKeyPair(pemKeyPair).getPrivate();
                }

                if (pemObject instanceof PrivateKeyInfo privateKeyInfo) {
                    return converter.getPrivateKey(privateKeyInfo);
                }

                throw new IllegalArgumentException("지원하지 않는 PEM 키 형식: " + pemObject.getClass().getName());
            }

        } catch (Exception e) {
            log.error("GitHub App private key 로딩 실패", e);
            throw new IllegalStateException("GitHub App private key 로딩 실패", e);
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
            throw new IllegalArgumentException("PEM 헤더/푸터를 찾을 수 없습니다.");
        }

        int beginIndex = value.indexOf(beginMarker);
        int endIndex = value.indexOf(endMarker);

        if (beginIndex < 0 || endIndex < 0 || endIndex <= beginIndex) {
            throw new IllegalArgumentException("PEM 헤더/푸터 위치가 올바르지 않습니다.");
        }

        String base64Body = value.substring(beginIndex + beginMarker.length(), endIndex)
                .replaceAll("\\s+", "");

        if (base64Body.isBlank()) {
            throw new IllegalArgumentException("PEM 본문이 비어 있습니다.");
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