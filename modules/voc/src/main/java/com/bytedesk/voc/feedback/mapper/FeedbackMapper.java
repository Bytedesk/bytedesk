package com.bytedesk.voc.feedback.mapper;

import com.bytedesk.voc.feedback.FeedbackEntity;
import com.bytedesk.voc.feedback.dto.FeedbackResponse;
import org.springframework.stereotype.Component;

@Component
public class FeedbackMapper {
    
    public FeedbackResponse toResponse(FeedbackEntity entity) {
        FeedbackResponse response = new FeedbackResponse();
        response.setId(entity.getId());
        response.setContent(entity.getContent());
        response.setType(entity.getType());
        response.setStatus(entity.getStatus());
        response.setUserId(entity.getUserId());
        response.setAssignedTo(entity.getAssignedTo());
        response.setReplyCount(entity.getReplyCount());
        response.setLikeCount(entity.getLikeCount());
        response.setCreatedAt(entity.getCreatedAt());
        response.setUpdatedAt(entity.getUpdatedAt());
        return response;
    }
} 