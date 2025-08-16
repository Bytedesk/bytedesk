/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-10-15 14:57:05
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-18 08:50:24
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.jms;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import com.bytedesk.core.config.BytedeskEventPublisher;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.utils.ApplicationContextHolder;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import jakarta.annotation.PreDestroy;


// https://spring.io/guides/gs/messaging-jms
@Slf4j
@Component
public class JmsArtemisListener {

	// 用于防止重复处理消息的缓存，使用时间戳作为值
	private final ConcurrentHashMap<String, LocalDateTime> processedMessages = new ConcurrentHashMap<>();
	
	// 定时清理任务，每5分钟清理一次过期的消息记录
	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	
	public JmsArtemisListener() {
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
			log.debug("Cleaned up expired message records, current size: {}", processedMessages.size());
		} catch (Exception e) {
			log.error("Error during cleanup of expired messages", e);
		}
	}

	/**
	 * 检查消息是否已被处理过
	 */
	private boolean isMessageProcessed(String messageContent) {
		try {
			// 从JSON中提取消息ID
			MessageProtobuf messageProtobuf = MessageProtobuf.fromJson(messageContent);
			if (messageProtobuf == null || messageProtobuf.getUid() == null) {
				return false;
			}
			
			String messageUid = messageProtobuf.getUid();
			LocalDateTime now = LocalDateTime.now();
			
			// 如果消息已存在且未过期，则认为已处理
			LocalDateTime lastProcessTime = processedMessages.get(messageUid);
			if (lastProcessTime != null && lastProcessTime.isAfter(now.minusMinutes(10))) {
				log.debug("JmsArtemisListener Message already processed recently, skipping: {}", messageUid);
				return true;
			}
			
			// 记录消息处理时间
			processedMessages.put(messageUid, now);
			return false;
		} catch (Exception e) {
			log.warn("Error checking message processed status: {}", e.getMessage());
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
			log.info("JmsArtemisListener scheduler shutdown completed");
		} catch (InterruptedException e) {
			scheduler.shutdownNow();
			Thread.currentThread().interrupt();
			log.warn("JmsArtemisListener scheduler shutdown interrupted");
		}
	}

	// queue point to point
	// @JmsListener(destination = JmsArtemisConstants.QUEUE_STRING_NAME, containerFactory = "jmsArtemisQueueFactory")
	// public void receiveQueueMessage(String json) {
	// 	// log.info("jms receiveQueueMessage string {}", json);
	// 	BytedeskEventPublisher bytedeskEventPublisher = ApplicationContextHolder.getBean(BytedeskEventPublisher.class);
    //     bytedeskEventPublisher.publishMessageJsonEvent(json);
	// }

	// @JmsListener(destination = JmsArtemisConstants.QUEUE_MESSAGE_NAME, containerFactory = "jmsArtemisQueueFactory")
	// public void receiveQueueMessage(MessageProtobuf messageProtobuf) {
	// 	String json = JSON.toJSONString(messageProtobuf);
    //     // log.info("jms receiveQueueMessage messageProtobuf: {}", json);
    //     BytedeskEventPublisher bytedeskEventPublisher = ApplicationContextHolder.getBean(BytedeskEventPublisher.class);
    //     bytedeskEventPublisher.publishMessageJsonEvent(json);
	// }

	// @JmsListener(destination = JmsArtemisConstants.QUEUE_TEST_NAME, containerFactory = "jmsArtemisQueueFactory")
	// public void receiveQueueMessage(JmsArtemisMessage message) {
	// 	// log.info("jms receiveQueueMessage test {}", message);
	// }

	// topic pub sub
	// @TabooJsonFilter(title = "敏感词", action = "JmsArtemisListener")
	@JmsListener(destination = JmsArtemisConsts.TOPIC_STRING_NAME, containerFactory = "jmsArtemisPubsubFactory")
	public void receiveTopicMessage(String json) {
		try {
			// 检查消息是否已处理过，避免重复处理
			if (isMessageProcessed(json)) {
				return;
			}
			
			log.debug("JmsArtemisListener receiveTopicMessage string {}", json);
			BytedeskEventPublisher bytedeskEventPublisher = ApplicationContextHolder.getBean(BytedeskEventPublisher.class);
			bytedeskEventPublisher.publishMessageJsonEvent(json);
		} catch (Exception e) {
			log.error("JmsArtemisListener 处理Topic消息失败: {}", e.getMessage(), e);
			// 不再重新抛出异常，避免阻塞消息队列
		}
	}

	// @JmsListener(destination = JmsArtemisConstants.TOPIC_MESSAGE_NAME, containerFactory = "jmsArtemisPubsubFactory")
	// public void receiveTopicMessage(MessageProtobuf messageProtobuf) {
	// 	String json = JSON.toJSONString(messageProtobuf);
    //     // log.info("jms receiveTopicMessage messageProtobuf: {}", json);
    //     BytedeskEventPublisher bytedeskEventPublisher = ApplicationContextHolder.getBean(BytedeskEventPublisher.class);
    //     bytedeskEventPublisher.publishMessageJsonEvent(json);
	// }

	// @JmsListener(destination = JmsArtemisConstants.TOPIC_TEST_NAME, containerFactory = "jmsArtemisPubsubFactory")
	// public void receiveTopicMessage(JmsArtemisMessage message) {
	// 	// log.info("jms receiveTopicMessage test {}", message);
	// }

}
