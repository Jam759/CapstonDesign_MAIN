package com.Hoseo.CapstoneDesign.project.controller;

import com.Hoseo.CapstoneDesign.global.exception.GlobalExceptionResponse;
import com.Hoseo.CapstoneDesign.project.dto.request.ProjectCreateRequest;
import com.Hoseo.CapstoneDesign.project.dto.request.ProjectInviteRequest;
import com.Hoseo.CapstoneDesign.project.dto.request.ProjectInviteResponseRequest;
import com.Hoseo.CapstoneDesign.project.dto.request.ProjectSettingRequest;
import com.Hoseo.CapstoneDesign.project.dto.response.InviteStatusResponse;
import com.Hoseo.CapstoneDesign.project.dto.response.ProjectCreateResponse;
import com.Hoseo.CapstoneDesign.project.dto.response.ProjectInviteResponse;
import com.Hoseo.CapstoneDesign.project.dto.response.ProjectSettingResponse;
import com.Hoseo.CapstoneDesign.project.dto.response.ProjectThumbnailResponse;
import com.Hoseo.CapstoneDesign.project.entity.enums.ProjectInviteStatus;
import com.Hoseo.CapstoneDesign.project.facade.ProjectFacade;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/projects")
@Tag(name = "Project", description = "프로젝트 생성, 설정, 초대 API")
@SecurityRequirement(name = "bearerAuth")
public class ProjectController {

    private final ProjectFacade facade;

    @PostMapping()
    @Operation(
            summary = "프로젝트 생성",
            description = "새 프로젝트를 생성하고 요청 사용자를 OWNER 멤버로 등록합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "프로젝트 생성 성공",
                    content = @Content(schema = @Schema(implementation = ProjectCreateResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(schema = @Schema(implementation = GlobalExceptionResponse.class))
            )
    })
    public ResponseEntity<ProjectCreateResponse> createProject(
            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetailImpl userDetail,
            @RequestBody ProjectCreateRequest request
    ) {
        ProjectCreateResponse res = facade.createProject(request, userDetail.getUser());
        return ResponseEntity.ok(res);
    }

