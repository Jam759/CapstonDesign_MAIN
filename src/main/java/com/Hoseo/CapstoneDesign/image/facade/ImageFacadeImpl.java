package com.Hoseo.CapstoneDesign.image.facade;

import com.Hoseo.CapstoneDesign.global.annotation.Facade;
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

    @Override
    @Transactional(readOnly = false)
    public ImageUploadResponse uploadTempImage(Long uploaderId, TargetType targetType, MultipartFile file) {

        Users uploader = userService.getReferenceById(uploaderId);

        // 💡 ImageService에서 파일을 S3에 올리고 DB에 저장한 엔티티를 받아옵니다.
        Image savedImage = imageService.createAndSaveTempImage(uploader, targetType, file);

        /* * 💡 [해결 포인트]
         * 기존: .../public/ + 파일명 (X)
         * 수정: 버킷주소 + 실제저장경로(imgPath) + 파일명 (O)
         * savedImage.getImgPath() 안에는 "/temp/profile/" 혹은 "/temp/post/"가 들어있습니다.
         */
        String bucketBaseUrl = "https://hoseo-capstonedesign-project-erp-405894844993-ap-northeast-2-an.s3.ap-northeast-2.amazonaws.com";

        // 경로가 슬래시(/)로 시작하므로 중복되지 않게 합쳐줍니다.
        String s3PublicUrl = bucketBaseUrl + savedImage.getImgPath() + savedImage.getUploadImgName();

        return ImageDtoFactory.toUploadResponse(savedImage, s3PublicUrl);
    }
}