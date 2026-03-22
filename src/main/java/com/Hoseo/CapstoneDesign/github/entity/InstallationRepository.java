package com.Hoseo.CapstoneDesign.github.entity;


import com.Hoseo.CapstoneDesign.global.entity.CreatableEntity;
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
public class InstallationRepository extends CreatableEntity {

    @Id
    private Long installationRepositoryId;

    @JoinColumn(name = "github_app_installation_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private GithubAppInstallations githubAppInstallation;

    @Column(name = "full_name", nullable = false, length = 255)
    private String fullName;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "is_private", nullable = false)
    private boolean isPrivate;

    public void markGithubAppInstallation(GithubAppInstallations githubAppInstallation) {
        this.githubAppInstallation = githubAppInstallation;
    }

}
