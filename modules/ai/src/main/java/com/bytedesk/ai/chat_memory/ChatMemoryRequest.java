package com.bytedesk.ai.chat_memory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ChatMemory 查询请求（分页 + 过滤）。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMemoryRequest {

    private Integer pageNumber;
    private Integer pageSize;
    private String sortBy;
    private String sortDirection;

    /** 按会话 ID 过滤（模糊匹配） */
    private String conversationId;

    /** 按消息类型过滤 */
    private String type;

    /** 按内容关键字过滤 */
    private String content;
}
