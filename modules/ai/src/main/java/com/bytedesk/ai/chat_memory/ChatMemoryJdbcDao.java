/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-08-11
 * @Description: Spring AI ChatMemory 只读查询 DAO（原生 JdbcTemplate）。
 *   与官方 JdbcChatMemoryRepository 一致，直接原生 SQL 查询 SPRING_AI_CHAT_MEMORY 表，
 *   避免为该表建立 JPA 实体（该表无单一主键，timestamp 为保留字，不适合 JPA 映射）。
 *
 *   参考：spring-ai/memory-repositories/spring-ai-model-chat-memory-repository-jdbc
 */
package com.bytedesk.ai.chat_memory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * ChatMemory 只读查询 DAO。
 *
 * <p>复用主库 DataSource（与 Spring AI JdbcChatMemoryRepository 同一数据源）。
 * 支持分页 + 按 conversationId/type/content 过滤。</p>
 */
@Slf4j
@Repository
public class ChatMemoryJdbcDao {

    private final JdbcTemplate jdbcTemplate;

    public ChatMemoryJdbcDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    private static final RowMapper<ChatMemoryRecord> ROW_MAPPER = new ChatMemoryRowMapper();

    /**
     * 分页查询 ChatMemory（支持过滤）。
     *
     * @param request 过滤 + 分页参数
     * @return 当前页记录列表
     */
    public List<ChatMemoryRecord> findPage(ChatMemoryRequest request) {
        StringBuilder sql = new StringBuilder(
                "SELECT conversation_id, content, type, timestamp, sequence_id FROM SPRING_AI_CHAT_MEMORY WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (StringUtils.hasText(request.getConversationId())) {
            sql.append(" AND conversation_id LIKE ?");
            params.add("%" + request.getConversationId() + "%");
        }
        if (StringUtils.hasText(request.getType())) {
            sql.append(" AND type = ?");
            params.add(request.getType());
        }
        if (StringUtils.hasText(request.getContent())) {
            sql.append(" AND content LIKE ?");
            params.add("%" + request.getContent() + "%");
        }

        // 排序：默认按 sequence_id 降序（最新消息在前）
        String sortBy = StringUtils.hasText(request.getSortBy()) ? request.getSortBy() : "sequence_id";
        String direction = "ascend".equalsIgnoreCase(request.getSortDirection()) ? "ASC" : "DESC";
        // timestamp 是 PostgreSQL 保留字，需加双引号
        String sortColumn = "timestamp".equalsIgnoreCase(sortBy) ? "\"timestamp\"" : sortBy;
        sql.append(" ORDER BY ").append(sortColumn).append(" ").append(direction);

        // 分页（LIMIT/OFFSET 在 MySQL 和 PostgreSQL 中均支持）
        int pageNumber = request.getPageNumber() != null ? request.getPageNumber() : 0;
        int pageSize = request.getPageSize() != null ? request.getPageSize() : 10;
        sql.append(" LIMIT ? OFFSET ?");
        params.add(pageSize);
        params.add(pageNumber * pageSize);

        return jdbcTemplate.query(sql.toString(), ROW_MAPPER, params.toArray());
    }

    /**
     * 统计满足过滤条件的记录总数。
     */
    public long count(ChatMemoryRequest request) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM SPRING_AI_CHAT_MEMORY WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (StringUtils.hasText(request.getConversationId())) {
            sql.append(" AND conversation_id LIKE ?");
            params.add("%" + request.getConversationId() + "%");
        }
        if (StringUtils.hasText(request.getType())) {
            sql.append(" AND type = ?");
            params.add(request.getType());
        }
        if (StringUtils.hasText(request.getContent())) {
            sql.append(" AND content LIKE ?");
            params.add("%" + request.getContent() + "%");
        }

        Long count = jdbcTemplate.queryForObject(sql.toString(), Long.class, params.toArray());
        return count != null ? count : 0L;
    }

    /**
     * 删除指定会话的所有记忆记录。
     */
    public void deleteByConversationId(String conversationId) {
        jdbcTemplate.update("DELETE FROM SPRING_AI_CHAT_MEMORY WHERE conversation_id = ?", conversationId);
    }

    /**
     * RowMapper：将 ResultSet 映射为 ChatMemoryRecord。
     */
    private static class ChatMemoryRowMapper implements RowMapper<ChatMemoryRecord> {
        @Override
        public ChatMemoryRecord mapRow(ResultSet rs, int rowNum) throws SQLException {
            Timestamp ts = rs.getTimestamp("timestamp");
            LocalDateTime timestamp = ts != null ? ts.toLocalDateTime() : null;
            return ChatMemoryRecord.builder()
                    .conversationId(rs.getString("conversation_id"))
                    .content(rs.getString("content"))
                    .type(rs.getString("type"))
                    .timestamp(timestamp)
                    .sequenceId(rs.getLong("sequence_id"))
                    .build();
        }
    }
}
