package com.bytedesk.ticket.agi.export.excel;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import com.bytedesk.ticket.agi.satisfaction.report.SatisfactionReportDTO;
import com.bytedesk.ticket.ticket.TicketEntity;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class ExcelExporter {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public byte[] exportTickets(List<TicketEntity> tickets) throws Exception {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Tickets");
            
            // 创建标题行
            Row headerRow = sheet.createRow(0);
            String[] headers = {"ID", "标题", "内容", "状态", "优先级", "分类", "提交人", "处理人", 
                "创建时间", "更新时间", "解决时间", "关闭时间"};
            
            CellStyle headerStyle = createHeaderStyle(workbook);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
                sheet.autoSizeColumn(i);
            }
            
            // 填充数据
            int rowNum = 1;
            CellStyle dateStyle = createDateStyle(workbook);
            for (TicketEntity ticket : tickets) {
                Row row = sheet.createRow(rowNum++);
                
                row.createCell(0).setCellValue(ticket.getId());
                row.createCell(1).setCellValue(ticket.getTitle());
                row.createCell(2).setCellValue(ticket.getContent());
                row.createCell(3).setCellValue(ticket.getStatus());
                row.createCell(4).setCellValue(ticket.getPriority());
                row.createCell(5).setCellValue(ticket.getCategoryId());
                row.createCell(6).setCellValue(ticket.getUserId());
                row.createCell(7).setCellValue(ticket.getAssignedTo());
                
                Cell createdAtCell = row.createCell(8);
                createdAtCell.setCellValue(ticket.getCreatedAt().format(DATE_FORMATTER));
                createdAtCell.setCellStyle(dateStyle);
                
                Cell updatedAtCell = row.createCell(9);
                if (ticket.getUpdatedAt() != null) {
                    updatedAtCell.setCellValue(ticket.getUpdatedAt().format(DATE_FORMATTER));
                    updatedAtCell.setCellStyle(dateStyle);
                }
                
                Cell resolvedAtCell = row.createCell(10);
                if (ticket.getResolvedAt() != null) {
                    resolvedAtCell.setCellValue(ticket.getResolvedAt().format(DATE_FORMATTER));
                    resolvedAtCell.setCellStyle(dateStyle);
                }
                
                Cell closedAtCell = row.createCell(11);
                if (ticket.getClosedAt() != null) {
                    closedAtCell.setCellValue(ticket.getClosedAt().format(DATE_FORMATTER));
                    closedAtCell.setCellStyle(dateStyle);
                }
            }
            
            // 写入文件
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    public byte[] exportSatisfactionReport(SatisfactionReportDTO report) throws Exception {
        try (Workbook workbook = new XSSFWorkbook()) {
            // 总体满意度sheet
            Sheet overallSheet = workbook.createSheet("Overall Satisfaction");
            createOverallSatisfactionSheet(overallSheet, report);
            
            // 客服满意度sheet
            // Sheet agentSheet = workbook.createSheet("Agent Satisfaction");
            // createAgentSatisfactionSheet(agentSheet, report.getAgentSatisfactions());
            
            // 分类满意度sheet
            // Sheet categorySheet = workbook.createSheet("Category Satisfaction");
            // createCategorySatisfactionSheet(categorySheet, report.getCategorySatisfactions());
            
            // 趋势数据sheet
            // Sheet trendSheet = workbook.createSheet("Daily Trends");
            // createTrendDataSheet(trendSheet, report.getTrends());
            
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        
        return style;
    }

    private CellStyle createDateStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setDataFormat(workbook.createDataFormat().getFormat("yyyy-mm-dd hh:mm:ss"));
        return style;
    }

    private void createOverallSatisfactionSheet(Sheet sheet, SatisfactionReportDTO report) {
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("指标");
        headerRow.createCell(1).setCellValue("数值");
        
        Row row1 = sheet.createRow(1);
        row1.createCell(0).setCellValue("平均评分");
        row1.createCell(1).setCellValue(report.getAverageRating());
        
        Row row2 = sheet.createRow(2);
        row2.createCell(0).setCellValue("评价总数");
        row2.createCell(1).setCellValue(report.getTotalRatings());
        
        Row row3 = sheet.createRow(3);
        row3.createCell(0).setCellValue("响应时间满意度");
        row3.createCell(1).setCellValue(report.getResponseTimeSatisfaction());
        
        Row row4 = sheet.createRow(4);
        row4.createCell(0).setCellValue("解决方案满意度");
        row4.createCell(1).setCellValue(report.getSolutionSatisfaction());
        
        Row row5 = sheet.createRow(5);
        row5.createCell(0).setCellValue("服务态度满意度");
        row5.createCell(1).setCellValue(report.getServiceAttitudeSatisfaction());
        
        // 自动调整列宽
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
    }

    // private void createAgentSatisfactionSheet(Sheet sheet, Map<String, AgentSatisfactionDTO> agentSatisfactions) {
    //     // 创建标题行
    //     Row headerRow = sheet.createRow(0);
    //     String[] headers = {"客服", "平均评分", "评价数", "响应时间满意度", "解决方案满意度", "服务态度满意度"};
    //     for (int i = 0; i < headers.length; i++) {
    //         headerRow.createCell(i).setCellValue(headers[i]);
    //         sheet.autoSizeColumn(i);
    //     }
        
    //     // 填充数据
    //     int rowNum = 1;
    //     for (Map.Entry<String, AgentSatisfactionDTO> entry : agentSatisfactions.entrySet()) {
    //         Row row = sheet.createRow(rowNum++);
    //         AgentSatisfactionDTO data = entry.getValue();
            
    //         row.createCell(0).setCellValue(data.getAgentName());
    //         row.createCell(1).setCellValue(data.getAverageRating());
    //         row.createCell(2).setCellValue(data.getTotalRatings());
    //         row.createCell(3).setCellValue(data.getResponseTimeSatisfaction());
    //         row.createCell(4).setCellValue(data.getSolutionSatisfaction());
    //         row.createCell(5).setCellValue(data.getServiceAttitudeSatisfaction());
    //     }
    // }

    // private void createCategorySatisfactionSheet(Sheet sheet, Map<String, CategorySatisfactionDTO> categorySatisfactions) {
    //     // 类似createAgentSatisfactionSheet的实现
    // }

    // private void createTrendDataSheet(Sheet sheet, Map<String, TrendDataDTO> trends) {
    //     // 类似createAgentSatisfactionSheet的实现
    // }
} 