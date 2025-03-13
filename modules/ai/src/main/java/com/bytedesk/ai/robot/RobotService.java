/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-03-11 17:29:51
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-13 12:16:15
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.robot;

import java.util.Optional;

// import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.SerializationUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.ai.provider.LlmProviderConsts;
import com.bytedesk.ai.robot_message.RobotMessageUtils;
import com.bytedesk.ai.springai.dashscope.SpringAIDashscopeService;
import com.bytedesk.ai.springai.deepseek.SpringAIDeepseekService;
import com.bytedesk.ai.springai.ollama.SpringAIOllamaService;
import com.bytedesk.core.message.IMessageSendService;
import com.bytedesk.ai.springai.zhipuai.SpringAIZhipuaiService;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.MessageService;
import com.bytedesk.core.message.MessageTypeEnum;
import com.bytedesk.core.rbac.user.UserTypeEnum;
import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.core.thread.ThreadProtobuf;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.core.thread.ThreadTypeEnum;
import com.bytedesk.core.uid.UidUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RobotService {

    private final Optional<SpringAIDeepseekService> springAIDeepseekService;
    private final Optional<SpringAIZhipuaiService> springAIZhipuaiService;
    private final Optional<SpringAIDashscopeService> springAIDashscopeService;
    private final Optional<SpringAIOllamaService> springAIOllamaService;

    private final UidUtils uidUtils;
    private final ThreadRestService threadRestService;
    private final IMessageSendService messageSendService;
    private final RobotRestService robotRestService;
    private final MessageService messageService;

    // 处理员工/客服SSE请求消息
    public void processSseMemberMessage(String messageJson, SseEmitter emitter) {
        log.info("processSseMemberMessage: messageJson: {}", messageJson);
        Assert.notNull(messageJson, "messageJson is null");
        Assert.notNull(emitter, "emitter is null");
        //
        messageJson = messageService.processMessageJson(messageJson);
        MessageProtobuf messageProtobuf = JSON.parseObject(messageJson, MessageProtobuf.class);
        MessageTypeEnum messageType = messageProtobuf.getType();
        // if (messageType.equals(MessageTypeEnum.STREAM)) {
        // return;
        // }
        String query = messageProtobuf.getContent();
        log.info("robot processSseMessage {}", query);
        ThreadProtobuf threadProtobuf = messageProtobuf.getThread();
        if (threadProtobuf == null) {
            throw new RuntimeException("thread is null");
        }
        // 暂时仅支持文字消息类型，其他消息类型，大模型暂不处理。
        if (!messageType.equals(MessageTypeEnum.TEXT)) {
            return;
        }
        String threadTopic = threadProtobuf.getTopic();
        // if (!threadProtobuf.getType().equals(ThreadTypeEnum.LLM)) {
        //     return;
        // }
        // 仅处理员工/客服消息
        ThreadEntity thread = threadRestService.findFirstByTopic(threadTopic)
                .orElseThrow(() -> new RuntimeException("thread with topic " + threadTopic +
                        " not found"));
        if (!StringUtils.hasText(thread.getAgent())) {
            return;
        }
        RobotProtobuf robot = JSON.parseObject(thread.getAgent(), RobotProtobuf.class);
        // UserProtobuf agent = JSON.parseObject(thread.getAgent(), UserProtobuf.class);
        // && messageProtobuf.getUser().getType().equals(UserTypeEnum.VISITOR.name())
        // if (robot.getType().equals(UserTypeEnum.ROBOT.name())) {
        log.info("processSseMemberMessage thread reply");
        // RobotEntity robot = robotRestService.findByUid(agent.getUid())
        // .orElseThrow(() -> new RuntimeException("robot " + agent.getUid() + " not
        // found"));
        //
        MessageProtobuf message = RobotMessageUtils.createMemberMessage(thread, threadProtobuf, robot,
                messageProtobuf);
        //
        // MessageProtobuf clonedMessage = SerializationUtils.clone(message);
        // clonedMessage.setUid(uidUtils.getUid());
        // clonedMessage.setType(MessageTypeEnum.PROCESSING);
        // messageSendService.sendProtobufMessage(clonedMessage);
        //
        if (robot.getLlm().getProvider().equalsIgnoreCase(LlmProviderConsts.OLLAMA)) {
            springAIOllamaService
                    .ifPresent(service -> service.sendSseMemberMessage(query, robot, message, emitter));
        } else if (robot.getLlm().getProvider().equalsIgnoreCase(LlmProviderConsts.DEEPSEEK)) {
            springAIDeepseekService
                    .ifPresent(service -> service.sendSseMemberMessage(query, robot, message, emitter));
        } else if (robot.getLlm().getProvider().equalsIgnoreCase(LlmProviderConsts.DASHSCOPE)) {
            springAIDashscopeService
                    .ifPresent(service -> service.sendSseMemberMessage(query, robot, message, emitter));
        } else if (robot.getLlm().getProvider().equalsIgnoreCase(LlmProviderConsts.ZHIPU)) {
            springAIZhipuaiService
                    .ifPresent(service -> service.sendSseMemberMessage(query, robot, message, emitter));
        } else {
            springAIZhipuaiService
                    .ifPresent(service -> service.sendSseMemberMessage(query, robot, message, emitter));
        }
        // }
    }


    // 处理访客端SSE请求消息
    public void processSseVisitorMessage(String messageJson, SseEmitter emitter) {
        log.info("processSseVisitorMessage: messageJson: {}", messageJson);
        Assert.notNull(messageJson, "messageJson is null");
        Assert.notNull(emitter, "emitter is null");
        //
        messageJson = messageService.processMessageJson(messageJson);
        // 
        MessageProtobuf messageProtobuf = JSON.parseObject(messageJson, MessageProtobuf.class);
        MessageTypeEnum messageType = messageProtobuf.getType();
        // if (messageType.equals(MessageTypeEnum.STREAM)) {
        // return;
        // }
        String query = messageProtobuf.getContent();
        log.info("processSseVisitorMessage robot processSseMessage {}", query);
        ThreadProtobuf threadProtobuf = messageProtobuf.getThread();
        if (threadProtobuf == null) {
            throw new RuntimeException("thread is null");
        }
        // 暂时仅支持文字消息类型，其他消息类型，大模型暂不处理。
        if (!messageType.equals(MessageTypeEnum.TEXT)) {
            return;
        }
        String threadTopic = threadProtobuf.getTopic();
        // 仅处理访客端消息
        ThreadEntity thread = threadRestService.findFirstByTopic(threadTopic)
                .orElseThrow(() -> new RuntimeException("thread with topic " + threadTopic +
                        " not found"));
        if (!StringUtils.hasText(thread.getAgent())) {
            return;
        }
        RobotProtobuf agent = JSON.parseObject(thread.getAgent(), RobotProtobuf.class);
        // UserProtobuf agent = JSON.parseObject(thread.getAgent(), UserProtobuf.class);
        // && messageProtobuf.getUser().getType().equals(UserTypeEnum.VISITOR.name())
        if (agent.getType().equals(UserTypeEnum.ROBOT.name())) {
            log.info("processSseVisitorMessage thread reply");
            RobotEntity robot = robotRestService.findByUid(agent.getUid())
                    .orElseThrow(() -> new RuntimeException("robot " + agent.getUid() + " not found"));
            //
            MessageProtobuf message = RobotMessageUtils.createRobotMessage(thread, threadProtobuf, robot,
                    messageProtobuf);
            //
            // MessageProtobuf clonedMessage = SerializationUtils.clone(message);
            // clonedMessage.setUid(uidUtils.getUid());
            // clonedMessage.setType(MessageTypeEnum.PROCESSING);
            // messageSendService.sendProtobufMessage(clonedMessage);
            //
            if (robot.getLlm().getProvider().equalsIgnoreCase(LlmProviderConsts.OLLAMA)) {
                springAIOllamaService
                        .ifPresent(service -> service.sendSseVisitorMessage(query, robot, message, emitter));
            } else if (robot.getLlm().getProvider().equalsIgnoreCase(LlmProviderConsts.DEEPSEEK)) {
                springAIDeepseekService
                        .ifPresent(service -> service.sendSseVisitorMessage(query, robot, message, emitter));
            } else if (robot.getLlm().getProvider().equalsIgnoreCase(LlmProviderConsts.DASHSCOPE)) {
                springAIDashscopeService
                        .ifPresent(service -> service.sendSseVisitorMessage(query, robot, message, emitter));
            } else if (robot.getLlm().getProvider().equalsIgnoreCase(LlmProviderConsts.ZHIPU)) {
                springAIZhipuaiService
                        .ifPresent(service -> service.sendSseVisitorMessage(query, robot, message, emitter));
            } else {
                springAIZhipuaiService
                        .ifPresent(service -> service.sendSseVisitorMessage(query, robot, message, emitter));
            }
        }
    }

    
    // 处理访客端同步请求消息，用于微信公众号等平台
    public void processSyncVisitorMessage(String messageJson) {
        MessageProtobuf messageProtobuf = JSON.parseObject(messageJson, MessageProtobuf.class);
        MessageTypeEnum messageType = messageProtobuf.getType();
        if (messageType.equals(MessageTypeEnum.STREAM)) {
            return;
        }
        String query = messageProtobuf.getContent();
        log.info("robot processSyncMessage {}", query);
        ThreadProtobuf threadProtobuf = messageProtobuf.getThread();
        if (threadProtobuf == null) {
            throw new RuntimeException("thread is null");
        }
        // 暂时仅支持文字消息类型，其他消息类型，大模型暂不处理。
        if (!messageType.equals(MessageTypeEnum.TEXT)) {
            return;
        }
        // String threadTopic = threadProtobuf.getTopic();
        if (!threadProtobuf.getType().equals(ThreadTypeEnum.ROBOT)) {
            return;
        }
    }

    // 处理websocket请求消息
    public void processWebsocketMessage(String messageJson) {
        MessageProtobuf messageProtobuf = JSON.parseObject(messageJson, MessageProtobuf.class);
        MessageTypeEnum messageType = messageProtobuf.getType();
        if (messageType.equals(MessageTypeEnum.STREAM)) {
            return;
        }
        String query = messageProtobuf.getContent();
        log.info("robot processWebsocketMessage {}", query);
        ThreadProtobuf threadProtobuf = messageProtobuf.getThread();
        if (threadProtobuf == null) {
            throw new RuntimeException("thread is null");
        }
        // 暂时仅支持文字消息类型，其他消息类型，大模型暂不处理。
        if (!messageType.equals(MessageTypeEnum.TEXT)) {
            return;
        }
        String threadTopic = threadProtobuf.getTopic();
        if (threadProtobuf.getType().equals(ThreadTypeEnum.LLM)) {
            log.info("robot threadTopic {}, thread.type {}", threadTopic, threadProtobuf.getType());
            processRobotThreadWebsocketMessage(query, threadTopic, threadProtobuf, messageProtobuf);
        }
    }

    private void processRobotThreadWebsocketMessage(String query, String threadTopic, ThreadProtobuf threadProtobuf,
            MessageProtobuf messageProtobuf) {
        ThreadEntity thread = threadRestService.findFirstByTopic(threadTopic)
                .orElseThrow(() -> new RuntimeException("thread with topic " + threadTopic +
                        " not found"));
        if (!StringUtils.hasText(thread.getAgent())) {
            return;
        }
        // 实际上是
        RobotProtobuf agent = JSON.parseObject(thread.getAgent(), RobotProtobuf.class);
        // UserProtobuf agent = JSON.parseObject(thread.getAgent(), UserProtobuf.class);
        // && messageProtobuf.getUser().getType().equals(UserTypeEnum.VISITOR.name())
        if (agent.getType().equals(UserTypeEnum.ROBOT.name())) {
            log.info("processRobotThreadWebsocketMessage thread reply");
            RobotEntity robot = robotRestService.findByUid(agent.getUid())
                    .orElseThrow(() -> new RuntimeException("robot " + agent.getUid() + " not found"));
            //
            MessageProtobuf message = RobotMessageUtils.createRobotMessage(thread, threadProtobuf, robot,
                    messageProtobuf);
            //
            MessageProtobuf clonedMessage = SerializationUtils.clone(message);
            clonedMessage.setUid(uidUtils.getUid());
            clonedMessage.setType(MessageTypeEnum.PROCESSING);
            messageSendService.sendProtobufMessage(clonedMessage);
            //
            if (robot.getLlm().getProvider().equalsIgnoreCase(LlmProviderConsts.OLLAMA)) {
                springAIOllamaService.ifPresent(service -> service.sendWebsocketMessage(query, robot, message));
            } else if (robot.getLlm().getProvider().equalsIgnoreCase(LlmProviderConsts.DEEPSEEK)) {
                springAIDeepseekService.ifPresent(service -> service.sendWebsocketMessage(query, robot, message));
            } else if (robot.getLlm().getProvider().equalsIgnoreCase(LlmProviderConsts.DASHSCOPE)) {
                springAIDashscopeService.ifPresent(service -> service.sendWebsocketMessage(query, robot, message));
            } else if (robot.getLlm().getProvider().equalsIgnoreCase(LlmProviderConsts.ZHIPU)) {
                springAIZhipuaiService.ifPresent(service -> service.sendWebsocketMessage(query, robot, message));
            } else {
                springAIZhipuaiService.ifPresent(service -> service.sendWebsocketMessage(query, robot, message));
            }
        }
    }

}
