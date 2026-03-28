package com.Hoseo.CapstoneDesign.notification.facade;

import com.Hoseo.CapstoneDesign.notification.dto.application.NotificationQueueBaseMessage;

public interface NotificationFacade {
    void successHandle(NotificationQueueBaseMessage envelope);

    void failedHandle(NotificationQueueBaseMessage envelope);
}
