package com.Hoseo.CapstoneDesign.user.dto.request;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Set;

@Schema(description = "사용자 프로필 수정 요청")
public record UserProfileUpdateRequest(
        @Schema(description = "서비스 내 표시 닉네임", example = "commit-master")
        String userServiceNickname,
        @ArraySchema(schema = @Schema(description = "대표로 장착할 사용자 배지 ID", example = "101"))
        Set<Long> equippedBadges
) {
}
