package com.Hoseo.CapstoneDesign.project.dto.request;

import com.Hoseo.CapstoneDesign.project.entity.enums.ProjectInviteStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectInviteResponseRequest {

    private Long projectId;
    //ACCEPTED, DECLINED 둘중 하나만 받게 하기
    private ProjectInviteStatus responseStatus;
}
