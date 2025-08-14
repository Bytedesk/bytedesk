/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 22:59:32
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-08-27 18:04:45
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.quick_reply.mq;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import com.bytedesk.core.jms.JmsArtemisConsts;

import lombok.extern.slf4j.Slf4j;

/**
 * 快捷回复消息服务
 * 使用核心模块中的JmsTemplate发送快捷回复索引请求
 */
@Slf4j
@Service
public class QuickReplyMessageService {

    @Autowired
    private JmsTemplate jmsTemplate;
    
    /**
     * 发送快捷回复到索引队列，用于创建或更新索引
     * 增加了延迟发送机制和重试机制，避免同时大量索引导致冲突
     * 
     * @param quickReplyUid 快捷回复的唯一标识
     */
    public void sendToIndexQueue(String quickReplyUid) {
        try {
            log.debug("发送快捷回复到索引队列: {}", quickReplyUid);
            
            QuickReplyIndexMessage message = QuickReplyIndexMessage.builder()
                    .quickReplyUid(quickReplyUid)
                    .operationType("index")
                    .updateElasticIndex(true)
                    .updateVectorIndex(true)
                    .build();
            
            // 创建一个消息后置处理器，设置消息属性
            org.springframework.jms.core.MessagePostProcessor postProcessor = new org.springframework.jms.core.MessagePostProcessor() {
                @Override
                public jakarta.jms.Message postProcessMessage(jakarta.jms.Message jmsMessage) throws jakarta.jms.JMSException {
                    // 添加随机小延迟（0-300ms），避免消息同时到达造成冲突
                    long randomDelay = System.currentTimeMillis() + new java.util.Random().nextInt(300);
                    jmsMessage.setLongProperty("_AMQ_SCHED_DELIVERY", randomDelay);
                    
                    // 设置消息的重试策略
                    jmsMessage.setIntProperty("JMSXDeliveryCount", 0); // 初始投递次数
                    jmsMessage.setBooleanProperty("_AMQ_ORIG_MESSAGE", true); // 标记为原始消息
                    
                    // 设置消息分组，确保相同快捷回复的消息按顺序处理
                    jmsMessage.setStringProperty("JMSXGroupID", "quick_reply-" + quickReplyUid);
                    
                    return jmsMessage;
                }
            };
            
            // 使用JmsArtemisConstants中定义的常量，带后置处理器发送消息
            jmsTemplate.convertAndSend(JmsArtemisConsts.QUEUE_QUICK_REPLY_INDEX, message, postProcessor);
            log.debug("消息已发送到队列: {}", JmsArtemisConsts.QUEUE_QUICK_REPLY_INDEX);
        } catch (Exception e) {
            log.error("发送快捷回复索引消息失败: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 发送快捷回复删除请求到索引队列
     * 增加了延迟发送机制和重试机制，避免同时大量删除导致冲突
     * 
     * @param quickReplyUid 快捷回复的唯一标识
     */
    public void sendToDeleteQueue(String quickReplyUid) {
        try {
            log.debug("发送快捷回复删除请求到索引队列: {}", quickReplyUid);
            
            QuickReplyIndexMessage message = QuickReplyIndexMessage.builder()
                    .quickReplyUid(quickReplyUid)
                    .operationType("delete")
                    .updateElasticIndex(true)
                    .updateVectorIndex(true)
                    .build();
            
            // 创建一个消息后置处理器，设置消息属性
            org.springframework.jms.core.MessagePostProcessor postProcessor = new org.springframework.jms.core.MessagePostProcessor() {
                @Override
                public jakarta.jms.Message postProcessMessage(jakarta.jms.Message jmsMessage) throws jakarta.jms.JMSException {
                    // 添加随机小延迟（100-400ms），避免消息同时到达造成冲突
                    // 删除消息延迟稍长一些，优先处理索引消息
                    long randomDelay = System.currentTimeMillis() + 100 + new java.util.Random().nextInt(300);
                    jmsMessage.setLongProperty("_AMQ_SCHED_DELIVERY", randomDelay);
                    
                    // 设置消息的重试策略
                    jmsMessage.setIntProperty("JMSXDeliveryCount", 0); // 初始投递次数
                    jmsMessage.setBooleanProperty("_AMQ_ORIG_MESSAGE", true); // 标记为原始消息
                    
                    // 设置消息优先级，删除操作优先级略低
                    jmsMessage.setJMSPriority(4); // 正常优先级为5
                    
                    // 设置消息分组，确保相同快捷回复的消息按顺序处理
                    jmsMessage.setStringProperty("JMSXGroupID", "quick_reply-" + quickReplyUid);
                    
                    return jmsMessage;
                }
            };
            
            jmsTemplate.convertAndSend(JmsArtemisConsts.QUEUE_QUICK_REPLY_INDEX, message, postProcessor);
            log.debug("删除消息已发送到队列: {}", JmsArtemisConsts.QUEUE_QUICK_REPLY_INDEX);
        } catch (Exception e) {
            log.error("发送快捷回复删除消息失败: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 批量发送快捷回复到索引队列
     * 增加了发送间隔，避免短时间大量索引请求导致的冲突
     * 使用批处理方式并引入延迟策略，更均匀地分散消息发送时间
     * 
     * @param quickReplyUids 快捷回复唯一标识列表
     */
    public void batchSendToIndexQueue(Iterable<String> quickReplyUids) {
        try {
            log.debug("批量发送快捷回复到索引队列");
            
            java.util.Random random = new java.util.Random();
            int count = 0;
            int batchSize = 20; // 批量发送大小
            long startTime = System.currentTimeMillis();
            java.util.List<String> batch = new java.util.ArrayList<>(batchSize);
            
            for (String quickReplyUid : quickReplyUids) {
                batch.add(quickReplyUid);
                count++;
                
                // 当达到批处理大小或是最后一批时，处理并发送
                if (batch.size() >= batchSize) {
                    processBatch(batch, count);
                    batch.clear();
                    
                    // 每处理一批后休眠一小段时间，避免消息队列压力过大
                    try {
                        Thread.sleep(50 + random.nextInt(100)); // 50-150ms的随机间隔
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        log.warn("批量发送快捷回复索引消息被中断");
                        break;
                    }
                }
            }
            
            // 处理剩余的消息
            if (!batch.isEmpty()) {
                processBatch(batch, count);
            }
            
            long duration = System.currentTimeMillis() - startTime;
            log.info("批量发送快捷回复到索引队列完成，共发送了{}条消息，耗时{}ms", count, duration);
        } catch (Exception e) {
            log.error("批量发送快捷回复索引消息失败: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 处理一批快捷回复索引请求
     * 为每一批内的消息设置不同的延迟，使其错峰到达队列
     * 
     * @param batch 快捷回复唯一标识批次
     * @param totalCount 当前总处理计数
     */
    private void processBatch(java.util.List<String> batch, int totalCount) {
        java.util.Random random = new java.util.Random();
        int batchSize = batch.size();
        
        log.debug("处理第{}批快捷回复索引请求，共{}条", (totalCount / batchSize) + 1, batchSize);
        
        // 基础延迟时间，后面的消息会在此基础上增加随机延迟
        long baseDelay = System.currentTimeMillis();
        
        // 处理每条消息，设置不同的延迟
        for (int i = 0; i < batch.size(); i++) {
            String quickReplyUid = batch.get(i);
            try {
                // 计算当前消息的延迟时间，使批次内的消息错开到达
                // 每个消息增加50-150ms的递增延迟
                final long messageDelay = baseDelay + (i * 50) + random.nextInt(100);
                
                // 将循环索引i保存为final变量，以便在Lambda中使用
                final int messageIndex = i;
                
                QuickReplyIndexMessage message = QuickReplyIndexMessage.builder()
                        .quickReplyUid(quickReplyUid)
                        .operationType("index")
                        .updateElasticIndex(true)
                        .updateVectorIndex(true)
                        .build();
                
                // 创建后置处理器
                org.springframework.jms.core.MessagePostProcessor postProcessor = jmsMessage -> {
                    // 设置延迟投递时间
                    jmsMessage.setLongProperty("_AMQ_SCHED_DELIVERY", messageDelay);
                    
                    // 设置消息分组，确保相同快捷回复的消息按顺序处理
                    jmsMessage.setStringProperty("JMSXGroupID", "quick_reply-" + quickReplyUid);
                    
                    // 设置批处理标记，方便跟踪
                    jmsMessage.setStringProperty("batchId", "batch-" + ((totalCount / batchSize) + 1));
                    jmsMessage.setIntProperty("batchSize", batchSize);
                    jmsMessage.setIntProperty("batchIndex", messageIndex); // 使用final变量
                    
                    return jmsMessage;
                };
                
                // 发送消息
                jmsTemplate.convertAndSend(JmsArtemisConsts.QUEUE_QUICK_REPLY_INDEX, message, postProcessor);
                
            } catch (Exception e) {
                log.error("发送快捷回复索引消息失败: {}, 错误: {}", quickReplyUid, e.getMessage(), e);
            }
        }
        
        log.debug("批次处理完成，{}条消息已发送到队列", batch.size());
    }
} 