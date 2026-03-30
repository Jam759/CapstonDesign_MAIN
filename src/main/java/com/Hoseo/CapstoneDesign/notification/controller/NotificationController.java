package com.Hoseo.CapstoneDesign.notification.controller;


import com.Hoseo.CapstoneDesign.notification.dto.response.NotificationResponse;
import com.Hoseo.CapstoneDesign.notification.facade.NotificationFacade;
import com.Hoseo.CapstoneDesign.notification.service.NotificationSseService;
import com.Hoseo.CapstoneDesign.security.entity.UserDetailImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notification")
public class NotificationController {

    private final NotificationSseService notificationSseService;
    private final NotificationFacade facade;

    @GetMapping(value = "/sse/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@AuthenticationPrincipal UserDetailImpl userDetail) {
        return notificationSseService.subscribe(userDetail.getUser());
    }

    //최신 알림 조회
    @GetMapping()
    public ResponseEntity<List<NotificationResponse>> getNotificationList(
            @AuthenticationPrincipal UserDetailImpl userDetail,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size
    ){
        // TODO : 추후 구현 현재는 mock데이터 반환
//        List<NotificationResponse> res
//                = facade.getNotification(userDetail.getUser(), page, size);
        List<NotificationResponse> res = paginate(mockNotifications(), page, size);
        return ResponseEntity.ok(res);
    }

    //알림 읽기 처리
    @PatchMapping()
    public ResponseEntity<Void> markAsRead(
            @AuthenticationPrincipal UserDetailImpl userDetail,
            @RequestParam("notificationId") Long notificationId
    ) {

        // TODO : 추후 구현 현재는 mock데이터 반환
//        facade.readNotification(userDetail.getUser(),notificationId);
        return ResponseEntity.ok().build();
    }

    //안 읽은 알림 만 조회
    @GetMapping("/unread")
    public ResponseEntity<List<NotificationResponse>> getUnReadNotification(
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
