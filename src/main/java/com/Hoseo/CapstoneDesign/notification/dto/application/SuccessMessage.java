package com.Hoseo.CapstoneDesign.notification.dto.application;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SuccessMessage {
    private List<Long> completeQuestIds;
    private List<Long> newQuestIds;
    private Long newProjectKBid;
    private Long userViewReportId;
}
