package com.Hoseo.CapstoneDesign.security.service.impl;

import com.Hoseo.CapstoneDesign.security.entity.UserDetailImpl;
import com.Hoseo.CapstoneDesign.security.exception.JwtUtilErrorCode;
import com.Hoseo.CapstoneDesign.security.exception.JwtUtilException;
import com.Hoseo.CapstoneDesign.user.entity.Users;
import com.Hoseo.CapstoneDesign.user.exception.CustomUserException;
import com.Hoseo.CapstoneDesign.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserDetailServiceImpl implements UserDetailsService {

    private final UserService service;

    @Override
    public UserDetails loadUserByUsername(String username) {
        UUID identityId = UUID.fromString(username);
        try {
            Users member = service.getByIdentityId(identityId);
            return new UserDetailImpl(member);
        } catch (CustomUserException e) {
            //jwt 서명부분은 맞는데 member를 못찾을 경우 이 예외를 던지게 함
            throw new JwtUtilException(JwtUtilErrorCode.TOKEN_ILLEGAL_ARGUMENT);
        }
    }
}
