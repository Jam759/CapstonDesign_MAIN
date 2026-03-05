package com.Hoseo.CapstoneDesign.user.repository;

import com.Hoseo.CapstoneDesign.user.entity.Users;
import com.Hoseo.CapstoneDesign.user.entity.enums.OauthType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UsersRepository extends JpaRepository<Users, Long> {

    Optional<Users> findByIdentityId(UUID identityId);
    Optional<Users> findByOauthTypeAndOauthProviderId(OauthType oauthType, String oauthProviderId);

}
