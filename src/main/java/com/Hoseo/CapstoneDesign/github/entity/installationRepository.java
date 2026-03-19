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
public class installationRepository extends CreatableEntity {

    @Id
    private Long installationRepositoryId;

    @JoinColumn(name = "github_app_installation _id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private GithubAppInstallations GithubAppInstallationsId;

    @Column(name = "full_name", nullable = false, length = 255)
    private String fullName;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "is_private", nullable = false)
    private boolean isPrivate;

}
