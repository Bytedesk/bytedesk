/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-25 09:44:18
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-05 14:50:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm_chunk;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.bytedesk.kbase.llm_chunk.event.ChunkCreateEvent;
import com.bytedesk.kbase.llm_chunk.event.ChunkDeleteEvent;
import com.bytedesk.kbase.llm_chunk.event.ChunkUpdateDocEvent;
import com.bytedesk.kbase.llm_chunk.mq.ChunkMessageService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class ChunkEventListener {

    private final ChunkMessageService chunkMessageService;

    // 使用MQ异步处理Chunk索引，解决大文件处理时的并发问题
    @EventListener
    public void onChunkCreateEvent(ChunkCreateEvent event) {
        ChunkEntity chunk = event.getChunk();
        log.info("ChunkEventListener onChunkCreateEvent: {} - 发送到MQ队列异步处理", chunk.getName());
        
        try {
            // 发送到MQ队列异步处理，避免直接阻塞事件处理
            String fileUid = chunk.getFile() != null ? chunk.getFile().getUid() : null;
            chunkMessageService.sendToIndexQueue(chunk.getUid(), fileUid);
            log.debug("Chunk索引消息已发送到队列: {}", chunk.getUid());
        } catch (Exception e) {
            log.error("发送Chunk索引消息到队列失败: {}, 错误: {}", chunk.getName(), e.getMessage(), e);
            // 这里不抛出异常，避免影响主流程
        }
    }

    // 使用MQ异步处理Chunk更新索引
    @EventListener
    public void onChunkUpdateDocEvent(ChunkUpdateDocEvent event) {
        ChunkEntity chunk = event.getChunk();
        log.info("ChunkEventListener ChunkUpdateDocEvent: {} - 发送到MQ队列异步处理", chunk.getName());
        
        try {
            // 发送到MQ队列异步处理
            String fileUid = chunk.getFile() != null ? chunk.getFile().getUid() : null;
            chunkMessageService.sendToIndexQueue(chunk.getUid(), fileUid);
            log.debug("Chunk更新索引消息已发送到队列: {}", chunk.getUid());
        } catch (Exception e) {
            log.error("发送Chunk更新索引消息到队列失败: {}, 错误: {}", chunk.getName(), e.getMessage(), e);
            // 这里不抛出异常，避免影响主流程
        }
    }

    @EventListener
    public void onChunkDeleteEvent(ChunkDeleteEvent event) {
        ChunkEntity chunk = event.getChunk();
        log.info("ChunkEventListener onChunkDeleteEvent: {} - 发送到MQ队列异步处理", chunk.getName());
        
        try {
            // 发送到MQ队列异步处理删除
            String fileUid = chunk.getFile() != null ? chunk.getFile().getUid() : null;
            chunkMessageService.sendToDeleteQueue(chunk.getUid(), fileUid);
            log.debug("Chunk删除消息已发送到队列: {}", chunk.getUid());
        } catch (Exception e) {
            log.error("发送Chunk删除消息到队列失败: {}, 错误: {}", chunk.getName(), e.getMessage(), e);
            // 这里不抛出异常，避免影响主流程
        }
    }
}


