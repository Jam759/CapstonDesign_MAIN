package com.Hoseo.CapstoneDesign.analysis.entity;

import com.Hoseo.CapstoneDesign.analysis.entity.enums.AnalysisJobStatus;
import com.Hoseo.CapstoneDesign.global.entity.CreatableEntity;
import com.Hoseo.CapstoneDesign.user.entity.Users;
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
@Table(name = "analysis_jobs")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AnalysisJob extends CreatableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "analysis_job_id", nullable = false)
    private Long analysisJobId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @Column(name = "repo_url", nullable = false, columnDefinition = "TEXT")
    private String repoUrl;

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

    @Column(name = "delivery_id", nullable = false, length = 255)
    private String deliveryId;

}
