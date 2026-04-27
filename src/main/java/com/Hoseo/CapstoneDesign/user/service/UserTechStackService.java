package com.Hoseo.CapstoneDesign.user.service;

import com.Hoseo.CapstoneDesign.common.entity.CommonGroupDetail;
import com.Hoseo.CapstoneDesign.user.entity.UserTechStack;
import com.Hoseo.CapstoneDesign.user.entity.Users;
import com.Hoseo.CapstoneDesign.user.factory.UserEntityFactory;
import com.Hoseo.CapstoneDesign.user.repository.UserTechStackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserTechStackService {

    private final UserTechStackRepository repository;

    public List<UserTechStack> getByUser(Users user) {
        return repository.findByUserOrderByUserTechStackCommonGroupDetailIdAsc(user);
    }

    public void replaceUserTechStacks(Users user, List<CommonGroupDetail> techStacks) {
        repository.deleteByUser(user);
        if (techStacks == null || techStacks.isEmpty()) {
            return;
        }
        repository.saveAll(UserEntityFactory.toUserTechStackList(user, techStacks));
    }
}
