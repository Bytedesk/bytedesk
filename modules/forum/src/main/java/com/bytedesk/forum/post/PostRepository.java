package com.bytedesk.forum.post;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bytedesk.core.category.CategoryEntity;

public interface PostRepository extends JpaRepository<PostEntity, Long>, JpaSpecificationExecutor<PostEntity> {
    
    Page<PostEntity> findByCategory(CategoryEntity category, Pageable pageable);
    
    Page<PostEntity> findByUserId(Long userId, Pageable pageable);
    
    Integer countByUserId(Long userId);
    
    @Modifying
    @Query("UPDATE PostEntity p SET p.viewCount = p.viewCount + 1 WHERE p.id = ?1")
    void incrementViewCount(Long postId);
    
    @Modifying
    @Query("UPDATE PostEntity p SET p.likeCount = p.likeCount + 1 WHERE p.id = ?1")
    void incrementLikeCount(Long postId);
    
    @Query(value = "SELECT * FROM bytedesk_post WHERE MATCH(title, content) AGAINST(:keyword IN BOOLEAN MODE)",
           countQuery = "SELECT COUNT(*) FROM bytedesk_post WHERE MATCH(title, content) AGAINST(:keyword IN BOOLEAN MODE)",
           nativeQuery = true)
    Page<PostEntity> fullTextSearch(@Param("keyword") String keyword, Pageable pageable);
    
    @Query(value = "SELECT * FROM bytedesk_forum_post " +
                   "WHERE MATCH(title, content) AGAINST(:keyword IN BOOLEAN MODE) " +
                   "AND category_id = :categoryId",
           countQuery = "SELECT COUNT(*) FROM bytedesk_forum_post " +
                       "WHERE MATCH(title, content) AGAINST(:keyword IN BOOLEAN MODE) " +
                       "AND category_id = :categoryId",
           nativeQuery = true)
    Page<PostEntity> fullTextSearchByCategory(
            @Param("keyword") String keyword,
            @Param("categoryId") Long categoryId,
            Pageable pageable);
} 