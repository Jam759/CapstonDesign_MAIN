package com.Hoseo.CapstoneDesign.project.dto.response;

import com.Hoseo.CapstoneDesign.project.entity.enums.ProjectInviteStatus;

public record ProjectInviteResponse(
        Long projectMemberId,
        Long invitedUserId,
        ProjectInviteStatus status
) {}
