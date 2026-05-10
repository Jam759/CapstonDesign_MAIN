package com.Hoseo.CapstoneDesign.image.factory;

import com.Hoseo.CapstoneDesign.image.dto.response.ImageUploadResponse;
import com.Hoseo.CapstoneDesign.image.entity.Image;

public class ImageDtoFactory {

    private ImageDtoFactory() {}

    public static ImageUploadResponse toUploadResponse(Image image, String publicUrl) {
        return new ImageUploadResponse(image.getImageId(), publicUrl);
    }
}