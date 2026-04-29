package com.Hoseo.CapstoneDesign.image.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "임시 이미지 업로드 응답")
public record ImageUploadResponse(
        @Schema(description = "이미지 고유 ID", example = "101")
        Long imageId,

        @Schema(description = "S3 퍼블릭 URL", example = "https://.../public/uuid.png")
        String imageUrl
) {
}