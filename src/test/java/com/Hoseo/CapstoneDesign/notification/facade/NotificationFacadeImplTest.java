package com.Hoseo.CapstoneDesign.notification.facade;

import com.Hoseo.CapstoneDesign.analysis.entity.AnalysisJob;
import com.Hoseo.CapstoneDesign.analysis.entity.enums.AnalysisJobStatus;
import com.Hoseo.CapstoneDesign.analysis.enums.AnalysisEventType;
import com.Hoseo.CapstoneDesign.common.entity.CommonGroupDetail;
import com.Hoseo.CapstoneDesign.common.service.CommonGroupDetailService;
import com.Hoseo.CapstoneDesign.github.entity.GithubAppInstallations;
import com.Hoseo.CapstoneDesign.github.entity.InstallationRepository;
import com.Hoseo.CapstoneDesign.notification.dto.application.NotificationQueueBaseMessage;
import com.Hoseo.CapstoneDesign.notification.dto.application.SseBaseResponse;
import com.Hoseo.CapstoneDesign.notification.dto.application.SuccessMessage;
import com.Hoseo.CapstoneDesign.notification.entity.SseNotification;
import com.Hoseo.CapstoneDesign.notification.service.NotificationService;
import com.Hoseo.CapstoneDesign.project.entity.Projects;
import com.Hoseo.CapstoneDesign.project.entity.enums.ProjectStatus;
import com.Hoseo.CapstoneDesign.support.builder.UsersTestBuilder;
import com.Hoseo.CapstoneDesign.user.entity.Users;
import com.Hoseo.CapstoneDesign.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class NotificationFacadeImplTest {

    @Test
    @DisplayName("retryable failure requeues the analysis job")
    void requeuesRetryableFailure() {
        NotificationService notificationService = mock(NotificationService.class);
        AnalysisJob analysisJob = createJob((short) 0, true);
        AnalysisJobServiceStub analysisJobService = new AnalysisJobServiceStub(analysisJob);
        CommonGroupDetailService commonGroupDetailService = mock(CommonGroupDetailService.class);
        UserService userService = mock(UserService.class);

        NotificationFacadeImpl facade = new NotificationFacadeImpl(
                notificationService,
                analysisJobService,
                commonGroupDetailService,
                userService,
                new ObjectMapper()
        );

        facade.failedHandle(NotificationQueueBaseMessage.builder()
                .traceId("trace-1")
                .jobId(analysisJob.getAnalysisJobId())
                .userId(900L)
                .eventType(AnalysisEventType.NORMAL_ANALYSIS_REQUEST)
                .data(Map.of(
                        "errorCode", "WORKER_TIMEOUT",
                        "errorMessage", "worker timeout",
                        "HTTPStatus", 500,
                        "retryable", true
                ))
                .build());

        verify(notificationService, never()).createAndDispatch(any(), any());

        assertThat(analysisJob.getRetryCount()).isEqualTo((short) 1);
        assertThat(analysisJob.getJobStatus()).isEqualTo(AnalysisJobStatus.PENDING);
    }

    @Test
    @DisplayName("non-retryable failure marks the job failed and sends SSE")
    void finalizesNonRetryableFailure() {
        NotificationService notificationService = mock(NotificationService.class);
        AnalysisJob analysisJob = createJob((short) 0, false);
        AnalysisJobServiceStub analysisJobService = new AnalysisJobServiceStub(analysisJob);
        CommonGroupDetailService commonGroupDetailService = mock(CommonGroupDetailService.class);
        UserService userService = mock(UserService.class);
        CommonGroupDetail projectLinkType = CommonGroupDetail.builder()
                .commonGroupDetailId("PROJECT")
                .build();
        when(commonGroupDetailService.getReferenceById("PROJECT")).thenReturn(projectLinkType);

        NotificationFacadeImpl facade = new NotificationFacadeImpl(
                notificationService,
                analysisJobService,
                commonGroupDetailService,
                userService,
                new ObjectMapper()
        );

        facade.failedHandle(NotificationQueueBaseMessage.builder()
                .jobId(analysisJob.getAnalysisJobId())
                .userId(901L)
                .eventType(AnalysisEventType.NORMAL_ANALYSIS_REQUEST)
                .data(Map.of(
                        "errorCode", "VALIDATION_FAILED",
                        "errorMessage", "invalid payload",
                        "HTTPStatus", 400,
                        "retryable", false
                ))
                .build());

        ArgumentCaptor<SseNotification> notificationCaptor = ArgumentCaptor.forClass(SseNotification.class);
        ArgumentCaptor<SseBaseResponse> responseCaptor = ArgumentCaptor.forClass(SseBaseResponse.class);
        verify(notificationService).createAndDispatch(notificationCaptor.capture(), responseCaptor.capture());

        assertThat(analysisJob.getJobStatus()).isEqualTo(AnalysisJobStatus.FAILED);
        assertThat(responseCaptor.getValue().getEventType()).isEqualTo("analysis-failed");
        assertThat(responseCaptor.getValue().getData()).isInstanceOf(Map.class);
        assertThat(((Map<?, ?>) responseCaptor.getValue().getData()).get("errorCode")).isEqualTo("VALIDATION_FAILED");
        assertThat(notificationCaptor.getValue().getLinkType().getCommonGroupDetailId()).isEqualTo("PROJECT");
        assertThat(notificationCaptor.getValue().getTitle()).isEqualTo("캡스톤 디자인 프로젝트 실패!");
    }

    @Test
    @DisplayName("success marks notification completed and sends SSE")
    void sendsSuccessNotification() {
        NotificationService notificationService = mock(NotificationService.class);
        AnalysisJob analysisJob = createJob((short) 0, false);
        AnalysisJobServiceStub analysisJobService = new AnalysisJobServiceStub(analysisJob);
        CommonGroupDetailService commonGroupDetailService = mock(CommonGroupDetailService.class);
        UserService userService = mock(UserService.class);
        CommonGroupDetail projectLinkType = CommonGroupDetail.builder()
                .commonGroupDetailId("PROJECT")
                .build();
        when(commonGroupDetailService.getReferenceById("PROJECT")).thenReturn(projectLinkType);

        NotificationFacadeImpl facade = new NotificationFacadeImpl(
                notificationService,
                analysisJobService,
                commonGroupDetailService,
                userService,
                new ObjectMapper()
        );

        facade.successHandle(NotificationQueueBaseMessage.builder()
                .jobId(analysisJob.getAnalysisJobId())
                .userId(902L)
                .eventType(AnalysisEventType.FULL_SCAN_ANALYSIS_REQUEST)
                .data(SuccessMessage.builder()
                        .completeQuestIds(List.of(1L, 2L))
                        .newQuestIds(List.of(3L))
                        .newProjectKBid(10L)
                        .userViewReportId(20L)
                        .build())
                .build());

        ArgumentCaptor<SseNotification> notificationCaptor = ArgumentCaptor.forClass(SseNotification.class);
        ArgumentCaptor<SseBaseResponse> responseCaptor = ArgumentCaptor.forClass(SseBaseResponse.class);
        verify(notificationService).createAndDispatch(notificationCaptor.capture(), responseCaptor.capture());

        assertThat(analysisJob.getJobStatus()).isEqualTo(AnalysisJobStatus.NOTIFICATION_COMPLETED);
        assertThat(responseCaptor.getValue().getEventType()).isEqualTo("analysis-success");
        assertThat(responseCaptor.getValue().getData()).isInstanceOf(Map.class);
        assertThat(((Map<?, ?>) responseCaptor.getValue().getData()).get("userViewReportId")).isEqualTo(20L);
        assertThat(notificationCaptor.getValue().getLinkType().getCommonGroupDetailId()).isEqualTo("PROJECT");
        assertThat(notificationCaptor.getValue().getTitle()).isEqualTo("캡스톤 디자인 프로젝트 성공!");
    }

    private AnalysisJob createJob(short retryCount, boolean mergeAnalysis) {
        Users user = UsersTestBuilder.defaultUser()
                .userId(900L)
                .build();

        Projects project = Projects.builder()
                .projectId(44L)
                .user(user)
                .title("캡스톤 디자인")
                .projectStatus(ProjectStatus.REPO_CONNECTED)
                .build();

        GithubAppInstallations installation = GithubAppInstallations.builder()
                .githubAppInstallationsId(11L)
                .accountId(22L)
                .accountLogin("octocat")
                .build();

        InstallationRepository repository = InstallationRepository.builder()
                .installationRepositoryId(33L)
                .githubAppInstallation(installation)
                .fullName("owner/repo")
                .name("repo")
                .isPrivate(true)
                .build();

        return AnalysisJob.builder()
                .analysisJobId(101L)
                .project(project)
                .user(user)
                .githubAppInstallation(installation)
                .installationRepository(repository)
                .beforeCommitHash("before-sha")
                .afterCommitHash("after-sha")
                .branch("main")
                .jobStatus(AnalysisJobStatus.PENDING)
                .retryCount(retryCount)
                .deliveryId("delivery-1")
                .analysisEventType(AnalysisEventType.NORMAL_ANALYSIS_REQUEST)
                .mergeAnalysis(mergeAnalysis)
                .build();
    }

    private static final class AnalysisJobServiceStub extends com.Hoseo.CapstoneDesign.analysis.service.AnalysisJobService {
        private final AnalysisJob analysisJob;

        private AnalysisJobServiceStub(AnalysisJob analysisJob) {
            super(null, null);
            this.analysisJob = analysisJob;
        }

        @Override
        public AnalysisJob getById(Long jobId) {
            return analysisJob;
        }

        @Override
        public void requestDispatch(Long jobId) {
        }
    }
}
