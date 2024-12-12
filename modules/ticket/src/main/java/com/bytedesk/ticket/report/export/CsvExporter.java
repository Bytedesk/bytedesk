package com.bytedesk.ticket.report.export;

import com.bytedesk.ticket.report.TicketReportDTO;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.io.IOException;
class CsvExporter {
    
    byte[] export(TicketReportDTO report) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             OutputStreamWriter writer = new OutputStreamWriter(baos, StandardCharsets.UTF_8);
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT)) {
            
            // 添加标题
            csvPrinter.printRecord("Report Title", report.getTitle());
            csvPrinter.printRecord("Period", 
                report.getStartTime().format(DateTimeFormatter.ISO_LOCAL_DATE),
                report.getEndTime().format(DateTimeFormatter.ISO_LOCAL_DATE));
            csvPrinter.println();
            
            // 添加基本统计
            csvPrinter.printRecord("Basic Statistics");
            csvPrinter.printRecord("Total Tickets", report.getTotalTickets());
            csvPrinter.printRecord("New Tickets", report.getNewTickets());
            csvPrinter.printRecord("Resolved Tickets", report.getResolvedTickets());
            csvPrinter.printRecord("Closed Tickets", report.getClosedTickets());
            csvPrinter.println();
            
            // 添加客服绩效
            csvPrinter.printRecord("Agent Performance");
            csvPrinter.printRecord("Agent ID", "Ticket Count");
            report.getAgentPerformance().forEach(agent -> {
                try {
                    csvPrinter.printRecord(
                        agent.get("agentId"),
                        agent.get("count")
                    );
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            
            // ... 添加其他数据
            
            writer.flush();
            return baos.toByteArray();
        }
    }
} 