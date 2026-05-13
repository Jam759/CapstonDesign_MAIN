package com.Hoseo.CapstoneDesign.image.controller;

import com.Hoseo.CapstoneDesign.image.dto.response.ImageUploadResponse;
import com.Hoseo.CapstoneDesign.image.entity.enums.TargetType;
import com.Hoseo.CapstoneDesign.image.facade.ImageFacade;
import com.Hoseo.CapstoneDesign.security.entity.UserDetailImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Image", description = "이미지 처리 API")
@RestController
@RequestMapping("/api/v1/images")
@RequiredArgsConstructor
public class ImageController {

    private final ImageFacade imageFacade;

    @Operation(summary = "이미지 임시 업로드")
    @PostMapping(value = "/temp", consumes = "multipart/form-data")
    public ResponseEntity<ImageUploadResponse> uploadTempImage(
            // 인증 토큰에서 유저 정보를 직접 가져옵니다.
            @AuthenticationPrincipal UserDetailImpl userDetail,
            // 이미지 용도(PROFILE, POST)를 파라미터로 받습니다.
            @RequestParam("targetType") TargetType targetType,
            @RequestPart("file") MultipartFile file) {

        // private 필드 대신 public 메서드인 getUserId()를 사용합니다.
        Long userId = userDetail.getUserId();

        // Facade로 용도(targetType)를 함께 전달합니다.
        ImageUploadResponse response = imageFacade.uploadTempImage(userId, targetType, file);
        return ResponseEntity.ok(response);
    }
}