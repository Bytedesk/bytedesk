/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-12 07:17:13
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-27 15:53:24
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.robot;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.SerializationUtils;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.bytedesk.ai.provider.vendors.ollama.OllamaChatService;
import com.bytedesk.ai.provider.vendors.zhipuai.ZhipuaiChatService;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.enums.ClientEnum;
import com.bytedesk.core.message.IMessageSendService;
import com.bytedesk.core.message.MessageExtra;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.MessageStatusEnum;
import com.bytedesk.core.message.MessageTypeEnum;
import com.bytedesk.core.message.MessageUtils;
import com.bytedesk.core.message.event.MessageJsonEvent;
import com.bytedesk.core.rbac.organization.OrganizationEntity;
import com.bytedesk.core.rbac.organization.OrganizationCreateEvent;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.rbac.user.UserTypeEnum;
import com.bytedesk.core.thread.ThreadProtobuf;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.core.thread.ThreadTypeEnum;
import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.utils.Utils;
import com.bytedesk.kbase.faq.FaqEntity;
import com.bytedesk.kbase.faq.event.FaqCreateEvent;

import jakarta.annotation.PostConstruct;

import com.bytedesk.ai.provider.LlmProviderConsts;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;

@Slf4j
@Component
@AllArgsConstructor
public class RobotEventListener {

    private final RobotRestService robotRestService;
    private final Optional<ZhipuaiChatService> zhipuaiChatService;
    private final Optional<OllamaChatService> ollamaChatService;
    private final UidUtils uidUtils;
    private final ThreadRestService threadService;
    private final IMessageSendService messageSendService;
    private final RobotFaqProcessor robotFaqProcessor;

    @Order(5)
    @EventListener
    public void onOrganizationCreateEvent(OrganizationCreateEvent event) {
        OrganizationEntity organization = (OrganizationEntity) event.getSource();
        String orgUid = organization.getUid();
        log.info("robot - organization created: {}", organization.getName());
        String robotUid = Utils.formatUid(orgUid, BytedeskConsts.DEFAULT_ROBOT_UID);
        robotRestService.initDefaultRobot(orgUid, robotUid);
    }

    @EventListener
    public void onFaqCreateEvent(FaqCreateEvent event) {
        robotFaqProcessor.addFaqToQueue(event.getFaq());
    }

    @EventListener
    public void onMessageJsonEvent(MessageJsonEvent event) {
        processMessage(event.getJson());
    }

