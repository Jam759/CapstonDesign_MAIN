package com.Hoseo.CapstoneDesign.project.service;

import com.Hoseo.CapstoneDesign.project.entity.ProjectMember;
import com.Hoseo.CapstoneDesign.project.entity.Projects;
import com.Hoseo.CapstoneDesign.project.entity.enums.ProjectInviteStatus;
import com.Hoseo.CapstoneDesign.project.repository.ProjectMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectMemberService {

    private final ProjectMemberRepository repository;

    public ProjectMember create(ProjectMember projectMember) {
        return repository.save(projectMember);
    }

    public List<ProjectMember> getProjectMember(Projects p) {
        return repository.findByProject(p);
    }

    public boolean isAcceptedMember(Long projectId, Long userId) {
        return repository.existsByProjectProjectIdAndUserUserIdAndResponse(
                projectId,
                userId,
                ProjectInviteStatus.ACCEPTED
        );
    }
}
