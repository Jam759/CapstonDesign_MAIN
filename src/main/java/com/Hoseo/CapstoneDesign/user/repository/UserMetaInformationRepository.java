package com.Hoseo.CapstoneDesign.user.repository;

import com.Hoseo.CapstoneDesign.user.entity.UserMetaInformation;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserMetaInformationRepository extends JpaRepository<UserMetaInformation, Long> {


    @Query( value = """
    SELECT umi.*
        FROM user_meta_information umi
    INNER JOIN users u
        ON u.user_id = umi.user_id
    WHERE 
        u.user_id = :userId
        AND u.deleted_at IS NULL
    """,nativeQuery = true)
    Optional<UserMetaInformation> findByUser(Long userId);

    @Query("""
            SELECT umi
            FROM UserMetaInformation umi
            JOIN umi.user u
            WHERE u.deletedAt IS NULL
            ORDER BY umi.currentRank ASC, umi.userId ASC
            """)
    List<UserMetaInformation> findAllOrderByRankAscUserIdAsc();

    @Query(value = """
            SELECT
                SUM(CASE WHEN umi.total_exp > :totalExp THEN 1 ELSE 0 END),
                COUNT(*)
            FROM user_meta_information umi
            INNER JOIN users u ON u.user_id = umi.user_id AND u.deleted_at IS NULL
            """, nativeQuery = true)
    Object[] findRankStats(Long totalExp);

    @Query(value = """
            SELECT COUNT(DISTINCT umi.total_exp) + 1
            FROM user_meta_information umi
            INNER JOIN users u ON u.user_id = umi.user_id AND u.deleted_at IS NULL
            WHERE umi.total_exp > :totalExp
            """, nativeQuery = true)
    Long findDenseRankByTotalExp(Long totalExp);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = """
            UPDATE user_meta_information umi
            INNER JOIN (
                SELECT ranked.user_id, ranked.new_rank
                FROM (
                    SELECT umi2.user_id,
                           DENSE_RANK() OVER (ORDER BY umi2.total_exp DESC) AS new_rank
                    FROM user_meta_information umi2
                    INNER JOIN users u ON u.user_id = umi2.user_id
                    WHERE u.deleted_at IS NULL
                ) ranked
            ) computed ON computed.user_id = umi.user_id
            SET umi.current_rank = computed.new_rank
            """, nativeQuery = true)
    int rebuildDenseRanks();
}
