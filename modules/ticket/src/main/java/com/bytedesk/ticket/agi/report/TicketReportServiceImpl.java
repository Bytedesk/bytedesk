package com.bytedesk.ticket.agi.report;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bytedesk.ticket.agi.report.export.ReportExporter;
import com.bytedesk.ticket.agi.report.mail.ReportMailService;
import com.bytedesk.ticket.agi.stats.TicketStatsDTO;
import com.bytedesk.ticket.agi.stats.TicketStatsService;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.io.IOException;

@Service
public class TicketReportServiceImpl implements TicketReportService {

    @Autowired
    private TicketStatsService statsService;

    @Autowired
    private ReportExporter reportExporter;

    @Autowired
    private ReportMailService mailService;

    @Override
    public TicketReportDTO generateReport(ReportType type, LocalDateTime startTime, LocalDateTime endTime) {
        TicketReportDTO report = new TicketReportDTO();
        report.setType(type);
        report.setStartTime(startTime);
        report.setEndTime(endTime);
        report.setTitle(generateTitle(type, startTime, endTime));
        
        // 获取统计数据
        TicketStatsDTO stats = statsService.getOverallStats(startTime, endTime);
        
        // 设置基本统计
        report.setTotalTickets(stats.getTotalTickets());
        report.setNewTickets(stats.getOpenTickets());
        report.setResolvedTickets(stats.getResolvedTickets());
        report.setClosedTickets(stats.getClosedTickets());
        
        // 设置响应时间
        report.setAvgFirstResponseTime(stats.getAvgFirstResponseTime());
        report.setAvgResolutionTime(stats.getAvgResolutionTime());
        report.setSlaComplianceRate(100 - stats.getOverdueRate());
        
        // 设置客服绩效
        report.setAgentPerformance(statsService.getAgentWorkloadRanking(startTime, endTime));
        
        // 设置分类统计
        report.setCategoryStats(statsService.getCategoryDistribution(startTime, endTime));
        
        // 设置满意度
        report.setAvgSatisfactionRating(stats.getAvgSatisfactionRating());
        report.setSatisfactionDistribution(statsService.getSatisfactionDistribution(startTime, endTime));
        
        // 设置趋势
        report.setTicketTrend(statsService.getDailyTicketTrend(startTime, endTime));
        report.setResponseTrend(statsService.getResponseTimeTrend(startTime, endTime));
        
        return report;
    }

    private String generateTitle(ReportType type, LocalDateTime startTime, LocalDateTime endTime) {
        String period = switch (type) {
            case DAILY -> startTime.toLocalDate().toString();
            case WEEKLY -> String.format("Week %d, %d", startTime.get(java.time.temporal.WeekFields.ISO.weekOfWeekBasedYear()), startTime.getYear());
            case MONTHLY -> String.format("%s %d", startTime.getMonth(), startTime.getYear());
            case QUARTERLY -> String.format("Q%d %d", (startTime.getMonthValue() - 1) / 3 + 1, startTime.getYear());
            case YEARLY -> String.valueOf(startTime.getYear());
        };
        return String.format("%s Report - %s", type.name(), period);
    }

    @Override
    public byte[] exportReport(TicketReportDTO report, String format) {
        try {
            return reportExporter.exportReport(report, format);
        } catch (IOException e) {
            throw new RuntimeException("Failed to export report", e);
        }
    }

    @Override
    public void sendReport(TicketReportDTO report, String[] recipients) {
        try {
            // 导出为PDF格式
            byte[] reportFile = exportReport(report, "pdf");
            // 发送邮件
            mailService.sendReport(report, reportFile, "pdf", recipients);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send report", e);
        }
    }

    @Override
    public void scheduleReport(ReportType type, String[] recipients) {
        // TODO: 实现报表调度功能
    }

    // 每天凌晨1点生成日报
    @Scheduled(cron = "0 0 1 * * ?")
    @Transactional
    public void generateDailyReport() {
        LocalDateTime endTime = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS);
        LocalDateTime startTime = endTime.minusDays(1);
        
        TicketReportDTO report = generateReport(ReportType.DAILY, startTime, endTime);
        sendReport(report, getDefaultRecipients());
    }

    private String[] getDefaultRecipients() {
        // TODO: 从配置或数据库获取默认收件人列表
        return new String[]{"admin@bytedesk.com"};
    }

    // 每周一凌晨2点生成周报
    @Scheduled(cron = "0 0 2 ? * MON")
    @Transactional
    public void generateWeeklyReport() {
        // LocalDateTime endTime = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS);
        // LocalDateTime startTime = endTime.minusWeeks(1);
        
        // TicketReportDTO report = generateReport(ReportType.WEEKLY, startTime, endTime);
        // TODO: 发送报表
    }

    // 每月1号凌晨3点生成月报
    @Scheduled(cron = "0 0 3 1 * ?")
    @Transactional
    public void generateMonthlyReport() {
        // LocalDateTime endTime = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS);
        // LocalDateTime startTime = endTime.minusMonths(1);
        
        // TicketReportDTO report = generateReport(ReportType.MONTHLY, startTime, endTime);
        // TODO: 发送报表
    }
} 