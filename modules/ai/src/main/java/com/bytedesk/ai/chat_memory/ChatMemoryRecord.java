/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-08-11
 * @Description: ChatMemory 记录 DTO（只读）。
 */
package com.bytedesk.ai.chat_memory;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ChatMemory 记录（只读 DTO）。
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