package com.Hoseo.CapstoneDesign.auth.dto.application;

import java.time.LocalDateTime;

public record TokenPair(
        String accessToken,
        String refreshToken,
        LocalDateTime accessTokenExpiredAt,
        LocalDateTime refreshTokenExpiredAt,
        boolean isNewUser
) {
}
