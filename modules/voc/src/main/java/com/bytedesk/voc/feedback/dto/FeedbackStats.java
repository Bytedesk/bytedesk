package com.bytedesk.voc.feedback.dto;

import lombok.Data;
import java.util.Map;

@Data
public class FeedbackStats {
    // 总体统计
    private Long totalCount;
    private Long pendingCount;
    private Long processingCount;
    private Long resolvedCount;
    private Long closedCount;
    
    // 类型分布
    private Map<String, Long> typeDistribution;
    
    // 响应时间
    private Double avgResponseTime;
    private Double avgResolutionTime;
    
    // 满意度
    private Double avgSatisfactionRating;
    private Map<Integer, Long> ratingDistribution;
} 