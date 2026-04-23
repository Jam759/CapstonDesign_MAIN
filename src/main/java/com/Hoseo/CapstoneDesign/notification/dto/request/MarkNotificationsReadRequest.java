package com.Hoseo.CapstoneDesign.notification.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@Schema(description = "알림 읽음 처리 요청")
public class MarkNotificationsReadRequest {
    @Schema(description = "읽음 처리할 알림 ID 목록", example = "[9001, 9002]")
    private List<Long> ids;
}
