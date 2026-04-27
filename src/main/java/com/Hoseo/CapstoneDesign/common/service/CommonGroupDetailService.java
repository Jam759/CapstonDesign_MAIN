package com.Hoseo.CapstoneDesign.common.service;

import com.Hoseo.CapstoneDesign.common.entity.CommonGroupDetail;
import com.Hoseo.CapstoneDesign.common.exception.CommonErrorCode;
import com.Hoseo.CapstoneDesign.common.exception.CommonException;
import com.Hoseo.CapstoneDesign.common.repository.CommonGroupDetailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommonGroupDetailService {

    public static final String PROJECT_TECH_STACK_GROUP_ID = "PROJECT_TECH_STACK";
    public static final String PROJECT_POSITION_GROUP_ID = "PROJECT_POSITION";
    public static final String USER_GOAL_GROUP_ID = "USER_GOAL";

    private final CommonGroupDetailRepository repository;

    public CommonGroupDetail getReferenceById(String commonGroupDetailId) {
        return repository.getReferenceById(commonGroupDetailId);
    }

    public List<CommonGroupDetail> getReferencesByIds(List<String> commonGroupDetailIds) {
        return commonGroupDetailIds.stream()
                .map(repository::getReferenceById)
                .toList();
    }

    public List<String> getProjectTechStackIds() {
        return getCommonGroupDetailIds(PROJECT_TECH_STACK_GROUP_ID);
    }

    public List<String> getProjectPositionIds() {
        return getCommonGroupDetailIds(PROJECT_POSITION_GROUP_ID);
    }

    public List<String> getUserGoalIds() {
        return getCommonGroupDetailIds(USER_GOAL_GROUP_ID);
    }

    public CommonGroupDetail getRequiredReferenceByGroupAndId(String commonGroupId, String commonGroupDetailId) {
        if (!StringUtils.hasText(commonGroupDetailId)) {
            throw new CommonException(CommonErrorCode.COMMON_CODE_INVALID);
        }

        return repository
                .findByCommonGroupCommonGroupIdAndCommonGroupDetailIdAndDeletedAtIsNull(
                        commonGroupId,
                        commonGroupDetailId
                )
                .orElseThrow(() -> new CommonException(CommonErrorCode.COMMON_CODE_INVALID));
    }

    public List<CommonGroupDetail> getRequiredReferencesByGroupAndIds(
            String commonGroupId,
            List<String> commonGroupDetailIds
    ) {
        if (commonGroupDetailIds == null || commonGroupDetailIds.isEmpty()) {
            return List.of();
        }

        Set<String> normalizedIds = new LinkedHashSet<>();
        for (String commonGroupDetailId : commonGroupDetailIds) {
            if (!StringUtils.hasText(commonGroupDetailId)) {
                throw new CommonException(CommonErrorCode.COMMON_CODE_INVALID);
            }
            normalizedIds.add(commonGroupDetailId);
        }

        List<CommonGroupDetail> commonGroupDetails =
                repository.findByCommonGroupCommonGroupIdAndCommonGroupDetailIdInAndDeletedAtIsNull(
                        commonGroupId,
                        normalizedIds
                );

        Map<String, CommonGroupDetail> commonGroupDetailMap = commonGroupDetails.stream()
                .collect(Collectors.toMap(CommonGroupDetail::getCommonGroupDetailId, Function.identity()));

        if (commonGroupDetailMap.size() != normalizedIds.size()) {
            throw new CommonException(CommonErrorCode.COMMON_CODE_INVALID);
        }

        return normalizedIds.stream()
                .map(commonGroupDetailMap::get)
                .toList();
    }

    private List<String> getCommonGroupDetailIds(String commonGroupId) {
        return repository.findByCommonGroupCommonGroupIdAndDeletedAtIsNullOrderByCommonGroupDetailIdAsc(
                        commonGroupId
                )
                .stream()
                .map(CommonGroupDetail::getCommonGroupDetailId)
                .toList();
    }
}
