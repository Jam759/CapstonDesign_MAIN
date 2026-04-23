package com.Hoseo.CapstoneDesign.common.repository;

import com.Hoseo.CapstoneDesign.common.entity.CommonGroupDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommonGroupDetailRepository extends JpaRepository<CommonGroupDetail, String> {

    List<CommonGroupDetail> findByCommonGroupCommonGroupIdAndDeletedAtIsNullOrderByCommonGroupDetailIdAsc(
            String commonGroupId
    );
}
