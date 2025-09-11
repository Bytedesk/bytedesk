/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-09-05 14:35:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-05 14:35:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm_image.mq;

import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import com.bytedesk.core.jms.JmsArtemisConsts;

import lombok.extern.slf4j.Slf4j;

/**
 * Image消息服务
 * 使用核心模块中的JmsTemplate发送Image索引请求
 * 优化了批处理逻辑，避免大文件处理时的并发问题
 */
@Slf4j
@Service
public class ImageMessageService {

    @Autowired
    private JmsTemplate jmsTemplate;
    
    private final Random random = new Random();
    
    /**
     * 发送单个Image到索引队列
     * 
     * @param imageUid Image的唯一标识
     */
    public void sendToIndexQueue(String imageUid) {
        sendToIndexQueue(imageUid, null);
    }
    
    /**
     * 发送单个Image到索引队列，附带文件UID信息
     * 
     * @param imageUid Image的唯一标识
     * @param fileUid 文件UID
     */
    public void sendToIndexQueue(String imageUid, String fileUid) {
        try {
            log.debug("发送Image到索引队列: {}", imageUid);
            
            ImageIndexMessage message = ImageIndexMessage.builder()
                    .imageUid(imageUid)
                    .operationType("index")
                    .updateElasticIndex(true)
                    .updateVectorIndex(true)
                    .fileUid(fileUid)
                    .build();
            
            // 创建消息后置处理器，设置消息属性
            org.springframework.jms.core.MessagePostProcessor postProcessor = jmsMessage -> {
                // 添加随机小延迟（0-200ms），避免消息同时到达造成冲突
                long randomDelay = System.currentTimeMillis() + random.nextInt(200);
                jmsMessage.setLongProperty("_AMQ_SCHED_DELIVERY", randomDelay);
                
                // 设置消息的重试策略
                jmsMessage.setIntProperty("JMSXDeliveryCount", 0);
                jmsMessage.setBooleanProperty("_AMQ_ORIG_MESSAGE", true);
                
                // 设置消息分组，确保相同文件的Image按顺序处理
                if (fileUid != null) {
                    jmsMessage.setStringProperty("JMSXGroupID", "file-" + fileUid);
                } else {
                    jmsMessage.setStringProperty("JMSXGroupID", "image-" + imageUid);
                }
                
                return jmsMessage;
            };
            
            jmsTemplate.convertAndSend(JmsArtemisConsts.QUEUE_CHUNK_INDEX, message, postProcessor);
            log.debug("消息已发送到队列: {}", JmsArtemisConsts.QUEUE_CHUNK_INDEX);
        } catch (Exception e) {
            log.error("发送Image索引消息失败: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 发送Image删除请求到索引队列
     * 
     * @param imageUid Image的唯一标识
     */
    public void sendToDeleteQueue(String imageUid) {
        sendToDeleteQueue(imageUid, null);
    }
    
    /**
     * 发送Image删除请求到索引队列，附带文件UID信息
     * 
     * @param imageUid Image的唯一标识
     * @param fileUid 文件UID
     */
    public void sendToDeleteQueue(String imageUid, String fileUid) {
        try {
            log.debug("发送Image删除请求到索引队列: {}", imageUid);
            
            ImageIndexMessage message = ImageIndexMessage.builder()
                    .imageUid(imageUid)
                    .operationType("delete")
                    .updateElasticIndex(true)
                    .updateVectorIndex(true)
                    .fileUid(fileUid)
                    .build();
            
            // 创建消息后置处理器，设置消息属性
            org.springframework.jms.core.MessagePostProcessor postProcessor = jmsMessage -> {
                // 删除消息延迟稍长一些（100-500ms），优先处理索引消息
                long randomDelay = System.currentTimeMillis() + 100 + random.nextInt(400);
                jmsMessage.setLongProperty("_AMQ_SCHED_DELIVERY", randomDelay);
                
                // 设置消息的重试策略
                jmsMessage.setIntProperty("JMSXDeliveryCount", 0);
                jmsMessage.setBooleanProperty("_AMQ_ORIG_MESSAGE", true);
                
                // 删除操作优先级略低
                jmsMessage.setJMSPriority(4);
                
                // 设置消息分组
                if (fileUid != null) {
                    jmsMessage.setStringProperty("JMSXGroupID", "file-" + fileUid);
                } else {
                    jmsMessage.setStringProperty("JMSXGroupID", "image-" + imageUid);
                }
                
                return jmsMessage;
            };
            
            jmsTemplate.convertAndSend(JmsArtemisConsts.QUEUE_CHUNK_INDEX, message, postProcessor);
            log.debug("删除消息已发送到队列: {}", JmsArtemisConsts.QUEUE_CHUNK_INDEX);
        } catch (Exception e) {
            log.error("发送Image删除消息失败: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 批量发送Image到索引队列
     * 优化了大文件处理的批量策略，减少并发冲突
     * 
     * @param imageUids Image唯一标识列表
     * @param fileUid 文件UID（用于分组）
     */
    public void batchSendToIndexQueue(List<String> imageUids, String fileUid) {
        if (imageUids == null || imageUids.isEmpty()) {
            log.warn("批量发送的Image列表为空");
            return;
        }
        
        try {
            log.info("批量发送Image到索引队列，共{}个images，文件: {}", imageUids.size(), fileUid);
            
            int batchSize = Math.min(10, imageUids.size()); // 动态调整批处理大小，最大10个
            String batchId = "batch-" + System.currentTimeMillis() + "-" + random.nextInt(1000);
            long startTime = System.currentTimeMillis();
            int totalCount = imageUids.size();
            
            // 分批处理
            for (int i = 0; i < totalCount; i += batchSize) {
                int endIndex = Math.min(i + batchSize, totalCount);
                List<String> batch = imageUids.subList(i, endIndex);
                
                log.debug("处理第{}批Image，范围: {}-{}", (i / batchSize) + 1, i, endIndex - 1);
                
                processBatch(batch, fileUid, batchId, i, totalCount);
                
                // 每批之间增加间隔，避免队列压力过大
                if (endIndex < totalCount) {
                    try {
                        Thread.sleep(100 + random.nextInt(200)); // 100-300ms间隔
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        log.warn("批量发送Image索引消息被中断");
                        break;
                    }
                }
            }
            
            long duration = System.currentTimeMillis() - startTime;
            log.info("批量发送Image到索引队列完成，共发送了{}条消息，耗时{}ms，批次ID: {}", 
                    totalCount, duration, batchId);
        } catch (Exception e) {
            log.error("批量发送Image索引消息失败: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 处理一批Image索引请求
     * 为每个消息设置递增延迟，避免并发冲突
     * 
     * @param batch Image唯一标识批次
     * @param fileUid 文件UID
     * @param batchId 批次ID
     * @param startIndex 起始索引
     * @param totalCount 总数量
     */
    private void processBatch(List<String> batch, String fileUid, String batchId, int startIndex, int totalCount) {
        long baseDelay = System.currentTimeMillis();
        
        for (int i = 0; i < batch.size(); i++) {
            String imageUid = batch.get(i);
            try {
                // 计算递增延迟，使消息错开到达
                final long messageDelay = baseDelay + (i * 100) + random.nextInt(200); // 每个消息递增100ms + 随机200ms
                final int messageIndex = startIndex + i;
                final int batchIndex = i;
                
                ImageIndexMessage message = ImageIndexMessage.builder()
                        .imageUid(imageUid)
                        .operationType("index")
                        .updateElasticIndex(true)
                        .updateVectorIndex(true)
                        .fileUid(fileUid)
                        .batchId(batchId)
                        .batchSize(batch.size())
                        .batchIndex(batchIndex)
                        .build();
                
                // 创建后置处理器
                org.springframework.jms.core.MessagePostProcessor postProcessor = jmsMessage -> {
                    // 设置延迟投递时间
                    jmsMessage.setLongProperty("_AMQ_SCHED_DELIVERY", messageDelay);
                    
                    // 设置消息分组，确保相同文件的消息按顺序处理
                    jmsMessage.setStringProperty("JMSXGroupID", "file-" + fileUid);
                    
                    // 设置批处理标记，方便跟踪
                    jmsMessage.setStringProperty("batchId", batchId);
                    jmsMessage.setIntProperty("batchSize", batch.size());
                    jmsMessage.setIntProperty("batchIndex", batchIndex);
                    jmsMessage.setIntProperty("totalCount", totalCount);
                    jmsMessage.setIntProperty("messageIndex", messageIndex);
                    
                    // 为大批量设置较低的优先级，避免阻塞其他消息
                    if (totalCount > 50) {
                        jmsMessage.setJMSPriority(3); // 低于正常优先级
                    }
                    
                    return jmsMessage;
                };
                
                // 发送消息
                jmsTemplate.convertAndSend(JmsArtemisConsts.QUEUE_CHUNK_INDEX, message, postProcessor);
                
            } catch (Exception e) {
                log.error("发送Image索引消息失败: {}, 错误: {}", imageUid, e.getMessage(), e);
            }
        }
        
        log.debug("批次处理完成，{}条消息已发送到队列，批次ID: {}", batch.size(), batchId);
    }
    
    /**
     * 批量发送文件的所有Image删除请求
     * 
     * @param imageUids Image唯一标识列表
     * @param fileUid 文件UID
     */
    public void batchSendToDeleteQueue(List<String> imageUids, String fileUid) {
        if (imageUids == null || imageUids.isEmpty()) {
            log.warn("批量删除的Image列表为空");
            return;
        }
        
        try {
            log.info("批量发送Image删除请求，共{}个images，文件: {}", imageUids.size(), fileUid);
            
            String batchId = "delete-batch-" + System.currentTimeMillis() + "-" + random.nextInt(1000);
            
            for (int i = 0; i < imageUids.size(); i++) {
                String imageUid = imageUids.get(i);
                
                // 为删除消息设置较长的延迟，确保在索引消息之后处理
                final long deleteDelay = System.currentTimeMillis() + 500 + (i * 50) + random.nextInt(200);
                final int messageIndex = i;
                
                ImageIndexMessage message = ImageIndexMessage.builder()
                        .imageUid(imageUid)
                        .operationType("delete")
                        .updateElasticIndex(true)
                        .updateVectorIndex(true)
                        .fileUid(fileUid)
                        .batchId(batchId)
                        .batchSize(imageUids.size())
                        .batchIndex(messageIndex)
                        .build();
                
                org.springframework.jms.core.MessagePostProcessor postProcessor = jmsMessage -> {
                    jmsMessage.setLongProperty("_AMQ_SCHED_DELIVERY", deleteDelay);
                    jmsMessage.setStringProperty("JMSXGroupID", "file-" + fileUid);
                    jmsMessage.setStringProperty("batchId", batchId);
                    jmsMessage.setJMSPriority(4); // 删除优先级稍低
                    return jmsMessage;
                };
                
                jmsTemplate.convertAndSend(JmsArtemisConsts.QUEUE_CHUNK_INDEX, message, postProcessor);
            }
            
            log.info("批量删除消息发送完成，共{}条消息，批次ID: {}", imageUids.size(), batchId);
        } catch (Exception e) {
            log.error("批量发送Image删除消息失败: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 发送image处理完成通知
     * 用于统计文件处理进度
     * 
     * @param imageUid Image UID
     * @param fileUid 文件UID
     * @param status 处理状态
     * @param processType 处理类型
     */
    public void sendImageCompleteNotification(String imageUid, String fileUid, String status, String processType) {
        try {
            ImageCompleteMessage message = ImageCompleteMessage.builder()
                    .imageUid(imageUid)
                    .fileUid(fileUid)
                    .status(status)
                    .processType(processType)
                    .build();
            
            jmsTemplate.convertAndSend(JmsArtemisConsts.QUEUE_CHUNK_COMPLETE, message);
            log.debug("已发送image完成通知: image={}, file={}, status={}, type={}", 
                    imageUid, fileUid, status, processType);
        } catch (Exception e) {
            log.error("发送image完成通知失败: {}, 错误: {}", imageUid, e.getMessage(), e);
        }
    }
}
