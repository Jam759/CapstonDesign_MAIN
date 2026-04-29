package com.Hoseo.CapstoneDesign.image.facade;

import com.Hoseo.CapstoneDesign.global.annotation.Facade; // [규칙 Must] @Facade 사용
import com.Hoseo.CapstoneDesign.image.dto.response.ImageUploadResponse;
import com.Hoseo.CapstoneDesign.image.entity.Image;
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

    @Override
    @Transactional(readOnly = false) // [규칙 Must] 트랜잭션 명시
    public ImageUploadResponse uploadTempImage(Long uploaderId, MultipartFile file) {

        // 1. 서비스 호출 (도메인 로직 및 영속성 처리)
        Users uploader = userService.getReferenceById(uploaderId);
        Image savedImage = imageService.createAndSaveTempImage(uploader, file);

        // 2. 가공 로직
        String s3PublicUrl = "https://s3.../public/" + savedImage.getUploadImgName();

        // 3. [규칙 Must] 팩토리를 통해 DTO 변환 후 반환
        return ImageDtoFactory.toUploadResponse(savedImage, s3PublicUrl);
    }
}