package com.Hoseo.CapstoneDesign.security.properties;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

class JwtPropertiesTest {

    @DisplayName("ttl 변환과 secret key 생성이 정상 동작한다")
    @Test
    void ttlAndSecretKey_success() {
        JwtProperties properties = new JwtProperties(
                "issuer",
                "MDEyMzQ1Njc4OWFiY2RlZjAxMjM0NTY3ODlhYmNkZWY=",
                "MDEyMzQ1Njc4OWFiY2RlZjAxMjM0NTY3ODlhYmNkZWY=",
                15,
                7,
                "REFRESH_TOKEN",
                null,
                false,
                "Lax",
                true,
                URI.create("http://localhost:5500/index.html")
        );

        assertThat(properties.accessTokenTtl()).isEqualTo(Duration.ofMinutes(15));
        assertThat(properties.refreshTokenTtl()).isEqualTo(Duration.ofDays(7));
        assertThat(properties.accessTokenSecretKey()).isNotNull();
        assertThat(properties.refreshTokenSecretKey()).isNotNull();
    }
}

