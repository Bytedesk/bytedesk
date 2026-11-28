/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-08-11
 * @Description: Spring AI Session 会话日志只读查询 DAO（原生 JdbcTemplate）。
 *   查询 AI_SESSION_EVENT 表（Spring AI 2.1.0 Session 持久化，取代旧 SPRING_AI_CHAT_MEMORY）。
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
 * Spring AI Session 会话日志只读查询 DAO。
 *
 * <p>复用主库 DataSource。支持分页 + 按 conversationId/type/content 过滤。
 * 删除按会话删 AI_SESSION（外键 ON DELETE CASCADE 级联清空 AI_SESSION_EVENT）。</p>
 *
 * <p>排序字段采用固定白名单映射，禁止直接拼接前端传入值，避免 SQL 注入。</p>
 *
 * <p>旧表 SPRING_AI_CHAT_MEMORY 已废弃不再写入（ChatMemory 迁移到 Session API），
 * 本 DAO 已切换到新表。</p>
 */
@Slf4j
@Repository
public class ChatMemoryJdbcDao {

    /**
     * Spring AI 2.1.0 Session 事件表（取代旧 SPRING_AI_CHAT_MEMORY）。
     */
    public static final String TABLE_NAME = "AI_SESSION_EVENT";

    /**
     * Spring AI 2.1.0 Session 主表（删除会话时级联清空事件）。
     */
    public static final String SESSION_TABLE_NAME = "AI_SESSION";

    /**
     * 排序字段白名单：前端 sortBy 值 -> 数据库列名。
     * 未知字段回退到 seq，避免拼接任意字符串造成 SQL 注入。
     */
    private static final Map<String, String> SORT_COLUMNS = Map.of(
            "conversationId", "session_id",
            "type", "message_type",
            "timestamp", "timestamp",
            "sequenceId", "seq");

    private final JdbcTemplate jdbcTemplate;

    /**
     * timestamp 列在 SQL 中的转义形式，通过 DataSource 元数据自动探测：
     * PostgreSQL/Kingbase 用双引号，MySQL/MariaDB 用反引号。
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
                // 显式 lambda 代替方法引用：方法引用的接收者 this 经方法描述符传入时
                // 无法保证符合 @Nonnull DatabaseMetaData，触发空类型安全告警；
                // lambda 参数则按接口声明按 @Nonnull 处理，无此问题
                productName = org.springframework.jdbc.support.JdbcUtils
                    .extractDatabaseMetaData(dataSource, dbmd -> dbmd.getDatabaseProductName());
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
     * 拼接公共过滤条件（findPage/count 共用）。
     */
    private void appendFilters(StringBuilder sql, List<Object> params, ChatMemoryRequest request) {
        if (StringUtils.hasText(request.getConversationId())) {
            sql.append(" AND session_id LIKE ?");
            params.add("%" + request.getConversationId() + "%");
        }
        if (StringUtils.hasText(request.getType())) {
            sql.append(" AND message_type = ?");
            params.add(request.getType());
        }
        if (StringUtils.hasText(request.getContent())) {
            sql.append(" AND message_content LIKE ?");
            params.add("%" + request.getContent() + "%");
        }
    }

    /**
     * 分页查询会话日志（支持过滤）。
     *
     * <p>只返回 MESSAGE 事件；列映射：conversationId→session_id、content→message_content、
     * type→message_type、sequenceId→seq。</p>
     *
     * @param request 过滤 + 分页参数
     * @return 当前页记录列表
     */
    public List<ChatMemoryRecord> findPage(ChatMemoryRequest request) {
        StringBuilder sql = new StringBuilder("SELECT session_id, message_content, message_type, ")
            .append(timestampColumn)
            .append(", seq FROM ")
            .append(TABLE_NAME)
            .append(" WHERE event_type = 'MESSAGE'");
        List<Object> params = new ArrayList<>();
        appendFilters(sql, params, request);

        // 排序：白名单映射，未知字段回退到 seq（默认按 seq 降序，最新消息在前）
        String sortByKey = StringUtils.hasText(request.getSortBy()) ? request.getSortBy() : "sequenceId";
        String sortColumn = SORT_COLUMNS.getOrDefault(sortByKey, "seq");
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
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM ")
            .append(TABLE_NAME)
            .append(" WHERE event_type = 'MESSAGE'");
        List<Object> params = new ArrayList<>();
        appendFilters(sql, params, request);

        Long count = jdbcTemplate.queryForObject(sql.toString(), Long.class, params.toArray());
        return count != null ? count : 0L;
    }

    /**
     * 删除指定会话的所有记忆记录。
     *
     * <p>删 AI_SESSION 主表行，AI_SESSION_EVENT/AI_SESSION_INDEX 经外键
     * ON DELETE CASCADE 级联清空。</p>
     */
    public void deleteByConversationId(String conversationId) {
        if (!StringUtils.hasText(conversationId)) {
            throw new IllegalArgumentException("conversationId is required");
        }
        jdbcTemplate.update("DELETE FROM " + SESSION_TABLE_NAME + " WHERE id = ?", conversationId);
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
                    .conversationId(rs.getString("session_id"))
                    .content(rs.getString("message_content"))
                    .type(rs.getString("message_type"))
                    .timestamp(timestamp)
                    .sequenceId(rs.getLong("seq"))
                    .build();
        }
    }
}
