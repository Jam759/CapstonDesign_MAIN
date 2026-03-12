package com.Hoseo.CapstoneDesign.user.dto.response;

import java.util.List;

public record UserProfileResponse(
        String tier, //TODO : 이거 안하기로 하지 않았나?
        Short level,
        Long totalExp,
        String gitHubId, //TODO: 이거 관련해서 찾아보고 가능한지 볼것(어떤 ID인지 oauthID인지 아님 깃헙 내 유니크 값인지)
        String userServiceNickname,
        List<Long> equippedBadges
) {}
