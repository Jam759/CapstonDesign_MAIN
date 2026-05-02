package com.Hoseo.CapstoneDesign.friend.entity;

import com.Hoseo.CapstoneDesign.global.entity.CreatableEntity;
import com.Hoseo.CapstoneDesign.user.entity.Users;
import jakarta.persistence.*;
import lombok.*;

// 친구 관계 테이블
// 두 유저 ID를 비교해서 작은 쪽을 user, 큰 쪽을 friendUser에 위치하게 하여 생성
// 요청자가 8, 수락자가 3이어도 결과는 항상
//  user_id = 3
//  friend_user_id = 8
@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "friendships",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_friendships_pair",
                        columnNames = {"user_id", "friend_user_id"}
                )
        }
)
public class Friendship extends CreatableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "friendship_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "friend_user_id", nullable = false)
    private Users friendUser;

}
