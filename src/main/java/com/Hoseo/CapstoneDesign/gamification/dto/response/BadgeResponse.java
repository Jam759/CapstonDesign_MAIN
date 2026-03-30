package com.Hoseo.CapstoneDesign.gamification.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BadgeResponse {

    private Long badgeId;
    private String badgeName;
    private String badgeDescription;
    private String badgeImageUrl;
    private String badgeType;
    private LocalDateTime acquiredAt;

}
