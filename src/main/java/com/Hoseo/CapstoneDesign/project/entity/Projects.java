package com.Hoseo.CapstoneDesign.project.entity;

import com.Hoseo.CapstoneDesign.github.entity.GithubAppInstallations;
import com.Hoseo.CapstoneDesign.github.entity.InstallationRepository;
import com.Hoseo.CapstoneDesign.global.entity.LifecycleTimestampEntity;
import com.Hoseo.CapstoneDesign.project.entity.enums.ProjectStatus;
import com.Hoseo.CapstoneDesign.project.entity.enums.ProjectType;
import com.Hoseo.CapstoneDesign.user.entity.Users;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE projects SET deleted_at = now() WHERE project_id = ?")
public class Projects extends LifecycleTimestampEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long projectId;

    @JoinColumn(name = "owner_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Users user;

    @JoinColumn(name = "github_app_installation _id", nullable = true)
    @ManyToOne(fetch = FetchType.LAZY)
    private GithubAppInstallations GithubAppInstallations;

    @JoinColumn(name = "installation_repository_id", nullable = true)
    @ManyToOne(fetch = FetchType.LAZY)
    private InstallationRepository installationRepository;

    @Column(name = "title", nullable = false, length = 50)
    private String title;

    @Column(name = "description", length = 150)
    private String description;

    @Column(name = "goal", length = 255)
    private String goal;

    @Enumerated(EnumType.STRING)
    @Column(name = "project_type", nullable = false, length = 30)
    private ProjectType projectType;

    @Enumerated(EnumType.STRING)
    @Column(name = "project_status", nullable = false)
    private ProjectStatus projectStatus;

    @Column(name = "tracked_branch", nullable = true)
    private String trackedBranch;

    public void setTrackedSetting(
            GithubAppInstallations installations,
            InstallationRepository installationRepository,
            String trackedBranch
    ) {
        this.GithubAppInstallations = installations;
        this.installationRepository = installationRepository;
        this.trackedBranch = trackedBranch;
    }

}