    private void processMessage(String messageJson) {
        MessageProtobuf messageProtobuf = JSON.parseObject(messageJson, MessageProtobuf.class);
        MessageTypeEnum messageType = messageProtobuf.getType();
        if (messageType.equals(MessageTypeEnum.STREAM)) {
            return;
        }
        String query = messageProtobuf.getContent();
        log.info("robot processMessage {}", query);
        ThreadProtobuf threadProtobuf = messageProtobuf.getThread();
        if (threadProtobuf == null) {
            throw new RuntimeException("thread is null");
        }
        if (!messageType.equals(MessageTypeEnum.TEXT)) {
            return;
        }
        String threadTopic = threadProtobuf.getTopic();
        if (threadProtobuf.getType().equals(ThreadTypeEnum.LLM)) {
            log.info("robot llm threadTopic {}, thread.type {}", threadTopic, threadProtobuf.getType());
            String[] splits = threadTopic.split("/");
            if (splits.length < 4) {
                throw new RuntimeException("robot topic format error");
            }
            String robotUid = splits[2];
            if (!StringUtils.hasText(robotUid)) {
                throw new RuntimeException("robotUid is null");
            }
            Optional<RobotEntity> robotOptional = robotRestService.findByUid(robotUid);
            if (robotOptional.isPresent()) {
                RobotEntity robot = robotOptional.get();

                if (robot.getLlm().isEnabled()) {
                    if (robot.isKbEnabled()) {
                    }
                } else if (robot.isKbEnabled()) {
                } else {
                }
                Optional<ThreadEntity> threadOptional = threadService.findFirstByTopic(threadTopic);
                if (!threadOptional.isPresent()) {
                    throw new RuntimeException("thread with topic " + threadTopic + " not found");
                }
                ThreadEntity thread = threadOptional.get();
                MessageExtra extraObject = JSONObject.parseObject(messageProtobuf.getExtra(), MessageExtra.class);
                String agent = thread.getAgent();
                RobotProtobuf robotProtobuf = JSON.parseObject(agent, RobotProtobuf.class);
                UserProtobuf user = UserProtobuf.builder()
                        .nickname(robotProtobuf.getNickname())
                        .avatar(robotProtobuf.getAvatar())
                        .type(UserTypeEnum.ROBOT.name())
                        .build();
                user.setUid(robotProtobuf.getUid());
                String messageUid = uidUtils.getUid();
                MessageProtobuf message = MessageProtobuf.builder()
                        .uid(messageUid)
                        .status(MessageStatusEnum.SUCCESS)
                        .thread(threadProtobuf)
                        .user(user)
                        .client(ClientEnum.ROBOT)
                        .extra(JSONObject.toJSONString(extraObject))
                        .createdAt(LocalDateTime.now())
                        .build();
                MessageProtobuf clonedMessage = SerializationUtils.clone(message);
                clonedMessage.setUid(uidUtils.getUid());
                clonedMessage.setType(MessageTypeEnum.PROCESSING);
                messageSendService.sendProtobufMessage(clonedMessage);
                if (robotProtobuf.getLlm().getProvider().equals(LlmProviderConsts.OLLAMA)) {
                    ollamaChatService.ifPresent(service -> 
                        service.sendWsMessage(query, robotProtobuf.getLlm(), message));
                } else {
                    zhipuaiChatService.ifPresent(service -> 
                        service.sendWsMessage(query, robotProtobuf.getLlm(), message));
                }
            } else {
                log.error("robot not found");
            }

            return;
        }

        log.info("robot robot threadTopic {}, thread.type {}", threadTopic,
                threadProtobuf.getType());
        ThreadEntity thread = threadService.findFirstByTopic(threadTopic)
                .orElseThrow(() -> new RuntimeException("thread with topic " + threadTopic +
                        " not found"));
        if (!StringUtils.hasText(thread.getAgent())) {
            return;
        }
        UserProtobuf agent = JSON.parseObject(thread.getAgent(), UserProtobuf.class);
        if (agent.getType().equals(UserTypeEnum.ROBOT.name())
                && messageProtobuf.getUser().getType().equals(UserTypeEnum.VISITOR.name())) {
            log.info("robot thread reply");
            RobotEntity robot = robotRestService.findByUid(agent.getUid())
                    .orElseThrow(() -> new RuntimeException("robot " + agent.getUid() + " not found"));
            MessageExtra extra = MessageUtils.getMessageExtra(robot.getOrgUid());
            String messageUid = uidUtils.getUid();
            MessageProtobuf message = MessageProtobuf.builder()
                    .uid(messageUid)
                    .status(MessageStatusEnum.SUCCESS)
                    .thread(threadProtobuf)
                    .user(agent)
                    .client(ClientEnum.ROBOT)
                    .extra(JSONObject.toJSONString(extra))
                    .createdAt(LocalDateTime.now())
                    .build();

            MessageProtobuf clonedMessage = SerializationUtils.clone(message);
            clonedMessage.setUid(uidUtils.getUid());
            clonedMessage.setType(MessageTypeEnum.PROCESSING);
            messageSendService.sendProtobufMessage(clonedMessage);

            if (robot.getLlm().getProvider().equals(LlmProviderConsts.OLLAMA)) {
                ollamaChatService.ifPresent(service -> 
                    service.sendWsKbMessage(query, robot, message));
            } else {
                zhipuaiChatService.ifPresent(service -> 
                    service.sendWsKbMessage(query, robot, message));
            }
        }
    }

}

