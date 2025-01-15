package com.bytedesk.ticket.agi.performance;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.bytedesk.ticket.agi.performance.dto.AgentPerformanceDTO;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/tickets/performance")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
public class AgentPerformanceController {

    @Autowired
    private AgentPerformanceService performanceService;

    @GetMapping("/agents/{agentId}")
    public ResponseEntity<AgentPerformanceDTO> getAgentPerformance(
            @PathVariable Long agentId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        return ResponseEntity.ok(performanceService.getAgentPerformance(agentId, startTime, endTime));
    }

    @GetMapping("/teams/{teamId}")
    public ResponseEntity<List<AgentPerformanceDTO>> getTeamPerformance(
            @PathVariable Long teamId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        return ResponseEntity.ok(performanceService.getTeamPerformance(teamId, startTime, endTime));
    }

    @GetMapping("/ranking")
    public ResponseEntity<List<AgentPerformanceDTO>> getPerformanceRanking(
            @RequestParam String metric,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(performanceService.getPerformanceRanking(metric, startTime, endTime, limit));
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportPerformanceReport(
            @RequestParam List<Long> agentIds,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            @RequestParam(defaultValue = "excel") String format) {
        
        byte[] reportData = performanceService.exportPerformanceReport(agentIds, startTime, endTime, format);
        
        String filename = String.format("performance_report_%s_%s.%s",
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