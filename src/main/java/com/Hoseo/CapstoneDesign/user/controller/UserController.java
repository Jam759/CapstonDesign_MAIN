package com.Hoseo.CapstoneDesign.user.controller;

import com.Hoseo.CapstoneDesign.global.exception.GlobalExceptionResponse;
import com.Hoseo.CapstoneDesign.security.entity.UserDetailImpl;
import com.Hoseo.CapstoneDesign.user.dto.request.UserProfileUpdateRequest;
import com.Hoseo.CapstoneDesign.user.dto.response.MyInfoResponse;
import com.Hoseo.CapstoneDesign.user.dto.response.UpdateUserInfoResponse;
import com.Hoseo.CapstoneDesign.user.dto.response.UserProfileThumbnail;
import com.Hoseo.CapstoneDesign.user.facade.UserFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
@Tag(name = "User", description = "User profile API")
@Validated
public class UserController {

    private final UserFacade facade;

    @GetMapping("/me")
    @Operation(summary = "Get my info", description = "Returns the current user's summary information.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Success",
                    content = @Content(schema = @Schema(implementation = MyInfoResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = GlobalExceptionResponse.class))
            )
    })
    public ResponseEntity<MyInfoResponse> getMyInfo(
            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetailImpl userDetail
    ) {
        return ResponseEntity.ok(facade.getMyInfo(userDetail.getAuthenticatedUser()));
    }

    @PatchMapping("/me")
    @Operation(summary = "Update my profile", description = "Updates the current user's profile fields.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Success",
                    content = @Content(schema = @Schema(implementation = UpdateUserInfoResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = GlobalExceptionResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(schema = @Schema(implementation = GlobalExceptionResponse.class))
            )
    })
    public ResponseEntity<UpdateUserInfoResponse> updateUser(
            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetailImpl userDetail,
            @Parameter(description = "User profile update request")
            @Valid @RequestBody UserProfileUpdateRequest request
    ) {
        UpdateUserInfoResponse res =
                facade.updateUserProfile(userDetail.getAuthenticatedUser(), request);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/search")
    @Operation(summary = "Search users", description = "Searches users by service nickname.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Success",
                    content = @Content(schema = @Schema(implementation = UserProfileThumbnail.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid search parameter",
                    content = @Content(schema = @Schema(implementation = GlobalExceptionResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = GlobalExceptionResponse.class))
            )
    })
    public ResponseEntity<List<UserProfileThumbnail>> searchUser(
            @Parameter(description = "Service nickname keyword", example = "commit")
            @RequestParam @NotBlank String serviceNickname,
            @Parameter(description = "Page number starting from 1", example = "1")
            @RequestParam(required = false, defaultValue = "1") @Min(1) Integer page,
            @Parameter(description = "Page size, maximum 50", example = "10")
            @RequestParam(required = false, defaultValue = "10") @Min(1) @Max(50) Integer size
    ) {
        List<UserProfileThumbnail> res =
                facade.searchUserByServiceNickname(serviceNickname, page, size);
        return ResponseEntity.ok(res);
    }
}
