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
package com.bytedesk.core.push.apns_push;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.bytedesk.core.enums.ChannelEnum;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.enums.MessageTypeEnum;
import com.bytedesk.core.message.event.MessageJsonEvent;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.thread.ThreadProtobuf;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class ApnsPushEventListener {

	private final ApnsPushService apnsPushService;

	@EventListener
	public void onMessageJsonEvent(MessageJsonEvent event) {
		try {
			MessageProtobuf message = MessageProtobuf.fromJson(event.getJson());
			if (!shouldPush(message)) {
				log.debug("Skip APNS thread-user routing because message type/channel is not pushable, messageUid={}",
						message != null ? message.getUid() : null);
				return;
			}

			ThreadProtobuf thread = message.getThread();
			UserProtobuf sender = message.getUser();
			if (!shouldPushToThreadUser(thread, sender)) {
				log.debug(
						"Skip APNS thread-user routing because current message should not target thread.user, messageUid={}, threadUid={}, threadType={}, senderUid={}",
						message.getUid(),
						thread != null ? thread.getUid() : null,
						thread != null ? thread.getType() : null,
						sender != null ? sender.getUid() : null);
				return;
			}

			UserProtobuf receiver = thread != null ? thread.getUser() : null;
			if (receiver == null || sender == null) {
				log.debug("Skip APNS thread-user routing because sender or receiver is empty, messageUid={}, threadUid={}",
						message.getUid(),
						thread != null ? thread.getUid() : null);
				return;
			}
			if (receiver.getUid() != null && receiver.getUid().equals(sender.getUid())) {
				log.debug("Skip APNS thread-user routing because sender equals receiver, messageUid={}, uid={}",
						message.getUid(),
						receiver.getUid());
				return;
			}

			log.info("Route APNS message to thread.user, messageUid={}, threadUid={}, threadType={}, senderUid={}, receiverUid={}",
					message.getUid(),
					thread != null ? thread.getUid() : null,
					thread != null ? thread.getType() : null,
					sender.getUid(),
					receiver.getUid());

			apnsPushService.pushMessageToUser(receiver.getUid(), message);
		} catch (Exception e) {
			log.error("Failed to process APNS push event, json={}", event.getJson(), e);
		}
	}

	private boolean shouldPush(MessageProtobuf message) {
		if (message == null || message.getType() == null) {
			return false;
		}
		if (ChannelEnum.SYSTEM.equals(message.getChannel())) {
			return false;
		}
		MessageTypeEnum type = message.getType();
		return MessageTypeEnum.TEXT.equals(type)
				|| MessageTypeEnum.IMAGE.equals(type)
				|| MessageTypeEnum.FILE.equals(type)
				|| MessageTypeEnum.DOCUMENT.equals(type)
				|| MessageTypeEnum.AUDIO.equals(type)
				|| MessageTypeEnum.VOICE.equals(type)
				|| MessageTypeEnum.VIDEO.equals(type);
	}

	private boolean shouldPushToThreadUser(ThreadProtobuf thread, UserProtobuf sender) {
		if (thread == null || sender == null) {
			return false;
		}
		if (!thread.isAgentType() && !thread.isWorkgroupType()) {
			return true;
		}
		return sender.isAgent() || !StringUtils.hasText(thread.getAgent());
	}

 
}

