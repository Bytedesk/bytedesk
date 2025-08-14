/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-28 09:35:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-01-28 09:35:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm_webpage.mq;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import com.bytedesk.core.jms.JmsArtemisConsts;

import lombok.extern.slf4j.Slf4j;

/**
 * 网页消息服务
 * 使用Artemis JMS将网页索引操作发送到消息队列进行异步处理
 * 
 * @author jackning
 */
@Service
@Slf4j
public class WebpageMessageService {
    
    @Autowired
    private JmsTemplate jmsTemplate;
    
    /**
     * 发送网页到索引队列，用于创建或更新索引
     * 增加了延迟发送机制和重试机制，避免同时大量索引导致冲突
     * 
     * @param webpageUid 网页的唯一标识
     */
    public void sendToIndexQueue(String webpageUid) {
        try {
            WebpageIndexMessage message = WebpageIndexMessage.builder()
                .webpageUid(webpageUid)
                .operationType("index") // 创建或更新索引
                .updateElasticIndex(true)
                .updateVectorIndex(true)
                .build();
                
            // 创建消息后置处理器，用于设置延迟发送和重试
            org.springframework.jms.core.MessagePostProcessor postProcessor = new org.springframework.jms.core.MessagePostProcessor() {
                @Override
                public jakarta.jms.Message postProcessMessage(jakarta.jms.Message jmsMessage) throws jakarta.jms.JMSException {
                    // 设置延迟发送（2-5秒的随机延迟，避免大量并发索引）
                    long delay = 2000 + (long)(Math.random() * 3000);
                    jmsMessage.setLongProperty("_AMQ_SCHED_DELIVERY", System.currentTimeMillis() + delay);
                    
                    // 设置重试策略
                    jmsMessage.setIntProperty("_AMQ_MAX_RETRIES", 3);
                    jmsMessage.setLongProperty("_AMQ_RETRY_DELAY", 5000);
                    
                    return jmsMessage;
                }
            };
            
            // 使用JmsArtemisConstants中定义的常量，带后置处理器发送消息
            jmsTemplate.convertAndSend(JmsArtemisConsts.QUEUE_WEBPAGE_INDEX, message, postProcessor);
            log.debug("网页索引消息已发送到队列: {}", webpageUid);
        } catch (Exception e) {
            log.error("发送网页索引消息失败: {}, 错误: {}", webpageUid, e.getMessage(), e);
        }
    }
    
    /**
     * 发送网页删除消息到队列
     * 
     * @param webpageUid 网页的唯一标识
     */
    public void sendToDeleteQueue(String webpageUid) {
        try {
            WebpageIndexMessage message = WebpageIndexMessage.builder()
                .webpageUid(webpageUid)
                .operationType("delete")
                .updateElasticIndex(true)
                .updateVectorIndex(true)
                .build();
                
            jmsTemplate.convertAndSend(JmsArtemisConsts.QUEUE_WEBPAGE_INDEX, message);
            log.debug("网页删除消息已发送到队列: {}", webpageUid);
        } catch (Exception e) {
            log.error("发送网页删除消息失败: {}, 错误: {}", webpageUid, e.getMessage(), e);
        }
    }
    
    /**
     * 发送仅更新全文索引的消息
     * 
     * @param webpageUid 网页的唯一标识
     */
    public void sendToElasticIndexQueue(String webpageUid) {
        try {
            WebpageIndexMessage message = WebpageIndexMessage.builder()
                .webpageUid(webpageUid)
                .operationType("index")
                .updateElasticIndex(true)
                .updateVectorIndex(false)
                .build();
                
            jmsTemplate.convertAndSend(JmsArtemisConsts.QUEUE_WEBPAGE_INDEX, message);
            log.debug("网页全文索引消息已发送到队列: {}", webpageUid);
        } catch (Exception e) {
            log.error("发送网页全文索引消息失败: {}, 错误: {}", webpageUid, e.getMessage(), e);
        }
    }
    
    /**
     * 发送仅更新向量索引的消息
     * 
     * @param webpageUid 网页的唯一标识
     */
    public void sendToVectorIndexQueue(String webpageUid) {
        try {
            WebpageIndexMessage message = WebpageIndexMessage.builder()
                .webpageUid(webpageUid)
                .operationType("index")
                .updateElasticIndex(false)
                .updateVectorIndex(true)
                .build();
                
            jmsTemplate.convertAndSend(JmsArtemisConsts.QUEUE_WEBPAGE_INDEX, message);
            log.debug("网页向量索引消息已发送到队列: {}", webpageUid);
        } catch (Exception e) {
            log.error("发送网页向量索引消息失败: {}, 错误: {}", webpageUid, e.getMessage(), e);
        }
    }
}
