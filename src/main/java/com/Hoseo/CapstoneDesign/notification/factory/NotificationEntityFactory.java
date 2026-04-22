package com.Hoseo.CapstoneDesign.notification.factory;

import com.Hoseo.CapstoneDesign.analysis.entity.AnalysisJob;
import com.Hoseo.CapstoneDesign.common.entity.CommonGroupDetail;
import com.Hoseo.CapstoneDesign.notification.entity.SseNotification;
import com.Hoseo.CapstoneDesign.user.entity.Users;

public class NotificationEntityFactory {

    private static final String SUCCESS_MESSAGE = "새로 추가된 퀘스트와 분석 결과를 확인해보세요.";
    private static final String FAILURE_MESSAGE = "이번 분석은 실패했지만 다음 push 때 이번 변경 내용까지 포함해 다시 분석됩니다.";

    public static SseNotification toAnalysisSuccessNotification(
            Users user,
            AnalysisJob job,
            CommonGroupDetail linkType,
            String ssePayload
    ) {
        return toSseNotification(
                user,
                job,
                linkType,
                formatProjectTitle(job) + " 성공!",
                SUCCESS_MESSAGE,
                ssePayload
        );
    }

    public static SseNotification toAnalysisFailureNotification(
            Users user,
            AnalysisJob job,
            CommonGroupDetail linkType,
            String ssePayload
    ) {
        return toSseNotification(
                user,
                job,
                linkType,
                formatProjectTitle(job) + " 실패!",
                FAILURE_MESSAGE,
                ssePayload
        );
    }

    private static SseNotification toSseNotification(
            Users user,
            AnalysisJob job,
            CommonGroupDetail linkType,
            String title,
            String message,
            String ssePayload
    ) {
        return SseNotification.builder()
                .user(user)
                .title(title)
                .message(message)
                .isRead(false)
                .linkType(linkType)
                .ssePayload(ssePayload)
                .linkId(job.getProject() == null ? null : job.getProject().getProjectId())
                .build();
    }

    private static String formatProjectTitle(AnalysisJob job) {
        if (job.getProject() == null || job.getProject().getTitle() == null || job.getProject().getTitle().isBlank()) {
            return "프로젝트";
        }

        return job.getProject().getTitle() + " 프로젝트";
    }
}
