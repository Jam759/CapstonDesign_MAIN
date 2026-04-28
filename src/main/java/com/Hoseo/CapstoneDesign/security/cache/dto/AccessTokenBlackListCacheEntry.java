package com.Hoseo.CapstoneDesign.security.cache.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record AccessTokenBlackListCacheEntry(
        UUID jti,
        String tokenHash,
        LocalDateTime logoutTime,
        LocalDateTime expiresAt,
        Long userId
) {
}
