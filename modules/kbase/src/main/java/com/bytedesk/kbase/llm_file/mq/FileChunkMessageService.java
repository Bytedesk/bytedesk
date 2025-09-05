/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-09-05 12:50:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-05 12:50:00
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
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import com.bytedesk.core.jms.JmsArtemisConsts;

import lombok.extern.slf4j.Slf4j;

/**
 * 文件Chunk消息服务
 * 使用消息队列处理文件chunk相关的异步任务
 */
@Slf4j
@Service
public class FileChunkMessageService {

    @Autowired
    private JmsTemplate jmsTemplate;
    
    // 定义队列名称常量
    private static final String QUEUE_FILE_CHUNK_PROCESS = "queue.file.chunk.process";
    private static final String QUEUE_FILE_CHUNK_RETRY = "queue.file.chunk.retry";
    private static final String QUEUE_FILE_CHUNK_COMPLETE = "queue.file.chunk.complete";
    
    /**
     * 发送文件chunk处理请求到队列
     * 
     * @param fileUid 文件UID
     */
    public void sendChunkProcessMessage(String fileUid) {
        try {
            log.debug("发送文件chunk处理请求: {}", fileUid);
            
            FileChunkMessage message = FileChunkMessage.builder()
                    .fileUid(fileUid)
                    .operationType("process")
                    .createTime(System.currentTimeMillis())
                    .build();
            
            jmsTemplate.convertAndSend(QUEUE_FILE_CHUNK_PROCESS, message);
            log.debug("文件chunk处理消息已发送: {}", fileUid);
            
        } catch (Exception e) {
            log.error("发送文件chunk处理消息失败: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 发送chunk重试消息
     * 
     * @param fileUid 文件UID
     * @param documentIndex 文档索引
     * @param errorMessage 错误消息
     */
    public void sendChunkRetryMessage(String fileUid, Integer documentIndex, String errorMessage) {
        try {
            log.debug("发送chunk重试消息: fileUid={}, documentIndex={}", fileUid, documentIndex);
            
            FileChunkMessage message = FileChunkMessage.builder()
                    .fileUid(fileUid)
                    .operationType("retry")
                    .documentIndex(documentIndex)
                    .errorMessage(errorMessage)
                    .retryCount(1)
                    .createTime(System.currentTimeMillis())
                    .build();
            
            // 创建消息后置处理器，设置延迟重试
            org.springframework.jms.core.MessagePostProcessor postProcessor = jmsMessage -> {
                // 延迟30秒后重试
                long retryDelay = System.currentTimeMillis() + 30000;
                jmsMessage.setLongProperty("_AMQ_SCHED_DELIVERY", retryDelay);
                
                // 设置消息分组
                jmsMessage.setStringProperty("JMSXGroupID", "file-chunk-" + fileUid);
                
                return jmsMessage;
            };
            
            jmsTemplate.convertAndSend(QUEUE_FILE_CHUNK_RETRY, message, postProcessor);
            log.debug("chunk重试消息已发送: {}", fileUid);
            
        } catch (Exception e) {
            log.error("发送chunk重试消息失败: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 发送chunk处理完成通知
     * 
     * @param fileUid 文件UID
     * @param chunkCount 生成的chunk数量
     */
    public void sendChunkProcessCompleteMessage(String fileUid, Integer chunkCount) {
        try {
            log.debug("发送chunk处理完成通知: fileUid={}, chunkCount={}", fileUid, chunkCount);
            
            FileChunkMessage message = FileChunkMessage.builder()
                    .fileUid(fileUid)
                    .operationType("complete")
                    .chunkCount(chunkCount)
                    .createTime(System.currentTimeMillis())
                    .build();
            
            jmsTemplate.convertAndSend(QUEUE_FILE_CHUNK_COMPLETE, message);
            log.info("文件chunk处理完成通知已发送: fileUid={}, chunkCount={}", fileUid, chunkCount);
            
        } catch (Exception e) {
            log.error("发送chunk处理完成通知失败: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 批量发送chunk索引消息
     * 借鉴FAQ的批量处理机制，错峰发送chunk索引请求
     * 
     * @param chunkUids chunk UID列表
     */
    public void batchSendChunkIndexMessages(Iterable<String> chunkUids) {
        try {
            log.debug("批量发送chunk索引消息");
            
            java.util.Random random = new java.util.Random();
            int count = 0;
            int batchSize = 15; // 比FAQ稍小的批次大小
            long startTime = System.currentTimeMillis();
            java.util.List<String> batch = new java.util.ArrayList<>(batchSize);
            
            for (String chunkUid : chunkUids) {
                batch.add(chunkUid);
                count++;
                
                if (batch.size() >= batchSize) {
                    processBatch(batch, count, random);
                    batch.clear();
                    
                    // 批次间休眠
                    try {
                        Thread.sleep(100 + random.nextInt(150));
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        log.warn("批量发送chunk索引消息被中断");
                        break;
                    }
                }
            }
            
            // 处理剩余消息
            if (!batch.isEmpty()) {
                processBatch(batch, count, random);
            }
            
            long duration = System.currentTimeMillis() - startTime;
            log.info("批量发送chunk索引消息完成，共发送{}条消息，耗时{}ms", count, duration);
            
        } catch (Exception e) {
            log.error("批量发送chunk索引消息失败: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 处理一批chunk索引请求
     */
    private void processBatch(java.util.List<String> batch, int totalCount, java.util.Random random) {
        int batchSize = batch.size();
        log.debug("处理第{}批chunk索引请求，共{}条", (totalCount / batchSize) + 1, batchSize);
        
        long baseDelay = System.currentTimeMillis();
        
        for (int i = 0; i < batch.size(); i++) {
            String chunkUid = batch.get(i);
            try {
                final long messageDelay = baseDelay + (i * 80) + random.nextInt(120);
                final int messageIndex = i;
                
                FileChunkMessage message = FileChunkMessage.builder()
                        .fileUid(chunkUid) // 这里重用fileUid字段存储chunkUid
                        .operationType("index")
                        .createTime(System.currentTimeMillis())
                        .build();
                
                org.springframework.jms.core.MessagePostProcessor postProcessor = jmsMessage -> {
                    jmsMessage.setLongProperty("_AMQ_SCHED_DELIVERY", messageDelay);
                    jmsMessage.setStringProperty("JMSXGroupID", "chunk-index-" + chunkUid);
                    jmsMessage.setStringProperty("batchId", "batch-" + ((totalCount / batchSize) + 1));
                    jmsMessage.setIntProperty("batchSize", batchSize);
                    jmsMessage.setIntProperty("batchIndex", messageIndex);
                    return jmsMessage;
                };
                
                // 发送到chunk索引队列（可以复用FAQ的索引队列或创建新的）
                jmsTemplate.convertAndSend(JmsArtemisConsts.QUEUE_FAQ_INDEX, message, postProcessor);
                
            } catch (Exception e) {
                log.error("发送chunk索引消息失败: {}, 错误: {}", chunkUid, e.getMessage(), e);
            }
        }
        
        log.debug("批次处理完成，{}条消息已发送到队列", batch.size());
    }
}
