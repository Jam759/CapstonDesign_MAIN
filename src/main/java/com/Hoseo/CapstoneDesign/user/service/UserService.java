package com.Hoseo.CapstoneDesign.user.service;

import com.Hoseo.CapstoneDesign.user.entity.Users;
import com.Hoseo.CapstoneDesign.user.entity.enums.OauthType;
import com.Hoseo.CapstoneDesign.user.exception.CustomUserException;
import com.Hoseo.CapstoneDesign.user.exception.UserErrorCode;
import com.Hoseo.CapstoneDesign.user.factory.UserEntityFactory;
import com.Hoseo.CapstoneDesign.user.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UsersRepository repository;

    public Users getByIdentityId(UUID identityId) {
        return repository.findByIdentityId(identityId)
                .orElseThrow( () -> new CustomUserException(UserErrorCode.USER_NOT_FOUND_ERROR));
    }

    public Users getOrCreateOauthUser(OauthType oauthType, String oauthProviderId, String oauthNickname) {
        Optional<Users> user =
                repository.findByOauthTypeAndOauthProviderId(oauthType, oauthProviderId);
        return user
                .map( u -> {
                    u.updateOauthNickname(oauthNickname);
                    return repository.save(u);
                })
                .orElseGet( () -> {
                    Users newUser = UserEntityFactory.toUsers(oauthType, oauthProviderId, oauthNickname);
                    return repository.save(newUser);
                });
    }

}
