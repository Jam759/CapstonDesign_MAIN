package com.Hoseo.CapstoneDesign.image.factory;

import com.Hoseo.CapstoneDesign.image.entity.Image;
import com.Hoseo.CapstoneDesign.user.entity.Users;

public class ImageEntityFactory {

    private ImageEntityFactory() {
        // 인스턴스화 방지 (순수 팩토리 역할)
    }

    // 초기 임시(TEMP) 이미지 엔티티 생성
    public static Image createTempImage(Users uploader, Long fileSize, String originName, String uploadName, String imgPath, String extension) {
        return Image.builder()
                .uploader(uploader)
                .fileSize(fileSize)
                .originImgName(originName)
                .uploadImgName(uploadName)
                .imgPath(imgPath)
                .imgExtensionCmdId(extension)
                // targetId, targetTypeCmdId는 글 작성 완료 시점에 세팅되므로 비워둡니다.
                .build();
    }
}