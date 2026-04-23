package com.Hoseo.CapstoneDesign.notification.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "알림 응답")
public class NotificationResponse {
    @Schema(description = "알림 ID", example = "9001")
    private Long id;

    @Schema(description = "알림 타입 (링크 대상 유형)", example = "PROJECT")
    private String type;

    @Schema(description = "알림 메시지", example = "캡스톤 디자인 프로젝트의 최신 분석이 완료되었습니다.")
    private String message;

    @Schema(description = "읽음 여부", example = "false")
    private Boolean read;

    @Schema(description = "알림 생성 시각", example = "2026-04-23T10:15:00")
    private LocalDateTime createdAt;

    @Schema(description = "링크 대상 식별자", example = "102")
    private String linkId;
}
