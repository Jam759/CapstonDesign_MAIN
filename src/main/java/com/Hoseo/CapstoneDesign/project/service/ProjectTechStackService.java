package com.Hoseo.CapstoneDesign.project.service;

import com.Hoseo.CapstoneDesign.project.entity.ProjectTechStack;
import com.Hoseo.CapstoneDesign.project.entity.Projects;
import com.Hoseo.CapstoneDesign.project.repository.ProjectTechStackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectTechStackService {

    private final ProjectTechStackRepository repository;

    public List<ProjectTechStack> createAll(List<ProjectTechStack> projectTechStacks) {
        return repository.saveAll(projectTechStacks);
    }

    public List<ProjectTechStack> getByProject(Projects project) {
        return repository.findByProject(project);
    }


}
