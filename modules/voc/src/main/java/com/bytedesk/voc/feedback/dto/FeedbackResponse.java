package com.bytedesk.voc.feedback.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class FeedbackResponse {
    private Long id;
    private String content;
    private String type;
    private String status;
    private Long userId;
    private Long assignedTo;
    private Integer replyCount;
    private Integer likeCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 