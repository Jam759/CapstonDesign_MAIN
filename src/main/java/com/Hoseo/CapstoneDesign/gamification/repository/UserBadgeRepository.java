package com.Hoseo.CapstoneDesign.gamification.repository;

import com.Hoseo.CapstoneDesign.gamification.entity.UserBadge;
import com.Hoseo.CapstoneDesign.user.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface UserBadgeRepository extends JpaRepository<UserBadge, Long> {

    Set<UserBadge> findByUser(Users user);

}
