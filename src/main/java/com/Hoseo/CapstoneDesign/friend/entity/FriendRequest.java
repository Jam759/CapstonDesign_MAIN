package com.Hoseo.CapstoneDesign.friend.entity;

import com.Hoseo.CapstoneDesign.friend.entity.enums.FriendRequestStatus;
import com.Hoseo.CapstoneDesign.global.entity.CreatableEntity;
import com.Hoseo.CapstoneDesign.user.entity.Users;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

//친구 요청테이블(요청기록)
@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FriendRequest extends CreatableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "friend_request_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "requester_id", nullable = false)
    private Users requester;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "receiver_id", nullable = false)
    private Users receiver;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private FriendRequestStatus status;

    @Column(name = "responded_at")
    private LocalDateTime respondedAt;

    public void accept() {
        this.status = FriendRequestStatus.ACCEPTED;
        this.respondedAt = LocalDateTime.now();
    }

    public void reject() {
        this.status = FriendRequestStatus.REJECTED;
        this.respondedAt = LocalDateTime.now();
    }

    public void cancel() {
        this.status = FriendRequestStatus.CANCELED;
        this.respondedAt = LocalDateTime.now();
    }

}
