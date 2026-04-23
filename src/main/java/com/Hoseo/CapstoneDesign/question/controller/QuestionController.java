package com.Hoseo.CapstoneDesign.question.controller;

import com.Hoseo.CapstoneDesign.global.exception.GlobalExceptionResponse;
import com.Hoseo.CapstoneDesign.question.dto.request.AnswerCreateRequest;
import com.Hoseo.CapstoneDesign.question.dto.request.QuestionCreateRequest;
import com.Hoseo.CapstoneDesign.question.dto.response.AnswerResponse;
import com.Hoseo.CapstoneDesign.question.dto.response.QuestionDetailResponse;
import com.Hoseo.CapstoneDesign.question.dto.response.QuestionSummaryResponse;
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
    public ResponseEntity<QuestionDetailResponse> createQuestion(@RequestBody QuestionCreateRequest request) {
        QuestionDetailResponse response = new QuestionDetailResponse(
                1L,
                request.title(),
                "service-user",
                LocalDateTime.now(),
                request.content(),
                request.tags() == null ? List.of() : request.tags(),
                0,
                List.of()
        );
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
            @Parameter(description = "Question id", example = "1")
            @PathVariable Long questionId,
            @RequestBody AnswerCreateRequest request
    ) {
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
