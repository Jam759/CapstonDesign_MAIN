package com.Hoseo.CapstoneDesign.user.service;

import com.Hoseo.CapstoneDesign.gamification.entity.LevelRule;
import com.Hoseo.CapstoneDesign.gamification.exception.GamificationErrorCode;
import com.Hoseo.CapstoneDesign.gamification.exception.GamificationException;
import com.Hoseo.CapstoneDesign.gamification.repository.LevelRuleRepository;
import com.Hoseo.CapstoneDesign.user.entity.UserMetaInformation;
import com.Hoseo.CapstoneDesign.user.entity.Users;
import com.Hoseo.CapstoneDesign.user.repository.UserMetaInformationRepository;
import com.Hoseo.CapstoneDesign.user.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserMetaInformationService {

    private static final int INITIAL_LEVEL = 1;

    private final UserMetaInformationRepository repository;
    private final LevelRuleRepository levelRuleRepository;
    private final UsersRepository usersRepository;

    @Transactional
    public UserMetaInformation getMetaInfo(Users user) {
        return repository.findById(user.getUserId())
                .orElseGet(() -> {
                    LevelRule level1 = levelRuleRepository.findById(INITIAL_LEVEL)
                            .orElseThrow(() -> new GamificationException(GamificationErrorCode.USER_META_NOT_FOUND));
                    // detached Users를 현재 세션에 붙은 프록시 참조로 교체
                    Users managedUser = usersRepository.getReferenceById(user.getUserId());
                    return repository.save(UserMetaInformation.builder()
                            .user(managedUser)
                            .totalExp(0L)
                            .levelRule(level1)
                            .build());
                });
    }

    public List<UserMetaInformation> getAllMetaInfoByExpDesc() {
        return repository.findByUserDeletedAtIsNullOrderByTotalExpDesc();
    }

    public long countUsersAbove(Long totalExp) {
        return repository.countByUserDeletedAtIsNullAndTotalExpGreaterThan(totalExp);
    }

    public long countAll() {
        return repository.count();
    }
}
