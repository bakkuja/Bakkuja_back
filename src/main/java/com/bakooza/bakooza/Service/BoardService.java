package com.bakooza.bakooza.Service;

import com.bakooza.bakooza.DTO.BoardDTO;
import com.bakooza.bakooza.DTO.BoardRequestDTO;
import com.bakooza.bakooza.DTO.BoardResponseDTO;
import com.bakooza.bakooza.DTO.ImageResponseDTO;
import com.bakooza.bakooza.Entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface BoardService {

    public Long save(final BoardDTO boardDTO);

    public List<Board> findByCategoryId(final int categoryId);

    public void update(final Long id, final BoardRequestDTO params);

    public int deleteExpiredPost();

    public void delete(final Long postId);

    public Page<Board> search(final String keyword, final Pageable pageable);

    public BoardResponseDTO findById(final Long postId);

    public List<ImageResponseDTO> findByPostId(Long postId);

    public List<Board> findAllPostId();
}