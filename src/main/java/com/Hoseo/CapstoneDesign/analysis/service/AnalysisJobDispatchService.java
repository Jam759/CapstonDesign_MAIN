package com.Hoseo.CapstoneDesign.analysis.service;

import com.Hoseo.CapstoneDesign.analysis.entity.AnalysisJob;
import com.Hoseo.CapstoneDesign.analysis.entity.enums.AnalysisJobStatus;
import com.Hoseo.CapstoneDesign.analysis.factory.AnalysisDtoFactory;
import com.Hoseo.CapstoneDesign.global.aws.properties.SqsProperties;
import com.Hoseo.CapstoneDesign.global.aws.sqs.SqsBaseMessage;
import com.Hoseo.CapstoneDesign.global.aws.sqs.SqsMessageSender;
import com.Hoseo.CapstoneDesign.global.logging.support.TraceIdContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AnalysisJobDispatchService {

    private final AnalysisJobService analysisJobService;
    private final SqsMessageSender sqsMessageSender;
    private final SqsProperties sqsProperties;

    @Transactional
    public void dispatchIfPending(Long jobId, String traceId) {
        String effectiveTraceId = StringUtils.hasText(traceId) ? traceId : UUID.randomUUID().toString();

        try (TraceIdContext ignored = TraceIdContext.open(effectiveTraceId)) {
            AnalysisJob job = analysisJobService.getByIdForUpdate(jobId);
            if (!job.isPending()) {
                return;
            }

            SqsBaseMessage message = AnalysisDtoFactory.toSqsAnalysisQueueMessage(job)
                    .toBuilder()
                    .traceId(effectiveTraceId)
                    .build();

            sqsMessageSender.send(sqsProperties.analysisQueue(), message);
            job.updateJobStatus(AnalysisJobStatus.ANALYSIS_JOB_QUEUED);
        }
    }
}
