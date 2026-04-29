package com.Hoseo.CapstoneDesign.image.facade;

import com.Hoseo.CapstoneDesign.image.dto.response.ImageUploadResponse;
import com.Hoseo.CapstoneDesign.image.entity.enums.TargetType;
import org.springframework.web.multipart.MultipartFile;

public interface ImageFacade {
    // 💡 [수정] 파라미터에 TargetType 추가
    ImageUploadResponse uploadTempImage(Long uploaderId, TargetType targetType, MultipartFile file);
}