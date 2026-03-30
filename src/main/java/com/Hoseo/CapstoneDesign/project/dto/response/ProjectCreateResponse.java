package com.Hoseo.CapstoneDesign.project.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectCreateResponse {
    private Long projectId;
    private String title;
    private String description;
    private LocalDateTime createdAt;
}
