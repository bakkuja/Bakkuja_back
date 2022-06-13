package com.bakooza.bakooza.DTO;

import java.time.LocalDate;

public interface ImageResponseDTO {
    Long getPostId(); // 게시글 번호

    String getImagePath(); // 이미지 경로

    String getImageSize(); // 이미지 사이즈

    LocalDate getUploadDate(); // 업로드 날짜
}