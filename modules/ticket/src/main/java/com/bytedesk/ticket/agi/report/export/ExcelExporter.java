package com.bytedesk.ticket.agi.report.export;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.bytedesk.ticket.agi.report.TicketReportDTO;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.Map;

class ExcelExporter {
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;
    
    byte[] export(TicketReportDTO report) {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle normalStyle = createNormalStyle(workbook);
            
            addOverviewSheet(workbook, report, headerStyle, normalStyle);
            addTrendsSheet(workbook, report, headerStyle, normalStyle);
            addAgentPerformanceSheet(workbook, report, headerStyle, normalStyle);
            addCategoryStatsSheet(workbook, report, headerStyle, normalStyle);
            addSatisfactionSheet(workbook, report, headerStyle, normalStyle);
            
            workbook.write(baos);
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate Excel report", e);
        }
    }
    
    private void addOverviewSheet(Workbook workbook, TicketReportDTO report, 
            CellStyle headerStyle, CellStyle normalStyle) {
        Sheet sheet = workbook.createSheet("Overview");
        int rowNum = 0;
        
        // 添加标题
        Row titleRow = sheet.createRow(rowNum++);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue(report.getTitle());
        titleCell.setCellStyle(headerStyle);
        
        // 添加时间范围
        Row periodRow = sheet.createRow(rowNum++);
        Cell periodCell = periodRow.createCell(0);
        periodCell.setCellValue(String.format("Period: %s - %s",
            report.getStartTime().format(DATE_FORMATTER),
            report.getEndTime().format(DATE_FORMATTER)));
        periodCell.setCellStyle(normalStyle);
        
        rowNum++; // 空行
        
        // 添加基本统计
        addSection(sheet, rowNum++, "Basic Statistics", headerStyle);
        addKeyValueRow(sheet, rowNum++, "Total Tickets", report.getTotalTickets(), normalStyle);
        addKeyValueRow(sheet, rowNum++, "New Tickets", report.getNewTickets(), normalStyle);
        addKeyValueRow(sheet, rowNum++, "Resolved Tickets", report.getResolvedTickets(), normalStyle);
        addKeyValueRow(sheet, rowNum++, "Closed Tickets", report.getClosedTickets(), normalStyle);
        
        rowNum++; // 空行
        
        // 添加响应时间统计
        addSection(sheet, rowNum++, "Response Time Statistics", headerStyle);
        addKeyValueRow(sheet, rowNum++, "Average First Response Time", 
            String.format("%.2f minutes", report.getAvgFirstResponseTime()), normalStyle);
        addKeyValueRow(sheet, rowNum++, "Average Resolution Time", 
            String.format("%.2f minutes", report.getAvgResolutionTime()), normalStyle);
        addKeyValueRow(sheet, rowNum++, "SLA Compliance Rate", 
            String.format("%.2f%%", report.getSlaComplianceRate()), normalStyle);
        
        // 自动调整列宽
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
    }
    
    private void addTrendsSheet(Workbook workbook, TicketReportDTO report, 
            CellStyle headerStyle, CellStyle normalStyle) {
        Sheet sheet = workbook.createSheet("Trends");
        int rowNum = 0;
        
        // 添加工单趋势
        addSection(sheet, rowNum++, "Ticket Trends", headerStyle);
        Row headerRow = sheet.createRow(rowNum++);
        headerRow.createCell(0).setCellValue("Date");
        headerRow.createCell(1).setCellValue("Count");
        headerRow.getCell(0).setCellStyle(headerStyle);
        headerRow.getCell(1).setCellStyle(headerStyle);
        
        for (Map<String, Object> trend : report.getTicketTrend()) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(trend.get("date").toString());
            row.createCell(1).setCellValue(Long.valueOf(trend.get("count").toString()));
            row.getCell(0).setCellStyle(normalStyle);
            row.getCell(1).setCellStyle(normalStyle);
        }
        
        // 自动调整列宽
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
    }
    
    private void addAgentPerformanceSheet(Workbook workbook, TicketReportDTO report, 
            CellStyle headerStyle, CellStyle normalStyle) {
        Sheet sheet = workbook.createSheet("Agent Performance");
        int rowNum = 0;
        
        // 添加表头
        Row headerRow = sheet.createRow(rowNum++);
        headerRow.createCell(0).setCellValue("Agent ID");
        headerRow.createCell(1).setCellValue("Ticket Count");
        headerRow.getCell(0).setCellStyle(headerStyle);
        headerRow.getCell(1).setCellStyle(headerStyle);
        
        // 添加数据
        for (Map<String, Object> agent : report.getAgentPerformance()) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(agent.get("agentId").toString());
            row.createCell(1).setCellValue(Long.valueOf(agent.get("count").toString()));
            row.getCell(0).setCellStyle(normalStyle);
            row.getCell(1).setCellStyle(normalStyle);
        }
        
        // 自动调整列宽
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
    }
    
    private void addCategoryStatsSheet(Workbook workbook, TicketReportDTO report, 
            CellStyle headerStyle, CellStyle normalStyle) {
        Sheet sheet = workbook.createSheet("Category Stats");
        int rowNum = 0;
        
        // 添加表头
        Row headerRow = sheet.createRow(rowNum++);
        headerRow.createCell(0).setCellValue("Category");
        headerRow.createCell(1).setCellValue("Count");
        headerRow.getCell(0).setCellStyle(headerStyle);
        headerRow.getCell(1).setCellStyle(headerStyle);
        
        // 添加数据
        for (Map<String, Object> category : report.getCategoryStats()) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(category.get("categoryId").toString());
            row.createCell(1).setCellValue(Long.valueOf(category.get("count").toString()));
            row.getCell(0).setCellStyle(normalStyle);
            row.getCell(1).setCellStyle(normalStyle);
        }
        
        // 自动调整列宽
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
    }
    
    private void addSatisfactionSheet(Workbook workbook, TicketReportDTO report, 
            CellStyle headerStyle, CellStyle normalStyle) {
        Sheet sheet = workbook.createSheet("Satisfaction");
        int rowNum = 0;
        
        // 添加平均评分
        addKeyValueRow(sheet, rowNum++, "Average Rating", 
            String.format("%.2f", report.getAvgSatisfactionRating()), normalStyle);
            
        rowNum++; // 空行
        
        // 添加评分分布
        addSection(sheet, rowNum++, "Rating Distribution", headerStyle);
        Row headerRow = sheet.createRow(rowNum++);
        headerRow.createCell(0).setCellValue("Rating");
        headerRow.createCell(1).setCellValue("Count");
        headerRow.getCell(0).setCellStyle(headerStyle);
        headerRow.getCell(1).setCellStyle(headerStyle);
        
        for (Map.Entry<Integer, Long> entry : report.getSatisfactionDistribution().entrySet()) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(entry.getKey() + " Stars");
            row.createCell(1).setCellValue(entry.getValue());
            row.getCell(0).setCellStyle(normalStyle);
            row.getCell(1).setCellStyle(normalStyle);
        }
        
        // 自动调整列宽
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
    }
    
    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        return style;
    }
    
    private CellStyle createNormalStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        return style;
    }
    
    private void addSection(Sheet sheet, int rowNum, String title, CellStyle style) {
        Row row = sheet.createRow(rowNum);
        Cell cell = row.createCell(0);
        cell.setCellValue(title);
        cell.setCellStyle(style);
    }
    
    private void addKeyValueRow(Sheet sheet, int rowNum, String key, Object value, CellStyle style) {
        Row row = sheet.createRow(rowNum);
        row.createCell(0).setCellValue(key);
        row.createCell(1).setCellValue(String.valueOf(value));
        row.getCell(0).setCellStyle(style);
        row.getCell(1).setCellStyle(style);
    }
} 