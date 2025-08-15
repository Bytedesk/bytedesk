/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-03-11 17:29:51
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-21 14:20:23
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.robot;

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.ai.robot_message.RobotMessageUtils;
import com.bytedesk.ai.springai.service.SpringAIServiceRegistry;
import com.bytedesk.core.constant.LlmConsts;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.MessageService;
import com.bytedesk.core.message.MessageTypeEnum;
import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.core.thread.ThreadProtobuf;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.core.thread.enums.ThreadTypeEnum;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Description;

@Slf4j
@Service
@RequiredArgsConstructor
@Description("Robot Service - AI robot message processing and LLM integration service")
public class RobotService {

    private final SpringAIServiceRegistry springAIServiceRegistry;
    private final ThreadRestService threadRestService;
    private final MessageService messageService;

    // 处理内部成员SSE请求消息
    public void processSseMemberMessage(String messageJson, SseEmitter emitter) {
        log.info("processSseMemberMessage: messageJson: {}", messageJson);
        Assert.notNull(messageJson, "messageJson is null");
        Assert.notNull(emitter, "emitter is null");
        //
        messageJson = messageService.processMessageJson(messageJson);
        MessageProtobuf messageProtobufQuery = MessageProtobuf.fromJson(messageJson);
        MessageTypeEnum messageType = messageProtobufQuery.getType();
        //
        String query = messageProtobufQuery.getContent();
        log.info("robot processSseMessage {}", query);
        ThreadProtobuf threadProtobuf = messageProtobufQuery.getThread();
        if (threadProtobuf == null) {
            throw new RuntimeException("thread is null");
        }
        // 暂时仅支持文字消息类型，其他消息类型，大模型暂不处理。
        if (!messageType.equals(MessageTypeEnum.TEXT) &&
                !messageType.equals(MessageTypeEnum.ROBOT_QUESTION)) {
            log.info("processSseMemberMessage messageType is not TEXT or ROBOT_QUESTION, skip");
            throw new RuntimeException("暂不支持此消息类型");
        }
        //
        String threadTopic = threadProtobuf.getTopic();
        // 仅处理员工/客服消息
        ThreadEntity thread = threadRestService.findFirstByTopic(threadTopic)
                .orElseThrow(() -> new RuntimeException("thread with topic " + threadTopic +
                        " not found"));
        Assert.notNull(thread.getRobot(), "thread agent is null, threadTopic:" + threadTopic);
        // 
        RobotProtobuf robot = RobotProtobuf.fromJson(thread.getRobot());
        if (robot == null) {
            throw new RuntimeException("robot is null, threadTopic:" + threadTopic);
        }
        log.info("processSseMemberMessage thread reply");
        //
        MessageProtobuf messageProtobufReply = RobotMessageUtils.createRobotMessage(threadProtobuf, robot,
                messageProtobufQuery);
        // 处理LLM消息
        processLlmMessage(query, robot, messageProtobufQuery, messageProtobufReply, emitter);
    }

    // 处理访客端SSE请求消息
    public void processSseVisitorMessage(String messageJson, SseEmitter emitter) {
        log.info("processSseVisitorMessage: messageJson: {}", messageJson);
        Assert.notNull(messageJson, "messageJson is null");
        Assert.notNull(emitter, "emitter is null");
        //
        messageJson = messageService.processMessageJson(messageJson);
        //
        MessageProtobuf messageProtobufQuery = MessageProtobuf.fromJson(messageJson);
        MessageTypeEnum messageType = messageProtobufQuery.getType();
        String query = messageProtobufQuery.getContent();
        log.info("processSseVisitorMessage robot processSseMessage {}", query);
        ThreadProtobuf threadProtobuf = messageProtobufQuery.getThread();
        Assert.notNull(threadProtobuf, "thread is null");
        // 暂时仅支持文字消息类型，其他消息类型，大模型暂不处理。
        if (!messageType.equals(MessageTypeEnum.TEXT) &&
                !messageType.equals(MessageTypeEnum.ROBOT_QUESTION)) {
            log.info("processSseMemberMessage messageType is not TEXT or ROBOT_QUESTION, skip");
            throw new RuntimeException("暂不支持此消息类型");
        }
        String threadTopic = threadProtobuf.getTopic();
        // 仅处理访客端消息
        ThreadEntity thread = threadRestService.findFirstByTopic(threadTopic)
                .orElseThrow(() -> new RuntimeException("thread with topic " + threadTopic +
                        " not found"));
        Assert.notNull(thread.getRobot(), "thread agent is null, threadTopic:" + threadTopic);
        //
        RobotProtobuf robot = RobotProtobuf.fromJson(thread.getRobot());
        if (robot == null) {
            throw new RuntimeException("robot is null, threadTopic:" + threadTopic);
        }
        log.info("processSseVisitorMessage thread reply");
        // 机器人回复访客消息
        MessageProtobuf messageProtobufReply = RobotMessageUtils.createRobotMessage(threadProtobuf, robot,
                messageProtobufQuery);
        // 处理LLM消息
        processLlmMessage(query, robot, messageProtobufQuery, messageProtobufReply, emitter);
    }

    // 处理访客端同步请求消息，机器人设置为stream=false的情况，用于微信公众号等平台
    public MessageProtobuf processSyncVisitorMessage(String messageJson) {
        MessageProtobuf messageProtobuf = JSON.parseObject(messageJson, MessageProtobuf.class);
        MessageTypeEnum messageType = messageProtobuf.getType();
        String query = messageProtobuf.getContent();
        log.info("robot processSyncMessage {}", query);
        ThreadProtobuf threadProtobuf = messageProtobuf.getThread();
        if (threadProtobuf == null) {
            throw new RuntimeException("thread is null");
        }
        // 暂时仅支持文字消息类型，其他消息类型，大模型暂不处理。
        if (!messageType.equals(MessageTypeEnum.TEXT)) {
            return null;
        }
        // String threadTopic = threadProtobuf.getTopic();
        if (!threadProtobuf.getType().equals(ThreadTypeEnum.ROBOT)) {
            return null;
        }

        return messageProtobuf;
    }

    // 提取的公共方法，用于处理不同LLM提供商的消息
    private void processLlmMessage(String query, RobotProtobuf robot, MessageProtobuf messageProtobufQuery,
            MessageProtobuf messageProtobufReply, SseEmitter emitter) {
        
        // 获取提供商名称，默认为智谱AI
        String provider = LlmConsts.ZHIPUAI;
        if (robot.getLlm() != null) {
            provider = robot.getLlm().getTextProvider().toLowerCase();
        }
        
        try {
            // 使用SpringAIServiceRegistry获取对应的服务
            springAIServiceRegistry.getServiceByProviderName(provider)
                .sendSseMessage(query, robot, messageProtobufQuery, messageProtobufReply, emitter);
        } catch (IllegalArgumentException e) {
            log.warn("未找到AI服务提供商: {}, 使用默认提供商: {}", provider, LlmConsts.ZHIPUAI);
            // 如果找不到指定的提供商，尝试使用默认的智谱AI
            try {
                springAIServiceRegistry.getServiceByProviderName(LlmConsts.ZHIPUAI)
                    .sendSseMessage(query, robot, messageProtobufQuery, messageProtobufReply, emitter);
            } catch (Exception ex) {
                log.error("使用默认AI服务提供商失败", ex);
                throw new RuntimeException("无法处理AI消息，所有提供商服务均不可用");
            }
        }
    }


}
