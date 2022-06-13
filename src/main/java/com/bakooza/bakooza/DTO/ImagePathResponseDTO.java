package com.bakooza.bakooza.DTO;

import com.bakooza.bakooza.Entity.PostImage;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ImagePathResponseDTO {
    private String imagePath;
    
    public PostImage toEntity() {
        return PostImage.builder()
                .imagePath(imagePath)
                .build();
    }
}
