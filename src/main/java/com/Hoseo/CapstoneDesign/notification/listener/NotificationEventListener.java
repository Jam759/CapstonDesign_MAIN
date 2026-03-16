package com.Hoseo.CapstoneDesign.notification.listener;

import com.Hoseo.CapstoneDesign.notification.facade.NotificationFacadeImpl;
import com.Hoseo.CapstoneDesign.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

//여기서 이벤트를 받고 각 이벤트별 페이로드 처리 ㅎ
@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final NotificationFacadeImpl notificationFacade;

    /*
    예시
    @Async("notificationExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void UserLevelUpEventHandle(UserLevelUpEvent event) {
        notificationFacade.notifyLevelUp(event);
    }

     */
}
