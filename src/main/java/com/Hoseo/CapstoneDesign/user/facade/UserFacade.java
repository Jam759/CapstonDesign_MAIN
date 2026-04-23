package com.Hoseo.CapstoneDesign.user.facade;

import com.Hoseo.CapstoneDesign.user.dto.request.UserProfileUpdateRequest;
import com.Hoseo.CapstoneDesign.user.dto.response.MyInfoResponse;
import com.Hoseo.CapstoneDesign.user.dto.response.UpdateUserInfoResponse;
import com.Hoseo.CapstoneDesign.user.entity.Users;

public interface UserFacade {
    UpdateUserInfoResponse updateUserProfile(Users user, UserProfileUpdateRequest request);
    MyInfoResponse getMyInfo(Users user);
}
