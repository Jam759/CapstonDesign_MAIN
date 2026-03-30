package com.Hoseo.CapstoneDesign.notification.controller;

import com.Hoseo.CapstoneDesign.global.exception.GlobalExceptionResponse;
import com.Hoseo.CapstoneDesign.notification.dto.response.NotificationResponse;
import com.Hoseo.CapstoneDesign.notification.facade.NotificationFacade;
import com.Hoseo.CapstoneDesign.notification.service.NotificationSseService;
import com.Hoseo.CapstoneDesign.security.entity.UserDetailImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notification")
@Tag(name = "Notification", description = "알림 조회 및 SSE 구독 API")
@SecurityRequirement(name = "bearerAuth")
public class NotificationController {

    private final NotificationSseService notificationSseService;
    private final NotificationFacade facade;

    @GetMapping(value = "/sse/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(
            summary = "알림 SSE 구독",
            description = "60초 타임아웃의 SSE 연결을 생성합니다. 최초 연결 시 connect 이벤트가 전송됩니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "SSE 연결 생성 성공",
                    content = @Content(
                            mediaType = MediaType.TEXT_EVENT_STREAM_VALUE,
                            schema = @Schema(
                                    type = "string",
                                    example = "event: connect\nid: 550e8400-e29b-41d4-a716-446655440000\ndata: SSE connected\n"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(schema = @Schema(implementation = GlobalExceptionResponse.class))
            )
    })
    public SseEmitter subscribe(
            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetailImpl userDetail
    ) {
        return notificationSseService.subscribe(userDetail.getUser());
    }

    @GetMapping()
    @Operation(
            summary = "알림 목록 조회",
            description = "현재 구현은 실제 알림 저장소 대신 mock 알림 목록을 페이지 단위로 반환합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "알림 목록 조회 성공",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = NotificationResponse.class)))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(schema = @Schema(implementation = GlobalExceptionResponse.class))
            )
    })
    public ResponseEntity<List<NotificationResponse>> getNotificationList(
            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetailImpl userDetail,
            @Parameter(description = "페이지 번호(1부터 시작)", example = "1")
            @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "페이지 크기", example = "20")
            @RequestParam(defaultValue = "20") Integer size
    ) {
        // TODO : 추후 구현 현재는 mock데이터 반환
//        List<NotificationResponse> res
//                = facade.getNotification(userDetail.getUser(), page, size);
        List<NotificationResponse> res = paginate(mockNotifications(), page, size);
        return ResponseEntity.ok(res);
    }

    @PatchMapping()
    @Operation(
            summary = "알림 읽음 처리",
            description = "현재 구현은 실제 읽음 저장 없이 200 응답만 반환합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "읽음 처리 요청 성공"),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(schema = @Schema(implementation = GlobalExceptionResponse.class))
            )
    })
    public ResponseEntity<Void> markAsRead(
            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetailImpl userDetail,
            @Parameter(description = "읽음 처리할 알림 ID", example = "9001")
            @RequestParam("notificationId") Long notificationId
    ) {

        // TODO : 추후 구현 현재는 mock데이터 반환
//        facade.readNotification(userDetail.getUser(),notificationId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/unread")
    @Operation(
            summary = "읽지 않은 알림 목록 조회",
            description = "현재 구현은 실제 미읽음 저장소 대신 mock 미읽음 알림 목록을 반환합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "미읽음 알림 목록 조회 성공",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = NotificationResponse.class)))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(schema = @Schema(implementation = GlobalExceptionResponse.class))
            )
    })
    public ResponseEntity<List<NotificationResponse>> getUnReadNotification(
            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetailImpl userDetail
    ) {
        // TODO : 추후 구현 현재는 mock데이터 반환
//        List<NotificationResponse> res = facade.getUnReadNotification(userDetail.getUser());
        List<NotificationResponse> res = List.of(
                NotificationResponse.builder()
                        .notificationId(9001L)
                        .title("분석 완료")
                        .message("캡스톤 디자인 프로젝트의 최신 분석이 완료되었습니다.")
                        .linkType("PROJECT")
                        .linkId("102")
                        .build(),
                NotificationResponse.builder()
                        .notificationId(9002L)
                        .title("퀘스트 갱신")
                        .message("새로운 AI 퀘스트 2개가 생성되었습니다.")
                        .linkType("QUEST")
                        .linkId("3201")
                        .build()
        );
        return ResponseEntity.ok(res);
    }

    private List<NotificationResponse> mockNotifications() {
        return List.of(
                NotificationResponse.builder()
                        .notificationId(9001L)
                        .title("분석 완료")
                        .message("캡스톤 디자인 프로젝트의 최신 분석이 완료되었습니다.")
                        .linkType("PROJECT")
                        .linkId("102")
                        .build(),
                NotificationResponse.builder()
                        .notificationId(9002L)
                        .title("퀘스트 갱신")
                        .message("새로운 AI 퀘스트 2개가 생성되었습니다.")
                        .linkType("QUEST")
                        .linkId("3201")
                        .build(),
                NotificationResponse.builder()
                        .notificationId(9003L)
                        .title("초대 도착")
                        .message("알고리즘 스터디 프로젝트에 초대되었습니다.")
                        .linkType("INVITE")
                        .linkId("201")
                        .build()
        );
    }

    private <T> List<T> paginate(List<T> values, Integer page, Integer size) {
        int safePage = page == null || page < 1 ? 1 : page;
        int safeSize = size == null || size < 1 ? values.size() : size;
        int fromIndex = Math.min((safePage - 1) * safeSize, values.size());
        int toIndex = Math.min(fromIndex + safeSize, values.size());
        return values.subList(fromIndex, toIndex);
    }
}
