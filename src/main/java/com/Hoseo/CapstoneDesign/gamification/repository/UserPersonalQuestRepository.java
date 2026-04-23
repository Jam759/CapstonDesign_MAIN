package com.Hoseo.CapstoneDesign.gamification.repository;

import com.Hoseo.CapstoneDesign.gamification.entity.UserAiQuest;
import com.Hoseo.CapstoneDesign.user.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserPersonalQuestRepository extends JpaRepository<UserAiQuest, Long> {
    List<UserAiQuest> findByUser(Users user);
    List<UserAiQuest> findByUserAndProject_ProjectId(Users user, Long projectId);
}
