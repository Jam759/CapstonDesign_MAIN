package com.Hoseo.CapstoneDesign.analysis.entity;

import com.Hoseo.CapstoneDesign.analysis.entity.enums.AnalysisJobStatus;
import com.Hoseo.CapstoneDesign.github.entity.GithubAppInstallations;
import com.Hoseo.CapstoneDesign.github.entity.InstallationRepository;
import com.Hoseo.CapstoneDesign.global.entity.CreatableEntity;
import com.Hoseo.CapstoneDesign.user.entity.Users;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@Table(name = "analysis_jobs")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AnalysisJob extends CreatableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "analysis_job_id", nullable = false)
    private Long analysisJobId;

    @JoinColumn(name = "github_app_installation_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private GithubAppInstallations githubAppInstallation;

    @JoinColumn(name = "installation_repository_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private InstallationRepository installationRepository;

    @Column(name = "before_commit_hash", columnDefinition = "TEXT")
    private String beforeCommitHash;

    @Column(name = "after_commit_hash", columnDefinition = "TEXT")
    private String afterCommitHash;

    @Column(name = "branch", nullable = false, length = 100)
    private String branch;

    @Enumerated(EnumType.STRING)
    @Column(name = "job_status", nullable = false)
    private AnalysisJobStatus jobStatus;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @Column(name = "retry_count", nullable = false)
    private Short retryCount;

    @Column(name = "delivery_id", nullable = false, length = 255, unique = true)
    private String deliveryId;

    public void updateJobStatus(AnalysisJobStatus analysisJobStatus) {
        this.jobStatus = analysisJobStatus;
    }
}
