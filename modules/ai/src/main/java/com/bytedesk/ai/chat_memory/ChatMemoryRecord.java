/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-08-11
 * @Description: Spring AI ChatMemory 记录 DTO（只读）。
 *   对应 SPRING_AI_CHAT_MEMORY 表，由 Spring AI MessageChatMemoryAdvisor 写入。
 *   本类仅用于管理后台查询展示，不参与持久化写入。
 */
package com.bytedesk.ai.chat_memory;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ChatMemory 记录（只读 DTO，映射 SPRING_AI_CHAT_MEMORY 表）。
 *
 * <p>注意：这是纯 DTO，不是 JPA 实体。SPRING_AI_CHAT_MEMORY 表由 Spring AI
 * {@code JdbcChatMemoryRepository}（原生 JdbcTemplate）管理，无单一主键，
 * 管理后台查询也走原生 JdbcTemplate（见 {@link ChatMemoryJdbcDao}），与官方实现风格一致。</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMemoryRecord {

    /** 会话 ID（= threadTopic） */
    private String conversationId;

    /** 消息内容（JSON 或文本） */
    private String content;

    /** 消息类型：USER / ASSISTANT / SYSTEM / TOOL */
    private String type;

    /** 时间戳（PostgreSQL 中为保留字，查询时需引为 "timestamp"） */
    private LocalDateTime timestamp;

    /** 序列号（同一会话内递增） */
    private Long sequenceId;
}