/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-25 09:44:18
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-04 15:50:06
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.topic_subscription;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.quartz.event.QuartzDay0Event;
import com.bytedesk.core.quartz.event.QuartzFiveSecondEvent;
import com.bytedesk.core.quartz.event.QuartzOneMinEvent;
import com.bytedesk.core.socket.mqtt.service.MqttConnectionService;
import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.core.thread.enums.ThreadProcessStatusEnum;
import com.bytedesk.core.topic.TopicUtils;
import com.bytedesk.core.topic_subscription.event.TopicSubscriptionCreateEvent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class TopicSubscriptionEventListener {

	private final TopicSubscriptionRestService topicSubscriptionRestService;

	private final TopicSubscriptionCacheService topicSubscriptionCacheService;

	private final ThreadRestService threadRestService;

	private final MqttConnectionService mqttConnectionService;

	@EventListener
	public void onTopicSubscriptionCreateEvent(TopicSubscriptionCreateEvent event) {
		TopicSubscriptionEntity topicSubscription = event.getTopicSubscription();
		if (topicSubscription == null || StringUtils.hasText(topicSubscription.getUid())) {
			return;
		}

		log.info("topic subscription onTopicSubscriptionCreateEvent: {}", topicSubscription);
		TopicSubscriptionRequest request = TopicSubscriptionRequest.builder()
				.topic(topicSubscription.getTopic())
				.userUid(topicSubscription.getUserUid())
				.build();
		topicSubscriptionCacheService.pushRequest(request);
	}

	@EventListener
	public void onQuartzFiveSecondEvent(QuartzFiveSecondEvent event) {
		List<String> topicRequestList = topicSubscriptionCacheService.getTopicRequestList();
		topicSubscriptionCacheService.getClientIdList();
		if (topicRequestList == null) {
			return;
		}

		List<String> topicRequestsSnapshot = new ArrayList<>(topicRequestList);
		for (String item : topicRequestsSnapshot) {
			TopicSubscriptionRequest topicRequest = JSON.parseObject(item, TopicSubscriptionRequest.class);
			topicSubscriptionRestService.createSystemTopicSubscription(topicRequest);
		}
	}

	@EventListener
	public void onQuartzOneMinEvent(QuartzOneMinEvent event) {
		Set<String> clientIds = mqttConnectionService.getConnectedClientIds();
		if (clientIds == null) {
			return;
		}

		for (String clientId : clientIds) {
			topicSubscriptionCacheService.pushClientId(clientId);
		}
	}

	@EventListener
	public void onQuartzDay0Event(QuartzDay0Event event) {
		log.info("topic subscription onQuartzDay0Event: 开始清理已结束的会话topics");
		List<TopicSubscriptionEntity> subscriptions = topicSubscriptionRestService.findAllTopicSubscriptions();

		for (TopicSubscriptionEntity subscription : subscriptions) {
			if (subscription == null || subscription.isDeleted()) {
				continue;
			}
			String topic = subscription.getTopic();
			String userUid = subscription.getUserUid();
			if (!StringUtils.hasText(topic) || !StringUtils.hasText(userUid)) {
				continue;
			}
			if (topic.startsWith(TopicUtils.TOPIC_ORG_AGENT_PREFIX) || topic.startsWith(TopicUtils.TOPIC_ORG_WORKGROUP_PREFIX)) {
				List<ThreadEntity> relatedThreads = threadRestService.findListByTopic(topic);
				if (relatedThreads.isEmpty()) {
					continue;
				}

				boolean allClosed = true;
				for (ThreadEntity thread : relatedThreads) {
					if (!ThreadProcessStatusEnum.CLOSED.name().equals(thread.getStatus())) {
						allClosed = false;
						break;
					}
				}

				if (allClosed) {
					topicSubscriptionRestService.remove(topic, userUid);
					log.info("成功删除topic: {} 从 userUid: {}", topic, userUid);
				}
			}
		}

		log.info("topic subscription onQuartzDay0Event: 已完成清理已结束的会话topics");
	}


}

