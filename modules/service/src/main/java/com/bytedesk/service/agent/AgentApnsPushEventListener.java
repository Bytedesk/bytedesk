/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-06-06 00:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2026-06-06 00:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.agent;

import java.util.Optional;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.bytedesk.core.enums.ChannelEnum;
import com.bytedesk.core.member.MemberEntity;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.enums.MessageTypeEnum;
import com.bytedesk.core.message.event.MessageJsonEvent;
import com.bytedesk.core.push.apns_push.ApnsPushService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.thread.ThreadProtobuf;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class AgentApnsPushEventListener {

    private final ApnsPushService apnsPushService;

    private final AgentRestService agentRestService;

    @EventListener
    public void onMessageJsonEvent(MessageJsonEvent event) {
        try {
            MessageProtobuf message = MessageProtobuf.fromJson(event.getJson());
            if (!shouldPushToAgent(message)) {
                log.debug("Skip agent APNS routing because message is not eligible, messageUid={}",
                        message != null ? message.getUid() : null);
                return;
            }

            ThreadProtobuf thread = message.getThread();
            UserProtobuf sender = message.getUser();
            UserProtobuf agentProtobuf = thread != null ? thread.getAgentProtobuf() : null;
            if (sender == null || agentProtobuf == null || !StringUtils.hasText(agentProtobuf.getUid())) {
                log.debug(
                        "Skip agent APNS routing because sender or thread.agent is empty, messageUid={}, threadUid={}, threadType={}, senderUid={}",
                        message.getUid(),
                        thread != null ? thread.getUid() : null,
                        thread != null ? thread.getType() : null,
                        sender != null ? sender.getUid() : null);
                return;
            }

            Optional<AgentEntity> agentOptional = agentRestService.findByUid(agentProtobuf.getUid());
            if (agentOptional.isEmpty()) {
                log.info("Skip agent APNS routing because agent entity not found, messageUid={}, agentUid={}",
                        message.getUid(),
                        agentProtobuf.getUid());
                return;
            }

            MemberEntity member = agentOptional.get().getMember();
            UserEntity agentUser = member != null ? member.getUser() : null;
            if (agentUser == null || !StringUtils.hasText(agentUser.getUid())) {
                log.info("Skip agent APNS routing because agent.member.user is empty, messageUid={}, agentUid={}",
                        message.getUid(),
                        agentOptional.get().getUid());
                return;
            }
            if (agentUser.getUid().equals(sender.getUid())) {
                log.debug("Skip agent APNS routing because sender is the target agent user, messageUid={}, agentUserUid={}",
                        message.getUid(),
                        agentUser.getUid());
                return;
            }

            log.info(
                    "Route APNS message to agent.user, messageUid={}, threadUid={}, threadType={}, senderUid={}, agentUid={}, agentUserUid={}",
                    message.getUid(),
                    thread != null ? thread.getUid() : null,
                    thread != null ? thread.getType() : null,
                    sender.getUid(),
                    agentOptional.get().getUid(),
                    agentUser.getUid());

            apnsPushService.pushMessageToUser(agentUser.getUid(), message);
        } catch (Exception e) {
            log.error("Failed to process agent APNS push event, json={}", event.getJson(), e);
        }
    }

    private boolean shouldPushToAgent(MessageProtobuf message) {
        if (message == null || message.getType() == null || message.getThread() == null) {
            return false;
        }
        if (ChannelEnum.SYSTEM.equals(message.getChannel())) {
            return false;
        }
        UserProtobuf sender = message.getUser();
        if (sender == null || sender.isAgent()) {
            return false;
        }
        ThreadProtobuf thread = message.getThread();
        if (!thread.isAgentType() && !thread.isWorkgroupType()) {
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
}