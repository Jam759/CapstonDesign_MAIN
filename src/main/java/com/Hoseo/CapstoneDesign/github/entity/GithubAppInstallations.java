package com.Hoseo.CapstoneDesign.github.entity;

import com.Hoseo.CapstoneDesign.global.entity.CreatableEntity;
import com.Hoseo.CapstoneDesign.user.entity.Users;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class GithubAppInstallations extends CreatableEntity {

    @Id
    private Long GithubAppInstallationsId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @Column(name = "account_id", nullable = false,unique = true)
    private Long accountId;

    @Column(name = "account_login", nullable = false)
    private String accountLogin;

}
