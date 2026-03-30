package com.Hoseo.CapstoneDesign.project.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectInviteRequest {
    private Long projectId;
    private Long inviteMemberId;
}
