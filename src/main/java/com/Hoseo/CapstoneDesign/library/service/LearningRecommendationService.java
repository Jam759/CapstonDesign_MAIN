package com.Hoseo.CapstoneDesign.library.service;

import com.Hoseo.CapstoneDesign.analysis.entity.AnalysisJob;
import com.Hoseo.CapstoneDesign.analysis.service.AnalysisJobService;
import com.Hoseo.CapstoneDesign.library.cache.service.LearningRecommendationCacheService;
import com.Hoseo.CapstoneDesign.library.client.KmoocLearningContentClient;
import com.Hoseo.CapstoneDesign.library.client.YoutubeLearningContentClient;
import com.Hoseo.CapstoneDesign.library.dto.application.LearningRecommendationItem;
import com.Hoseo.CapstoneDesign.library.dto.response.LearningRecommendationResponse;
import com.Hoseo.CapstoneDesign.project.exception.ProjectsErrorCode;
import com.Hoseo.CapstoneDesign.project.exception.ProjectsException;
import com.Hoseo.CapstoneDesign.project.service.ProjectMemberService;
import com.Hoseo.CapstoneDesign.project.service.ProjectService;
import com.Hoseo.CapstoneDesign.security.cache.dto.AuthenticatedUserCacheEntry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LearningRecommendationService {

    private static final String KMOOC_PLATFORM = "kmooc";
    private static final String YOUTUBE_PLATFORM = "youtube";

    private final ProjectService projectService;
    private final ProjectMemberService projectMemberService;
    private final AnalysisJobService analysisJobService;
    private final ProjectSearchKeywordService projectSearchKeywordService;
    private final LearningRecommendationCacheService cacheService;
    private final KmoocLearningContentClient kmoocClient;
    private final YoutubeLearningContentClient youtubeClient;

    public List<LearningRecommendationResponse> recommend(Long projectId, AuthenticatedUserCacheEntry user) {
        projectService.getById(projectId);
        validateProjectAccess(projectId, user.userId());

        AnalysisJob latestJob = analysisJobService.findLatestCompletedJob(projectId).orElse(null);
        if (latestJob == null) {
            return List.of();
        }

        List<String> keywords = projectSearchKeywordService.findKeywords(projectId, latestJob.getAnalysisJobId());
        if (keywords.isEmpty()) {
            return List.of();
        }

        List<LearningRecommendationItem> kmoocItems = findOrLoad(KMOOC_PLATFORM, latestJob.getAnalysisJobId(), keywords);
        List<LearningRecommendationItem> youtubeItems = findOrLoad(YOUTUBE_PLATFORM, latestJob.getAnalysisJobId(), keywords);

        List<LearningRecommendationItem> merged = new ArrayList<>();
        merged.addAll(kmoocItems);
        merged.addAll(youtubeItems);

        return toResponses(merged);
    }

    private void validateProjectAccess(Long projectId, Long userId) {
        if (!projectMemberService.isAcceptedMember(projectId, userId)) {
            throw new ProjectsException(ProjectsErrorCode.PROJECT_FORBIDDEN);
        }
    }

    private List<LearningRecommendationItem> findOrLoad(String platform, Long jobId, List<String> keywords) {
        return cacheService.find(platform, jobId)
                .orElseGet(() -> {
                    List<LearningRecommendationItem> items = load(platform, keywords);
                    cacheService.save(platform, jobId, items);
                    return items;
                });
    }

    private List<LearningRecommendationItem> load(String platform, List<String> keywords) {
        if (KMOOC_PLATFORM.equals(platform)) {
            return kmoocClient.search(keywords);
        }
        if (YOUTUBE_PLATFORM.equals(platform)) {
            return youtubeClient.search(keywords);
        }
        return List.of();
    }

    private List<LearningRecommendationResponse> toResponses(List<LearningRecommendationItem> items) {
        List<LearningRecommendationResponse> responses = new ArrayList<>();
        long id = 1L;

        for (LearningRecommendationItem item : items) {
            responses.add(new LearningRecommendationResponse(
                    id++,
                    item.type(),
                    item.title(),
                    item.description(),
                    item.source(),
                    item.duration(),
                    item.url(),
                    item.thumbnailUrl()
            ));
        }

        return responses;
    }
}
