package com.Hoseo.CapstoneDesign.friend.controller;

import com.Hoseo.CapstoneDesign.friend.dto.response.FriendInviteResponse;
import com.Hoseo.CapstoneDesign.friend.dto.response.FriendInviteStatusResponse;
import com.Hoseo.CapstoneDesign.friend.dto.response.FriendResponse;
import com.Hoseo.CapstoneDesign.friend.facade.FriendFacade;
import com.Hoseo.CapstoneDesign.global.exception.GlobalExceptionResponse;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/friends")
@Tag(name = "Friend", description = "Friend and invite API entry points")
@SecurityRequirement(name = "bearerAuth")
public class FriendController {

    private final FriendFacade friendFacade;

    @GetMapping
    @Operation(summary = "Get friends", description = "Returns the current user's accepted friend list.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Friend list returned",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = FriendResponse.class)))),
            @ApiResponse(responseCode = "401", description = "Authentication failed",
                    content = @Content(schema = @Schema(implementation = GlobalExceptionResponse.class)))
    })
    public ResponseEntity<List<FriendResponse>> getFriends(
            @AuthenticationPrincipal UserDetailImpl userDetail
    ) {
        return ResponseEntity.ok(friendFacade.getFriends(userDetail.getAuthenticatedUser()));
    }

    @GetMapping("/invites")
    @Operation(summary = "Get pending friend invites", description = "Returns friend requests received by the current user.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Invite list returned",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = FriendInviteStatusResponse.class)))),
            @ApiResponse(responseCode = "401", description = "Authentication failed",
                    content = @Content(schema = @Schema(implementation = GlobalExceptionResponse.class)))
    })
    public ResponseEntity<FriendInviteStatusResponse> getInvites(
            @AuthenticationPrincipal UserDetailImpl userDetail
    ) {
        return ResponseEntity.ok(friendFacade.getInvites(userDetail.getAuthenticatedUser()));
    }

    @PostMapping("/{friendId}/request")
    @Operation(summary = "Send friend request", description = "Sends a friend request to the target user.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Friend request sent",
                    content = @Content(schema = @Schema(implementation = FriendInviteStatusResponse.class))),
            @ApiResponse(responseCode = "400", description = "Already friends or request exists",
                    content = @Content(schema = @Schema(implementation = GlobalExceptionResponse.class))),
            @ApiResponse(responseCode = "401", description = "Authentication failed",
                    content = @Content(schema = @Schema(implementation = GlobalExceptionResponse.class)))
    })
    public ResponseEntity<FriendInviteResponse> requestFriend(
            @Parameter(description = "Target user id", example = "2")
            @PathVariable Long friendId,
            @AuthenticationPrincipal UserDetailImpl userDetail
    ) {
        return ResponseEntity.ok(friendFacade.sendRequest(userDetail.getAuthenticatedUser(), friendId));
    }

    @PostMapping("/invites/{inviteId}/accept")
    @Operation(summary = "Accept friend invite", description = "Accepts a pending friend request.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Invite accepted",
                    content = @Content(schema = @Schema(implementation = FriendInviteStatusResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invite not found",
                    content = @Content(schema = @Schema(implementation = GlobalExceptionResponse.class))),
            @ApiResponse(responseCode = "401", description = "Authentication failed",
                    content = @Content(schema = @Schema(implementation = GlobalExceptionResponse.class)))
    })
    public ResponseEntity<FriendInviteResponse> acceptInvite(
            @Parameter(description = "Invite id", example = "1")
            @PathVariable Long inviteId,
            @AuthenticationPrincipal UserDetailImpl userDetail
    ) {
        return ResponseEntity.ok(friendFacade.acceptInvite(userDetail.getAuthenticatedUser(), inviteId));
    }

    @PostMapping("/invites/{inviteId}/decline")
    @Operation(summary = "Decline friend invite", description = "Declines a pending friend request.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Invite declined",
                    content = @Content(schema = @Schema(implementation = FriendInviteStatusResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invite not found",
                    content = @Content(schema = @Schema(implementation = GlobalExceptionResponse.class))),
            @ApiResponse(responseCode = "401", description = "Authentication failed",
                    content = @Content(schema = @Schema(implementation = GlobalExceptionResponse.class)))
    })
    public ResponseEntity<FriendInviteResponse> declineInvite(
            @Parameter(description = "Invite id", example = "1")
            @PathVariable Long inviteId,
            @AuthenticationPrincipal UserDetailImpl userDetail
    ) {
        return ResponseEntity.ok(friendFacade.declineInvite(userDetail.getAuthenticatedUser(), inviteId));
    }

    // 보낸 친구 요청 취소 (대기 중인 요청만 가능)
    @PostMapping("/invites/{inviteId}/cancel")
    @Operation(summary = "Cancel friend invite", description = "Cancels a pending friend request.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Invite canceled",
                    content = @Content(schema = @Schema(implementation = FriendInviteStatusResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invite not found",
                    content = @Content(schema = @Schema(implementation = GlobalExceptionResponse.class))),
            @ApiResponse(responseCode = "401", description = "Authentication failed",
                    content = @Content(schema = @Schema(implementation = GlobalExceptionResponse.class)))
    })
    public ResponseEntity<FriendInviteResponse> cancelInviteRequest(
            @Parameter(description = "Invite id", example = "1")
            @PathVariable Long inviteId,
            @AuthenticationPrincipal UserDetailImpl userDetail
    ) {
        return ResponseEntity.ok(friendFacade.cancelInviteRequest(userDetail.getAuthenticatedUser(),inviteId));
    }
}