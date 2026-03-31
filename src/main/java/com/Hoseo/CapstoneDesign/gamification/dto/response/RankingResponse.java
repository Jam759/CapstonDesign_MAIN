package com.Hoseo.CapstoneDesign.gamification.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "경험치 랭킹 정보")
public class RankingResponse {
    @Schema(description = "랭킹 순위", example = "1")
    private Integer rank;

    @Schema(description = "사용자 ID", example = "1001")
    private Long userId;

    @Schema(description = "서비스 닉네임", example = "commit-master")
    private String serviceNickname;

    @Schema(description = "레벨", example = "9")
    private Integer level;

    @Schema(description = "누적 경험치", example = "4820")
    private Long totalExp;
}
