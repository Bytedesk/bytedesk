package com.bytedesk.ticket.agi.satisfaction.report;

import lombok.Data;
import java.util.Map;

@Data
public class SatisfactionReportDTO {
    // 总体满意度
    private Double averageRating;
    private Integer totalRatings;
    private Map<Integer, Long> ratingDistribution;  // 评分分布
    
    // 分维度满意度
    private Double responseTimeSatisfaction;    // 响应时间满意度
    private Double solutionSatisfaction;        // 解决方案满意度
    private Double serviceAttitudeSatisfaction; // 服务态度满意度
    
    // 客服满意度
    private Map<String, AgentSatisfactionDTO> agentSatisfactions;  // key: 客服名称
    
    // 分类满意度
    private Map<String, CategorySatisfactionDTO> categorySatisfactions;  // key: 分类名称
    
    // 趋势数据
    private Map<String, TrendDataDTO> trends;  // key: 日期(yyyy-MM-dd)
}

@Data
class AgentSatisfactionDTO {
    private String agentName;
    private Double averageRating;
    private Integer totalRatings;
    private Double responseTimeSatisfaction;
    private Double solutionSatisfaction;
    private Double serviceAttitudeSatisfaction;
}

@Data
class CategorySatisfactionDTO {
    private String categoryName;
    private Double averageRating;
    private Integer totalRatings;
    private Double responseTimeSatisfaction;
    private Double solutionSatisfaction;
    private Double serviceAttitudeSatisfaction;
}

@Data
class TrendDataDTO {
    private Double averageRating;
    private Integer totalRatings;
    private Map<Integer, Long> ratingDistribution;
} 