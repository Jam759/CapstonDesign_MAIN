package com.Hoseo.CapstoneDesign.image.facade;

import com.Hoseo.CapstoneDesign.global.annotation.Facade;
import com.Hoseo.CapstoneDesign.global.aws.s3.S3ObjectService;
import com.Hoseo.CapstoneDesign.image.dto.response.ImageUploadResponse;
import com.Hoseo.CapstoneDesign.image.entity.Image;
import com.Hoseo.CapstoneDesign.image.entity.enums.TargetType;
import com.Hoseo.CapstoneDesign.image.factory.ImageDtoFactory;
import com.Hoseo.CapstoneDesign.image.service.ImageService;
import com.Hoseo.CapstoneDesign.user.entity.Users;
import com.Hoseo.CapstoneDesign.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Facade
@RequiredArgsConstructor
public class ImageFacadeImpl implements ImageFacade {

    private final ImageService imageService;
    private final UserService userService;
    // URL 조회를 위해 S3ObjectService를 주입받습니다.
    private final S3ObjectService s3ObjectService;

    @Override
    @Transactional(readOnly = false)
    public ImageUploadResponse uploadTempImage(Long uploaderId, TargetType targetType, MultipartFile file) {

        Users uploader = userService.getReferenceById(uploaderId);

        // 1. 이미지 저장 완료 (DB 저장 & S3 전송)
        Image savedImage = imageService.createAndSaveTempImage(uploader, targetType, file);

        // 2. 객체 키(objectKey) 조립 (맨 앞의 슬래시 제거)
        String objectKey = savedImage.getImgPath().startsWith("/")
                ? savedImage.getImgPath().substring(1) + savedImage.getUploadImgName()
                : savedImage.getImgPath() + savedImage.getUploadImgName();

        // S3ObjectService에게 URL 생성을 위임합니다.
        String s3PublicUrl = s3ObjectService.getPublicUrl(objectKey);

        return ImageDtoFactory.toUploadResponse(savedImage, s3PublicUrl);
    }
}