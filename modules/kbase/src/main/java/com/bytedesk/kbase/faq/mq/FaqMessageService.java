/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-05-17 10:05:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-17 10:36:20
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.faq.mq;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import com.bytedesk.core.jms.JmsArtemisConstants;

import lombok.extern.slf4j.Slf4j;

/**
 * FAQ消息服务
 * 使用核心模块中的JmsTemplate发送FAQ索引请求
 */
@Slf4j
@Service
public class FaqMessageService {

    @Autowired
    private JmsTemplate jmsTemplate;
    
    /**
     * 发送FAQ到索引队列，用于创建或更新索引
     * 
     * @param faqUid FAQ的唯一标识
     */
    public void sendToIndexQueue(String faqUid) {
        try {
            log.debug("发送FAQ到索引队列: {}", faqUid);
            
            FaqIndexMessage message = FaqIndexMessage.builder()
                    .faqUid(faqUid)
                    .operationType("index")
                    .updateElasticIndex(true)
                    .updateVectorIndex(true)
                    .build();
            
            // 使用JmsArtemisConstants中定义的常量
            jmsTemplate.convertAndSend(JmsArtemisConstants.QUEUE_FAQ_INDEX, message);
            log.debug("消息已发送到队列: {}", JmsArtemisConstants.QUEUE_FAQ_INDEX);
        } catch (Exception e) {
            log.error("发送FAQ索引消息失败: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 发送FAQ删除请求到索引队列
     * 
     * @param faqUid FAQ的唯一标识
     */
    public void sendToDeleteQueue(String faqUid) {
        try {
            log.debug("发送FAQ删除请求到索引队列: {}", faqUid);
            
            FaqIndexMessage message = FaqIndexMessage.builder()
                    .faqUid(faqUid)
                    .operationType("delete")
                    .updateElasticIndex(true)
                    .updateVectorIndex(true)
                    .build();
            
            jmsTemplate.convertAndSend(JmsArtemisConstants.QUEUE_FAQ_INDEX, message);
            log.debug("删除消息已发送到队列: {}", JmsArtemisConstants.QUEUE_FAQ_INDEX);
        } catch (Exception e) {
            log.error("发送FAQ删除消息失败: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 批量发送FAQ到索引队列
     * 
     * @param faqUids FAQ唯一标识列表
     */
    public void batchSendToIndexQueue(Iterable<String> faqUids) {
        try {
            log.debug("批量发送FAQ到索引队列");
            for (String faqUid : faqUids) {
                sendToIndexQueue(faqUid);
            }
        } catch (Exception e) {
            log.error("批量发送FAQ索引消息失败: {}", e.getMessage(), e);
        }
    }
}
