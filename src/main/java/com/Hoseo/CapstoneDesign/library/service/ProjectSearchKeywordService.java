package com.Hoseo.CapstoneDesign.library.service;

import com.Hoseo.CapstoneDesign.library.repository.ProjectSearchKeywordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectSearchKeywordService {

    private final ProjectSearchKeywordRepository repository;

    public List<String> findKeywords(Long projectId, Long jobId) {
        return repository.findByProjectProjectIdAndJobIdOrderByDisplayOrderAsc(projectId, jobId)
                .stream()
                .map(keyword -> keyword.getKeyword() == null ? "" : keyword.getKeyword().trim())
                .filter(keyword -> !keyword.isBlank())
                .distinct()
                .toList();
    }
}
