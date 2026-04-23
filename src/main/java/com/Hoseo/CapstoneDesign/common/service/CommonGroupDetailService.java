package com.Hoseo.CapstoneDesign.common.service;

import com.Hoseo.CapstoneDesign.common.entity.CommonGroupDetail;
import com.Hoseo.CapstoneDesign.common.repository.CommonGroupDetailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommonGroupDetailService {

    private final CommonGroupDetailRepository repository;

    private static final String PROJECT_TECH_STACK_GROUP_ID = "PROJECT_TECH_STACK";

    public CommonGroupDetail getReferenceById(String commonGroupDetailId) {
        return repository.getReferenceById(commonGroupDetailId);
    }

    public List<CommonGroupDetail> getReferencesByIds(List<String> commonGroupDetailIds) {
        return commonGroupDetailIds.stream()
                .map(repository::getReferenceById)
                .toList();
    }

    public List<String> getProjectTechStackIds() {
        return repository
                .findByCommonGroupCommonGroupIdAndDeletedAtIsNullOrderByCommonGroupDetailIdAsc(
                        PROJECT_TECH_STACK_GROUP_ID
                )
                .stream()
                .map(CommonGroupDetail::getCommonGroupDetailId)
                .toList();
    }
}
