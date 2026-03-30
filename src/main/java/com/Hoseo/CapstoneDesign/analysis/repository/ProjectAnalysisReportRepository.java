package com.Hoseo.CapstoneDesign.analysis.repository;

import com.Hoseo.CapstoneDesign.analysis.entity.ProjectAnalysisReport;
import com.Hoseo.CapstoneDesign.analysis.entity.enums.ReportType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProjectAnalysisReportRepository extends JpaRepository<ProjectAnalysisReport, Long> {
    Optional<ProjectAnalysisReport> findTopByProjectProjectIdAndReportTypeOrderByVersionDescProjectAnalysisReportIdDesc(
            Long projectId,
            ReportType reportType
    );

    Optional<ProjectAnalysisReport> findByProjectProjectIdAndReportTypeAndVersion(
            Long projectId,
            ReportType reportType,
            Integer version
    );
}
