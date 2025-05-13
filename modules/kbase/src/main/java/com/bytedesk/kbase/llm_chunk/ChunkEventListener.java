/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-25 09:44:18
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-13 15:22:42
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

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class ChunkEventListener {

    private final ChunkElasticService chunkService;
    
    // Chunk仅用于全文搜索
    @EventListener
    public void onChunkCreateEvent(ChunkCreateEvent event) {
        ChunkEntity chunk = event.getChunk();
        log.info("ChunkEventListener onChunkCreateEvent: {}", chunk.getName());
        // 仅做全文索引
        chunkService.indexChunk(chunk);
    }

    // Chunk仅用于全文搜索
    @EventListener
    public void onChunkUpdateDocEvent(ChunkUpdateDocEvent event) {
        ChunkEntity chunk = event.getChunk();
        log.info("ChunkEventListener ChunkUpdateDocEvent: {}", chunk.getName());
        // 更新全文索引
        chunkService.indexChunk(chunk);
    }

    @EventListener
    public void onChunkDeleteEvent(ChunkDeleteEvent event) {
        ChunkEntity chunk = event.getChunk();
        log.info("ChunkEventListener onChunkDeleteEvent: {}", chunk.getName());
        // 从全文索引中删除
        boolean deleted = chunkService.deleteChunk(chunk.getUid());
        if (!deleted) {
            log.warn("从Elasticsearch中删除Chunk索引失败: {}", chunk.getUid());
            // 可以考虑添加重试逻辑或者其他错误处理
        }
    }
}


