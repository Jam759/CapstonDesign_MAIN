package com.Hoseo.CapstoneDesign.user.event;

import java.util.UUID;

public record UserProfileChangedEvent(
        Long userId,
        UUID identityId
) {
}
