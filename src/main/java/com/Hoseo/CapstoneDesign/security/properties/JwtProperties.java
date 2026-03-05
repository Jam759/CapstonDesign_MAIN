package com.Hoseo.CapstoneDesign.security.properties;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.crypto.SecretKey;
import java.net.URI;
import java.time.Duration;

@Validated
@ConfigurationProperties(prefix = "auth.jwt")
public record JwtProperties(
        @NotBlank String issuer,

        @NotBlank String accessKey,
        @NotBlank String refreshKey,

        @Min(1) long accessTtl,    // minutes
        @Min(1) long refreshTtl,   // days

        @NotBlank String cookieName,
        String cookieDomain,       // "" 가능
        @NotNull Boolean cookieSecure,
        @NotBlank String cookieSamesite,
        @NotNull Boolean cookieHttpOnly,

        @NotNull URI frontRedirectUrl
) {
    public SecretKey accessTokenSecretKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(accessKey));
    }

    public SecretKey refreshTokenSecretKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(refreshKey));
    }

    public Duration accessTokenTtl() {
        return Duration.ofMinutes(accessTtl);
    }

    public Duration refreshTokenTtl() {
        return Duration.ofDays(refreshTtl);
    }
}