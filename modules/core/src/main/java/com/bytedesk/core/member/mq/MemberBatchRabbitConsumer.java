/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-02-05 10:20:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2026-02-05 10:20:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.member.mq;

import java.nio.charset.StandardCharsets;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.member.mq.MemberBatchMessageProcessor.RetryDecision;
import com.bytedesk.core.mq.jms.JmsArtemisConsts;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "bytedesk.mq.type", havingValue = "rabbitmq")
public class MemberBatchRabbitConsumer {

    private final MemberBatchMessageService memberBatchMessageService;
    private final MemberBatchMessageProcessor memberBatchMessageProcessor;

    @RabbitListener(queues = JmsArtemisConsts.QUEUE_MEMBER_BATCH_IMPORT, containerFactory = "rabbitListenerContainerFactory")
    @Transactional
    public void onMessage(Message message) {
        MessageProperties properties = message.getMessageProperties();
        int deliveryCount = getHeaderInt(properties, "JMSXDeliveryCount", 0);
        String batchUid = getHeaderString(properties, "batchUid", "");
        int batchIndex = getHeaderInt(properties, "batchIndex", 0);

        log.debug("收到Member批量导入消息(RabbitMQ): 批次{}, 索引{}, 投递次数{}", batchUid, batchIndex, deliveryCount);

        if (deliveryCount > MemberBatchMessageProcessor.MAX_RETRY_COUNT) {
            log.error("Member批量导入消息投递次数超过阈值，放弃处理: 批次{}, 索引{}", batchUid, batchIndex);
            return;
        }

        String messageBody = new String(message.getBody(), StandardCharsets.UTF_8);
        if (messageBody.isEmpty()) {
            log.error("Member批量导入消息内容为空: 批次{}", batchUid);
            return;
        }

        MemberBatchMessage batchMessage = JSON.parseObject(messageBody, MemberBatchMessage.class);
        if (batchMessage == null) {
            log.error("无法解析Member批量导入消息: 批次{}", batchUid);
            return;
        }

        // 如果消息体orgUid为空，尝试从header补充
        if ((batchMessage.getOrgUid() == null || batchMessage.getOrgUid().isEmpty())) {
            String orgUid = getHeaderString(properties, "orgUid", "");
            if (!orgUid.isEmpty()) {
                batchMessage.setOrgUid(orgUid);
                log.debug("从消息header中补充orgUid: {}", orgUid);
            }
        }

        if (batchMessage.getOrgUid() == null || batchMessage.getOrgUid().isEmpty()) {
            log.error("Member批量导入消息缺少必要的orgUid: 批次{}, 索引{}", 
                    batchMessage.getBatchUid(), batchMessage.getBatchIndex());
            return;
        }

        boolean success = memberBatchMessageProcessor.processBatchImport(batchMessage);

        if (success) {
            log.debug("Member批量导入消息处理成功: 批次{}, 索引{}/{}", 
                    batchUid, batchMessage.getBatchIndex(), batchMessage.getBatchTotal());

            if (Boolean.TRUE.equals(batchMessage.getIsLastBatch())) {
                log.info("Member批量导入完成: 批次{}, 总数{}", batchUid, batchMessage.getBatchTotal());
            }
            return;
        }

        RetryDecision retryDecision = memberBatchMessageProcessor.evaluateRetry(batchMessage);
        if (retryDecision.shouldRetry()) {
            log.info("Member批量导入失败，准备重试: 批次{}, 索引{}, 重试次数{}, 延迟{}ms",
                    batchUid, batchIndex, retryDecision.nextRetryCount(), retryDecision.delayMs());
            try {
                memberBatchMessageService.sendRetryMessage(batchMessage, retryDecision.delayMs());
            } catch (Exception e) {
                log.error("发送重试消息失败: 批次{}, 索引{}, 错误: {}", 
                        batchUid, batchIndex, e.getMessage(), e);
                throw e;
            }
        } else {
            log.error("Member批量导入重试次数达到上限，放弃处理: 批次{}, 索引{}, 重试次数{}",
                    batchUid, batchIndex, retryDecision.nextRetryCount());
        }
    }

    private int getHeaderInt(MessageProperties properties, String key, int defaultValue) {
        Object value = properties.getHeaders().get(key);
        if (value instanceof Integer intValue) {
            return intValue;
        }
        if (value instanceof String stringValue) {
            try {
                return Integer.parseInt(stringValue);
            } catch (NumberFormatException ignored) {
                return defaultValue;
            }
        }
        return defaultValue;
    }

    private String getHeaderString(MessageProperties properties, String key, String defaultValue) {
        Object value = properties.getHeaders().get(key);
        return value != null ? String.valueOf(value) : defaultValue;
    }
}