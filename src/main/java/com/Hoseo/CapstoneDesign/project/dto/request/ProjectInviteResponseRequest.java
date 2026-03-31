package com.Hoseo.CapstoneDesign.project.dto.request;

import com.Hoseo.CapstoneDesign.project.entity.enums.ProjectInviteStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "프로젝트 초대 응답 요청")
public class ProjectInviteResponseRequest {

    @Schema(description = "응답 대상 프로젝트 ID", example = "202")
    private Long projectId;

    @Schema(description = "초대 응답 상태", example = "DECLINED")
    private ProjectInviteStatus responseStatus;
}
