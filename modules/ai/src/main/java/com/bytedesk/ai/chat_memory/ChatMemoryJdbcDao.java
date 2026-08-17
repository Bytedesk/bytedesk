/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-08-11
 * @Description: ChatMemory 只读查询 DAO（原生 JdbcTemplate）。
 *   查询 SPRING_AI_CHAT_MEMORY 表。
 *   排序：sortBy 采用固定白名单映射，避免 SQL 注入。
 */
package com.bytedesk.ai.chat_memory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;

import com.bytedesk.core.utils.BdDateUtils;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * ChatMemory 只读查询 DAO。
 *
 * <p>复用主库 DataSource。支持分页 + 按 conversationId/type/content 过滤。</p>
 *
 * <p>排序字段采用固定白名单映射，禁止直接拼接前端传入值，避免 SQL 注入。</p>
 */
@Slf4j
@Repository
public class ChatMemoryJdbcDao {

    /**
     * Spring AI 官方 JDBC ChatMemory 表名。
     */
    public static final String TABLE_NAME = "SPRING_AI_CHAT_MEMORY";

    /**
     * 排序字段白名单：前端 sortBy 值 -> 数据库列名。
     * 未知字段回退到 sequence_id，避免拼接任意字符串造成 SQL 注入。
     */
    private static final Map<String, String> SORT_COLUMNS = Map.of(
            "conversationId", "conversation_id",
            "type", "type",
            "timestamp", "timestamp",
            "sequenceId", "sequence_id");

    private final JdbcTemplate jdbcTemplate;

    /**
     * timestamp 列在 SQL 中的转义形式，通过 DataSource 元数据自动探测：
     * PostgreSQL/Kingbase 用双引号，MySQL/MariaDB 用反引号。
     * 与 BytedeskChatMemoryRepository.resolveTimestampColumn() 保持一致。
     */
    private final String timestampColumn;

    public ChatMemoryJdbcDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.timestampColumn = resolveTimestampColumn(dataSource);
    }

    /**
     * 通过 DataSource 元数据探测数据库类型，决定 timestamp 列的引号形式。
     */
    private static String resolveTimestampColumn(DataSource dataSource) {
        String productName = "";
        if (dataSource != null) {
            try {
                productName = org.springframework.jdbc.support.JdbcUtils
                    .extractDatabaseMetaData(dataSource, java.sql.DatabaseMetaData::getDatabaseProductName);
            } catch (Exception ex) {
                log.warn("Failed to detect database product name, defaulting to double-quote for timestamp column", ex);
            }
        }
        if (productName == null || productName.trim().isEmpty()) {
            return "\"timestamp\"";
        }
        // PostgreSQL / Kingbase / Oracle 等标准 SQL 系用双引号；MySQL / MariaDB 用反引号
        if (productName.contains("PostgreSQL") || productName.contains("Postgres")
                || productName.contains("Kingbase") || productName.contains("Oracle")) {
            return "\"timestamp\"";
        }
        return "`timestamp`";
    }

    /**
     * 分页查询 ChatMemory（支持过滤）。
     *
     * @param request 过滤 + 分页参数
     * @return 当前页记录列表
     */
    public List<ChatMemoryRecord> findPage(ChatMemoryRequest request) {
        StringBuilder sql = new StringBuilder("SELECT conversation_id, content, type, ")
            .append(timestampColumn)
            .append(", sequence_id FROM ")
            .append(TABLE_NAME)
            .append(" WHERE 1=1");
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

        // 排序：白名单映射，未知字段回退到 sequence_id（默认按 sequence_id 降序，最新消息在前）
        String sortByKey = StringUtils.hasText(request.getSortBy()) ? request.getSortBy() : "sequenceId";
        String sortColumn = SORT_COLUMNS.getOrDefault(sortByKey, "sequence_id");
        // timestamp 需用引号包裹
        if ("timestamp".equals(sortColumn)) {
            sortColumn = timestampColumn;
        }
        String direction = "ascend".equalsIgnoreCase(request.getSortDirection()) ? "ASC" : "DESC";
        sql.append(" ORDER BY ").append(sortColumn).append(" ").append(direction);

        // 分页（LIMIT/OFFSET 在 MySQL 和 PostgreSQL 中均支持）
        int pageNumber = request.getPageNumber() != null ? request.getPageNumber() : 0;
        int pageSize = request.getPageSize() != null ? request.getPageSize() : 10;
        sql.append(" LIMIT ? OFFSET ?");
        params.add(pageSize);
        params.add(pageNumber * pageSize);

        return jdbcTemplate.query(sql.toString(), new ChatMemoryRowMapper(), params.toArray());
    }

    /**
     * 统计满足过滤条件的记录总数。
     */
    public long count(ChatMemoryRequest request) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM ").append(TABLE_NAME).append(" WHERE 1=1");
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
        if (!StringUtils.hasText(conversationId)) {
            throw new IllegalArgumentException("conversationId is required");
        }
        jdbcTemplate.update("DELETE FROM " + TABLE_NAME + " WHERE conversation_id = ?", conversationId);
    }

    /**
     * RowMapper：将 ResultSet 映射为 ChatMemoryRecord。
     * 注意：非 static，因为依赖实例的 timestampColumn 做方言适配。
     */
    private class ChatMemoryRowMapper implements RowMapper<ChatMemoryRecord> {
        @Override
        public ChatMemoryRecord mapRow(ResultSet rs, int rowNum) throws SQLException {
            // 注意：ResultSet 按 column label 查找，必须用不带引号的真实列名。
            // timestampColumn 含 SQL 引号（反引号/双引号）仅用于拼接 SELECT/ORDER BY 文本，
            // 不能传给 rs.getTimestamp()，否则 MySQL 会查找名为 "`timestamp`"（含反引号）的列。
            Timestamp ts = rs.getTimestamp("timestamp");
            // 统一转换为显示时区（默认北京时间 Asia/Shanghai），避免随 JVM/数据库时区漂移，
            // 序列化时再由 @JsonFormat 格式化为 yyyy-MM-dd HH:mm:ss
            LocalDateTime timestamp = ts != null
                    ? LocalDateTime.ofInstant(ts.toInstant(), BdDateUtils.getDisplayZoneId())
                    : null;
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
