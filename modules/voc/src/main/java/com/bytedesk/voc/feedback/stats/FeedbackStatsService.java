package com.bytedesk.voc.feedback.stats;

import java.time.LocalDateTime;
import java.util.Map;

import com.bytedesk.voc.feedback.dto.FeedbackStats;

public interface FeedbackStatsService {
    
    // 获取总体统计
    FeedbackStats getOverallStats(LocalDateTime startTime, LocalDateTime endTime);
    
    // 获取用户统计
    FeedbackStats getUserStats(Long userId, LocalDateTime startTime, LocalDateTime endTime);
    
    // 获取类型分布
    Map<String, Long> getTypeDistribution(LocalDateTime startTime, LocalDateTime endTime);
    
    // 获取状态分布
    Map<String, Long> getStatusDistribution(LocalDateTime startTime, LocalDateTime endTime);
    
    // 获取响应时间趋势
    Map<String, Double> getResponseTimeTrend(LocalDateTime startTime, LocalDateTime endTime);
    
    // 导出统计报告
    byte[] exportStatsReport(LocalDateTime startTime, LocalDateTime endTime, String format);
} 