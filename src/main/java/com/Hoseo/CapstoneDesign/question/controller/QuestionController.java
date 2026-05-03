package com.Hoseo.CapstoneDesign.question.controller;

import com.Hoseo.CapstoneDesign.global.exception.GlobalExceptionResponse;
import com.Hoseo.CapstoneDesign.question.dto.request.AnswerCreateRequest;
import com.Hoseo.CapstoneDesign.question.dto.request.QuestionCreateRequest;
import com.Hoseo.CapstoneDesign.question.dto.request.QuestionUpdateRequest;
import com.Hoseo.CapstoneDesign.question.dto.response.AnswerResponse;
import com.Hoseo.CapstoneDesign.question.dto.response.QuestionDetailResponse;
import com.Hoseo.CapstoneDesign.question.dto.response.QuestionSummaryResponse;
import com.Hoseo.CapstoneDesign.question.facade.QuestionFacade;
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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/questions")
@Tag(name = "Question", description = "Question board API entry points")
@SecurityRequirement(name = "bearerAuth")
public class QuestionController {

    private final QuestionFacade questionFacade;

    @GetMapping
    @Operation(summary = "Get questions", description = "Entry point for the frontend question board list.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Question list returned", content = @Content(array = @ArraySchema(schema = @Schema(implementation = QuestionSummaryResponse.class)))),
            @ApiResponse(responseCode = "401", description = "Authentication failed", content = @Content(schema = @Schema(implementation = GlobalExceptionResponse.class)))
    })
    public ResponseEntity<List<QuestionSummaryResponse>> getQuestions() {
        // [추가] 더미 데이터를 제거하고, 파사드를 호출하여 실제 DB의 전체 질문 목록을 반환합니다.
        List<QuestionSummaryResponse> response = questionFacade.getQuestions();
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Operation(summary = "Create question", description = "Entry point for creating a question.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Question created", content = @Content(schema = @Schema(implementation = QuestionDetailResponse.class))),
            @ApiResponse(responseCode = "401", description = "Authentication failed", content = @Content(schema = @Schema(implementation = GlobalExceptionResponse.class)))
    })
    public ResponseEntity<QuestionDetailResponse> createQuestion(
            @AuthenticationPrincipal UserDetailImpl userDetail,
            @Valid @RequestBody QuestionCreateRequest request) {

        Long userId = userDetail.getUserId();
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
        // 하드코딩된 더미 데이터를 삭제하고, 파사드를 통해 실제 질문 상세 정보 및 답변 목록을 반환합니다.
        QuestionDetailResponse response = questionFacade.getQuestion(questionId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{questionId}/answers")
    @Operation(summary = "Create answer", description = "Entry point for creating an answer.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Answer created", content = @Content(schema = @Schema(implementation = AnswerResponse.class))),
            @ApiResponse(responseCode = "401", description = "Authentication failed", content = @Content(schema = @Schema(implementation = GlobalExceptionResponse.class)))
    })
    public ResponseEntity<AnswerResponse> createAnswer(
            @AuthenticationPrincipal UserDetailImpl userDetail,
            @Parameter(description = "Question id", example = "1")
            @PathVariable Long questionId,
            @Valid @RequestBody AnswerCreateRequest request
    ) {
        Long userId = userDetail.getUserId();

        // 기존의 하드코딩된 데이터를 삭제하고, Facade를 호출하여 실제 연동 로직을 실행합니다.
        AnswerResponse response = questionFacade.createAnswer(userId, questionId, request);

        return ResponseEntity.ok(response);
    }

    // 내 질문 목록 조회 (프론트의 '프로필 내부 내 질문 목록' 용도)
    @GetMapping("/me")
    @Operation(summary = "Get my questions", description = "로그인한 유저가 작성한 질문 목록을 최신순으로 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "My Question list returned", content = @Content(array = @ArraySchema(schema = @Schema(implementation = QuestionSummaryResponse.class)))),
            @ApiResponse(responseCode = "401", description = "Authentication failed", content = @Content(schema = @Schema(implementation = GlobalExceptionResponse.class)))
    })
    public ResponseEntity<List<QuestionSummaryResponse>> getMyQuestions(
            @AuthenticationPrincipal UserDetailImpl userDetail) {

        List<QuestionSummaryResponse> response = questionFacade.getMyQuestions(userDetail.getUserId());
        return ResponseEntity.ok(response);
    }

    // 질문 수정 (본인이 작성한 글만 수정 가능)
    @PutMapping("/{questionId}")
    @Operation(summary = "Update question", description = "작성자가 본인의 질문글을 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Question updated", content = @Content(schema = @Schema(implementation = QuestionDetailResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request format", content = @Content(schema = @Schema(implementation = GlobalExceptionResponse.class))),
            @ApiResponse(responseCode = "403", description = "Access denied (not the author)", content = @Content(schema = @Schema(implementation = GlobalExceptionResponse.class))),
            @ApiResponse(responseCode = "404", description = "Question not found", content = @Content(schema = @Schema(implementation = GlobalExceptionResponse.class)))
    })
    public ResponseEntity<QuestionDetailResponse> updateQuestion(
            @AuthenticationPrincipal UserDetailImpl userDetail,
            @Parameter(description = "Question id", example = "1") @PathVariable Long questionId,
            @Valid @RequestBody QuestionUpdateRequest request) {

        QuestionDetailResponse response = questionFacade.updateQuestion(userDetail.getUserId(), questionId, request);
        return ResponseEntity.ok(response);
    }

    // 질문 삭제 (본인이 작성한 글만 삭제 가능, 소프트 딜리트 적용)
    @DeleteMapping("/{questionId}")
    @Operation(summary = "Delete question", description = "작성자가 본인의 질문글을 삭제(소프트 딜리트)합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Question deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied (not the author)", content = @Content(schema = @Schema(implementation = GlobalExceptionResponse.class))),
            @ApiResponse(responseCode = "404", description = "Question not found", content = @Content(schema = @Schema(implementation = GlobalExceptionResponse.class)))
    })
    public ResponseEntity<Void> deleteQuestion(
            @AuthenticationPrincipal UserDetailImpl userDetail,
            @Parameter(description = "Question id", example = "1") @PathVariable Long questionId) {

        questionFacade.deleteQuestion(userDetail.getUserId(), questionId);
        // 삭제 성공 시 본문 없는 204 No Content 반환이 RESTful 표준입니다.
        return ResponseEntity.noContent().build();
    }
}