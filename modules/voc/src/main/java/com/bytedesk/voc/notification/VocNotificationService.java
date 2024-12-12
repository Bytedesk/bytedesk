package com.bytedesk.voc.notification;

public interface VocNotificationService {
    
    void notifyNewFeedback(Long feedbackId);
    
    void notifyFeedbackAssigned(Long feedbackId, Long assignedTo);
    
    void notifyStatusChanged(Long feedbackId, String status);
    
    void notifyNewReply(Long feedbackId, Long replyId);
    
    void notifyInternalReply(Long feedbackId, Long replyId);
} 