package com.Hoseo.CapstoneDesign.user.repository;

import com.Hoseo.CapstoneDesign.user.entity.UserMetaInformation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserMetaInformationRepository extends JpaRepository<UserMetaInformation, Long> {
}
