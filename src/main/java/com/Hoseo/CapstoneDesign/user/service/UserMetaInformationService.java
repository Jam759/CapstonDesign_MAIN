package com.Hoseo.CapstoneDesign.user.service;

import com.Hoseo.CapstoneDesign.gamification.exception.GamificationErrorCode;
import com.Hoseo.CapstoneDesign.gamification.exception.GamificationException;
import com.Hoseo.CapstoneDesign.user.dto.query.RankStatsQueryResult;
import com.Hoseo.CapstoneDesign.user.entity.UserMetaInformation;
import com.Hoseo.CapstoneDesign.user.entity.Users;
import com.Hoseo.CapstoneDesign.user.factory.UserEntityFactory;
import com.Hoseo.CapstoneDesign.user.repository.UserMetaInformationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@RequiredArgsConstructor
public class UserMetaInformationService {

    private final UserMetaInformationRepository repository;
    private final AtomicBoolean rankRebuildRequested = new AtomicBoolean(false);

    @Transactional
    public UserMetaInformation getOrCreate(Users user) {
        return repository.findByUser(user.getUserId())
                .orElseGet( () -> {
                    long initialRank = repository.findDenseRankByTotalExp(0L);
                    UserMetaInformation newEntity = UserEntityFactory.toUserMetaInformation(user, initialRank);
                    return repository.save(newEntity);
                });
    }

    public UserMetaInformation getMetaInfo(Long userId) {
        return repository.findByUser(userId)
                .orElseThrow(() -> new GamificationException(GamificationErrorCode.USER_META_NOT_FOUND));
    }

    public List<UserMetaInformation> getAllMetaInfoByRank() {
        return repository.findAllOrderByRankAscUserIdAsc();
    }

    public int calculateTopPercentage(long totalExp) {
        RankStatsQueryResult stats = repository.findRankStats(totalExp);
        long usersAbove = stats.usersAbove();
        long total      = stats.total();
        return total == 0 ? 100 : (int) Math.ceil((double) (usersAbove + 1) / total * 100);
    }

    public void requestRankRebuild() {
        rankRebuildRequested.set(true);
    }

    @Transactional
    public void deleteByUserId(Long userId) {
        if (!repository.existsById(userId)) {
            return;
        }
        repository.deleteById(userId);
        requestRankRebuild();
    }

    @Transactional
    public boolean rebuildRanksIfRequested() {
        if (!rankRebuildRequested.compareAndSet(true, false)) {
            return false;
        }
        try {
            repository.rebuildDenseRanks();
            return true;
        } catch (RuntimeException e) {
            rankRebuildRequested.set(true);
            throw e;
        }
    }
}
