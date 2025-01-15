package com.bytedesk.ticket.agi.report.export;

import com.bytedesk.ticket.agi.report.TicketReportDTO;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.Map;

class PdfExporter {
    
    private static final Font TITLE_FONT = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
    private static final Font SECTION_FONT = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
    private static final Font NORMAL_FONT = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);
    
    byte[] export(TicketReportDTO report) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, baos);
            document.open();
            
            addReportHeader(document, report);
            addBasicStats(document, report);
            addResponseTimeStats(document, report);
            addAgentPerformance(document, report);
            addCategoryStats(document, report);
            addSatisfactionStats(document, report);
            
            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate PDF report", e);
        }
    }
    
    private void addReportHeader(Document document, TicketReportDTO report) throws DocumentException {
        Paragraph title = new Paragraph(report.getTitle(), TITLE_FONT);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        
        Paragraph period = new Paragraph(String.format("Period: %s - %s",
            report.getStartTime().format(DateTimeFormatter.ISO_LOCAL_DATE),
            report.getEndTime().format(DateTimeFormatter.ISO_LOCAL_DATE)), NORMAL_FONT);
        period.setAlignment(Element.ALIGN_CENTER);
        document.add(period);
        
        document.add(Chunk.NEWLINE);
    }
    
    private void addBasicStats(Document document, TicketReportDTO report) throws DocumentException {
        addSection(document, "Basic Statistics");
        
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        
        addTableRow(table, "Total Tickets", report.getTotalTickets());
        addTableRow(table, "New Tickets", report.getNewTickets());
        addTableRow(table, "Resolved Tickets", report.getResolvedTickets());
        addTableRow(table, "Closed Tickets", report.getClosedTickets());
        
        document.add(table);
        document.add(Chunk.NEWLINE);
    }
    
    private void addResponseTimeStats(Document document, TicketReportDTO report) throws DocumentException {
        addSection(document, "Response Time Statistics");
        
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        
        addTableRow(table, "Average First Response Time", 
            String.format("%.2f minutes", report.getAvgFirstResponseTime()));
        addTableRow(table, "Average Resolution Time", 
            String.format("%.2f minutes", report.getAvgResolutionTime()));
        addTableRow(table, "SLA Compliance Rate", 
            String.format("%.2f%%", report.getSlaComplianceRate()));
        
        document.add(table);
        document.add(Chunk.NEWLINE);
    }
    
    private void addAgentPerformance(Document document, TicketReportDTO report) throws DocumentException {
        addSection(document, "Agent Performance");
        
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        
        addTableHeader(table, "Agent ID", "Ticket Count");
        for (Map<String, Object> agent : report.getAgentPerformance()) {
            addTableRow(table, 
                String.valueOf(agent.get("agentId")),
                String.valueOf(agent.get("count")));
        }
        
        document.add(table);
        document.add(Chunk.NEWLINE);
    }
    
    private void addCategoryStats(Document document, TicketReportDTO report) throws DocumentException {
        addSection(document, "Category Statistics");
        
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        
        addTableHeader(table, "Category", "Count");
        for (Map<String, Object> category : report.getCategoryStats()) {
            addTableRow(table, 
                String.valueOf(category.get("categoryId")),
                String.valueOf(category.get("count")));
        }
        
        document.add(table);
        document.add(Chunk.NEWLINE);
    }
    
    private void addSatisfactionStats(Document document, TicketReportDTO report) throws DocumentException {
        addSection(document, "Satisfaction Statistics");
        
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        
        addTableRow(table, "Average Rating", 
            String.format("%.2f", report.getAvgSatisfactionRating()));
            
        for (Map.Entry<Integer, Long> entry : report.getSatisfactionDistribution().entrySet()) {
            addTableRow(table, 
                String.format("%d Stars", entry.getKey()),
                String.valueOf(entry.getValue()));
        }
        
        document.add(table);
    }
    
    private void addSection(Document document, String title) throws DocumentException {
        Paragraph section = new Paragraph(title, SECTION_FONT);
        section.setSpacingBefore(10);
        section.setSpacingAfter(10);
        document.add(section);
    }
    
    private void addTableHeader(PdfPTable table, String... headers) {
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, SECTION_FONT));
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            cell.setPadding(5);
            table.addCell(cell);
        }
    }
    
    private void addTableRow(PdfPTable table, Object... values) {
        for (Object value : values) {
            PdfPCell cell = new PdfPCell(new Phrase(String.valueOf(value), NORMAL_FONT));
            cell.setPadding(5);
            table.addCell(cell);
        }
    }
} 