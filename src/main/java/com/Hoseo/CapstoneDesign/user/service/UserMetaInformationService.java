package com.Hoseo.CapstoneDesign.user.service;

import com.Hoseo.CapstoneDesign.gamification.exception.GamificationErrorCode;
import com.Hoseo.CapstoneDesign.gamification.exception.GamificationException;
import com.Hoseo.CapstoneDesign.user.entity.UserMetaInformation;
import com.Hoseo.CapstoneDesign.user.entity.Users;
import com.Hoseo.CapstoneDesign.user.factory.UserEntityFactory;
import com.Hoseo.CapstoneDesign.user.repository.UserMetaInformationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserMetaInformationService {


    private final UserMetaInformationRepository repository;

    public UserMetaInformation getOrCreate(Users user) {
        return repository.findByUser(user.getUserId())
                .orElseGet( () -> {
                    UserMetaInformation newEntity = UserEntityFactory.toUserMetaInformation(user);
                    return repository.save(newEntity);
                });
    }

    public UserMetaInformation getMetaInfo(Long userId) {
        return repository.findByUser(userId)
                .orElseThrow(() -> new GamificationException(GamificationErrorCode.USER_META_NOT_FOUND));
    }

    public List<UserMetaInformation> getAllMetaInfoByExpDesc() {
        return repository.findAllOrderByTotalExpDesc();
    }

    public int calculateTopPercentage(long totalExp) {
        Object[] stats = repository.findRankStats(totalExp);
        long usersAbove = ((Number) stats[0]).longValue();
        long total      = ((Number) stats[1]).longValue();
        return total == 0 ? 100 : (int) Math.ceil((double) (usersAbove + 1) / total * 100);
    }
}
