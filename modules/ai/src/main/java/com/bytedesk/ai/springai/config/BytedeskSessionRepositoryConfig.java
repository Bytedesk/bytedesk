/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-11-24
 * @Description: Spring AI Session JDBC 仓库配置（ChatMemory 升级替代）。
 *   自建 JdbcSessionRepository bean 并做方言映射，补齐官方 from() 不支持的 Oracle/KingBase。
 */
package com.bytedesk.ai.springai.config;

import javax.sql.DataSource;

import org.springframework.ai.session.SessionRepository;
import org.springframework.ai.session.jdbc.JdbcSessionRepository;
import org.springframework.ai.session.jdbc.JdbcSessionRepositoryDialect;
import org.springframework.ai.session.jdbc.PostgresJdbcSessionRepositoryDialect;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.transaction.PlatformTransactionManager;

import lombok.extern.slf4j.Slf4j;

/**
 * Spring AI 2.1.0 Session JDBC 仓库配置。
 *
 * <p>取代已废弃的 JdbcChatMemoryRepository（SPRING_AI_CHAT_MEMORY 表），改用
 * AI_SESSION / AI_SESSION_INDEX / AI_SESSION_EVENT 三表（Liquibase 迁移统一建表）。</p>
 *
 * <p><b>为何自建 bean 而不用官方自动装配：</b></p>
 * <p>{@code JdbcSessionRepositoryAutoConfiguration} 内部直接调用
 * {@link JdbcSessionRepositoryDialect#from(DataSource)} 检测方言，仅支持
 * PostgreSQL/H2/MySQL/MariaDB/SQL Server，遇到 Oracle/KingBase 会抛
 * {@link IllegalStateException}。bytedesk 需支持 Oracle/KingBase（见
 * starter 的 kingbase profile 与部署矩阵），因此整体接管 SessionRepository：</p>
 * <ul>
 *   <li>KingBase：兼容 PostgreSQL 协议/语法，映射到 Postgres 方言（ON CONFLICT upsert）</li>
 *   <li>Oracle：自定义方言（MERGE upsert + FETCH FIRST 分页），参考官方
 *       SqlServerJdbcSessionRepositoryDialect 的 non-LIMIT 分页写法</li>
 *   <li>其余数据库：走官方 {@code from()} 检测</li>
 * </ul>
 * <p>自定义 bean 会使官方 {@code JdbcSessionRepositoryAutoConfiguration} 的
 * {@code @ConditionalOnMissingBean(SessionRepository.class)} 让位；
 * {@code SessionServiceAutoConfiguration} 检测到唯一 SessionRepository 候选后
 * 仍会自动装配 SessionService（前缀 {@code spring.ai.session}，
 * time-to-live 默认 60 天、allow-system-messages 默认 false）。</p>
 */
@Slf4j
@Configuration
@ConditionalOnClass(SessionRepository.class)
@AutoConfigureBefore(name = "org.springframework.ai.model.session.jdbc.autoconfigure.JdbcSessionRepositoryAutoConfiguration")
public class BytedeskSessionRepositoryConfig {

    /**
     * 自建 JdbcSessionRepository：方言映射补齐 Oracle/KingBase。
     *
     * <p>schema 由 Liquibase 建（application.properties 中
     * {@code spring.ai.session.repository.jdbc.initialize-schema=never}），
     * 这里不设置 indexedAttributes（按 principal 检索会话非当前需求）。</p>
     */
    @Bean
    @ConditionalOnMissingBean(SessionRepository.class)
    public JdbcSessionRepository bytedeskSessionRepository(
            DataSource dataSource,
            ObjectProvider<JdbcTemplate> jdbcTemplateProvider,
            ObjectProvider<PlatformTransactionManager> transactionManagerProvider) {
        JdbcSessionRepository.Builder builder = JdbcSessionRepository.builder()
                .dataSource(dataSource)
                .dialect(resolveDialect(dataSource));
        JdbcTemplate jdbcTemplate = jdbcTemplateProvider.getIfAvailable();
        if (jdbcTemplate != null) {
            builder.jdbcTemplate(jdbcTemplate);
        }
        PlatformTransactionManager transactionManager = transactionManagerProvider.getIfAvailable();
        if (transactionManager != null) {
            builder.transactionManager(transactionManager);
        }
        log.info("Bytedesk JdbcSessionRepository initialized (AI_SESSION tables, dialect resolved)");
        return builder.build();
    }

