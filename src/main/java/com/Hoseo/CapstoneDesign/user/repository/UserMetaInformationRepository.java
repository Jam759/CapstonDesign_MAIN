package com.Hoseo.CapstoneDesign.user.repository;

import com.Hoseo.CapstoneDesign.user.entity.UserMetaInformation;
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
            ORDER BY umi.totalExp DESC
            """)
    List<UserMetaInformation> findAllOrderByTotalExpDesc();
    @Query(value = """
            SELECT
                SUM(CASE WHEN umi.total_exp > :totalExp THEN 1 ELSE 0 END),
                COUNT(*)
            FROM user_meta_information umi
            INNER JOIN users u ON u.user_id = umi.user_id AND u.deleted_at IS NULL
            """, nativeQuery = true)
    Object[] findRankStats(Long totalExp);
}
