package com.Hoseo.CapstoneDesign.user.service;

import com.Hoseo.CapstoneDesign.common.entity.CommonGroupDetail;
import com.Hoseo.CapstoneDesign.user.entity.Users;
import com.Hoseo.CapstoneDesign.user.entity.enums.OauthType;
import com.Hoseo.CapstoneDesign.user.exception.CustomUserException;
import com.Hoseo.CapstoneDesign.user.exception.UserErrorCode;
import com.Hoseo.CapstoneDesign.user.factory.UserEntityFactory;
import com.Hoseo.CapstoneDesign.user.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
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

    public Users getById(Long userId) {
        return repository.findById(userId)
                .orElseThrow(() -> new CustomUserException(UserErrorCode.USER_NOT_FOUND_ERROR));
    }

    public Users getReferenceById(Long userId) {
        return repository.getReferenceById(userId);
    }


    public Users getOrCreateOauthUser(OauthType oauthType, String oauthProviderId, String oauthNickname) {
        Optional<Users> user =
                repository.findByOauthTypeAndOauthProviderId(oauthType, oauthProviderId);
        return user
                .map( u -> {
                    u.syncOauthProfile(oauthNickname);
                    return repository.save(u);
                })
                .orElseGet( () -> {
                    Users newUser = UserEntityFactory.toUsers(oauthType, oauthProviderId, oauthNickname);
                    return repository.save(newUser);
                });
    }

    public Users updateUserProfile(
            Users user,
            String serviceNickname,
            String bio,
            CommonGroupDetail userGoal,
            CommonGroupDetail userMainPosition,
            boolean profileComplete
    ) {
        user.applyProfileUpdate(
                serviceNickname,
                bio,
                userGoal,
                userMainPosition,
                profileComplete
        );
        return repository.save(user);
    }

    public List<Users> searchByServiceNickname(String serviceNickname, Integer page, Integer size) {
        PageRequest pageRequest = PageRequest.of(
                page - 1,
                size,
                Sort.by(Sort.Direction.DESC, "userId")
        );
        return repository.findByServiceNicknameContainingIgnoreCase(serviceNickname.trim(), pageRequest)
                .getContent();
    }

}
