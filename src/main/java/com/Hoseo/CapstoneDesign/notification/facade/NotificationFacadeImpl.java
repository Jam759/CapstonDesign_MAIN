package com.Hoseo.CapstoneDesign.notification.facade;

import com.Hoseo.CapstoneDesign.global.annotation.Facade;
import com.Hoseo.CapstoneDesign.notification.listener.NotificationEventListener;
import com.Hoseo.CapstoneDesign.notification.service.NotificationService;
import com.Hoseo.CapstoneDesign.notification.service.NotificationSseService;
import lombok.RequiredArgsConstructor;

//여기서 트렌젝션 및 SSE송신 처리
@Facade
@RequiredArgsConstructor
public class NotificationFacadeImpl {

    private final NotificationService notificationService;
    private final NotificationSseService sseService;

    /* 예시
     주의 : 흐름이나 문법 틀릴 수도 있음
            대신 엔티티 저장 및 알림 발송 response Dto 생성 및 알림 송신을 보장해야함

    @Transactional(readOnly = false)
    public void notifyLevelUp(UserLevelUpEvent event) {
        SseBaseResponse<UserLevelUpPayload> response
            = NotificationDtoFactory.toUserLevelUpPayload(event);

        SseNotification entity = notificationEntityFactory.toEntity(event);
        notificationRepository.save(entity);
        sseService.send(event.getUserId(), response);
    }

    * */
}
