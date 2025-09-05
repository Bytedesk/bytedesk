/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-25 09:44:18
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-05 11:00:00
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

import com.bytedesk.kbase.llm_chunk.elastic.ChunkElasticService;
import com.bytedesk.kbase.llm_chunk.event.ChunkCreateEvent;
import com.bytedesk.kbase.llm_chunk.event.ChunkDeleteEvent;
import com.bytedesk.kbase.llm_chunk.event.ChunkUpdateDocEvent;
import com.bytedesk.kbase.llm_chunk.vector.ChunkVectorService;

import org.springframework.beans.factory.annotation.Autowired;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ChunkEventListener {

    private final ChunkElasticService chunkElasticService;

    @Autowired(required = false)
    private ChunkVectorService chunkVectorService;

    public ChunkEventListener(ChunkElasticService chunkElasticService) {
        this.chunkElasticService = chunkElasticService;
    }

    // Chunk仅用于全文搜索
    @EventListener
    public void onChunkCreateEvent(ChunkCreateEvent event) {
        ChunkEntity chunk = event.getChunk();
        log.info("ChunkEventListener onChunkCreateEvent: {}", chunk.getName());
        // 仅做全文索引
        chunkElasticService.indexChunk(chunk);
        /// 索引向量
        if (chunkVectorService != null) {
            try {
                chunkVectorService.indexChunkVector(chunk);
            } catch (Exception e) {
                log.error("Chunk向量索引失败: {}, 错误: {}", chunk.getName(), e.getMessage());
            }
        }
    }

    // Chunk仅用于全文搜索
    @EventListener
    public void onChunkUpdateDocEvent(ChunkUpdateDocEvent event) {
        ChunkEntity chunk = event.getChunk();
        log.info("ChunkEventListener ChunkUpdateDocEvent: {}", chunk.getName());
        // 更新全文索引
        chunkElasticService.indexChunk(chunk);
        // 更新向量索引
        if (chunkVectorService != null) {
            try {
                // 先删除旧的向量索引
                chunkVectorService.deleteChunkVector(chunk);
                // 再创建新的向量索引
                chunkVectorService.indexChunkVector(chunk);
                
            } catch (Exception e) {
                log.error("文本向量索引更新失败: {}, 错误: {}", chunk.getContent(), e.getMessage());
            }
        }
    }

    @EventListener
    public void onChunkDeleteEvent(ChunkDeleteEvent event) {
        ChunkEntity chunk = event.getChunk();
        log.info("ChunkEventListener onChunkDeleteEvent: {}", chunk.getName());
        // 从全文索引中删除
        boolean deleted = chunkElasticService.deleteChunk(chunk.getUid());
        if (!deleted) {
            log.warn("从Elasticsearch中删除Chunk索引失败: {}", chunk.getUid());
            // 可以考虑添加重试逻辑或者其他错误处理
        }
        try {
            chunkVectorService.deleteChunkVector(chunk);
        } catch (Exception e) {
            log.error("删除Chunk向量索引失败: {}, 错误: {}", chunk.getName(), e.getMessage());
        }
    }
}


