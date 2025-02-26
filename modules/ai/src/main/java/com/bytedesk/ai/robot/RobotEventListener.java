/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-12 07:17:13
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-26 17:41:14
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
import java.util.Optional;

import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.bytedesk.ai.provider.vendors.ollama.OllamaChatService;
import com.bytedesk.ai.provider.vendors.zhipuai.ZhipuaiChatService;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.enums.ClientEnum;
import com.bytedesk.core.enums.LevelEnum;
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
import com.bytedesk.ai.provider.LlmProviderConsts;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class RobotEventListener {

    private final RobotRestService robotService;
    private final Optional<ZhipuaiChatService> zhipuaiChatService;
    private final Optional<OllamaChatService> ollamaChatService;
    private final UidUtils uidUtils;
    private final ThreadRestService threadService;
    private final IMessageSendService messageSendService;

    @Order(5)
    @EventListener
    public void onOrganizationCreateEvent(OrganizationCreateEvent event) {
        OrganizationEntity organization = (OrganizationEntity) event.getSource();
        String orgUid = organization.getUid();
        log.info("robot - organization created: {}", organization.getName());
        String robotUid = Utils.formatUid(orgUid, BytedeskConsts.DEFAULT_ROBOT_UID);
        // 为每个组织创建一个机器人
        robotService.initDefaultRobot(orgUid, robotUid);
    }

    @EventListener
    public void onMessageJsonEvent(MessageJsonEvent event) {
        // log.info("MessageJsonEvent {}", event.getJson());
        String messageJson = event.getJson();
        //
        processMessage(messageJson);
    }

    private void processMessage(String messageJson) {
        MessageProtobuf messageProtobuf = JSON.parseObject(messageJson, MessageProtobuf.class);
        MessageTypeEnum messageType = messageProtobuf.getType();
        if (messageType.equals(MessageTypeEnum.STREAM)) {
            // ai回答暂不处理
            return;
        }
        String query = messageProtobuf.getContent();
        log.info("robot processMessage {}", query);
        //
        ThreadProtobuf threadProtobuf = messageProtobuf.getThread();
        if (threadProtobuf == null) {
            throw new RuntimeException("thread is null");
        }
        // 仅针对文本类型自动回复
        if (!messageType.equals(MessageTypeEnum.TEXT)) {
            return;
        }
        //
        String threadTopic = threadProtobuf.getTopic();
        if (threadProtobuf.getType().equals(ThreadTypeEnum.LLM)) {
            // 内部大模型对话: 如客服AI助手，内部客服人员使用
            log.info("robot llm threadTopic {}, thread.type {}", threadTopic, threadProtobuf.getType());
            // 机器人消息 org/robot/df_robot_uid/1420995827073219
            String[] splits = threadTopic.split("/");
            if (splits.length < 4) {
                throw new RuntimeException("robot topic format error");
            }
            String robotUid = splits[2];
            if (!StringUtils.hasText(robotUid)) {
                throw new RuntimeException("robotUid is null");
            }
            Optional<RobotEntity> robotOptional = robotService.findByUid(robotUid);
            if (robotOptional.isPresent()) {
                RobotEntity robot = robotOptional.get();

                if (robot.getLlm().isEnabled()) {
                    // 调用大模型
                    if (robot.isKbEnabled()) {
                        // 搜索知识库
                    }

                    // 调用大模型

                } 
                // else if (robot.getFlow().isEnabled()) {
                //     // 调用流程引擎
                // } 
                else if (robot.isKbEnabled()) {
                    // 搜索知识库
                } else {
                    // 默认回复
                }
                // 大模型对话，无知识库
                Optional<ThreadEntity> threadOptional = threadService.findFirstByTopic(threadTopic);
                if (!threadOptional.isPresent()) {
                    throw new RuntimeException("thread with topic " + threadTopic + " not found");
                }
                ThreadEntity thread = threadOptional.get();
                MessageExtra extraObject = JSONObject.parseObject(messageProtobuf.getExtra(), MessageExtra.class);
                //
                String agent = thread.getAgent();
                RobotProtobuf robotProtobuf = JSON.parseObject(agent, RobotProtobuf.class);
                UserProtobuf user = UserProtobuf.builder()
                        .nickname(robotProtobuf.getNickname())
                        .avatar(robotProtobuf.getAvatar())
                        .type(UserTypeEnum.ROBOT.name())
                        .build();
                user.setUid(robotProtobuf.getUid());
                //
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
                // 返回一个输入中消息，让访客端显示输入中
                MessageProtobuf clonedMessage = SerializationUtils.clone(message);
                clonedMessage.setUid(uidUtils.getUid());
                clonedMessage.setType(MessageTypeEnum.PROCESSING);
                messageSendService.sendProtobufMessage(clonedMessage);
                //
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

        // 机器人客服对话，如果会话的agent是机器人，则处理
        log.info("robot robot threadTopic {}, thread.type {}", threadTopic,
                threadProtobuf.getType());
        ThreadEntity thread = threadService.findFirstByTopic(threadTopic)
                .orElseThrow(() -> new RuntimeException("thread with topic " + threadTopic +
                        " not found"));
        // thread.getAgent()为空，则不处理
        if (!StringUtils.hasText(thread.getAgent())) {
            return;
        }
        // 
        UserProtobuf agent = JSON.parseObject(thread.getAgent(), UserProtobuf.class);
        // 当前会话为机器人接待，而且是访客发送的消息
        if (agent.getType().equals(UserTypeEnum.ROBOT.name())
                && messageProtobuf.getUser().getType().equals(UserTypeEnum.VISITOR.name())) {
            // 机器人回复
            log.info("robot thread reply");
            RobotEntity robot = robotService.findByUid(agent.getUid())
                    .orElseThrow(() -> new RuntimeException("robot " + agent.getUid() + " not found"));
            //
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

            // 返回一个输入中消息，让访客端显示输入中
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

            // 知识库
            // if (bytedeskProperties.getJavaAi()) {
            // log.info("robot java ai kb");
            // zhipuaiService.sendWsKbMessage(query, robot.getKbUid(), robot, message);
            // }

            // 通知python ai模块处理回答
            // if (bytedeskProperties.getPythonAi()) {
            // log.info("robot python");
            // messageCache.put(messageUid, message);
            // redisPubsubService.sendQuestionMessage(messageUid, threadTopic,
            // robot.getKbUid(),
            // query);
            // }
        }
    }

    
}
