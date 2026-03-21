package com.Hoseo.CapstoneDesign.notification.controller;


import com.Hoseo.CapstoneDesign.notification.service.NotificationSseService;
import com.Hoseo.CapstoneDesign.security.entity.UserDetailImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sse")
public class NotificationController {

    private final NotificationSseService notificationSseService;

    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@AuthenticationPrincipal UserDetailImpl userDetail) {
        return notificationSseService.subscribe(userDetail.getUser());
    }

}