    @GetMapping()
    @Operation(
            summary = "내 프로젝트 목록 조회",
            description = "현재 구현은 facade 연동 전 단계로 mock 프로젝트 썸네일 목록을 반환합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "프로젝트 목록 조회 성공",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ProjectThumbnailResponse.class)))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(schema = @Schema(implementation = GlobalExceptionResponse.class))
            )
    })
    public ResponseEntity<List<ProjectThumbnailResponse>> getMyProject(
            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetailImpl userDetail
    ) {
        // TODO : 추후 구현 현재는 mock데이터 반환
//        List<ProjectThumbnailResponse> res = facade.getMyProject(userDetail.getUser());

        List<ProjectThumbnailResponse> res = List.of(
                ProjectThumbnailResponse.builder()
                        .projectId(101L)
                        .title("알고리즘 스터디")
                        .description("백준 풀이 기록과 회고를 관리하는 프로젝트")
                        .build(),
                ProjectThumbnailResponse.builder()
                        .projectId(102L)
                        .title("캡스톤 디자인")
                        .description("GitHub 분석 기반 협업 보조 서비스 메인 프로젝트")
                        .build(),
                ProjectThumbnailResponse.builder()
                        .projectId(103L)
                        .title("TypeScript 연습장")
                        .description("언어 학습용 미니 미션과 실습 코드를 모아둔 프로젝트")
                        .build()
        );
        return ResponseEntity.ok(res);
    }

    @DeleteMapping("/{projectId}")
    @Operation(
            summary = "프로젝트 삭제",
            description = "현재 구현은 실제 삭제를 수행하지 않고 빈 200 응답만 반환합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "삭제 요청 처리 성공"),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(schema = @Schema(implementation = GlobalExceptionResponse.class))
            )
    })
    public ResponseEntity<Void> deleteProject(
            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetailImpl userDetail,
            @Parameter(description = "프로젝트 ID", example = "101")
            @PathVariable Long projectId
    ) {

        // TODO : 추후 구현 현재는 mock데이터 반환
//        facade.deleteProject(userDetail.getUser(), projectId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{projectId}/setting")
    @Operation(
            summary = "프로젝트 GitHub 설정 조회",
            description = "프로젝트 소유자만 저장된 GitHub 설치/저장소/브랜치 설정을 조회할 수 있습니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "프로젝트 설정 조회 성공",
                    content = @Content(schema = @Schema(implementation = ProjectSettingResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(schema = @Schema(implementation = GlobalExceptionResponse.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "프로젝트 설정 조회 권한 없음",
                    content = @Content(schema = @Schema(implementation = GlobalExceptionResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "프로젝트를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = GlobalExceptionResponse.class))
            )
    })
    public ResponseEntity<ProjectSettingResponse> getProject(
            @Parameter(description = "프로젝트 ID", example = "101")
            @PathVariable Long projectId,
            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetailImpl userDetail
    ) {
        ProjectSettingResponse response
                = facade.getProjectSetting(projectId, userDetail.getUser());
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{projectId}/setting")
    @Operation(
            summary = "프로젝트 GitHub 설정 저장",
            description = "선택한 GitHub 저장소와 추적 브랜치를 연결하고, 전체 분석 작업을 큐에 발행합니다. 프로젝트 상태가 REPO_NOT_CONNECTED일 때만 성공합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "프로젝트 설정 저장 성공",
                    content = @Content(schema = @Schema(implementation = ProjectSettingResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "이미 저장소가 연결된 프로젝트이거나 요청 값이 유효하지 않음",
                    content = @Content(schema = @Schema(implementation = GlobalExceptionResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(schema = @Schema(implementation = GlobalExceptionResponse.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "프로젝트 설정 수정 권한 없음",
                    content = @Content(schema = @Schema(implementation = GlobalExceptionResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "프로젝트 또는 GitHub 연동 정보를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = GlobalExceptionResponse.class))
            )
    })
    public ResponseEntity<ProjectSettingResponse> updateProject(
            @Parameter(description = "프로젝트 ID", example = "101")
            @PathVariable Long projectId,
            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetailImpl userDetail,
            @RequestBody ProjectSettingRequest request
    ) {
        ProjectSettingResponse response
                = facade.updateProject(projectId, userDetail.getUser(), request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/members")
    @Operation(
            summary = "프로젝트 멤버 초대",
            description = "현재 구현은 실제 초대 저장 없이 mock 초대 응답을 반환합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "초대 응답 반환 성공",
                    content = @Content(schema = @Schema(implementation = ProjectInviteResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(schema = @Schema(implementation = GlobalExceptionResponse.class))
            )
    })
    public ResponseEntity<ProjectInviteResponse> inviteProject(
            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetailImpl userDetail,
            @RequestBody ProjectInviteRequest request
    ) {
        // TODO : 추후 구현 현재는 mock데이터 반환
//        ProjectInviteResponse res = facade.inviteProject(userDetail.getUser(),request);

        Long invitedUserId = request.getInviteMemberId() != null ? request.getInviteMemberId() : 2001L;
        ProjectInviteResponse res = new ProjectInviteResponse(
                mockProjectMemberId(request.getProjectId(), invitedUserId),
                invitedUserId,
                ProjectInviteStatus.INVITED
        );
        return ResponseEntity.ok(res);
    }

    @PatchMapping("/member")
    @Operation(
            summary = "프로젝트 초대 응답",
            description = "현재 구현은 실제 상태 변경 없이 mock 초대 응답 결과를 반환합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "초대 응답 결과 반환 성공",
                    content = @Content(schema = @Schema(implementation = ProjectInviteResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(schema = @Schema(implementation = GlobalExceptionResponse.class))
            )
    })
    public ResponseEntity<ProjectInviteResponse> responseInvite(
            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetailImpl userDetail,
            @RequestBody ProjectInviteResponseRequest request
    ) {
        // TODO : 추후 구현 현재는 mock데이터 반환
//        ProjectInviteResponse res = facade.responseInvite(userDetail.getUser(), request);

        Long invitedUserId = resolveUserId(userDetail);
        ProjectInviteStatus status = request.getResponseStatus() != null
                ? request.getResponseStatus()
                : ProjectInviteStatus.ACCEPTED;
        ProjectInviteResponse res = new ProjectInviteResponse(
                mockProjectMemberId(request.getProjectId(), invitedUserId),
                invitedUserId,
                status
        );
        return ResponseEntity.ok(res);
    }

    @GetMapping("member")
    @Operation(
            summary = "내 초대 상태 목록 조회",
            description = "현재 구현은 실제 DB 조회 대신 mock 초대 상태 목록을 반환합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "초대 상태 목록 조회 성공",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = InviteStatusResponse.class)))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(schema = @Schema(implementation = GlobalExceptionResponse.class))
            )
    })
    public ResponseEntity<List<InviteStatusResponse>> getMyInvitedList(
            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetailImpl userDetail
    ) {
        // TODO : 추후 구현 현재는 mock데이터 반환
//        List<InviteStatusResponse> res = facade.getMyInvited(userDetail.getUser());

        List<InviteStatusResponse> res = List.of(
                InviteStatusResponse.builder()
                        .projectId(201L)
                        .status(ProjectInviteStatus.INVITED)
                        .build(),
                InviteStatusResponse.builder()
                        .projectId(202L)
                        .status(ProjectInviteStatus.ACCEPTED)
                        .build(),
                InviteStatusResponse.builder()
                        .projectId(203L)
                        .status(ProjectInviteStatus.DECLINED)
                        .build()
        );
        return ResponseEntity.ok(res);
    }

    private Long mockProjectMemberId(Long projectId, Long userId) {
        long resolvedProjectId = projectId != null ? projectId : 999L;
        long resolvedUserId = userId != null ? userId : 2001L;
        return resolvedProjectId * 100 + resolvedUserId;
    }

    private Long resolveUserId(UserDetailImpl userDetail) {
        if (userDetail == null || userDetail.getUser() == null || userDetail.getUser().getUserId() == null) {
            return 2001L;
        }
        return userDetail.getUser().getUserId();
    }
}
