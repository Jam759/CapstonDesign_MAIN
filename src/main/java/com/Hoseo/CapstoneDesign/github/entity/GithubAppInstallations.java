package com.Hoseo.CapstoneDesign.github.entity;

import com.Hoseo.CapstoneDesign.global.entity.CreatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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

    @Column(name = "account_id", nullable = false,unique = true)
    private Long accountId;

    @Column(name = "account_login", nullable = false)
    private Long accountLogin;

}
