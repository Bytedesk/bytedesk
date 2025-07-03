package com.bytedesk.voc.feedback.stats;

import java.time.ZonedDateTime;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.voc.feedback.dto.FeedbackStats;

@RestController
@RequestMapping("/api/v1/voc/stats")
@PreAuthorize("hasRole('VOC_ADMIN')")
public class FeedbackStatsController {

    @Autowired
    private FeedbackStatsService statsService;

    @GetMapping("/overall")
    public ResponseEntity<FeedbackStats> getOverallStats(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime endTime) {
        return ResponseEntity.ok(statsService.getOverallStats(startTime, endTime));
    }

    @GetMapping("/type-distribution")
    public ResponseEntity<Map<String, Long>> getTypeDistribution(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime endTime) {
        return ResponseEntity.ok(statsService.getTypeDistribution(startTime, endTime));
    }

    @GetMapping("/status-distribution")
    public ResponseEntity<Map<String, Long>> getStatusDistribution(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime endTime) {
        return ResponseEntity.ok(statsService.getStatusDistribution(startTime, endTime));
    }

    @GetMapping("/response-time-trend")
    public ResponseEntity<Map<String, Double>> getResponseTimeTrend(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime endTime) {
        return ResponseEntity.ok(statsService.getResponseTimeTrend(startTime, endTime));
    }
} 