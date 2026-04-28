package com.Hoseo.CapstoneDesign.project.mapper;

import com.Hoseo.CapstoneDesign.project.dto.query.InviteStatusQueryRow;
import com.Hoseo.CapstoneDesign.project.dto.query.ProjectThumbnailQueryRow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProjectMapper {

    List<ProjectThumbnailQueryRow> findMyProjectThumbnails(@Param("userId") Long userId);

    List<InviteStatusQueryRow> findMyInvites(@Param("userId") Long userId);
}
