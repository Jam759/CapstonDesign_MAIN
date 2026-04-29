package com.Hoseo.CapstoneDesign.image.service;

import com.Hoseo.CapstoneDesign.image.entity.Image;
import com.Hoseo.CapstoneDesign.image.entity.enums.TargetType;
import com.Hoseo.CapstoneDesign.image.factory.ImageEntityFactory;
import com.Hoseo.CapstoneDesign.image.repository.ImageRepository;
import com.Hoseo.CapstoneDesign.user.entity.Users;
// 💡 [추가] S3 업로드 서비스를 사용하기 위해 임포트합니다.
import com.Hoseo.CapstoneDesign.global.aws.s3.S3ObjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final ImageRepository imageRepository;
    // 💡 [추가] S3ObjectService를 의존성 주입(DI) 받습니다.
    private final S3ObjectService s3ObjectService;

    @Transactional
    public Image createAndSaveTempImage(Users uploader, TargetType targetType, MultipartFile file) {
        // 💡 1. 용도에 따른 이미지 검증 (60번 줄 에러 해결 포인트)
        validateImage(targetType, file);

        // 2. 파일 정보 추출 및 UUID 생성
        String originName = file.getOriginalFilename();
        String extension = extractExtension(originName);
        String uploadName = UUID.randomUUID() + "." + extension;
        String imgPath = "/public/" + targetType.name().toLowerCase() + "/";

        // ==========================================
        // 💡 [새로 추가된 S3 실제 파일 업로드 로직]
        // ==========================================
        // AWS S3는 객체 키(경로) 맨 앞에 슬래시(/)가 붙는 것을 권장하지 않으므로,
        // DB에 저장되는 imgPath는 그대로 두되 S3에 올릴 때만 맨 앞 슬래시를 빼고 합쳐줍니다.
        String objectKey = imgPath.startsWith("/") ? imgPath.substring(1) + uploadName : imgPath + uploadName;

        // 실제 AWS S3로 파일을 전송합니다. (이 과정에서 실패하면 아래 DB 저장도 취소(Rollback)됩니다.)
        s3ObjectService.uploadFile(objectKey, file);
        // ==========================================

        // 💡 3. 엔티티 생성 및 저장 (기존 로직 유지)
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
        if (file.isEmpty()) {
            throw new IllegalArgumentException("파일이 비어있습니다.");
        }

        if (targetType == TargetType.PROFILE) {
            // 프로필 사진: 5MB 제한
            if (file.getSize() > 5 * 1024 * 1024) {
                throw new IllegalArgumentException("프로필 이미지는 5MB를 초과할 수 없습니다.");
            }
        } else if (targetType == TargetType.POST) {
            // 게시글 사진: 20MB 제한
            if (file.getSize() > 20 * 1024 * 1024) {
                throw new IllegalArgumentException("게시글 이미지는 20MB를 초과할 수 없습니다.");
            }
        }
    }

    private String extractExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }
}