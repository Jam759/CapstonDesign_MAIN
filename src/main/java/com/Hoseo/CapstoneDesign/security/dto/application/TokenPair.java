package com.Hoseo.CapstoneDesign.security.dto.application;

import java.time.LocalDateTime;

public record TokenPair(
        String accessToken,
        String refreshToken,
        LocalDateTime accessTokenExpiredAt,
        LocalDateTime refreshTokenExpiredAt
) {
}
