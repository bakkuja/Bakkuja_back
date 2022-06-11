package com.bakooza.bakooza.DTO;


import com.bakooza.bakooza.Entity.Board;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class BoardResponseDTO {

    private final Long postId; // PK
    private final String title; // 제목
    private final String userLocation; //작성자 주소
    private final String content; // 내용
    private final int views; // 조회 수
    private final LocalDateTime postDate; // 생성일

    public BoardResponseDTO(Board entity) {
        this.postId = entity.getPostId();
        this.title = entity.getTitle();
        this.userLocation = entity.getUserLocation();
        this.content = entity.getContent();
        this.views = entity.getViews();
        this.postDate = entity.getPostDate();
    }

}