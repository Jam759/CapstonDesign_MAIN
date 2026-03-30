package com.Hoseo.CapstoneDesign.analysis.service;

import com.Hoseo.CapstoneDesign.analysis.dto.application.ProjectAnalysisUserViewResponse;
import com.Hoseo.CapstoneDesign.analysis.entity.ProjectAnalysisReport;
import com.Hoseo.CapstoneDesign.analysis.entity.enums.ReportType;
import com.Hoseo.CapstoneDesign.analysis.exception.AnalysisErrorCode;
import com.Hoseo.CapstoneDesign.analysis.exception.AnalysisException;
import com.Hoseo.CapstoneDesign.analysis.repository.ProjectAnalysisReportRepository;
import com.github.benmanes.caffeine.cache.Cache;
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
    private final Cache<String, Object> simpleCache;

    public ProjectAnalysisUserViewResponse getRecentUserView(Long projectId) {
        return getUserView(projectId, null);
    }

    public ProjectAnalysisUserViewResponse getUserView(Long projectId, Integer version) {
        ProjectAnalysisUserViewResponse cached = getCachedUserView(projectId, version);
        if (cached != null) {
            return cached;
        }

        ProjectAnalysisReport report = findUserViewReport(projectId, version);

        S3StoredUrlUtil.S3Location location;
        try {
            location = S3StoredUrlUtil.resolveLocation(
                    report.getStoredUrl(),
                    report.getS3Bucket(),
                    s3Properties.bucketName()
            );
        } catch (IllegalArgumentException e) {
            throw new AnalysisException(AnalysisErrorCode.ANALYSIS_REPORT_STORAGE_INVALID);
        }

        ProjectAnalysisUserViewResponse response = s3ObjectService.getObjectAsJson(
                location.bucketName(),
                location.objectKey(),
                ProjectAnalysisUserViewResponse.class
        );

        cacheUserView(projectId, version, report.getVersion(), response);
        return response;
    }

    private ProjectAnalysisReport findUserViewReport(Long projectId, Integer version) {
        if (version == null) {
            return repository
                    .findTopByProjectProjectIdAndReportTypeOrderByVersionDescProjectAnalysisReportIdDesc(
                            projectId,
                            ReportType.USER_VIEW
                    )
                    .orElseThrow(() -> new AnalysisException(AnalysisErrorCode.ANALYSIS_USER_VIEW_NOT_FOUND));
        }

        return repository
                .findByProjectProjectIdAndReportTypeAndVersion(
                        projectId,
                        ReportType.USER_VIEW,
                        version
                )
                .orElseThrow(() -> new AnalysisException(AnalysisErrorCode.ANALYSIS_USER_VIEW_NOT_FOUND));
    }

    private ProjectAnalysisUserViewResponse getCachedUserView(Long projectId, Integer version) {
        Object cached = simpleCache.getIfPresent(buildCacheKey(projectId, version));
        if (cached instanceof ProjectAnalysisUserViewResponse response) {
            return response;
        }

        return null;
    }

    private void cacheUserView(
            Long projectId,
            Integer requestedVersion,
            Integer actualVersion,
            ProjectAnalysisUserViewResponse response
    ) {
        simpleCache.put(buildCacheKey(projectId, requestedVersion), response);

        if (requestedVersion == null && actualVersion != null) {
            simpleCache.put(buildCacheKey(projectId, actualVersion), response);
        }
    }

    private String buildCacheKey(Long projectId, Integer version) {
        return "analysis:user-view:" + projectId + ":" + (version == null ? "latest" : version);
    }
}
