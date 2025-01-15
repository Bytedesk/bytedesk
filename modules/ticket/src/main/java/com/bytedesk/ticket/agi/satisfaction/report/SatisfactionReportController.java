package com.bytedesk.ticket.agi.satisfaction.report;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/tickets/satisfaction/reports")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
public class SatisfactionReportController {

    @Autowired
    private SatisfactionReportService reportService;

    @GetMapping
    public ResponseEntity<SatisfactionReportDTO> getReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        return ResponseEntity.ok(reportService.generateReport(startTime, endTime));
    }

    @GetMapping("/agents/{agentId}")
    public ResponseEntity<SatisfactionReportDTO> getAgentReport(
            @PathVariable Long agentId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        return ResponseEntity.ok(reportService.generateAgentReport(agentId, startTime, endTime));
    }

    @GetMapping("/categories/{categoryId}")
    public ResponseEntity<SatisfactionReportDTO> getCategoryReport(
            @PathVariable Long categoryId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        return ResponseEntity.ok(reportService.generateCategoryReport(categoryId, startTime, endTime));
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            @RequestParam(defaultValue = "excel") String format) {
        
        byte[] reportData = reportService.exportReport(startTime, endTime, format);
        
        String filename = String.format("satisfaction_report_%s_%s.%s",
            startTime.toLocalDate(), endTime.toLocalDate(),
            format.equals("excel") ? "xlsx" : "pdf");
            
        return ResponseEntity.ok()
            .contentType(format.equals("excel") ? 
                MediaType.APPLICATION_OCTET_STREAM : MediaType.APPLICATION_PDF)
            .header(HttpHeaders.CONTENT_DISPOSITION, 
                "attachment; filename=\"" + filename + "\"")
            .body(reportData);
    }
} 