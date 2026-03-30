package com.Hoseo.CapstoneDesign.analysis.service;

import com.Hoseo.CapstoneDesign.analysis.dto.application.ProjectAnalysisUserViewResponse;
import com.Hoseo.CapstoneDesign.analysis.entity.ProjectAnalysisReport;
import com.Hoseo.CapstoneDesign.analysis.entity.enums.ReportType;
import com.Hoseo.CapstoneDesign.analysis.exception.AnalysisErrorCode;
import com.Hoseo.CapstoneDesign.analysis.exception.AnalysisException;
import com.Hoseo.CapstoneDesign.analysis.repository.ProjectAnalysisReportRepository;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.Hoseo.CapstoneDesign.global.aws.properties.S3Properties;
import com.Hoseo.CapstoneDesign.global.aws.s3.S3ObjectService;
import com.Hoseo.CapstoneDesign.project.entity.Projects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectAnalysisReportServiceTest {

    @Mock
    private ProjectAnalysisReportRepository repository;

    @Mock
    private S3ObjectService s3ObjectService;

    private ProjectAnalysisReportService service;

    @BeforeEach
    void setUp() {
        service = new ProjectAnalysisReportService(
                repository,
                s3ObjectService,
                new S3Properties("default-bucket"),
                Caffeine.newBuilder().build()
        );
    }

    @Test
    @DisplayName("returns the latest USER_VIEW report from the configured bucket")
    void getRecentUserViewSuccess() {
        ProjectAnalysisReport report = report("analysis-bucket", "reports/user-view.json");
        ProjectAnalysisUserViewResponse response = sampleResponse();

        when(repository.findTopByProjectProjectIdAndReportTypeOrderByVersionDescProjectAnalysisReportIdDesc(1L, ReportType.USER_VIEW))
                .thenReturn(Optional.of(report));
        when(s3ObjectService.getObjectAsJson("analysis-bucket", "reports/user-view.json", ProjectAnalysisUserViewResponse.class))
                .thenReturn(response);

        ProjectAnalysisUserViewResponse result = service.getRecentUserView(1L);

        assertThat(result.headline()).isEqualTo("headline");
        verify(s3ObjectService).getObjectAsJson("analysis-bucket", "reports/user-view.json", ProjectAnalysisUserViewResponse.class);
    }

    @Test
    @DisplayName("returns a specific USER_VIEW version when requested")
    void getUserViewByVersionSuccess() {
        ProjectAnalysisReport report = report("analysis-bucket", "reports/user-view-v2.json");
        ProjectAnalysisUserViewResponse response = sampleResponse();

        when(repository.findByProjectProjectIdAndReportTypeAndVersion(1L, ReportType.USER_VIEW, 2))
                .thenReturn(Optional.of(report));
        when(s3ObjectService.getObjectAsJson("analysis-bucket", "reports/user-view-v2.json", ProjectAnalysisUserViewResponse.class))
                .thenReturn(response);

        ProjectAnalysisUserViewResponse result = service.getUserView(1L, 2);

        assertThat(result.headline()).isEqualTo("headline");
        verify(s3ObjectService).getObjectAsJson("analysis-bucket", "reports/user-view-v2.json", ProjectAnalysisUserViewResponse.class);
    }

    @Test
    @DisplayName("reuses the cached USER_VIEW to avoid requesting the same JSON twice")
    void getUserViewUsesCache() {
        ProjectAnalysisReport report = report("analysis-bucket", "reports/user-view-v2.json");
        ProjectAnalysisUserViewResponse response = sampleResponse();

        when(repository.findByProjectProjectIdAndReportTypeAndVersion(1L, ReportType.USER_VIEW, 2))
                .thenReturn(Optional.of(report));
        when(s3ObjectService.getObjectAsJson("analysis-bucket", "reports/user-view-v2.json", ProjectAnalysisUserViewResponse.class))
                .thenReturn(response);

        ProjectAnalysisUserViewResponse first = service.getUserView(1L, 2);
        ProjectAnalysisUserViewResponse second = service.getUserView(1L, 2);

        assertThat(first).isEqualTo(response);
        assertThat(second).isEqualTo(response);
        verify(repository, times(1)).findByProjectProjectIdAndReportTypeAndVersion(1L, ReportType.USER_VIEW, 2);
        verify(s3ObjectService, times(1))
                .getObjectAsJson("analysis-bucket", "reports/user-view-v2.json", ProjectAnalysisUserViewResponse.class);
    }

    @Test
    @DisplayName("falls back to the default bucket when the report bucket is empty")
    void getRecentUserViewUsesDefaultBucket() {
        ProjectAnalysisReport report = report(null, "reports/user-view.json");
        ProjectAnalysisUserViewResponse response = sampleResponse();

        when(repository.findTopByProjectProjectIdAndReportTypeOrderByVersionDescProjectAnalysisReportIdDesc(1L, ReportType.USER_VIEW))
                .thenReturn(Optional.of(report));
        when(s3ObjectService.getObjectAsJson("default-bucket", "reports/user-view.json", ProjectAnalysisUserViewResponse.class))
                .thenReturn(response);

        ProjectAnalysisUserViewResponse result = service.getRecentUserView(1L);

        assertThat(result.summary()).isEqualTo("summary");
        verify(s3ObjectService).getObjectAsJson("default-bucket", "reports/user-view.json", ProjectAnalysisUserViewResponse.class);
    }

    @Test
    @DisplayName("parses s3 urls into bucket and object key")
    void getRecentUserViewParsesS3Url() {
        ProjectAnalysisReport report = report(null, "s3://user-view-bucket/reports/user-view.json");
        ProjectAnalysisUserViewResponse response = sampleResponse();

        when(repository.findTopByProjectProjectIdAndReportTypeOrderByVersionDescProjectAnalysisReportIdDesc(1L, ReportType.USER_VIEW))
                .thenReturn(Optional.of(report));
        when(s3ObjectService.getObjectAsJson("user-view-bucket", "reports/user-view.json", ProjectAnalysisUserViewResponse.class))
                .thenReturn(response);

        service.getRecentUserView(1L);

        verify(s3ObjectService).getObjectAsJson("user-view-bucket", "reports/user-view.json", ProjectAnalysisUserViewResponse.class);
    }

    @Test
    @DisplayName("throws ANALYSIS_USER_VIEW_NOT_FOUND when the report does not exist")
    void getRecentUserViewReportNotFound() {
        when(repository.findTopByProjectProjectIdAndReportTypeOrderByVersionDescProjectAnalysisReportIdDesc(1L, ReportType.USER_VIEW))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getRecentUserView(1L))
                .isInstanceOf(AnalysisException.class)
                .extracting("errorCode")
                .isEqualTo(AnalysisErrorCode.ANALYSIS_USER_VIEW_NOT_FOUND);
    }

    @Test
    @DisplayName("throws ANALYSIS_USER_VIEW_NOT_FOUND when the requested version does not exist")
    void getUserViewByVersionReportNotFound() {
        when(repository.findByProjectProjectIdAndReportTypeAndVersion(1L, ReportType.USER_VIEW, 2))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getUserView(1L, 2))
                .isInstanceOf(AnalysisException.class)
                .extracting("errorCode")
                .isEqualTo(AnalysisErrorCode.ANALYSIS_USER_VIEW_NOT_FOUND);
    }

    @Test
    @DisplayName("throws ANALYSIS_REPORT_STORAGE_INVALID when the stored url is blank")
    void getRecentUserViewInvalidStoredUrl() {
        ProjectAnalysisReport report = report("analysis-bucket", " ");

        when(repository.findTopByProjectProjectIdAndReportTypeOrderByVersionDescProjectAnalysisReportIdDesc(1L, ReportType.USER_VIEW))
                .thenReturn(Optional.of(report));

        assertThatThrownBy(() -> service.getRecentUserView(1L))
                .isInstanceOf(AnalysisException.class)
                .extracting("errorCode")
                .isEqualTo(AnalysisErrorCode.ANALYSIS_REPORT_STORAGE_INVALID);
    }

    private ProjectAnalysisReport report(String bucketName, String storedUrl) {
        return ProjectAnalysisReport.builder()
                .projectAnalysisReportId(10L)
                .project(Projects.builder().projectId(1L).build())
                .reportType(ReportType.USER_VIEW)
                .version(3)
                .s3Bucket(bucketName)
                .storedUrl(storedUrl)
                .build();
    }

    private ProjectAnalysisUserViewResponse sampleResponse() {
        return new ProjectAnalysisUserViewResponse(
                "1.0.0",
                "2026-03-27T16:45:03.585Z",
                new ProjectAnalysisUserViewResponse.Scope(
                        1L,
                        1L,
                        "Jam759/CapstonDesign_Worker",
                        "main",
                        "before-hash",
                        "after-hash"
                ),
                "headline",
                "summary",
                List.of("strength"),
                List.of("risk"),
                List.of(
                        new ProjectAnalysisUserViewResponse.Advice(
                                "advice-id",
                                "high",
                                "maintainability",
                                "title",
                                "body",
                                "recommended-action",
                                "expected-impact"
                        )
                ),
                new ProjectAnalysisUserViewResponse.Scorecard(
                        new ProjectAnalysisUserViewResponse.Overall(78, "B", "medium"),
                        List.of(
                                new ProjectAnalysisUserViewResponse.Category(
                                        "architecture",
                                        "structure",
                                        84,
                                        "reason",
                                        List.of("evidence")
                                )
                        )
                )
        );
    }
}
