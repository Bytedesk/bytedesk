/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-02-05 12:50:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2026-02-05 12:50:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.mq.rabbitmq;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.bytedesk.core.config.BytedeskEventPublisher;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.mq.jms.JmsArtemisConsts;
import com.bytedesk.core.utils.ApplicationContextHolder;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@ConditionalOnProperty(name = "bytedesk.mq.type", havingValue = "rabbitmq")
public class RabbitMqMessageListener {

    // 用于防止重复处理消息的缓存，使用时间戳作为值
    private final ConcurrentHashMap<String, LocalDateTime> processedMessages = new ConcurrentHashMap<>();

    // 定时清理任务，每5分钟清理一次过期的消息记录
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public RabbitMqMessageListener() {
        // 启动定时清理任务
        scheduler.scheduleAtFixedRate(this::cleanupExpiredMessages, 5, 5, TimeUnit.MINUTES);
    }

    /**
     * 清理超过10分钟的消息记录，防止内存泄漏
     */
    private void cleanupExpiredMessages() {
        try {
            LocalDateTime expiryTime = LocalDateTime.now().minusMinutes(10);
            processedMessages.entrySet().removeIf(entry -> entry.getValue().isBefore(expiryTime));
            log.debug("Cleaned up expired RabbitMQ message records, current size: {}", processedMessages.size());
        } catch (Exception e) {
            log.error("Error during cleanup of RabbitMQ expired messages", e);
        }
    }

    /**
     * 检查消息是否已被处理过
     */
    private boolean isMessageProcessed(String messageContent) {
        try {
            MessageProtobuf messageProtobuf = MessageProtobuf.fromJson(messageContent);
            if (messageProtobuf == null || messageProtobuf.getUid() == null) {
                return false;
            }

            String messageUid = messageProtobuf.getUid();
            LocalDateTime now = LocalDateTime.now();

            LocalDateTime lastProcessTime = processedMessages.get(messageUid);
            if (lastProcessTime != null && lastProcessTime.isAfter(now.minusMinutes(10))) {
                log.debug("RabbitMqMessageListener Message already processed recently, skipping: {}", messageUid);
                return true;
            }

            processedMessages.put(messageUid, now);
            return false;
        } catch (Exception e) {
            log.warn("Error checking RabbitMQ message processed status: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 应用关闭时清理资源
     */
    @PreDestroy
    public void destroy() {
        try {
            scheduler.shutdown();
            if (!scheduler.awaitTermination(30, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
            log.info("RabbitMqMessageListener scheduler shutdown completed");
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
            log.warn("RabbitMqMessageListener scheduler shutdown interrupted");
        }
    }

    @RabbitListener(queues = JmsArtemisConsts.TOPIC_STRING_NAME, containerFactory = "rabbitListenerContainerFactory")
    public void receiveTopicMessage(String json) {
        try {
            if (isMessageProcessed(json)) {
                return;
            }

            BytedeskEventPublisher bytedeskEventPublisher = ApplicationContextHolder.getBean(BytedeskEventPublisher.class);
            bytedeskEventPublisher.publishMessageJsonEvent(json);
        } catch (Exception e) {
            log.error("RabbitMqMessageListener 处理Topic消息失败: {}", e.getMessage(), e);
        }
    }
}
