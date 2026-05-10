package com.Hoseo.CapstoneDesign.user.facade;

import com.Hoseo.CapstoneDesign.security.cache.dto.AuthenticatedUserCacheEntry;
import com.Hoseo.CapstoneDesign.user.dto.request.UserProfileUpdateRequest;
import com.Hoseo.CapstoneDesign.user.dto.response.MyInfoResponse;
import com.Hoseo.CapstoneDesign.user.dto.response.UpdateUserInfoResponse;
import com.Hoseo.CapstoneDesign.user.dto.response.UserProfileThumbnail;

import java.util.List;

public interface UserFacade {
    UpdateUserInfoResponse updateUserProfile(AuthenticatedUserCacheEntry user, UserProfileUpdateRequest request);
    MyInfoResponse getMyInfo(AuthenticatedUserCacheEntry user);

    List<UserProfileThumbnail> searchUserByServiceNickname(String serviceNickname, Integer page, Integer size);
}
