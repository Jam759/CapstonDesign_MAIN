package com.Hoseo.CapstoneDesign.common.repository;

import com.Hoseo.CapstoneDesign.common.entity.CommonGroupDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface CommonGroupDetailRepository extends JpaRepository<CommonGroupDetail, String> {

    List<CommonGroupDetail> findByCommonGroupCommonGroupIdAndDeletedAtIsNullOrderByCommonGroupDetailIdAsc(
            String commonGroupId
    );

    Optional<CommonGroupDetail> findByCommonGroupCommonGroupIdAndCommonGroupDetailIdAndDeletedAtIsNull(
            String commonGroupId,
            String commonGroupDetailId
    );

    List<CommonGroupDetail> findByCommonGroupCommonGroupIdAndCommonGroupDetailIdInAndDeletedAtIsNull(
            String commonGroupId,
            Collection<String> commonGroupDetailIds
    );
}
