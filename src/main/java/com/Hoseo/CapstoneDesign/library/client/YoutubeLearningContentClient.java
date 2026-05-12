package com.Hoseo.CapstoneDesign.library.client;

import com.Hoseo.CapstoneDesign.library.dto.application.LearningRecommendationItem;
import com.Hoseo.CapstoneDesign.library.properties.LibraryApiProperties;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class YoutubeLearningContentClient {

    private static final int PLATFORM_LIMIT = 6;

    private final LibraryApiProperties properties;
    private final RestClient restClient;

    public YoutubeLearningContentClient(LibraryApiProperties properties) {
        this.properties = properties;
        this.restClient = RestClient.builder()
                .baseUrl(properties.getYoutubeBaseUrl())
                .build();
    }

    public List<LearningRecommendationItem> search(List<String> keywords) {
        Map<String, LearningRecommendationItem> deduplicated = new LinkedHashMap<>();

        for (String keyword : keywords) {
            String pageToken = null;
            int pageCount = 0;

            do {
                JsonNode response = searchPage(keyword, pageToken);
                if (response == null) {
                    break;
                }

                List<String> videoIds = extractVideoIds(response);
                Map<String, String> durations = findDurations(videoIds);
                appendSearchItems(response, durations, deduplicated);

                pageToken = text(response, "nextPageToken");
                pageCount++;
            } while (deduplicated.size() < PLATFORM_LIMIT && StringUtils.hasText(pageToken) && pageCount < 3);

            if (deduplicated.size() >= PLATFORM_LIMIT) {
                break;
            }
        }

        return deduplicated.values().stream().limit(PLATFORM_LIMIT).toList();
    }

    private JsonNode searchPage(String keyword, String pageToken) {
        try {
            return restClient.get()
                    .uri(uriBuilder -> {
                        var builder = uriBuilder
                                .path("/youtube/v3/search")
                                .queryParam("key", properties.getYOUTUBE_KEY())
                                .queryParam("part", "snippet")
                                .queryParam("q", keyword)
                                .queryParam("type", "video")
                                .queryParam("order", "viewCount")
                                .queryParam("maxResults", PLATFORM_LIMIT);
                        if (StringUtils.hasText(pageToken)) {
                            builder.queryParam("pageToken", pageToken);
                        }
                        return builder.build();
                    })
                    .retrieve()
                    .body(JsonNode.class);
        } catch (RestClientException ignored) {
            return null;
        }
    }

    private Map<String, String> findDurations(List<String> videoIds) {
        if (videoIds.isEmpty()) {
            return Map.of();
        }

        try {
            JsonNode response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/youtube/v3/videos")
                            .queryParam("key", properties.getYOUTUBE_KEY())
                            .queryParam("part", "contentDetails")
                            .queryParam("id", String.join(",", videoIds))
                            .build())
                    .retrieve()
                    .body(JsonNode.class);

            Map<String, String> durations = new LinkedHashMap<>();
            JsonNode items = response == null ? null : response.path("items");
            if (items != null && items.isArray()) {
                for (JsonNode item : items) {
                    durations.put(text(item, "id"), formatYoutubeDuration(text(item.path("contentDetails"), "duration")));
                }
            }
            return durations;
        } catch (RestClientException ignored) {
            return Map.of();
        }
    }

    private List<String> extractVideoIds(JsonNode response) {
        List<String> videoIds = new ArrayList<>();
        JsonNode items = response.path("items");
        if (!items.isArray()) {
            return videoIds;
        }

        for (JsonNode item : items) {
            String videoId = text(item.path("id"), "videoId");
            if (StringUtils.hasText(videoId)) {
                videoIds.add(videoId);
            }
        }
        return videoIds;
    }

    private void appendSearchItems(JsonNode response, Map<String, String> durations, Map<String, LearningRecommendationItem> deduplicated) {
        JsonNode items = response.path("items");
        if (!items.isArray()) {
            return;
        }

        for (JsonNode item : items) {
            String videoId = text(item.path("id"), "videoId");
            if (!StringUtils.hasText(videoId) || deduplicated.containsKey(videoId)) {
                continue;
            }

            JsonNode snippet = item.path("snippet");
            deduplicated.put(videoId, new LearningRecommendationItem(
                    "VIDEO",
                    text(snippet, "title"),
                    text(snippet, "description"),
                    "YouTube",
                    durations.getOrDefault(videoId, ""),
                    "https://www.youtube.com/watch?v=" + videoId,
                    thumbnailUrl(snippet)
            ));

            if (deduplicated.size() >= PLATFORM_LIMIT) {
                return;
            }
        }
    }

    private String thumbnailUrl(JsonNode snippet) {
        JsonNode thumbnails = snippet.path("thumbnails");
        String high = text(thumbnails.path("high"), "url");
        if (StringUtils.hasText(high)) {
            return high;
        }
        String medium = text(thumbnails.path("medium"), "url");
        if (StringUtils.hasText(medium)) {
            return medium;
        }
        return text(thumbnails.path("default"), "url");
    }

    private String formatYoutubeDuration(String rawDuration) {
        if (!StringUtils.hasText(rawDuration)) {
            return "";
        }

        try {
            Duration duration = Duration.parse(rawDuration);
            long hours = duration.toHours();
            long minutes = duration.toMinutesPart();
            long seconds = duration.toSecondsPart();

            if (hours > 0) {
                return hours + "시간 " + minutes + "분 소요";
            }
            if (minutes > 0) {
                return minutes + "분 소요";
            }
            return seconds + "초 소요";
        } catch (RuntimeException ignored) {
            return "";
        }
    }

    private String text(JsonNode node, String fieldName) {
        JsonNode value = node == null ? null : node.get(fieldName);
        return value == null || value.isNull() ? "" : value.asText("");
    }
}
