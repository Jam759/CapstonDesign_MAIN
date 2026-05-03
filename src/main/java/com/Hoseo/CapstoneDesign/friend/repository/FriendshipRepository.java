package com.Hoseo.CapstoneDesign.friend.repository;

import com.Hoseo.CapstoneDesign.friend.entity.Friendship;
import com.Hoseo.CapstoneDesign.user.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
    // ID 크기 비교 규칙에 따라 두 사람이 이미 친구인지 확인합니다.
    boolean existsByUserAndFriendUser(Users user, Users friendUser);

    // 내 친구 목록 전체를 조회합니다. (어느 컬럼에 내 ID가 있든 상관없이 모두 가져옴)
    List<Friendship> findAllByUserOrFriendUser(Users user, Users friendUser);
}