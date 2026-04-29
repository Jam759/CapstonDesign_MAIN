package com.Hoseo.CapstoneDesign.image.repository;

import com.Hoseo.CapstoneDesign.image.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {
}