package com.bakooza.bakooza.Service;

import com.bakooza.bakooza.DTO.*;
import com.bakooza.bakooza.Entity.Board;
import com.bakooza.bakooza.Repository.BoardRepository;
import com.bakooza.bakooza.Repository.PostImageRepository;
import com.bakooza.bakooza.Util.ErrorHandler.CustomException;
import com.bakooza.bakooza.Util.ErrorHandler.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {

    private final BoardRepository boardRepository;
    private final PostImageRepository postImageRepository;

    // 게시글 작성
    @Override
    public Long save(final BoardDTO params) {
        return boardRepository.save(params.toEntity()).getPostId();
    }

    // 게시판 조회
    @Override
    public Page<Board> findByCategoryId(final int categoryId, final Pageable pageable) {
        return boardRepository.findByCategoryId( pageable);
    }

    // 게시글 수정
    @Override
    public void update(final Long postId, final BoardRequestDTO params) {
        Board entity = boardRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POSTS_NOT_FOUND));
        entity.update(params.getTitle(), params.getContent(), params.getCategoryId(), params.getWriter());
    }

    // 게시글 삭제
    @Override
    public int deleteExpiredPost() {
        return boardRepository.deleteExpiredPost();
    }

    @Override
    public void delete(Long postId) {
        boardRepository.delete(postId);
        postImageRepository.deleteByPostId(postId);
    }

    // 게시글 검색

    @Override
    public Page<Board> search(final String keyword, final Pageable pageable) {
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "post_id"));
        return boardRepository.findByTitleContainingAndIsDeleted("%" + keyword + "%", pageRequest);
    }

    // 게시글 조회
    @Override
    public DetailBoardResponseDTO findById(final Long postId) {
        Board entity = boardRepository.findByPostIdAndIsDeleted(postId, 0)
            .orElseThrow(() -> new CustomException(ErrorCode.POSTS_NOT_FOUND));
        entity.increaseViews(); // 조회수 증가
        return new DetailBoardResponseDTO(entity);
    }

    // 해당 게시글의 이미지 찾기
    @Override
    public List<ImageResponseDTO> findByPostId(Long postId) {
        List<ImageResponseDTO> entity = postImageRepository.findByPostId(postId);
        return entity;
    }
}