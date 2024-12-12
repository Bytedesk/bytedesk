package com.bytedesk.voc.reply;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReplyService {
    
    ReplyEntity createReply(String content, Long feedbackId, Long userId, Long parentId, Boolean internal);
    
    ReplyEntity updateReply(Long replyId, String content);
    
    void deleteReply(Long replyId);
    
    ReplyEntity getReply(Long replyId);
    
    Page<ReplyEntity> getRepliesByFeedback(Long feedbackId, Pageable pageable);
    
    Page<ReplyEntity> getRepliesByUser(Long userId, Pageable pageable);
    
    void incrementLikeCount(Long replyId);
} 