/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-01 15:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-24 09:57:25
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.member.mq;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.member.mq.MemberBatchMessageProcessor.RetryDecision;
import com.bytedesk.core.mq.jms.JmsArtemisConsts;

import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import lombok.extern.slf4j.Slf4j;

/**
 * Member批量导入消息消费者
 * 参考FAQ的异步处理模式，用于处理Member批量导入消息
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "bytedesk.mq.type", havingValue = "artemis", matchIfMissing = true)
public class MemberBatchConsumer implements MessageListener {

    @Autowired
    private MemberBatchMessageService memberBatchMessageService;

    @Autowired
    private MemberBatchMessageProcessor memberBatchMessageProcessor;

    @Override
    @JmsListener(destination = JmsArtemisConsts.QUEUE_MEMBER_BATCH_IMPORT, containerFactory = "jmsArtemisQueueFactory")
    @Transactional
    public void onMessage(Message message) {
        try {
            // 安全获取消息属性，避免空值异常
            int deliveryCount = 0;
            String batchUid = "";
            int batchIndex = 0;
            
            try {
                // 检查属性是否存在，并提供默认值
                if (message.propertyExists("JMSXDeliveryCount")) {
                    deliveryCount = message.getIntProperty("JMSXDeliveryCount");
                }
                
                if (message.propertyExists("batchUid")) {
                    batchUid = message.getStringProperty("batchUid");
                }
                
                if (message.propertyExists("batchIndex")) {
                    batchIndex = message.getIntProperty("batchIndex");
                }
            } catch (JMSException ex) {
                log.warn("获取消息属性异常，使用默认值: {}", ex.getMessage());
            }
            
            log.debug("收到Member批量导入消息: 批次{}, 索引{}, 投递次数{}", batchUid, batchIndex, deliveryCount);

            // 如果投递次数超过阈值，记录错误并确认消息
            if (deliveryCount > MemberBatchMessageProcessor.MAX_RETRY_COUNT) {
                log.error("Member批量导入消息投递次数超过阈值，放弃处理: 批次{}, 索引{}", batchUid, batchIndex);
                message.acknowledge();
                return;
            }

            // 获取消息内容 - 从TextMessage中获取JSON字符串，然后转换为对象
            String messageBody = message.getBody(String.class);
            if (messageBody == null || messageBody.isEmpty()) {
                log.error("Member批量导入消息内容为空: 批次{}", batchUid);
                message.acknowledge();
                return;
            }
            
            // 使用JSON转换器解析消息内容为MemberBatchMessage对象
            MemberBatchMessage batchMessage = JSON.parseObject(messageBody, MemberBatchMessage.class);
            if (batchMessage == null) {
                log.error("无法解析Member批量导入消息: 批次{}", batchUid);
                message.acknowledge();
                return;
            }
            
            // 从JMS消息属性中获取orgUid，并确保batchMessage中有这个值
            try {
                if (message.propertyExists("orgUid")) {
                    String orgUidFromProperty = message.getStringProperty("orgUid");
                    // 如果消息体中的orgUid为空，但属性中有值，则使用属性中的值
                    if ((batchMessage.getOrgUid() == null || batchMessage.getOrgUid().isEmpty()) 
                            && orgUidFromProperty != null && !orgUidFromProperty.isEmpty()) {
                        batchMessage.setOrgUid(orgUidFromProperty);
                        log.debug("从消息属性中补充orgUid: {}", orgUidFromProperty);
                    }
                }
            } catch (JMSException ex) {
                log.warn("获取orgUid属性异常: {}", ex.getMessage());
            }
            
            // 最终检查orgUid是否存在
            if (batchMessage.getOrgUid() == null || batchMessage.getOrgUid().isEmpty()) {
                log.error("Member批量导入消息缺少必要的orgUid: 批次{}, 索引{}", 
                        batchMessage.getBatchUid(), batchMessage.getBatchIndex());
                message.acknowledge(); // 确认消息，不再重试
                return;
            }

            // 处理批量导入
            boolean success = memberBatchMessageProcessor.processBatchImport(batchMessage);
            
            if (success) {
                // 处理成功，确认消息
                message.acknowledge();
                log.debug("Member批量导入消息处理成功: 批次{}, 索引{}/{}", 
                         batchUid, batchMessage.getBatchIndex(), batchMessage.getBatchTotal());
                
                // 如果是最后一个批次，记录完成日志
                if (Boolean.TRUE.equals(batchMessage.getIsLastBatch())) {
                    log.info("Member批量导入完成: 批次{}, 总数{}", batchUid, batchMessage.getBatchTotal());
                }
            } else {
                // 处理失败，根据重试策略决定是否重试
                handleProcessingFailure(batchMessage, message);
            }

        } catch (JMSException e) {
            log.error("处理Member批量导入消息时发生JMS异常: {}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("处理Member批量导入消息时发生异常: {}", e.getMessage(), e);
        }
    }

    /**
     * 处理Member批量导入
     */
    /**
     * 处理失败的处理逻辑，采用指数退避策略进行重试
     */
    private void handleProcessingFailure(MemberBatchMessage batchMessage, Message message) throws JMSException {
        // 安全获取重试次数，如果为null则默认为0
        RetryDecision retryDecision = memberBatchMessageProcessor.evaluateRetry(batchMessage);

        // 确保批次信息存在，如果没有则使用空字符串
        String batchUid = (batchMessage.getBatchUid() != null) ? batchMessage.getBatchUid() : "";
        Integer batchIndex = (batchMessage.getBatchIndex() != null) ? batchMessage.getBatchIndex() : 0;
        
        if (retryDecision.shouldRetry()) {
            log.info("Member批量导入失败，准备重试: 批次{}, 索引{}, 重试次数{}, 延迟{}ms",
                    batchUid, batchIndex, retryDecision.nextRetryCount(), retryDecision.delayMs());
            
            try {
                // 发送重试消息
                memberBatchMessageService.sendRetryMessage(batchMessage, retryDecision.delayMs());
                
                // 确认当前消息（避免重复处理）
                message.acknowledge();
            } catch (Exception e) {
                log.error("发送重试消息失败: 批次{}, 索引{}, 错误: {}", 
                         batchUid, batchIndex, e.getMessage(), e);
                // 不确认消息，让消息重新投递
                throw new JMSException("发送重试消息失败: " + e.getMessage());
            }
            
        } else {
            // 重试次数已达上限，记录错误并确认消息
            log.error("Member批量导入重试次数达到上限，放弃处理: 批次{}, 索引{}, 重试次数{}",
                     batchUid, batchIndex, retryDecision.nextRetryCount());
            
            // 即使放弃处理，也需要确认消息，避免消息堆积
            message.acknowledge();
        }
    }
}
