package com.Hoseo.CapstoneDesign.badge.service;

import com.Hoseo.CapstoneDesign.badge.entity.UserBadge;
import com.Hoseo.CapstoneDesign.badge.exception.UserBadgeErrorCode;
import com.Hoseo.CapstoneDesign.badge.exception.UserBadgeException;
import com.Hoseo.CapstoneDesign.badge.repository.UserBadgeRepository;
import com.Hoseo.CapstoneDesign.user.entity.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserBadgeService {

    private final UserBadgeRepository repository;

    public UserBadge save(UserBadge userBadge) {
        if(
                userBadge.getBadge() == null ||
                userBadge.getUser() == null ||
                userBadge.getIsEquipped() == null
        ) throw new UserBadgeException(UserBadgeErrorCode.USER_BADGE_SAVE_ERROR);

        return repository.save(userBadge);
    }

    //착용한 뱃지를 반환
    public Set<UserBadge> updateUserBadgeEquip(Users user, Set<Long> equippedBadgeIds) {
        Set<UserBadge> userBadges = repository.findByUser(user);
        userBadges.forEach(UserBadge::unequipped);
        
        Set<Long> targetBadgeIds = equippedBadgeIds == null ? Set.of() : equippedBadgeIds;
        return userBadges.stream()
                        .filter(userBadge -> 
                                targetBadgeIds.contains(userBadge.getBadge().getBadgeId())
                        )
                        .map(UserBadge::equipped)
                        .collect(Collectors.toSet());
    }


}
