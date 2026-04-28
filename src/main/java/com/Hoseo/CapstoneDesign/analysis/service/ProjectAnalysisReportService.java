package com.Hoseo.CapstoneDesign.analysis.service;

import com.Hoseo.CapstoneDesign.analysis.cache.service.ProjectAnalysisUserViewCacheService;
import com.Hoseo.CapstoneDesign.analysis.dto.application.ProjectAnalysisUserViewResponse;
import com.Hoseo.CapstoneDesign.analysis.entity.ProjectAnalysisReport;
import com.Hoseo.CapstoneDesign.analysis.entity.enums.ReportType;
import com.Hoseo.CapstoneDesign.analysis.exception.ProjectAnalysisReportErrorCode;
import com.Hoseo.CapstoneDesign.analysis.exception.ProjectAnalysisReportException;
import com.Hoseo.CapstoneDesign.analysis.repository.ProjectAnalysisReportRepository;
import com.Hoseo.CapstoneDesign.global.aws.properties.S3Properties;
import com.Hoseo.CapstoneDesign.global.aws.s3.S3ObjectService;
import com.Hoseo.CapstoneDesign.global.aws.util.S3StoredUrlUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProjectAnalysisReportService {

    private final ProjectAnalysisReportRepository repository;
    private final S3ObjectService s3ObjectService;
    private final S3Properties s3Properties;
    private final ProjectAnalysisUserViewCacheService cacheService;

    public ProjectAnalysisUserViewResponse getRecentUserView(Long projectId) {
        return getUserView(projectId, null);
    }

    public ProjectAnalysisReport getById(Long reportId) {
        return repository.findById(reportId)
                .orElseThrow(() -> new ProjectAnalysisReportException(ProjectAnalysisReportErrorCode.ANALYSIS_USER_VIEW_NOT_FOUND));
    }

    public ProjectAnalysisUserViewResponse getUserView(Long projectId, Integer version) {
        return cacheService.findUserView(projectId, version)
                .orElseGet(() -> loadUserView(projectId, version));
    }

    private ProjectAnalysisUserViewResponse loadUserView(Long projectId, Integer version) {
        ProjectAnalysisReport report = findUserViewReport(projectId, version);

        S3StoredUrlUtil.S3Location location;
        try {
            location = S3StoredUrlUtil.resolveLocation(
                    report.getStoredUrl(),
                    report.getS3Bucket(),
                    s3Properties.bucketName()
            );
        } catch (IllegalArgumentException e) {
            throw new ProjectAnalysisReportException(ProjectAnalysisReportErrorCode.ANALYSIS_REPORT_STORAGE_INVALID);
        }

        ProjectAnalysisUserViewResponse response = s3ObjectService.getObjectAsJson(
                location.bucketName(),
                location.objectKey(),
                ProjectAnalysisUserViewResponse.class
        );

        cacheService.saveUserView(projectId, version, report.getVersion(), response);
        return response;
    }

    private ProjectAnalysisReport findUserViewReport(Long projectId, Integer version) {
        if (version == null) {
            return repository
                    .findTopByProjectProjectIdAndReportTypeOrderByVersionDescProjectAnalysisReportIdDesc(
                            projectId,
                            ReportType.USER_VIEW
                    )
                    .orElseThrow(() -> new ProjectAnalysisReportException(ProjectAnalysisReportErrorCode.ANALYSIS_USER_VIEW_NOT_FOUND));
        }

        return repository
                .findByProjectProjectIdAndReportTypeAndVersion(
                        projectId,
                        ReportType.USER_VIEW,
                        version
                )
                .orElseThrow(() -> new ProjectAnalysisReportException(ProjectAnalysisReportErrorCode.ANALYSIS_USER_VIEW_NOT_FOUND));
    }

}
