package com.Hoseo.CapstoneDesign.user.dto.response;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.Set;

@Schema(description = "사용자 프로필 수정 결과")
public record UpdateUserInfoResponse(
        @Schema(description = "수정된 서비스 닉네임", example = "new-service-nick")
        String serviceNickname,
        @ArraySchema(schema = @Schema(description = "현재 장착된 사용자 배지 ID", example = "101"))
        Set<Long> equippedBadges,
        @Schema(description = "마지막 수정 시각", example = "2026-03-12T12:00:00")
        LocalDateTime updateDate
) {}
