/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-18 11:45:43
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-27 12:18:26
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.message_leave;

import java.util.List;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.bytedesk.core.message.IMessageSendService;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.service.agent.AgentEntity;
import com.bytedesk.service.agent.AgentRepository;
import com.bytedesk.service.message_leave.event.MessageLeaveCreateEvent;
import com.bytedesk.service.queue_member.QueueMemberRestService;
import com.bytedesk.service.utils.ThreadMessageUtil;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class MessageLeaveEventListener {

	private final AgentRepository agentRepository;
	private final QueueMemberRestService queueMemberRestService;
	private final IMessageSendService messageSendService;

	@EventListener
	public void onMessageLeaveCreate(MessageLeaveCreateEvent event) {
		MessageLeaveEntity messageLeave = event.getMessageLeave();
		if (messageLeave == null || !StringUtils.hasText(messageLeave.getOrgUid())) {
			return;
		}

		List<AgentEntity> agents = agentRepository.findByOrgUidAndDeletedFalse(messageLeave.getOrgUid());
		if (agents == null || agents.isEmpty()) {
			log.debug("message leave notify skipped, no agents found: orgUid={}", messageLeave.getOrgUid());
			return;
		}

		MessageLeaveExtra payload = MessageLeaveExtra.builder()
				.uid(messageLeave.getUid())
				.nickname(messageLeave.getNickname())
				.contact(messageLeave.getContact())
				.content(messageLeave.getContent())
				.type(messageLeave.getType())
				.images(messageLeave.getImages())
				.status(messageLeave.getStatus())
				.build();

		for (AgentEntity agent : agents) {
			try {
				var agentQueueThread = queueMemberRestService.createAgentQueueThread(agent);
				MessageProtobuf message = ThreadMessageUtil.getAgentLeaveMsgSubmitMessage(payload, agentQueueThread);
				messageSendService.sendProtobufMessage(message);
			} catch (Exception e) {
				log.error("Failed to send LEAVE_MSG_SUBMIT notice: orgUid={}, agentUid={}, leaveUid={}, error={}",
						messageLeave.getOrgUid(), agent.getUid(), messageLeave.getUid(), e.getMessage(), e);
			}
		}
	}

}
