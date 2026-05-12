package com.Hoseo.CapstoneDesign.library.repository;

import com.Hoseo.CapstoneDesign.library.entity.ProjectSearchKeyword;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectSearchKeywordRepository extends JpaRepository<ProjectSearchKeyword, Long> {

    List<ProjectSearchKeyword> findByProjectProjectIdAndJobIdOrderByDisplayOrderAsc(Long projectId, Long jobId);
}