    /**
     * 方言解析：官方 from() + Oracle/KingBase 扩展映射。
     *
     * <p>探测失败（如嵌入库元数据异常）时回退 Postgres 方言并告警，
     * 与旧版 JdbcChatMemoryRepositoryDialect 对未知库默认 Postgres 的行为一致。</p>
     */
    private JdbcSessionRepositoryDialect resolveDialect(DataSource dataSource) {
        String productName;
        try {
            // 显式 lambda 代替方法引用：方法引用的接收者 this 经方法描述符传入时
            // 无法保证符合 @Nonnull DatabaseMetaData，触发空类型安全告警；
            // lambda 参数则按接口声明按 @Nonnull 处理，无此问题
            productName = JdbcUtils.extractDatabaseMetaData(dataSource,
                    dbmd -> dbmd.getDatabaseProductName());
        } catch (Exception ex) {
            log.warn("Failed to detect database product name, falling back to Postgres dialect", ex);
            return new PostgresJdbcSessionRepositoryDialect();
        }
        if (productName == null || productName.isBlank()) {
            log.warn("Blank database product name, falling back to Postgres dialect");
            return new PostgresJdbcSessionRepositoryDialect();
        }
        // KingBase 兼容 PostgreSQL 协议（ON CONFLICT 等），映射到 Postgres 方言
        if (productName.contains("Kingbase")) {
            log.info("KingBase detected: using Postgres session repository dialect");
            return new PostgresJdbcSessionRepositoryDialect();
        }
        // Oracle：官方未提供方言，自定义实现（MERGE upsert + FETCH FIRST 分页）
        if (productName.contains("Oracle")) {
            log.info("Oracle detected: using custom Oracle session repository dialect");
            return new OracleJdbcSessionRepositoryDialect();
        }
        // DM 数据库同样兼容部分 Oracle 语法，但 upsert 语法不同，暂不支持，走官方检测报错提示
        return JdbcSessionRepositoryDialect.from(dataSource);
    }

    /**
     * Oracle 方言：MERGE upsert + FETCH FIRST 分页。
     *
     * <p>参考旧版 OracleChatMemoryRepositoryDialect（已废弃）与官方
     * SqlServerJdbcSessionRepositoryDialect 的分页写法：</p>
     * <ul>
     *   <li>upsert：MERGE INTO ... USING dual，命中 id 更新 expires_at/metadata，
     *       不触碰 created_at 与 event_version（与官方语义一致）</li>
     *   <li>insert-if-absent：INSERT ... SELECT ... FROM dual WHERE NOT EXISTS，
     *       避免 Oracle 失败语句中止外层事务（对应 PG 的 ON CONFLICT DO NOTHING）</li>
     *   <li>分页：OFFSET ? ROWS FETCH FIRST ? ROWS ONLY（Oracle 12c+）</li>
     *   <li>keyword 检索：默认 LIKE 谓词在 Oracle 下可用（LOWER + ESCAPE）</li>
     * </ul>
     */
    static class OracleJdbcSessionRepositoryDialect implements JdbcSessionRepositoryDialect {

        @Override
        public String getUpsertSessionSql() {
            return """
                    MERGE INTO AI_SESSION USING dual
                    ON (id = ?)
                    WHEN NOT MATCHED THEN
                        INSERT (id, created_at, expires_at, metadata)
                        VALUES (?, ?, ?, ?)
                    WHEN MATCHED THEN
                        UPDATE SET expires_at = ?, metadata = ?
                    """;
        }

        @Override
        public String getInsertSessionIfAbsentSql() {
            // 语句失败会中止外层事务，用 NOT EXISTS 静默跳过（对应 PG 的 DO NOTHING）
            return """
                    INSERT INTO AI_SESSION (id, created_at, expires_at, metadata)
                    SELECT ?, ?, ?, ? FROM dual
                    WHERE NOT EXISTS (SELECT 1 FROM AI_SESSION WHERE id = ?)
                    """;
        }

        @Override
        public String getLimitClause() {
            return "OFFSET 0 ROWS FETCH NEXT ? ROWS ONLY";
        }
    }
}
