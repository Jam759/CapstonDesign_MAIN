package com.Hoseo.CapstoneDesign.image.service;

import com.Hoseo.CapstoneDesign.image.entity.Image;
import com.Hoseo.CapstoneDesign.image.entity.enums.TargetType;
import com.Hoseo.CapstoneDesign.image.factory.ImageEntityFactory;
import com.Hoseo.CapstoneDesign.image.repository.ImageRepository;
import com.Hoseo.CapstoneDesign.user.entity.Users;
import com.Hoseo.CapstoneDesign.global.aws.s3.S3ObjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final ImageRepository imageRepository;
    private final S3ObjectService s3ObjectService;

    @Transactional
    public Image createAndSaveTempImage(Users uploader, TargetType targetType, MultipartFile file) {
        validateImage(targetType, file);

        String originName = file.getOriginalFilename();
        String extension = extractExtension(originName);
        String uploadName = UUID.randomUUID() + "." + extension;
        String imgPath = "/public/" + targetType.name().toLowerCase() + "/";

        String objectKey = imgPath.startsWith("/") ? imgPath.substring(1) + uploadName : imgPath + uploadName;

        // 실제 AWS S3로 파일을 전송합니다. (반환되는 URL 값은 여기서 쓰지 않고 Facade에서 조회합니다)
        s3ObjectService.uploadFile(objectKey, file);

        Image image = ImageEntityFactory.createTempImage(
                uploader,
                file.getSize(),
                originName,
                uploadName,
                imgPath,
                extension
        );

        return imageRepository.save(image);
    }

    private void validateImage(TargetType targetType, MultipartFile file) {
        if (file.isEmpty()) throw new IllegalArgumentException("파일이 비어있습니다.");
        if (targetType == TargetType.PROFILE && file.getSize() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException("프로필 이미지는 5MB를 초과할 수 없습니다.");
        } else if (targetType == TargetType.POST && file.getSize() > 20 * 1024 * 1024) {
            throw new IllegalArgumentException("게시글 이미지는 20MB를 초과할 수 없습니다.");
        }
    }

    private String extractExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    // 새롭게 추가된 이미지 타겟 연결 메서드 (클래스 내부로 올바르게 이동됨)
    @Transactional
    public void attachImagesToTarget(List<Long> imageIds, Long targetId, String targetTypeCmdId) {
        if (imageIds == null || imageIds.isEmpty()) {
            return;
        }
        // 전달받은 ID들로 이미지 엔티티들을 조회합니다.
        List<Image> images = imageRepository.findAllById(imageIds);

        // 조회된 이미지들에 타겟 정보(예: 질문글 ID, "QUESTION")를 업데이트합니다.
        for (Image image : images) {
            image.attachToTarget(targetId, targetTypeCmdId);
        }
    }
}