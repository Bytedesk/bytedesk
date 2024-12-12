package com.bytedesk.forum.comment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentService {

    CommentEntity createComment(String content, Long postId, Long userId, Long parentId);

    CommentEntity updateComment(Long commentId, String content);

    void deleteComment(Long commentId);

    CommentEntity getComment(Long commentId);

    Page<CommentEntity> getCommentsByPost(Long postId, Pageable pageable);

    Page<CommentEntity> getCommentsByUser(Long userId, Pageable pageable);

    void incrementLikeCount(Long commentId);
} 