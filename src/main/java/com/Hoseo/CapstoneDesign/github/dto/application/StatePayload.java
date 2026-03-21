package com.Hoseo.CapstoneDesign.github.dto.application;

import java.util.UUID;

public record StatePayload(
        UUID userIdentityId,
        String nonce,
        long exp,
        String returnTo
) {
}
