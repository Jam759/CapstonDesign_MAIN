package com.Hoseo.CapstoneDesign.notification.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "알림 응답")
public class NotificationResponse {
    @Schema(description = "알림 ID", example = "9001")
    private Long notificationId;

    @Schema(description = "알림 제목", example = "분석 완료")
    private String title;

    @Schema(description = "알림 메시지", example = "캡스톤 디자인 프로젝트의 최신 분석이 완료되었습니다.")
    private String message;

    @Schema(description = "링크 대상 유형", example = "PROJECT")
    private String linkType;

    @Schema(description = "링크 대상 식별자", example = "102")
    private String linkId;
}
