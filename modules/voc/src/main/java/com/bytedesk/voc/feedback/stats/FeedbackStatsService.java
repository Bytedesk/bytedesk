package com.bytedesk.voc.feedback.stats;

import java.time.ZonedDateTime;
import java.util.Map;

import com.bytedesk.voc.feedback.dto.FeedbackStats;

public interface FeedbackStatsService {
    
    // 获取总体统计
    FeedbackStats getOverallStats(ZonedDateTime startTime, ZonedDateTime endTime);
    
    // 获取用户统计
    FeedbackStats getUserStats(Long userId, ZonedDateTime startTime, ZonedDateTime endTime);
    
    // 获取类型分布
    Map<String, Long> getTypeDistribution(ZonedDateTime startTime, ZonedDateTime endTime);
    
    // 获取状态分布
    Map<String, Long> getStatusDistribution(ZonedDateTime startTime, ZonedDateTime endTime);
    
    // 获取响应时间趋势
    Map<String, Double> getResponseTimeTrend(ZonedDateTime startTime, ZonedDateTime endTime);
    
    // 导出统计报告
    byte[] exportStatsReport(ZonedDateTime startTime, ZonedDateTime endTime, String format);
} 