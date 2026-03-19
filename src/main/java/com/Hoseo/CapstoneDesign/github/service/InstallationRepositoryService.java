package com.Hoseo.CapstoneDesign.github.service;

import com.Hoseo.CapstoneDesign.github.entity.InstallationRepository;
import com.Hoseo.CapstoneDesign.github.repository.InstallationRepositoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InstallationRepositoryService {

    private final InstallationRepositoryRepository repository;

    public List<InstallationRepository> saveAll(List<InstallationRepository> entities){
        return repository.saveAll(entities);
    }

}
