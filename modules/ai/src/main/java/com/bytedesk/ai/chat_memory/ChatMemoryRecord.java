/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-08-11
 * @Description: ChatMemory 记录 DTO（只读）。
 */
package com.bytedesk.ai.chat_memory;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ChatMemory 记录（只读 DTO）。
 *
 * <p>字段名保持旧版（conversationId/type/...）以兼容前端；
 * 数据源已切换到 AI_SESSION_EVENT 表（列映射见 ChatMemoryJdbcDao）。</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMemoryRecord {

    /** 会话 ID（= threadTopic，对应 AI_SESSION_EVENT.session_id） */
    private String conversationId;

    /** 消息内容（纯文本，对应 message_content 列） */
    private String content;

    /** 消息类型：USER / ASSISTANT / SYSTEM / TOOL（对应 message_type 列） */
    private String type;

    /** 时间戳（北京时间；PostgreSQL 中为保留字，查询时需引为 "timestamp"） */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    /** 序列号（全局自增，对应 seq 列） */
    private Long sequenceId;
}