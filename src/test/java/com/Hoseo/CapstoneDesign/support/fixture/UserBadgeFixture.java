package com.Hoseo.CapstoneDesign.support.fixture;

import com.Hoseo.CapstoneDesign.badge.entity.UserBadge;

import java.util.Set;

public final class UserBadgeFixture {

    private UserBadgeFixture() {
    }

    public static Set<UserBadge> equippedBadgeSet(Long... userBadgeIds) {
        return Set.of(userBadgeIds).stream()
                .map(id -> UserBadge.builder()
                        .userBadgeId(id)
                        .isEquipped(true)
                        .build())
                .collect(java.util.stream.Collectors.toSet());
    }
}
