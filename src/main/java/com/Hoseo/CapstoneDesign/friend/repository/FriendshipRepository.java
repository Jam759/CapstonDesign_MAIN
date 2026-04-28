package com.Hoseo.CapstoneDesign.friend.repository;

import com.Hoseo.CapstoneDesign.friend.entity.Friendship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {

    @Query("SELECT f FROM Friendship f JOIN FETCH f.requester JOIN FETCH f.receiver " +
           "WHERE (f.requester.userId = :userId OR f.receiver.userId = :userId) " +
           "AND f.requester.deletedAt IS NULL " +
           "AND f.receiver.deletedAt IS NULL " +
           "AND f.status = com.Hoseo.CapstoneDesign.friend.entity.enums.FriendshipStatus.ACCEPTED")
    List<Friendship> findAllAcceptedByUserId(@Param("userId") Long userId);

    @Query("SELECT f FROM Friendship f JOIN FETCH f.requester JOIN FETCH f.receiver " +
           "WHERE f.receiver.userId = :userId " +
           "AND f.requester.deletedAt IS NULL " +
           "AND f.receiver.deletedAt IS NULL " +
           "AND f.status = com.Hoseo.CapstoneDesign.friend.entity.enums.FriendshipStatus.PENDING")
    List<Friendship> findPendingInvitesByReceiverId(@Param("userId") Long userId);

    @Query("SELECT f FROM Friendship f JOIN FETCH f.requester JOIN FETCH f.receiver " +
           "WHERE f.id = :id AND f.receiver.userId = :userId " +
           "AND f.requester.deletedAt IS NULL " +
           "AND f.receiver.deletedAt IS NULL")
    Optional<Friendship> findByIdAndReceiverUserId(@Param("id") Long id, @Param("userId") Long userId);

    boolean existsByRequesterUserIdAndReceiverUserId(Long requesterId, Long receiverId);
}
