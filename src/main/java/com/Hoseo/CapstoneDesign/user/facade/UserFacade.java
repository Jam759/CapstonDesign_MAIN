package com.Hoseo.CapstoneDesign.user.facade;

import com.Hoseo.CapstoneDesign.user.dto.request.UserProfileUpdateRequest;
import com.Hoseo.CapstoneDesign.user.dto.response.UpdateUserInfoResponse;
import com.Hoseo.CapstoneDesign.user.dto.response.UserProfileResponse;
import com.Hoseo.CapstoneDesign.user.entity.Users;

public interface UserFacade {
    UpdateUserInfoResponse updateUserProfile(Users user, UserProfileUpdateRequest request);
}
