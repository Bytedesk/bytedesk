package com.bytedesk.voc.feedback.stats;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.bytedesk.voc.feedback.dto.FeedbackStats;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class FeedbackStatsServiceImpl implements FeedbackStatsService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public FeedbackStats getOverallStats(LocalDateTime startTime, LocalDateTime endTime) {
        FeedbackStats stats = new FeedbackStats();
        
        // 获取总数
        stats.setTotalCount(getTotalCount(startTime, endTime));
        
        // 获取各状态数量
        stats.setPendingCount(getStatusCount("pending", startTime, endTime));
        stats.setProcessingCount(getStatusCount("processing", startTime, endTime));
        stats.setResolvedCount(getStatusCount("resolved", startTime, endTime));
        stats.setClosedCount(getStatusCount("closed", startTime, endTime));
        
        // 获取类型分布
        stats.setTypeDistribution(getTypeDistribution(startTime, endTime));
        
        // 获取响应时间
        stats.setAvgResponseTime(getAverageResponseTime(startTime, endTime));
        stats.setAvgResolutionTime(getAverageResolutionTime(startTime, endTime));
        
        return stats;
    }

    @Override
    public FeedbackStats getUserStats(Long userId, LocalDateTime startTime, LocalDateTime endTime) {
        FeedbackStats stats = new FeedbackStats();
        
        // 获取用户反馈总数
        stats.setTotalCount(getUserTotalCount(userId, startTime, endTime));
        
        // 获取用户各状态数量
        stats.setPendingCount(getUserStatusCount(userId, "pending", startTime, endTime));
        stats.setProcessingCount(getUserStatusCount(userId, "processing", startTime, endTime));
        stats.setResolvedCount(getUserStatusCount(userId, "resolved", startTime, endTime));
        stats.setClosedCount(getUserStatusCount(userId, "closed", startTime, endTime));
        
        // 获取用户类型分布
        stats.setTypeDistribution(getUserTypeDistribution(userId, startTime, endTime));
        
        return stats;
    }

    @Override
    public Map<String, Long> getTypeDistribution(LocalDateTime startTime, LocalDateTime endTime) {
        String sql = """
            SELECT type, COUNT(*) as count 
            FROM bytedesk_voc_feedback 
            WHERE created_at BETWEEN ? AND ?
            GROUP BY type
            """;
            
        return jdbcTemplate.query(sql, 
            (rs, rowNum) -> Map.entry(rs.getString("type"), rs.getLong("count")),
            startTime, endTime)
            .stream()
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public Map<String, Long> getStatusDistribution(LocalDateTime startTime, LocalDateTime endTime) {
        String sql = """
            SELECT status, COUNT(*) as count 
            FROM bytedesk_voc_feedback 
            WHERE created_at BETWEEN ? AND ?
            GROUP BY status
            """;
            
        return jdbcTemplate.query(sql,
            (rs, rowNum) -> Map.entry(rs.getString("status"), rs.getLong("count")),
            startTime, endTime)
            .stream()
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public Map<String, Double> getResponseTimeTrend(LocalDateTime startTime, LocalDateTime endTime) {
        String sql = """
            SELECT 
                DATE(created_at) as date,
                AVG(TIMESTAMPDIFF(SECOND, created_at, updated_at)) as avg_response_time
            FROM bytedesk_voc_feedback
            WHERE created_at BETWEEN ? AND ?
                AND status IN ('resolved', 'closed')
            GROUP BY DATE(created_at)
            ORDER BY date
            """;
            
        return jdbcTemplate.query(sql,
            (rs, rowNum) -> Map.entry(
                rs.getString("date"),
                rs.getDouble("avg_response_time")),
            startTime, endTime)
            .stream()
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public byte[] exportStatsReport(LocalDateTime startTime, LocalDateTime endTime, String format) {
        // 获取统计数据
        FeedbackStats stats = getOverallStats(startTime, endTime);
        Map<String, Long> typeDistribution = getTypeDistribution(startTime, endTime);
        Map<String, Long> statusDistribution = getStatusDistribution(startTime, endTime);
        Map<String, Double> responseTrend = getResponseTimeTrend(startTime, endTime);
        
        // 根据格式导出
        if ("excel".equalsIgnoreCase(format)) {
            return exportExcel(stats, typeDistribution, statusDistribution, responseTrend);
        } else if ("pdf".equalsIgnoreCase(format)) {
            return exportPdf(stats, typeDistribution, statusDistribution, responseTrend);
        } else {
            throw new IllegalArgumentException("Unsupported format: " + format);
        }
    }

    private byte[] exportExcel(FeedbackStats stats, Map<String, Long> typeDistribution,
            Map<String, Long> statusDistribution, Map<String, Double> responseTrend) {
        // TODO: 实现Excel导出
        return new byte[0];
    }

    private byte[] exportPdf(FeedbackStats stats, Map<String, Long> typeDistribution,
            Map<String, Long> statusDistribution, Map<String, Double> responseTrend) {
        // TODO: 实现PDF导出
        return new byte[0];
    }

    private Long getTotalCount(LocalDateTime startTime, LocalDateTime endTime) {
        return jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM bytedesk_voc_feedback WHERE created_at BETWEEN ? AND ?",
            Long.class,
            startTime,
            endTime
        );
    }

    private Long getStatusCount(String status, LocalDateTime startTime, LocalDateTime endTime) {
        return jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM bytedesk_voc_feedback WHERE status = ? AND created_at BETWEEN ? AND ?",
            Long.class,
            status,
            startTime,
            endTime
        );
    }

    private Double getAverageResponseTime(LocalDateTime startTime, LocalDateTime endTime) {
        // TODO: 实现响应时间计算
        return 0.0;
    }

    private Double getAverageResolutionTime(LocalDateTime startTime, LocalDateTime endTime) {
        // TODO: 实现解决时间计算
        return 0.0;
    }

    private Long getUserTotalCount(Long userId, LocalDateTime startTime, LocalDateTime endTime) {
        return jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM bytedesk_voc_feedback WHERE user_id = ? AND created_at BETWEEN ? AND ?",
            Long.class,
            userId, startTime, endTime
        );
    }

    private Long getUserStatusCount(Long userId, String status, LocalDateTime startTime, LocalDateTime endTime) {
        return jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM bytedesk_voc_feedback WHERE user_id = ? AND status = ? AND created_at BETWEEN ? AND ?",
            Long.class,
            userId, status, startTime, endTime
        );
    }

    private Map<String, Long> getUserTypeDistribution(Long userId, LocalDateTime startTime, LocalDateTime endTime) {
        String sql = """
            SELECT type, COUNT(*) as count 
            FROM bytedesk_voc_feedback 
            WHERE user_id = ? AND created_at BETWEEN ? AND ?
            GROUP BY type
            """;
            
        return jdbcTemplate.query(sql,
            (rs, rowNum) -> Map.entry(rs.getString("type"), rs.getLong("count")),
            userId, startTime, endTime)
            .stream()
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    // 实现其他方法...
} 