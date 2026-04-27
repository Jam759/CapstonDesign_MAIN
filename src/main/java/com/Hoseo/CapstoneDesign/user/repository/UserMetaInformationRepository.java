package com.Hoseo.CapstoneDesign.user.repository;

import com.Hoseo.CapstoneDesign.user.entity.UserMetaInformation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserMetaInformationRepository extends JpaRepository<UserMetaInformation, Long> {
    List<UserMetaInformation> findByUserDeletedAtIsNullOrderByTotalExpDesc();
    long countByUserDeletedAtIsNullAndTotalExpGreaterThan(Long totalExp);
}
