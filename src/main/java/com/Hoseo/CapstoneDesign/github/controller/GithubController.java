package com.Hoseo.CapstoneDesign.github.controller;

import com.Hoseo.CapstoneDesign.global.exception.GlobalExceptionResponse;
import com.Hoseo.CapstoneDesign.github.dto.response.InstallationsAvailableResponse;
import com.Hoseo.CapstoneDesign.github.dto.response.RepositoryBranchesResponse;
import com.Hoseo.CapstoneDesign.github.dto.response.RepositoryResponse;
import com.Hoseo.CapstoneDesign.github.facade.GitHubFacade;
import com.Hoseo.CapstoneDesign.security.entity.UserDetailImpl;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/github")
@Tag(name = "GitHub", description = "GitHub App 연동 및 저장소 조회 API")
public class GithubController {

    private final GitHubFacade facade;

    @GetMapping("/installations/available")
    @Operation(
            summary = "GitHub App 설치 가능 여부 조회",
            description = "현재 로그인 사용자가 GitHub App을 설치 또는 연결했는지 확인하고, 미설치 상태면 설치 URL을 반환합니다."
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "설치 가능 여부 조회 성공",
                    content = @Content(schema = @Schema(implementation = InstallationsAvailableResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(schema = @Schema(implementation = GlobalExceptionResponse.class))
            )
    })
    public ResponseEntity<InstallationsAvailableResponse> getInstallAvailable(
            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetailImpl userDetail,
            @Parameter(
                    description = "설치 완료 후 프런트엔드에서 이동할 상대 경로",
                    example = "/projects/101/setting"
            )
            @RequestParam(required = false, defaultValue = "/tmp/oauth2/test") String returnTo
    ) {
        InstallationsAvailableResponse response =
                facade.getAvailable(userDetail.getUser(), returnTo);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/setup/callback")
    @Operation(
            summary = "GitHub App 설치 콜백",
            description = "GitHub App 설치 또는 업데이트 완료 후 GitHub가 호출하는 공개 콜백입니다. 설치 정보를 사용자와 연결한 뒤 프런트엔드로 302 리다이렉트합니다."
    )
    @SecurityRequirements
    @ApiResponses({
            @ApiResponse(responseCode = "302", description = "프런트엔드 리다이렉트 성공"),
            @ApiResponse(
                    responseCode = "400",
                    description = "state 값이 잘못되었거나 설치 연결에 실패함",
                    content = @Content(schema = @Schema(implementation = GlobalExceptionResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "연결 대상 사용자 또는 설치 정보를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = GlobalExceptionResponse.class))
            )
    })
    public ResponseEntity<Void> setupCallback(
            @Parameter(description = "GitHub App installation ID", example = "98765432")
            @RequestParam("installation_id") Long installationId,
            @Parameter(description = "설치 시작 시 발급된 state 값", example = "eyJhbGciOiJIUzI1NiJ9...")
            @RequestParam(value = "state", required = false) String state,
            @Parameter(description = "GitHub setup action 값", example = "install")
            @RequestParam(value = "setup_action", required = false) String setupAction
    ) {
        URI redirectUri =
                facade.connectInstallationIdAndUser(state, installationId, setupAction);

        return ResponseEntity.status(302)
                .location(redirectUri)
                .build();
    }

    @PostMapping("/webhook/callback")
    @Operation(
            summary = "GitHub Webhook 콜백",
            description = "GitHub Webhook 이벤트를 처리합니다. 이 엔드포인트는 인증 대신 X-Hub-Signature-256 서명 검증을 사용합니다."
    )
    @SecurityRequirements
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Webhook 처리 성공"),
            @ApiResponse(
                    responseCode = "401",
                    description = "Webhook 서명 검증 실패",
                    content = @Content(schema = @Schema(implementation = GlobalExceptionResponse.class))
            )
    })
    public ResponseEntity<Void> githupWebhook(
            @Parameter(description = "GitHub event type", example = "push")
            @RequestHeader(value = "X-GitHub-Event", required = false) String event,
            @Parameter(description = "GitHub delivery ID", example = "2f4b6a30-7a54-11ef-8d8a-0242ac120002")
            @RequestHeader(value = "X-GitHub-Delivery", required = false) String deliveryId,
            @Parameter(description = "GitHub webhook signature", example = "sha256=8f5c8d5f3a...")
            @RequestHeader(value = "X-Hub-Signature-256", required = false) String signature256,
            @RequestBody(required = false) JsonNode payload
    ) {
        facade.webhookEvent(event, deliveryId, signature256, payload);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/repositories/{repositoryId}/branches")
    @Operation(
            summary = "저장소 브랜치 목록 조회",
            description = "연결된 GitHub 설치 기준으로 저장소 브랜치 목록을 조회합니다. repositoryId는 /api/v1/github/repositories 응답의 repositoryId 값을 사용합니다."
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "브랜치 목록 조회 성공",
                    content = @Content(schema = @Schema(implementation = RepositoryBranchesResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "GitHub 설치 연결 상태가 유효하지 않음",
                    content = @Content(schema = @Schema(implementation = GlobalExceptionResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(schema = @Schema(implementation = GlobalExceptionResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "저장소 또는 GitHub 설치 정보를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = GlobalExceptionResponse.class))
            )
    })
    public ResponseEntity<RepositoryBranchesResponse> getBranches(
            @Parameter(description = "설치 저장소 ID", example = "3001")
            @PathVariable Long repositoryId,
            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetailImpl userDetail
    ) {
        RepositoryBranchesResponse response =
                facade.getBranches(userDetail.getUser(), repositoryId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/repositories")
    @Operation(
            summary = "연결 가능한 저장소 목록 조회",
            description = "현재 사용자와 연결된 GitHub App installation에 포함된 저장소 목록을 반환합니다."
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "저장소 목록 조회 성공",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = RepositoryResponse.class)))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(schema = @Schema(implementation = GlobalExceptionResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "GitHub 설치 정보를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = GlobalExceptionResponse.class))
            )
    })
    public ResponseEntity<List<RepositoryResponse>> getRepositories(
            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetailImpl userDetail
    ) {
        List<RepositoryResponse> res =
                facade.getRepositories(userDetail.getUser());
        return ResponseEntity.ok(res);
    }
}
