package com.Hoseo.CapstoneDesign.image.controller;

import com.Hoseo.CapstoneDesign.image.dto.response.ImageUploadResponse;
import com.Hoseo.CapstoneDesign.image.facade.ImageFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
            @RequestParam("userId") Long userId,
            @RequestPart("file") MultipartFile file) {

        ImageUploadResponse response = imageFacade.uploadTempImage(userId, file);
        return ResponseEntity.ok(response);
    }
}