package com.Hoseo.CapstoneDesign.project.dto.application;

import com.Hoseo.CapstoneDesign.project.entity.enums.ProjectType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class NewProjectJobMessage {
    public String repoUrl;
    public ProjectType projectType;
}
