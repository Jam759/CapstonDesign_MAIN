package com.Hoseo.CapstoneDesign.gamification.repository;

import com.Hoseo.CapstoneDesign.gamification.entity.UserAiQuest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserPersonalQuestRepository extends JpaRepository<UserAiQuest, Long> {
}
