package com.Hoseo.CapstoneDesign.user.entity;

import com.Hoseo.CapstoneDesign.common.entity.CommonGroupDetail;
import com.Hoseo.CapstoneDesign.global.entity.CreatableEntity;
import com.Hoseo.CapstoneDesign.user.entity.compositeKey.UserNotificationTriggerId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@IdClass(UserNotificationTriggerId.class)
@Table(name = "user_notification_trigger")
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class UserNotificationTrigger extends CreatableEntity {

    @Id
    @JoinColumn(name = "user_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Users user;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_notification_trigger_cmd_id", nullable = false)
    private CommonGroupDetail notifyType;

}
