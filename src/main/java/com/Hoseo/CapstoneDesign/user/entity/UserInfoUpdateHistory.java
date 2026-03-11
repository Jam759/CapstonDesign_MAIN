package com.Hoseo.CapstoneDesign.user.entity;

import com.Hoseo.CapstoneDesign.user.entity.enums.SystemRole;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user_info_update_history") // ERD original table: 유저정보변경기록
public class UserInfoUpdateHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "uiuh_id", nullable = false)
    private Long userInfoUpdateHistoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @Column(name = "previous_nickname", length = 100)
    private String previousNickname;

    @Column(name = "new_nickname", length = 100)
    private String newNickname;

    @Column(name = "previous_oauth_nickname", length = 255)
    private String previousOauthNickname;

    @Column(name = "new_oauth_nickname", length = 255)
    private String newOauthNickname;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "updated_by", nullable = false)
    private SystemRole updatedBy;
}
