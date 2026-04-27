package com.Hoseo.CapstoneDesign.user.repository;

import com.Hoseo.CapstoneDesign.user.entity.UserTechStack;
import com.Hoseo.CapstoneDesign.user.entity.Users;
import com.Hoseo.CapstoneDesign.user.entity.compositeKey.UserTechStackId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserTechStackRepository extends JpaRepository<UserTechStack, UserTechStackId> {

    void deleteByUser(Users user);

    List<UserTechStack> findByUserOrderByUserTechStackCommonGroupDetailIdAsc(Users user);
}
