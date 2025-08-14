/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-05-31 16:30:50
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-31 16:50:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.article.mq;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import com.bytedesk.core.jms.JmsArtemisConsts;

import lombok.extern.slf4j.Slf4j;

/**
 * 文章消息服务
 * 使用核心模块中的JmsTemplate发送文章索引请求
 */
@Slf4j
@Service
public class ArticleMessageService {

    @Autowired
    private JmsTemplate jmsTemplate;
    
    /**
     * 发送文章到索引队列，用于创建或更新索引
     * 增加了延迟发送机制和重试机制，避免同时大量索引导致冲突
     * 
     * @param articleUid 文章的唯一标识
     */
    public void sendToIndexQueue(String articleUid) {
        try {
            log.debug("发送文章到索引队列: {}", articleUid);
            
            ArticleIndexMessage message = ArticleIndexMessage.builder()
                    .articleUid(articleUid)
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
                    
                    // 设置消息分组，确保相同文章的消息按顺序处理
                    jmsMessage.setStringProperty("JMSXGroupID", "article-" + articleUid);
                    
                    return jmsMessage;
                }
            };
            
            // 使用JmsArtemisConstants中定义的常量，带后置处理器发送消息
            jmsTemplate.convertAndSend(JmsArtemisConsts.QUEUE_ARTICLE_INDEX, message, postProcessor);
            log.debug("消息已发送到队列: {}", JmsArtemisConsts.QUEUE_ARTICLE_INDEX);
        } catch (Exception e) {
            log.error("发送文章索引消息失败: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 发送文章删除请求到索引队列
     * 增加了延迟发送机制和重试机制，避免同时大量删除导致冲突
     * 
     * @param articleUid 文章的唯一标识
     */
    public void sendToDeleteQueue(String articleUid) {
        try {
            log.debug("发送文章删除请求到索引队列: {}", articleUid);
            
            ArticleIndexMessage message = ArticleIndexMessage.builder()
                    .articleUid(articleUid)
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
                    
                    // 设置消息分组，确保相同文章的消息按顺序处理
                    jmsMessage.setStringProperty("JMSXGroupID", "article-" + articleUid);
                    
                    return jmsMessage;
                }
            };
            
            jmsTemplate.convertAndSend(JmsArtemisConsts.QUEUE_ARTICLE_INDEX, message, postProcessor);
            log.debug("删除消息已发送到队列: {}", JmsArtemisConsts.QUEUE_ARTICLE_INDEX);
        } catch (Exception e) {
            log.error("发送文章删除消息失败: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 批量发送文章到索引队列
     * 增加了发送间隔，避免短时间大量索引请求导致的冲突
     * 使用批处理方式并引入延迟策略，更均匀地分散消息发送时间
     * 
     * @param articleUids 文章唯一标识列表
     */
    public void batchSendToIndexQueue(Iterable<String> articleUids) {
        try {
            log.debug("批量发送文章到索引队列");
            
            java.util.Random random = new java.util.Random();
            int count = 0;
            int batchSize = 20; // 批量发送大小
            long startTime = System.currentTimeMillis();
            java.util.List<String> batch = new java.util.ArrayList<>(batchSize);
            
            for (String articleUid : articleUids) {
                batch.add(articleUid);
                count++;
                
                // 当达到批处理大小或是最后一批时，处理并发送
                if (batch.size() >= batchSize) {
                    processBatch(batch, count);
                    batch.clear();
                    
                    // 每批次间隔一定时间，避免对服务器造成瞬时压力
                    try {
                        Thread.sleep(300 + random.nextInt(200));
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
            
            // 处理最后一批
            if (!batch.isEmpty()) {
                processBatch(batch, count);
            }
            
            long totalTime = System.currentTimeMillis() - startTime;
            log.debug("批量发送完成，共{}条消息，耗时{}ms", count, totalTime);
            
        } catch (Exception e) {
            log.error("批量发送文章索引消息失败: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 处理批量发送的辅助方法
     * @param batch 批次数据
     * @param count 当前计数
     */
    private void processBatch(java.util.List<String> batch, int count) {
        log.debug("处理第{}批, 大小: {}", (count / batch.size()), batch.size());
        
        for (String articleUid : batch) {
            try {
                ArticleIndexMessage message = ArticleIndexMessage.builder()
                        .articleUid(articleUid)
                        .operationType("index")
                        .updateElasticIndex(true)
                        .updateVectorIndex(true)
                        .build();
                
                // 创建消息后置处理器，设置延迟发送
                org.springframework.jms.core.MessagePostProcessor postProcessor = new org.springframework.jms.core.MessagePostProcessor() {
                    @Override
                    public jakarta.jms.Message postProcessMessage(jakarta.jms.Message jmsMessage) throws jakarta.jms.JMSException {
                        // 添加随机延迟（0-500ms），分散消息到达时间
                        long randomDelay = System.currentTimeMillis() + new java.util.Random().nextInt(500);
                        jmsMessage.setLongProperty("_AMQ_SCHED_DELIVERY", randomDelay);
                        
                        // 设置消息的重试策略
                        jmsMessage.setIntProperty("JMSXDeliveryCount", 0);
                        jmsMessage.setBooleanProperty("_AMQ_ORIG_MESSAGE", true);
                        
                        // 设置消息分组
                        jmsMessage.setStringProperty("JMSXGroupID", "article-" + articleUid);
                        
                        return jmsMessage;
                    }
                };
                
                jmsTemplate.convertAndSend(JmsArtemisConsts.QUEUE_ARTICLE_INDEX, message, postProcessor);
                
            } catch (Exception e) {
                log.error("发送文章索引消息失败: {}, 错误: {}", articleUid, e.getMessage());
            }
        }
    }
}
