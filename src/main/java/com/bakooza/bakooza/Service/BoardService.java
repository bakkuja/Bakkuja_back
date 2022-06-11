package com.bakooza.bakooza.Service;

import com.bakooza.bakooza.DTO.*;
import com.bakooza.bakooza.Entity.Board;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface BoardService {

    public Long save(final BoardDTO boardDTO);

    public List<Board> findByCategoryId(final int categoryId);

    public void update(final Long id, final BoardRequestDTO params);

    public int deleteExpiredPost();

    public void delete(final Long postId);

    public List<Board> search(final String keyword);

    public DetailBoardResponseDTO findById(final Long postId);

    public List<ImageResponseDTO> findByPostId(Long postId);

    public List<Board> findAllPostId();

    public void updateThumbnail(Long postId, String imagePath);
}