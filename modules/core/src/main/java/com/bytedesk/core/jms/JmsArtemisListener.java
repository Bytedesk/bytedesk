/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-10-15 14:57:05
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-15 16:48:46
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

// import com.bytedesk.core.annotation.TabooJsonFilter;
// import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.config.BytedeskEventPublisher;
// import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.utils.ApplicationContextHolder;

import jakarta.jms.JMSException;
import lombok.extern.slf4j.Slf4j;


// https://spring.io/guides/gs/messaging-jms
@Slf4j
@Component
public class JmsArtemisListener {

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
	@JmsListener(destination = JmsArtemisConstants.TOPIC_STRING_NAME, containerFactory = "jmsArtemisPubsubFactory")
	public void receiveTopicMessage(String json, jakarta.jms.Message message) throws JMSException {
		try {
			// log.info("jms receiveTopicMessage string {}", json);
			BytedeskEventPublisher bytedeskEventPublisher = ApplicationContextHolder.getBean(BytedeskEventPublisher.class);
			bytedeskEventPublisher.publishMessageJsonEvent(json);
			// 处理成功后，显式确认消息已被消费
			message.acknowledge();
		} catch (Exception e) {
			log.error("处理Topic消息失败: {}", e.getMessage(), e);
			throw e; // 重新抛出异常，让错误处理器处理
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
