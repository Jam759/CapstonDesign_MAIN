package com.Hoseo.CapstoneDesign.notification.dto.application;

import com.Hoseo.CapstoneDesign.analysis.enums.AnalysisEventType;
import com.Hoseo.CapstoneDesign.analysis.enums.AnalysisStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationQueueBaseMessage {

    private String jobId;
    private AnalysisEventType eventType;
    private AnalysisStatus status;
    private Object data;

}
