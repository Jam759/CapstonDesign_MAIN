package com.Hoseo.CapstoneDesign.badge.repository;

import com.Hoseo.CapstoneDesign.badge.entity.UserBadge;
import com.Hoseo.CapstoneDesign.user.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface UserBadgeRepository extends JpaRepository<UserBadge, Long> {

    Set<UserBadge> findByUser(Users user);

}
