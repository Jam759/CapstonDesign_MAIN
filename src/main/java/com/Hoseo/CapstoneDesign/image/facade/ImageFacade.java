package com.Hoseo.CapstoneDesign.image.facade;

import com.Hoseo.CapstoneDesign.image.dto.response.ImageUploadResponse;
import org.springframework.web.multipart.MultipartFile;

public interface ImageFacade {
    ImageUploadResponse uploadTempImage(Long uploaderId, MultipartFile file);
}