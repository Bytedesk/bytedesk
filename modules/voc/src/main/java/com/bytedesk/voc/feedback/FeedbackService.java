package com.bytedesk.voc.feedback;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FeedbackService {
    
    FeedbackEntity createFeedback(String content, Long userId, String type);
    
    FeedbackEntity updateFeedback(Long feedbackId, String content);
    
    void deleteFeedback(Long feedbackId);
    
    FeedbackEntity getFeedback(Long feedbackId);
    
    Page<FeedbackEntity> getFeedbacks(Pageable pageable);
    
    Page<FeedbackEntity> getFeedbacksByUser(Long userId, Pageable pageable);
    
    Page<FeedbackEntity> getFeedbacksByType(String type, Pageable pageable);
    
    Page<FeedbackEntity> getFeedbacksByStatus(String status, Pageable pageable);
    
    void assignFeedback(Long feedbackId, Long assignedTo);
    
    void updateStatus(Long feedbackId, String status);
    
    void incrementLikeCount(Long feedbackId);
    
    void incrementReplyCount(Long feedbackId);
    
    Page<FeedbackEntity> search(String keyword, String type, String status, Pageable pageable);
} 