package com.Hoseo.CapstoneDesign.image.service;

import com.Hoseo.CapstoneDesign.image.entity.Image;
import com.Hoseo.CapstoneDesign.image.factory.ImageEntityFactory;
import com.Hoseo.CapstoneDesign.image.repository.ImageRepository;
import com.Hoseo.CapstoneDesign.user.entity.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final ImageRepository imageRepository;

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;
    private static final int MAX_WIDTH = 2000;
    private static final int MAX_HEIGHT = 2000;

    /**
     * 이미지 검증 후 DB에 임시 메타데이터를 저장하고 엔티티를 반환합니다.
     */
    public Image createAndSaveTempImage(Users uploader, MultipartFile file) {
        // 1. 도메인 규칙 검증
        validateImage(file);

        // 2. 가상의 S3 업로드 정보 생성 (나중에 S3 로직 추가)
        String originName = file.getOriginalFilename();
        String uploadName = UUID.randomUUID().toString() + ".png"; // 고유명
        String imgPath = "/public/"; // S3 경로
        String extension = "CMD_EXT_PNG"; // 공통 코드 (예시)

        // 3. Factory를 통해 엔티티 조립 (Service는 직접 new를 쓰지 않습니다)
        Image image = ImageEntityFactory.createTempImage(
                uploader, file.getSize(), originName, uploadName, imgPath, extension
        );

        // 4. 영속성 처리 (DB 저장)
        return imageRepository.save(image);
    }

    private void validateImage(MultipartFile file) {
        // 규격서 Rule 10: 추후 이 부분의 IllegalArgumentException을
        // GlobalBaseException을 상속받는 ImageException 등으로 교체해야 합니다.
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("이미지 용량이 너무 큽니다. (5MB 제한)");
        }

        try {
            BufferedImage image = ImageIO.read(file.getInputStream());
            if (image == null) throw new IllegalArgumentException("유효하지 않은 이미지입니다.");

            if (image.getWidth() > MAX_WIDTH || image.getHeight() > MAX_HEIGHT) {
                throw new IllegalArgumentException("해상도가 너무 큽니다. (최대 2000x2000)");
            }
        } catch (IOException e) {
            throw new RuntimeException("이미지 검증 중 오류 발생", e);
        }
    }
}