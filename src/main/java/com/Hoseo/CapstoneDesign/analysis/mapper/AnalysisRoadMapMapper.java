package com.Hoseo.CapstoneDesign.analysis.mapper;

import com.Hoseo.CapstoneDesign.analysis.dto.query.ProjectRoadMapQueryRow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AnalysisRoadMapMapper {

    List<ProjectRoadMapQueryRow> findRoadMapRows(
            @Param("projectId") Long projectId,
            @Param("userId") Long userId
    );
}
