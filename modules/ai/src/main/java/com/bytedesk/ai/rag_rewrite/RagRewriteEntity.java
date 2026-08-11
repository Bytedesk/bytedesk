/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-08-10
 * @Description: RAG Query 增强记录实体。
 *   持久化 ragRewriteEnabled（单 query 改写）与 ragMultiQueryEnabled（多查询扩展）的执行过程与结果，
 *   便于后续分析改写效果、召回率提升情况，以及排查改写偏离原意等问题。
 *
 *   字段说明：
 *   - originalQuery: 用户原始 query
 *   - rewriteType: 增强类型（REWRITE / MULTI_QUERY / NONE）
 *   - status: 执行状态（SUCCESS / FAILED / SKIPPED）
 *   - rewrittenQuery: 改写后的 query（ragRewriteEnabled 时填充）
 *   - expandedQueries: 多查询扩展结果 JSON 数组（ragMultiQueryEnabled 时填充）
 *   - effectiveQuery: 最终用于 KB 检索的 query（改写后或原 query）
 *   - resultCount: KB 检索命中条数
 *   - resultSummary: KB 检索结果摘要 JSON（sourceUid / sourceName / score 等）
 *   - errorMessage: 执行失败时的错误信息
 *   - latencyMs: 增强执行耗时（毫秒）
 */
package com.bytedesk.ai.rag_rewrite;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.TypeConsts;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Index;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(
    name = "bytedesk_ai_rag_rewrite",
    indexes = {
        @Index(name = "idx_rag_rewrite_uid", columnList = "uuid"),
        @Index(name = "idx_rag_rewrite_org_robot", columnList = "org_uid,robot_uid"),
        @Index(name = "idx_rag_rewrite_thread", columnList = "thread_topic")
    }
)
public class RagRewriteEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    // 机器人 UID
    @Column(name = "robot_uid")
    private String robotUid;

    // 会话主题（threadTopic，与 conversationId 一致）
    @Column(name = "thread_topic")
    private String threadTopic;

    // 消息 UID（关联触发本次增强的用户消息）
    @Column(name = "message_uid")
    private String messageUid;

    // 原始用户 query
    @Column(name = "original_query", columnDefinition = TypeConsts.COLUMN_TYPE_TEXT, nullable = false)
    private String originalQuery;

    // 增强类型：REWRITE / MULTI_QUERY / NONE
    @Builder.Default
    @Column(name = "rewrite_type")
    private String rewriteType = RagRewriteTypeEnum.NONE.name();

    // 执行状态：SUCCESS / FAILED / SKIPPED
    @Builder.Default
    @Column(name = "status")
    private String status = RagRewriteStatusEnum.SKIPPED.name();

    // 改写后的 query（ragRewriteEnabled 时填充）
    @Column(name = "rewritten_query", columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String rewrittenQuery;

    // 多查询扩展结果 JSON 数组（ragMultiQueryEnabled 时填充，如 ["query1","query2","query3"]）
    @Column(name = "expanded_queries", columnDefinition = TypeConsts.COLUMN_TYPE_JSON)
    private String expandedQueries;

    // 最终用于 KB 检索的 query（改写后或原 query）
    @Column(name = "effective_query", columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String effectiveQuery;

    // KB 检索命中条数
    @Builder.Default
    @Column(name = "result_count")
    private Integer resultCount = 0;

    // KB 检索结果摘要 JSON（sourceUid / sourceName / score 等，便于分析召回效果）
    @Column(name = "result_summary", columnDefinition = TypeConsts.COLUMN_TYPE_JSON)
    private String resultSummary;

    // 执行失败时的错误信息
    @Column(name = "error_message", columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String errorMessage;

    // 增强执行耗时（毫秒）
    @Column(name = "latency_ms")
    private Long latencyMs;

    // 是否发生了降级（改写失败回退原 query）
    @Builder.Default
    @Column(name = "fallback_used")
    private Boolean fallbackUsed = false;
}
