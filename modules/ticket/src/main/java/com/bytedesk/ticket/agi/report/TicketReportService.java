package com.bytedesk.ticket.agi.report;

import java.time.LocalDateTime;

public interface TicketReportService {
    
    // 生成报表
    TicketReportDTO generateReport(ReportType type, LocalDateTime startTime, LocalDateTime endTime);
    
    // 导出报表
    byte[] exportReport(TicketReportDTO report, String format); // format: pdf, excel, csv
    
    // 发送报表
    void sendReport(TicketReportDTO report, String[] recipients);
    
    // 定时生成报表
    void scheduleReport(ReportType type, String[] recipients);
} 