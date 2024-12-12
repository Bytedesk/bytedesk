package com.bytedesk.forum.comment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
    
    Page<CommentEntity> findByPostId(Long postId, Pageable pageable);
    
    Page<CommentEntity> findByUserId(Long userId, Pageable pageable);
    
    Integer countByUserId(Long userId);
    
    @Modifying
    @Query("UPDATE CommentEntity c SET c.likeCount = c.likeCount + 1 WHERE c.id = ?1")
    void incrementLikeCount(Long commentId);
} 