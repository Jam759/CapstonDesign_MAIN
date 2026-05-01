package com.Hoseo.CapstoneDesign.question.controller;

import com.Hoseo.CapstoneDesign.global.exception.GlobalExceptionResponse;
import com.Hoseo.CapstoneDesign.question.dto.request.AnswerCreateRequest;
import com.Hoseo.CapstoneDesign.question.dto.request.QuestionCreateRequest;
import com.Hoseo.CapstoneDesign.question.dto.response.AnswerResponse;
import com.Hoseo.CapstoneDesign.question.dto.response.QuestionDetailResponse;
import com.Hoseo.CapstoneDesign.question.dto.response.QuestionSummaryResponse;
import com.Hoseo.CapstoneDesign.question.facade.QuestionFacade; // QuestionFacade 임포트 추가
// 서버가 유저 정보를 알기 위해 필요한 시큐리티 엔티티를 임포트합니다.
import com.Hoseo.CapstoneDesign.security.entity.UserDetailImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
// 토큰에서 유저 정보를 바로 꺼내올 수 있게 해주는 어노테이션을 임포트합니다.
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/questions")
@Tag(name = "Question", description = "Question board API entry points")
@SecurityRequirement(name = "bearerAuth")
public class QuestionController {

    // 새로 생성한 QuestionFacade를 의존성 주입받습니다.
    private final QuestionFacade questionFacade;

    @GetMapping
    @Operation(summary = "Get questions", description = "Entry point for the frontend question board list.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Question list returned", content = @Content(array = @ArraySchema(schema = @Schema(implementation = QuestionSummaryResponse.class)))),
            @ApiResponse(responseCode = "401", description = "Authentication failed", content = @Content(schema = @Schema(implementation = GlobalExceptionResponse.class)))
    })
    public ResponseEntity<List<QuestionSummaryResponse>> getQuestions() {
        return ResponseEntity.ok(List.of());
    }

    @PostMapping
    @Operation(summary = "Create question", description = "Entry point for creating a question.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Question created", content = @Content(schema = @Schema(implementation = QuestionDetailResponse.class))),
            @ApiResponse(responseCode = "401", description = "Authentication failed", content = @Content(schema = @Schema(implementation = GlobalExceptionResponse.class)))
    })
    public ResponseEntity<QuestionDetailResponse> createQuestion(
            // 스프링 시큐리티가 JWT 토큰을 분석해서 현재 로그인한 유저의 정보를 userDetail 객체에 담아줍니다.
            @AuthenticationPrincipal UserDetailImpl userDetail,
            @RequestBody QuestionCreateRequest request) {

        // userDetail 객체에서 현재 요청을 보낸 유저의 고유 ID 값을 안전하게 꺼내옵니다.
        Long userId = userDetail.getUserId();

        // 기존 더미 데이터를 삭제하고 Facade를 호출하여 실제 비즈니스 로직(DB 저장 및 이미지 연결)을 실행합니다.
        QuestionDetailResponse response = questionFacade.createQuestion(userId, request);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{questionId}")
    @Operation(summary = "Get question detail", description = "Entry point for the question detail page.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Question detail returned", content = @Content(schema = @Schema(implementation = QuestionDetailResponse.class))),
            @ApiResponse(responseCode = "401", description = "Authentication failed", content = @Content(schema = @Schema(implementation = GlobalExceptionResponse.class)))
    })
    public ResponseEntity<QuestionDetailResponse> getQuestion(
            @Parameter(description = "Question id", example = "1")
            @PathVariable Long questionId
    ) {
        QuestionDetailResponse response = new QuestionDetailResponse(
                questionId,
                "Question board entry point",
                "service-user",
                LocalDateTime.now(),
                "This is a temporary response until the question domain is implemented.",
                List.of("question"),
                0,
                List.of()
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{questionId}/answers")
    @Operation(summary = "Create answer", description = "Entry point for creating an answer.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Answer created", content = @Content(schema = @Schema(implementation = AnswerResponse.class))),
            @ApiResponse(responseCode = "401", description = "Authentication failed", content = @Content(schema = @Schema(implementation = GlobalExceptionResponse.class)))
    })
    public ResponseEntity<AnswerResponse> createAnswer(
            // 답변을 달 때도 작성자가 누구인지 파악하기 위해 동일하게 유저 정보를 주입받습니다.
            @AuthenticationPrincipal UserDetailImpl userDetail,
            @Parameter(description = "Question id", example = "1")
            @PathVariable Long questionId,
            @RequestBody AnswerCreateRequest request
    ) {
        // 토큰에서 추출한 답변 작성자의 고유 ID 값입니다.
        Long userId = userDetail.getUserId();

        AnswerResponse response = new AnswerResponse(
                1L,
                "service-user",
                LocalDateTime.now(),
                false,
                request.content()
        );
        return ResponseEntity.ok(response);
    }
}