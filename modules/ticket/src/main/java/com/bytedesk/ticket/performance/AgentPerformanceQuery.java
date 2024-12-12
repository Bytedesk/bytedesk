package com.bytedesk.ticket.performance;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Component
public class AgentPerformanceQuery {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Map<String, Object> getTicketStats(Long agentId, LocalDateTime startTime, LocalDateTime endTime) {
        return jdbcTemplate.queryForMap(
            "SELECT COUNT(*) as total_tickets, " +
            "SUM(CASE WHEN status = 'resolved' THEN 1 ELSE 0 END) as resolved_tickets, " +
            "SUM(CASE WHEN status NOT IN ('resolved', 'closed') THEN 1 ELSE 0 END) as pending_tickets " +
            "FROM bytedesk_ticket " +
            "WHERE assigned_to = ? AND created_at BETWEEN ? AND ?",
            agentId, startTime, endTime
        );
    }

    public Map<String, Object> getTimeStats(Long agentId, LocalDateTime startTime, LocalDateTime endTime) {
        return jdbcTemplate.queryForMap(
            "SELECT AVG(TIMESTAMPDIFF(MINUTE, created_at, first_response_at)) as avg_response_time, " +
            "AVG(TIMESTAMPDIFF(MINUTE, created_at, resolved_at)) as avg_resolution_time, " +
            "AVG(TIMESTAMPDIFF(MINUTE, first_response_at, resolved_at)) as avg_handling_time " +
            "FROM bytedesk_ticket " +
            "WHERE assigned_to = ? AND created_at BETWEEN ? AND ?",
            agentId, startTime, endTime
        );
    }

    public Map<String, Object> getSatisfactionStats(Long agentId, LocalDateTime startTime, LocalDateTime endTime) {
        return jdbcTemplate.queryForMap(
            "SELECT AVG(rating) as avg_rating, COUNT(*) as total_ratings " +
            "FROM bytedesk_ticket_satisfaction " +
            "WHERE agent_id = ? AND created_at BETWEEN ? AND ?",
            agentId, startTime, endTime
        );
    }

    public List<Map<String, Object>> getRatingDistribution(Long agentId, LocalDateTime startTime, LocalDateTime endTime) {
        return jdbcTemplate.queryForList(
            "SELECT rating, COUNT(*) as count " +
            "FROM bytedesk_ticket_satisfaction " +
            "WHERE agent_id = ? AND created_at BETWEEN ? AND ? " +
            "GROUP BY rating",
            agentId, startTime, endTime
        );
    }

    public Map<String, Object> getWorkloadStats(Long agentId, LocalDateTime startTime, LocalDateTime endTime) {
        return jdbcTemplate.queryForMap(
            "SELECT COUNT(*) as handled_tickets, " +
            "SUM(TIMESTAMPDIFF(HOUR, created_at, resolved_at)) as online_hours " +
            "FROM bytedesk_ticket " +
            "WHERE assigned_to = ? AND created_at BETWEEN ? AND ?",
            agentId, startTime, endTime
        );
    }
} 