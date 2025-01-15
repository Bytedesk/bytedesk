package com.bytedesk.ticket.agi.satisfaction.report;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Component
public class SatisfactionReportQuery {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Map<String, Object> getOverallStats(LocalDateTime startTime, LocalDateTime endTime) {
        return jdbcTemplate.queryForMap(
            "SELECT AVG(rating) as avg_rating, " +
            "COUNT(*) as total_ratings, " +
            "AVG(response_time_satisfaction) as avg_response_time, " +
            "AVG(solution_satisfaction) as avg_solution, " +
            "AVG(service_attitude_satisfaction) as avg_service_attitude " +
            "FROM bytedesk_ticket_satisfaction " +
            "WHERE created_at BETWEEN ? AND ?",
            startTime, endTime
        );
    }

    public List<Map<String, Object>> getRatingDistribution(LocalDateTime startTime, LocalDateTime endTime) {
        return jdbcTemplate.queryForList(
            "SELECT rating, COUNT(*) as count " +
            "FROM bytedesk_ticket_satisfaction " +
            "WHERE created_at BETWEEN ? AND ? " +
            "GROUP BY rating",
            startTime, endTime
        );
    }

    public List<Map<String, Object>> getAgentStats(LocalDateTime startTime, LocalDateTime endTime) {
        return jdbcTemplate.queryForList(
            "SELECT u.name as agent_name, " +
            "AVG(s.rating) as avg_rating, " +
            "COUNT(*) as total_ratings, " +
            "AVG(s.response_time_satisfaction) as avg_response_time, " +
            "AVG(s.solution_satisfaction) as avg_solution, " +
            "AVG(s.service_attitude_satisfaction) as avg_service_attitude " +
            "FROM bytedesk_ticket_satisfaction s " +
            "JOIN bytedesk_user u ON s.agent_id = u.id " +
            "WHERE s.created_at BETWEEN ? AND ? " +
            "GROUP BY s.agent_id, u.name",
            startTime, endTime
        );
    }

    public List<Map<String, Object>> getCategoryStats(LocalDateTime startTime, LocalDateTime endTime) {
        return jdbcTemplate.queryForList(
            "SELECT c.name as category_name, " +
            "AVG(s.rating) as avg_rating, " +
            "COUNT(*) as total_ratings, " +
            "AVG(s.response_time_satisfaction) as avg_response_time, " +
            "AVG(s.solution_satisfaction) as avg_solution, " +
            "AVG(s.service_attitude_satisfaction) as avg_service_attitude " +
            "FROM bytedesk_ticket_satisfaction s " +
            "JOIN bytedesk_ticket_category c ON s.category_id = c.id " +
            "WHERE s.created_at BETWEEN ? AND ? " +
            "GROUP BY s.category_id, c.name",
            startTime, endTime
        );
    }

    public List<Map<String, Object>> getDailyTrends(LocalDateTime startTime, LocalDateTime endTime) {
        return jdbcTemplate.queryForList(
            "SELECT DATE(created_at) as date, " +
            "AVG(rating) as avg_rating, " +
            "COUNT(*) as total_ratings " +
            "FROM bytedesk_ticket_satisfaction " +
            "WHERE created_at BETWEEN ? AND ? " +
            "GROUP BY DATE(created_at) " +
            "ORDER BY date",
            startTime, endTime
        );
    }
} 