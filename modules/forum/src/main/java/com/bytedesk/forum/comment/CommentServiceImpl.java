package com.bytedesk.forum.comment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Override
    @Transactional
    public CommentEntity createComment(String content, Long postId, Long userId, Long parentId) {
        CommentEntity comment = new CommentEntity();
        comment.setContent(content);
        comment.setPostId(postId);
        comment.setUserId(userId);
        comment.setParentId(parentId);
        
        return commentRepository.save(comment);
    }

    @Override
    @Transactional
    public CommentEntity updateComment(Long commentId, String content) {
        CommentEntity comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new RuntimeException("Comment not found"));
            
        comment.setContent(content);
        return commentRepository.save(comment);
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId) {
        CommentEntity comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new RuntimeException("Comment not found"));
            
        comment.setStatus("deleted");
        commentRepository.save(comment);
    }

    @Override
    public CommentEntity getComment(Long commentId) {
        return commentRepository.findById(commentId)
            .orElseThrow(() -> new RuntimeException("Comment not found"));
    }

    @Override
    public Page<CommentEntity> getCommentsByPost(Long postId, Pageable pageable) {
        return commentRepository.findByPostId(postId, pageable);
    }

    @Override
    public Page<CommentEntity> getCommentsByUser(Long userId, Pageable pageable) {
        return commentRepository.findByUserId(userId, pageable);
    }

    @Override
    @Transactional
    public void incrementLikeCount(Long commentId) {
        commentRepository.incrementLikeCount(commentId);
    }
} 