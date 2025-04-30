/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-03-11 17:29:51
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-30 18:10:28
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

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.ai.robot_message.RobotMessageUtils;
import com.bytedesk.ai.springai.providers.baidu.SpringAIBaiduService;
import com.bytedesk.ai.springai.providers.dashscope.SpringAIDashscopeService;
import com.bytedesk.ai.springai.providers.deepseek.SpringAIDeepseekService;
import com.bytedesk.ai.springai.providers.gitee.SpringAIGiteeService;
import com.bytedesk.ai.springai.providers.ollama.SpringAIOllamaService;
import com.bytedesk.ai.springai.providers.siliconflow.SpringAISiliconFlowService;
import com.bytedesk.ai.springai.providers.tencent.SpringAITencentService;
import com.bytedesk.ai.springai.providers.volcengine.SpringAIVolcengineService;
import com.bytedesk.ai.springai.providers.zhipuai.SpringAIZhipuaiService;
import com.bytedesk.core.constant.LlmConsts;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.MessageService;
import com.bytedesk.core.message.MessageTypeEnum;
import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.core.thread.ThreadProtobuf;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.core.thread.ThreadTypeEnum;
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
    private final Optional<SpringAISiliconFlowService> springAISiliconFlowService;
    private final Optional<SpringAIGiteeService> springAIGiteeService;
    private final Optional<SpringAITencentService> springAITencentService;
    private final Optional<SpringAIBaiduService> springAIBaiduService;
    private final Optional<SpringAIVolcengineService> springAIVolcengineService;

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
        log.info("processSseMemberMessage thread reply");
        //
        MessageProtobuf messageProtobufReply = RobotMessageUtils.createRobotMessage(thread, threadProtobuf, robot,
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
        log.info("processSseVisitorMessage thread reply");
        // 机器人回复访客消息
        MessageProtobuf messageProtobufReply = RobotMessageUtils.createRobotMessage(thread, threadProtobuf, robot,
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
        
        if (robot.getLlm() == null) {
            springAIZhipuaiService
                    .ifPresent(service -> service.sendSseMessage(query, robot, messageProtobufQuery,
                            messageProtobufReply, emitter));
            return;
        }

        String provider = robot.getLlm().getProvider();
        switch (provider.toLowerCase()) {
            case LlmConsts.OLLAMA:
                springAIOllamaService
                        .ifPresent(service -> service.sendSseMessage(query, robot, messageProtobufQuery,
                                messageProtobufReply, emitter));
                break;
            case LlmConsts.DEEPSEEK:
                springAIDeepseekService
                        .ifPresent(service -> service.sendSseMessage(query, robot, messageProtobufQuery,
                                messageProtobufReply, emitter));
                break;
            case LlmConsts.DASHSCOPE:
                springAIDashscopeService
                        .ifPresent(service -> service.sendSseMessage(query, robot, messageProtobufQuery,
                                messageProtobufReply, emitter));
                break;
            case LlmConsts.ZHIPU:
                springAIZhipuaiService
                        .ifPresent(service -> service.sendSseMessage(query, robot, messageProtobufQuery,
                                messageProtobufReply, emitter));
                break;
            case LlmConsts.SILICONFLOW:
                springAISiliconFlowService
                        .ifPresent(service -> service.sendSseMessage(query, robot, messageProtobufQuery,
                                messageProtobufReply, emitter));
                break;
            case LlmConsts.GITEE:
                springAIGiteeService
                        .ifPresent(service -> service.sendSseMessage(query, robot, messageProtobufQuery,
                                messageProtobufReply, emitter));
                break;
            case LlmConsts.TENCENT:
                springAITencentService
                        .ifPresent(service -> service.sendSseMessage(query, robot, messageProtobufQuery,
                                messageProtobufReply, emitter));
                break;
            case LlmConsts.BAIDU:
                springAIBaiduService
                        .ifPresent(service -> service.sendSseMessage(query, robot, messageProtobufQuery,
                                messageProtobufReply, emitter));
                break;
            case LlmConsts.VOLCENGINE:
                springAIVolcengineService
                        .ifPresent(service -> service.sendSseMessage(query, robot, messageProtobufQuery,
                                messageProtobufReply, emitter));
                break;
            default:
                // 默认使用智谱AI
                springAIZhipuaiService
                        .ifPresent(service -> service.sendSseMessage(query, robot, messageProtobufQuery,
                                messageProtobufReply, emitter));
                break;
        }
    }

}
