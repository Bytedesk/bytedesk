package com.bytedesk.ticket.agi.satisfaction.report;

import java.time.LocalDateTime;

public interface SatisfactionReportService {
    
    /**
     * 生成满意度报表
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 满意度报表数据
     */
    SatisfactionReportDTO generateReport(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 生成客服满意度报表
     * @param agentId 客服ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 满意度报表数据
     */
    SatisfactionReportDTO generateAgentReport(Long agentId, LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 生成分类满意度报表
     * @param categoryId 分类ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 满意度报表数据
     */
    SatisfactionReportDTO generateCategoryReport(Long categoryId, LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 导出满意度报表
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param format 导出格式(excel/pdf)
     * @return 报表文件字节数组
     */
    byte[] exportReport(LocalDateTime startTime, LocalDateTime endTime, String format);
} 