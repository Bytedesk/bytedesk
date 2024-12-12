package com.bytedesk.voc.feedback.dto;

import lombok.Data;

@Data
public class FeedbackSearchCriteria {
    private String keyword;
    private String type;
    private String status;
    private Long userId;
    private Long assignedTo;
    private String startDate;
    private String endDate;
} 