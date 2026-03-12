package com.Hoseo.CapstoneDesign.security.repository;

import com.Hoseo.CapstoneDesign.security.entity.AccessTokenBlackList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AccessTokenBlackListRepository extends JpaRepository<AccessTokenBlackList, UUID> {
}
