package com.Hoseo.CapstoneDesign.security.service;

import com.Hoseo.CapstoneDesign.security.entity.RefreshToken;
import com.Hoseo.CapstoneDesign.user.entity.Users;

import java.time.LocalDateTime;

public interface RefreshTokenService {

    String createAndSaveInitial(Users user);

    String rotate(Users user, String rawOldRefreshToken);
}
