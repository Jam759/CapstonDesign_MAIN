package com.Hoseo.CapstoneDesign.analysis.service;

import com.Hoseo.CapstoneDesign.analysis.entity.AnalysisJob;
import com.Hoseo.CapstoneDesign.analysis.repository.AnalysisJobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AnalysisJobService {
    private final AnalysisJobRepository repository;

    public AnalysisJob create(AnalysisJob job) {
        return repository.save(job);
    }

    public boolean existsByDeliveryId(String deliveryId) {
        return repository.existsByDeliveryId(deliveryId);
    }
}
