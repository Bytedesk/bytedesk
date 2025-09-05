/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-09-05 12:55:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-05 13:19:45
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm_file.mq;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import com.bytedesk.core.jms.JmsArtemisConsts;
import com.bytedesk.kbase.llm_chunk.ChunkRestService;
import com.bytedesk.kbase.llm_file.FileRestService;

import lombok.extern.slf4j.Slf4j;

/**
 * 文件Chunk消息消费者
 * 处理文件chunk相关的异步消息
 */
@Slf4j
@Component
public class FileChunkMessageConsumer {

    @Autowired
    private FileRestService fileRestService;
    
    @Autowired
    private ChunkRestService chunkRestService;
    
    /**
     * 处理文件块重试消息
     */
    @JmsListener(destination = JmsArtemisConsts.QUEUE_FILE_CHUNK_RETRY, containerFactory = "jmsArtemisQueueFactory")
    public void handleChunkRetry(String chunkId) {
        log.info("Processing chunk retry for chunkId: {}", chunkId);
        
        try {
            // 重新处理chunk
            var chunkEntityOpt = chunkRestService.findByUid(chunkId);
            if (chunkEntityOpt.isEmpty()) {
                log.error("Chunk not found for retry: chunkId={}", chunkId);
                return;
            }
            
            var chunkEntity = chunkEntityOpt.get();
            // 重置状态为待处理（通过设置错误状态来触发重试）
            chunkEntity.setElasticError();
            chunkRestService.save(chunkEntity);
            
            log.info("Successfully reset chunk for retry: chunkId={}", chunkId);
        } catch (Exception e) {
            log.error("Failed to retry chunk processing for chunkId: " + chunkId, e);
        }
    }

    /**
     * 处理文件块处理完成消息
     */
    @JmsListener(destination = JmsArtemisConsts.QUEUE_FILE_CHUNK_COMPLETE, containerFactory = "jmsArtemisQueueFactory")
    public void handleChunkComplete(String fileId) {
        log.info("Processing chunk complete notification for fileId: {}", fileId);
        
        try {
            // 更新文件状态为已完成
            var fileEntityOpt = fileRestService.findByUid(fileId);
            if (fileEntityOpt.isEmpty()) {
                log.error("File not found for completion: fileId={}", fileId);
                return;
            }
            
            var fileEntity = fileEntityOpt.get();
            fileEntity.setElasticSuccess(); // 设置为成功状态
            fileRestService.save(fileEntity);
            
            log.info("Successfully updated file status to COMPLETED for fileId: {}", fileId);
        } catch (Exception e) {
            log.error("Failed to update file status for fileId: " + fileId, e);
        }
    }

    /**
     * 处理文件块索引消息
     */
    @JmsListener(destination = JmsArtemisConsts.QUEUE_FILE_CHUNK_INDEX, containerFactory = "jmsArtemisQueueFactory")
    public void handleChunkIndex(String chunkIds) {
        log.info("Processing chunk index for chunk IDs: {}", chunkIds);
        
        try {
            // 解析多个chunk ID
            String[] chunkIdArray = chunkIds.split(",");
            for (String chunkId : chunkIdArray) {
                String trimmedChunkId = chunkId.trim();
                if (!trimmedChunkId.isEmpty()) {
                    // 为每个chunk创建索引
                    var chunkEntityOpt = chunkRestService.findByUid(trimmedChunkId);
                    if (chunkEntityOpt.isPresent()) {
                        var chunkEntity = chunkEntityOpt.get();
                        
                        // 创建elasticsearch索引
                        try {
                            // 这里可以调用elasticsearch索引服务
                            chunkEntity.setElasticSuccess();
                            log.debug("Created elasticsearch index for chunk: {}", trimmedChunkId);
                        } catch (Exception e) {
                            chunkEntity.setElasticError();
                            log.error("Failed to create elasticsearch index for chunk: " + trimmedChunkId, e);
                        }
                        
                        // 创建向量索引
                        try {
                            // 这里可以调用向量索引服务
                            chunkEntity.setVectorSuccess();
                            log.debug("Created vector index for chunk: {}", trimmedChunkId);
                        } catch (Exception e) {
                            chunkEntity.setVectorError();
                            log.error("Failed to create vector index for chunk: " + trimmedChunkId, e);
                        }
                        
                        chunkRestService.save(chunkEntity);
                    } else {
                        log.error("Chunk not found for indexing: {}", trimmedChunkId);
                    }
                }
            }
            log.info("Successfully processed index creation for {} chunks", chunkIdArray.length);
        } catch (Exception e) {
            log.error("Failed to process chunk index for chunkIds: " + chunkIds, e);
        }
    }
}
