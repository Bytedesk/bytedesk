package com.bytedesk.ticket.agi.stats;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/tickets/stats")
public class TicketStatsController {

    @Autowired
    private TicketStatsService statsService;

    @GetMapping("/overall")
    public ResponseEntity<TicketStatsDTO> getOverallStats(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        return ResponseEntity.ok(statsService.getOverallStats(startTime, endTime));
    }

    @GetMapping("/agents/{agentId}")
    public ResponseEntity<TicketStatsDTO> getAgentStats(
            @PathVariable Long agentId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        return ResponseEntity.ok(statsService.getAgentStats(agentId, startTime, endTime));
    }

    @GetMapping("/categories/{categoryId}")
    public ResponseEntity<TicketStatsDTO> getCategoryStats(
            @PathVariable Long categoryId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        return ResponseEntity.ok(statsService.getCategoryStats(categoryId, startTime, endTime));
    }

    @GetMapping("/trends/daily")
    public ResponseEntity<List<Map<String, Object>>> getDailyTicketTrend(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        return ResponseEntity.ok(statsService.getDailyTicketTrend(startTime, endTime));
    }

    @GetMapping("/distribution/category")
    public ResponseEntity<List<Map<String, Object>>> getCategoryDistribution(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        return ResponseEntity.ok(statsService.getCategoryDistribution(startTime, endTime));
    }

    @GetMapping("/ranking/workload")
    public ResponseEntity<List<Map<String, Object>>> getAgentWorkloadRanking(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        return ResponseEntity.ok(statsService.getAgentWorkloadRanking(startTime, endTime));
    }

    @GetMapping("/trends/response-time")
    public ResponseEntity<List<Map<String, Object>>> getResponseTimeTrend(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        return ResponseEntity.ok(statsService.getResponseTimeTrend(startTime, endTime));
    }

    @GetMapping("/distribution/satisfaction")
    public ResponseEntity<Map<Integer, Long>> getSatisfactionDistribution(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        return ResponseEntity.ok(statsService.getSatisfactionDistribution(startTime, endTime));
    }
} 