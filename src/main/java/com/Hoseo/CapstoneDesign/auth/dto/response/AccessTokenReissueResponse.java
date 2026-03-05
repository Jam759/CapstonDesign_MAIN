package com.Hoseo.CapstoneDesign.auth.dto.response;

import java.time.LocalDateTime;

public record AccessTokenReissueResponse(
        String accessToken,
        LocalDateTime accessTokenExpiredAt,
        LocalDateTime refreshTokenExpiredAt,
        boolean isNewUser
) {
}
