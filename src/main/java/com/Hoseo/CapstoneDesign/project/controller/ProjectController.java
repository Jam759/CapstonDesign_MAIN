package com.Hoseo.CapstoneDesign.project.controller;

import com.Hoseo.CapstoneDesign.global.exception.GlobalExceptionResponse;
import com.Hoseo.CapstoneDesign.project.dto.request.ProjectCreateRequest;
import com.Hoseo.CapstoneDesign.project.dto.request.ProjectInviteRequest;
import com.Hoseo.CapstoneDesign.project.dto.request.ProjectInviteResponseRequest;
import com.Hoseo.CapstoneDesign.project.dto.response.InviteStatusResponse;
import com.Hoseo.CapstoneDesign.project.dto.response.ProjectCreateResponse;
import com.Hoseo.CapstoneDesign.project.dto.response.ProjectInviteResponse;
import com.Hoseo.CapstoneDesign.project.dto.response.ProjectSettingResponse;
import com.Hoseo.CapstoneDesign.project.dto.response.ProjectThumbnailResponse;
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
@Tag(name = "Project", description = "Project create, list, GitHub setting, and invite APIs")
@SecurityRequirement(name = "bearerAuth")
public class ProjectController {

    private final ProjectFacade facade;

    @PostMapping
    @Operation(
            summary = "Create project",
            description = "Creates a project for a GitHub App installed user and stores repository, branch, tech stacks, and friend invites in one transaction."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Project created",
                    content = @Content(schema = @Schema(implementation = ProjectCreateResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication failed",
                    content = @Content(schema = @Schema(implementation = GlobalExceptionResponse.class))
            )
    })
    public ResponseEntity<ProjectCreateResponse> createProject(
            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetailImpl userDetail,
            @RequestBody ProjectCreateRequest request
    ) {
        ProjectCreateResponse res = facade.createProject(request, userDetail.getAuthenticatedUser());
        return ResponseEntity.ok(res);
    }

    @GetMapping
    @Operation(
            summary = "Get my projects",
            description = "Returns projects where the current user is an accepted member."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Projects returned",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ProjectThumbnailResponse.class)))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication failed",
                    content = @Content(schema = @Schema(implementation = GlobalExceptionResponse.class))
            )
    })
    public ResponseEntity<List<ProjectThumbnailResponse>> getMyProject(
            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetailImpl userDetail
    ) {
        List<ProjectThumbnailResponse> res = facade.getMyProject(userDetail.getAuthenticatedUser());
        return ResponseEntity.ok(res);
    }

    @GetMapping("/{projectId}/setting")
    @Operation(
            summary = "Get project GitHub setting",
            description = "Returns repository and branch fixed at project creation time. Update API is intentionally not exposed."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Project GitHub setting returned",
                    content = @Content(schema = @Schema(implementation = ProjectSettingResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication failed",
                    content = @Content(schema = @Schema(implementation = GlobalExceptionResponse.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Project owner required",
                    content = @Content(schema = @Schema(implementation = GlobalExceptionResponse.class))
            )
    })
    public ResponseEntity<ProjectSettingResponse> getProjectSetting(
            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetailImpl userDetail,
            @Parameter(description = "Project id", example = "101")
            @PathVariable Long projectId
    ) {
        ProjectSettingResponse response = facade.getProjectSetting(projectId, userDetail.getAuthenticatedUser());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{projectId}")
    @Operation(
            summary = "Delete project",
            description = "Soft-deletes a project owned by the current user."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Delete request accepted"),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication failed",
                    content = @Content(schema = @Schema(implementation = GlobalExceptionResponse.class))
            )
    })
    public ResponseEntity<Void> deleteProject(
            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetailImpl userDetail,
            @Parameter(description = "Project id", example = "101")
            @PathVariable Long projectId
    ) {
        facade.deleteProject(projectId, userDetail.getAuthenticatedUser());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/members")
    @Operation(
            summary = "Invite project members",
            description = "Invites one or more friends to a project. Only project owners can invite members. Already-existing members are skipped silently."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Invite results returned",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ProjectInviteResponse.class)))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication failed",
                    content = @Content(schema = @Schema(implementation = GlobalExceptionResponse.class))
            )
    })
    public ResponseEntity<List<ProjectInviteResponse>> inviteProject(
            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetailImpl userDetail,
            @RequestBody ProjectInviteRequest request
    ) {
        List<ProjectInviteResponse> res = facade.inviteProject(request, userDetail.getAuthenticatedUser());
        return ResponseEntity.ok(res);
    }

    @PatchMapping("/member")
    @Operation(
            summary = "Respond to project invite",
            description = "Accepts or declines a pending invite owned by the current user."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Invite response result returned",
                    content = @Content(schema = @Schema(implementation = ProjectInviteResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication failed",
                    content = @Content(schema = @Schema(implementation = GlobalExceptionResponse.class))
            )
    })
    public ResponseEntity<ProjectInviteResponse> responseInvite(
            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetailImpl userDetail,
            @RequestBody ProjectInviteResponseRequest request
    ) {
        ProjectInviteResponse res = facade.responseInvite(request, userDetail.getAuthenticatedUser());
        return ResponseEntity.ok(res);
    }

    @GetMapping("/member")
    @Operation(
            summary = "Get my project invites",
            description = "Returns project invite membership rows for the current user."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Invite list returned",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = InviteStatusResponse.class)))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication failed",
                    content = @Content(schema = @Schema(implementation = GlobalExceptionResponse.class))
            )
    })
    public ResponseEntity<List<InviteStatusResponse>> getMyInvitedList(
            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetailImpl userDetail
    ) {
        List<InviteStatusResponse> res = facade.getMyInvitedList(userDetail.getAuthenticatedUser());
        return ResponseEntity.ok(res);
    }
}
