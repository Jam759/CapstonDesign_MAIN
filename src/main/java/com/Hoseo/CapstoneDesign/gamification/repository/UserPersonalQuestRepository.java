package com.Hoseo.CapstoneDesign.gamification.repository;

import com.Hoseo.CapstoneDesign.gamification.entity.UserAiQuest;
import com.Hoseo.CapstoneDesign.user.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserPersonalQuestRepository extends JpaRepository<UserAiQuest, Long> {
    List<UserAiQuest> findByUserAndProjectDeletedAtIsNull(Users user);

    List<UserAiQuest> findByUserAndProjectProjectIdAndProjectDeletedAtIsNull(Users user, Long projectId);

    Optional<UserAiQuest> findByUserAiQuestIdAndUserAndProjectDeletedAtIsNull(Long questId, Users user);
}
