package com.bytedesk.forum.post;

import lombok.Data;

@Data
public class PostSearchCriteria {
    private String keyword;
    private Long categoryId;
    private Long userId;
    private String status;
    private String startDate;
    private String endDate;
} 