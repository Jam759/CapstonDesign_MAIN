package com.Hoseo.CapstoneDesign.analysis.entity;

import com.Hoseo.CapstoneDesign.analysis.entity.enums.ReportType;
import com.Hoseo.CapstoneDesign.global.entity.CreatableEntity;
import com.Hoseo.CapstoneDesign.project.entity.Projects;
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

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "project_analysis_reports")
public class ProjectAnalysisReport extends CreatableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_meta_reports_id", nullable = false)
    private Long projectAnalysisReportId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Projects project;

    @Enumerated(EnumType.STRING)
    @Column(name = "report_type", nullable = false)
    private ReportType reportType;

    @Column(name = "version", nullable = false)
    private Integer version;

    @Column(name = "before_commit_hash", columnDefinition = "TEXT")
    private String beforeCommitHash;

    @Column(name = "after_commit_hash", columnDefinition = "TEXT")
    private String afterCommitHash;

    @Column(name = "analysis_with_report_id", nullable = true)
    private Long analysisWithReport;

    @Column(name = "s3_bucket", length = 100)
    private String s3Bucket;

    @Column(name = "stored_url", columnDefinition = "TEXT")
    private String storedUrl;

    @Column(name = "size_bytes")
    private Long sizeBytes;
}
