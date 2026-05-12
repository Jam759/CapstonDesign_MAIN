package com.Hoseo.CapstoneDesign.library.client;

import com.Hoseo.CapstoneDesign.library.dto.application.LearningRecommendationItem;
import com.Hoseo.CapstoneDesign.library.properties.LibraryApiProperties;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class KmoocLearningContentClient {

    private static final int PLATFORM_LIMIT = 6;
    private static final int REQUEST_PAGE = 1;
    private static final int REQUEST_SIZE = 5;
    private static final int DESCRIPTION_MAX_LENGTH = 180;

    private final LibraryApiProperties properties;
    private final RestClient restClient;

    public KmoocLearningContentClient(LibraryApiProperties properties) {
        this.properties = properties;
        this.restClient = RestClient.builder()
                .baseUrl(properties.getKmoocBaseUrl())
                .build();
    }

    public List<LearningRecommendationItem> search(List<String> keywords) {
        Map<String, LearningRecommendationItem> deduplicated = new LinkedHashMap<>();

        for (String keyword : keywords) {
            appendItems(searchPage(keyword), keyword, deduplicated);

            if (deduplicated.size() >= PLATFORM_LIMIT) {
                break;
            }
        }

        return deduplicated.values().stream().limit(PLATFORM_LIMIT).toList();
    }

    private JsonNode searchPage(String keyword) {
        try {
            return restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/courseList_v2_0")
                            .queryParam("ServiceKey", properties.getK_MOOK_KEY())
                            .queryParam("Page", REQUEST_PAGE)
                            .queryParam("Size", REQUEST_SIZE)
                            .build())
                    .retrieve()
                    .body(JsonNode.class);
        } catch (RestClientException ignored) {
            return null;
        }
    }

    private void appendItems(JsonNode response, String keyword, Map<String, LearningRecommendationItem> deduplicated) {
        JsonNode items = findItems(response);
        if (items == null) {
            return;
        }

        if (items.isArray()) {
            for (JsonNode item : items) {
                appendItem(item, keyword, deduplicated);
                if (deduplicated.size() >= PLATFORM_LIMIT) {
                    return;
                }
            }
            return;
        }

        appendItem(items, keyword, deduplicated);
    }

    private void appendItem(JsonNode item, String keyword, Map<String, LearningRecommendationItem> deduplicated) {
        String title = firstText(item, "title", "courseName", "course_name", "name", "className", "subject");
        if (!matchesKeyword(item, keyword)) {
            return;
        }

        String courseId = firstText(item, "courseId", "course_id", "id");
        JsonNode detail = findCourseDetail(courseId);
        JsonNode source = detail == null ? item : detail;
        String url = firstText(item, "url", "courseUrl", "course_url", "classUrl", "homepage", "home");
        if (!StringUtils.hasText(url)) {
            url = firstText(source, "url", "courseUrl", "course_url", "classUrl", "homepage", "home");
        }
        if (!StringUtils.hasText(url) && StringUtils.hasText(courseId)) {
            url = "https://www.kmooc.kr/courses/" + courseId + "/about";
        }
        String key = StringUtils.hasText(url) ? url : title;

        if (!StringUtils.hasText(key) || deduplicated.containsKey(key)) {
            return;
        }

        deduplicated.put(key, new LearningRecommendationItem(
                "COURSE",
                title,
                cleanDescription(firstText(source, "description", "summary", "overview", "courseDescription", "course_description", "short_description")),
                "K-MOOC",
                formatDuration(firstText(source, "week", "weeks", "duration", "period", "learningTime", "effort", "start_display", "course_playtime", "vod_playtime")),
                url,
                thumbnailUrl(source)
        ));
    }

    private JsonNode findCourseDetail(String courseId) {
        if (!StringUtils.hasText(courseId)) {
            return null;
        }

        try {
            JsonNode response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/courseDetail_v2_0")
                            .queryParam("ServiceKey", properties.getK_MOOK_KEY())
                            .queryParam("CourseId", courseId)
                            .build())
                    .retrieve()
                    .body(JsonNode.class);

            JsonNode results = response == null ? null : response.path("results");
            return results == null || results.isMissingNode() || results.isNull() ? null : results;
        } catch (RestClientException ignored) {
            return null;
        }
    }

    private JsonNode findItems(JsonNode response) {
        if (response == null || response.isMissingNode() || response.isNull()) {
            return null;
        }

        JsonNode responseBodyItems = response.path("response").path("body").path("items").path("item");
        if (!responseBodyItems.isMissingNode()) {
            return responseBodyItems;
        }

        JsonNode responseBodyData = response.path("response").path("body").path("data");
        if (!responseBodyData.isMissingNode()) {
            return responseBodyData;
        }

        JsonNode bodyItems = response.path("body").path("items").path("item");
        if (!bodyItems.isMissingNode()) {
            return bodyItems;
        }

        JsonNode courses = response.path("courses");
        if (!courses.isMissingNode()) {
            return courses;
        }

        JsonNode courseList = response.path("courseList");
        if (!courseList.isMissingNode()) {
            return courseList;
        }

        JsonNode results = response.path("results");
        if (!results.isMissingNode()) {
            return results;
        }

        JsonNode data = response.path("data");
        if (!data.isMissingNode()) {
            return data;
        }

        JsonNode items = response.path("items");
        return items.isMissingNode() ? null : items;
    }

    private boolean matchesKeyword(JsonNode item, String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return true;
        }

        String normalizedKeyword = keyword.toLowerCase();
        String normalizedKoreanKeyword = toKoreanTechKeyword(normalizedKeyword);
        String target = String.join(" ",
                text(item, "id"),
                text(item, "shortname"),
                text(item, "name"),
                text(item, "org_name"),
                text(item, "professor")
        ).toLowerCase();
        return target.contains(normalizedKeyword)
                || (StringUtils.hasText(normalizedKoreanKeyword) && target.contains(normalizedKoreanKeyword));
    }

    private String toKoreanTechKeyword(String keyword) {
        return switch (keyword) {
            case "java" -> "자바";
            case "python" -> "파이썬";
            case "database", "db" -> "데이터베이스";
            case "ai" -> "인공지능";
            case "machine learning", "ml" -> "머신러닝";
            case "deep learning" -> "딥러닝";
            case "security" -> "보안";
            case "network" -> "네트워크";
            case "algorithm" -> "알고리즘";
            case "data" -> "데이터";
            case "web" -> "웹";
            case "spring" -> "스프링";
            case "react" -> "리액트";
            default -> "";
        };
    }

    private String cleanDescription(String description) {
        if (!StringUtils.hasText(description)) {
            return "";
        }

        String cleaned = description
                .replaceAll("(?is)<script.*?</script>", " ")
                .replaceAll("(?is)<style.*?</style>", " ")
                .replaceAll("<[^>]+>", " ")
                .replace("&nbsp;", " ")
                .replace("&amp;", "&")
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .replaceAll("\\s+", " ")
                .trim();

        if (cleaned.length() <= DESCRIPTION_MAX_LENGTH) {
            return cleaned;
        }
        return cleaned.substring(0, DESCRIPTION_MAX_LENGTH) + "...";
    }

    private String formatDuration(String duration) {
        if (!StringUtils.hasText(duration)) {
            return "";
        }
        String trimmed = duration.trim();
        if (trimmed.matches("\\d+")) {
            return trimmed + "주 과정";
        }
        return trimmed;
    }

    private String thumbnailUrl(JsonNode item) {
        String direct = firstText(item, "thumbnailUrl", "thumbnail_url", "thumbnail", "imageUrl", "image_url", "courseImage", "course_image");
        if (StringUtils.hasText(direct)) {
            return direct;
        }

        JsonNode mediaImage = item.path("media").path("image");
        String raw = firstText(mediaImage, "raw", "small", "large");
        if (StringUtils.hasText(raw)) {
            return raw;
        }

        JsonNode image = item.path("image");
        return firstText(image, "raw", "url", "small", "large");
    }

    private String firstText(JsonNode node, String... fieldNames) {
        for (String fieldName : fieldNames) {
            String value = text(node, fieldName);
            if (StringUtils.hasText(value)) {
                return value;
            }
        }
        return "";
    }

    private String text(JsonNode node, String fieldName) {
        JsonNode value = node == null ? null : node.get(fieldName);
        return value == null || value.isNull() ? "" : value.asText("");
    }
}
