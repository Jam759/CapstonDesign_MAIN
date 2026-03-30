package com.Hoseo.CapstoneDesign.project.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectThumbnailResponse {
    private Long projectId;
    private String title;
    private String description;
}
