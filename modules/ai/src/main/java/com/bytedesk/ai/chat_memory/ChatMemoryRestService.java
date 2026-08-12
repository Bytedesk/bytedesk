/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-08-11
 * @Description: ChatMemory 查询服务（只读 + 按会话删除）。
 */
package com.bytedesk.ai.chat_memory;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class ChatMemoryRestService {

    private final ChatMemoryJdbcDao chatMemoryJdbcDao;

    /**
     * 分页查询 ChatMemory（支持按 conversationId/type/content 过滤）。
     */
    public List<ChatMemoryRecord> queryByOrg(ChatMemoryRequest request) {
        return chatMemoryJdbcDao.findPage(request);
    }

    /**
     * 统计满足过滤条件的记录总数。
     */
    public long count(ChatMemoryRequest request) {
        return chatMemoryJdbcDao.count(request);
    }

    /**
     * 删除指定会话的所有记忆记录。
     */
    public void deleteByConversationId(String conversationId) {
        chatMemoryJdbcDao.deleteByConversationId(conversationId);
    }
}
