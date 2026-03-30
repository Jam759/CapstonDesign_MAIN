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
//        List<NotificationResponse> res
//                = facade.getNotification(userDetail.getUser(), page, size);
        return ResponseEntity.ok(res);
    }

    //알림 읽기 처리
    @PatchMapping()
    public ResponseEntity<Void> markAsRead(
            @AuthenticationPrincipal UserDetailImpl userDetail,
            @RequestParam("notificationId") Long notificationId
    ) {
//        facade.readNotification(userDetail.getUser(),notificationId);
        return ResponseEntity.ok().build();
    }

    //안 읽은 알림 만 조회
    @GetMapping("/unread")
    public ResponseEntity<List<NotificationResponse>> getUnReadNotification(
            @AuthenticationPrincipal UserDetailImpl userDetail
    ) {
//        List<NotificationResponse> res = facade.getUnReadNotification(userDetail.getUser());
        return ResponseEntity.ok(res);
    }

}
