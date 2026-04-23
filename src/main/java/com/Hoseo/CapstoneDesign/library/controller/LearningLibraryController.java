package com.Hoseo.CapstoneDesign.library.controller;

import com.Hoseo.CapstoneDesign.global.exception.GlobalExceptionResponse;
import com.Hoseo.CapstoneDesign.library.dto.response.LearningRecommendationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/library")
@Tag(name = "Learning Library", description = "Learning recommendation API entry points")
@SecurityRequirement(name = "bearerAuth")
public class LearningLibraryController {

    @GetMapping("/recommendations")
    @Operation(
            summary = "Get learning recommendations",
            description = "Entry point for the frontend learning library page."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Recommendations returned",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = LearningRecommendationResponse.class)))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication failed",
                    content = @Content(schema = @Schema(implementation = GlobalExceptionResponse.class))
            )
    })
    public ResponseEntity<List<LearningRecommendationResponse>> getRecommendations() {
        List<LearningRecommendationResponse> mock = List.of(
                new LearningRecommendationResponse(
                        1L,
                        "COURSE",
                        "Zustand를 활용한 안전한 전역 상태 관리",
                        "React 프로젝트에서 Zustand로 전역 상태를 모델링하는 방법을 배웁니다.",
                        "Inlearn",
                        "45분 강의",
                        "https://example.com/courses/zustand",
                        "https://example.com/thumbs/zustand.png"
                ),
                new LearningRecommendationResponse(
                        2L,
                        "VIDEO",
                        "Spring Boot 트랜잭션 전파 속성 완전 정복",
                        "@Transactional 전파 속성별 동작 차이를 실습으로 확인합니다.",
                        "인프런",
                        "25분 소요",
                        "https://example.com/videos/spring-tx",
                        "https://example.com/thumbs/spring-tx.png"
                ),
                new LearningRecommendationResponse(
                        3L,
                        "ARTICLE",
                        "효과적인 코드 리뷰 문화 만들기",
                        "팀의 코드 리뷰 품질을 끌어올리는 실천 가이드.",
                        "Medium",
                        "15분 소요",
                        "https://example.com/articles/code-review",
                        "https://example.com/thumbs/code-review.png"
                ),
                new LearningRecommendationResponse(
                        4L,
                        "COURSE",
                        "TypeScript 타입 시스템 심화",
                        "제네릭, 조건부 타입, 타입 레벨 프로그래밍을 다룹니다.",
                        "Inlearn",
                        "1시간 20분 강의",
                        "https://example.com/courses/ts-advanced",
                        "https://example.com/thumbs/ts-advanced.png"
                )
        );
        return ResponseEntity.ok(mock);
    }
}
