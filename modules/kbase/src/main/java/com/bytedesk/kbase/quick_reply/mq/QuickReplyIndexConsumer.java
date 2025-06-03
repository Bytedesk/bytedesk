/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-15 16:35:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-03 17:13:56
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
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import com.bytedesk.core.jms.JmsArtemisConstants;
import com.bytedesk.kbase.quick_reply.QuickReplyEntity;
import com.bytedesk.kbase.quick_reply.QuickReplyRepository;
import com.bytedesk.kbase.quick_reply.elastic.QuickReplyElasticService;
import com.bytedesk.kbase.quick_reply.vector.QuickReplyVectorService;

import jakarta.jms.JMSException;
import jakarta.jms.Message;
import lombok.extern.slf4j.Slf4j;

/**
 * 快捷回复索引消费者
 */
@Slf4j
@Component
public class QuickReplyIndexConsumer {

    @Autowired
    private QuickReplyRepository quickReplyRepository;

    @Autowired
    private QuickReplyElasticService quickReplyElasticService;

    @Autowired
    private QuickReplyVectorService quickReplyVectorService;

    /**
     * 处理快捷回复索引消息
     */
    @JmsListener(destination = JmsArtemisConstants.QUEUE_QUICK_REPLY_INDEX, containerFactory = "jmsArtemisQueueFactory")
    public void handleQuickReplyIndexMessage(QuickReplyIndexMessage indexMessage, Message message) throws JMSException {
        try {
            log.info("处理快捷回复索引消息: {}", indexMessage);
            
            String quickReplyUid = indexMessage.getQuickReplyUid();
            QuickReplyEntity quickReply = quickReplyRepository.findByUid(quickReplyUid)
                    .orElseThrow(() -> new IllegalArgumentException("快捷回复不存在: " + quickReplyUid));

            // 更新全文索引
            if (Boolean.TRUE.equals(indexMessage.getUpdateElasticIndex())) {
                try {
                    quickReplyElasticService.updateIndex(quickReply);
                    log.info("更新快捷回复全文索引成功: {}", quickReplyUid);
                } catch (Exception e) {
                    log.error("更新快捷回复全文索引失败: {}", e.getMessage(), e);
                    throw e;
                }
            }

            // 更新向量索引
            if (Boolean.TRUE.equals(indexMessage.getUpdateVectorIndex())) {
                try {
                    // 直接调用向量服务的 updateVectorIndex，让其内部处理向量化
                    quickReplyVectorService.updateVectorIndex(quickReply);
                    log.info("更新快捷回复向量索引成功: {}", quickReplyUid);
                } catch (Exception e) {
                    log.error("更新快捷回复向量索引失败: {}", e.getMessage(), e);
                    throw e;
                }
            }

            // 处理成功后，显式确认消息已被消费
            message.acknowledge();
        } catch (Exception e) {
            log.error("处理快捷回复索引消息失败: {}", e.getMessage(), e);
            throw e; // 重新抛出异常，让错误处理器处理
        }
    }
} 