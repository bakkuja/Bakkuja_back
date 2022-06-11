package com.bakooza.bakooza.Repository;

import com.bakooza.bakooza.Entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {

    Optional<Board> findByPostIdAndIsDeleted(Long postId, int yN);

    @Query(value = "SELECT * FROM board WHERE is_deleted = false AND title LIKE :keyword", nativeQuery = true)
    List<Board> findByTitleContainingAndIsDeleted(@Param("keyword") String keyword);

    @Query(value = "SELECT * FROM board WHERE category_id = :categoryId AND is_deleted = false ORDER BY post_id DESC",
           countQuery = "SELECT COUNT(post_id) FROM Board WHERE category_id = :categoryId AND is_deleted = false",
           nativeQuery = true)
    List<Board> findByCategoryId(@Param("categoryId") final int categoryId);

    @Modifying(clearAutomatically = true)
    @Query(value = "DELETE FROM board WHERE DATEDIFF(CURDATE(), deleted_date) >= 7", nativeQuery = true)
    int deleteExpiredPost();

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE board SET is_deleted = 1, deleted_date = CURDATE() WHERE post_id = :postId", nativeQuery = true)
    void delete(@Param("postId") Long postId);

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE board SET is_deleted = 1, deleted_date = CURDATE() WHERE post_id = :postId", nativeQuery = true)
    int saveImage(@Param("postId") Long postId);

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE board SET thumbnail = :imagePath WHERE post_id = :postId", nativeQuery = true)
    void updateThumbnail(@Param("postId") Long postId, @Param("imagePath") String imagePath);

}
