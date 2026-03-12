package com.Hoseo.CapstoneDesign.user.dto.response;

import java.time.LocalDateTime;
import java.util.Set;

public record UpdateUserInfoResponse(
        String serviceNickname,
        Set<Long> equippedBadges,
        LocalDateTime updateDate
) {}
