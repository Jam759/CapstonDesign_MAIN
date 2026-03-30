package com.Hoseo.CapstoneDesign.gamification.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RankingResponse {
    private Integer rank;
    private Long userId;
    private String serviceNickname;
    private Integer level;
    private Long totalExp;
}
