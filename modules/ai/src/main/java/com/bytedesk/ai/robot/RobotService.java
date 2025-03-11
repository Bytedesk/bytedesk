/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-03-11 17:29:51
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-11 18:14:29
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
import com.bytedesk.core.message.MessageTypeEnum;
import com.bytedesk.core.rbac.user.UserProtobuf;
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

    public void processSseMessage(String messageJson, SseEmitter emitter) {
        log.info("processPromptSSE: messageJson: {}", messageJson);
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
        // 暂时仅支持文字消息类型，其他消息类型，大模型暂不处理。
        if (!messageType.equals(MessageTypeEnum.TEXT)) {
            return;
        }
        String threadTopic = threadProtobuf.getTopic();
        if (threadProtobuf.getType().equals(ThreadTypeEnum.LLM) || 
            threadProtobuf.getType().equals(ThreadTypeEnum.ROBOT)) {
            log.info("robot robot threadTopic {}, thread.type {}", threadTopic, threadProtobuf.getType());
            processRobotThreadMessage(query, threadTopic, threadProtobuf, messageProtobuf, emitter);
        }
    }

    private void processRobotThreadMessage(String query, String threadTopic, ThreadProtobuf threadProtobuf, MessageProtobuf messageProtobuf, SseEmitter emitter) {
        ThreadEntity thread = threadRestService.findFirstByTopic(threadTopic)
                .orElseThrow(() -> new RuntimeException("thread with topic " + threadTopic +
                        " not found"));
        if (!StringUtils.hasText(thread.getAgent())) {
            return;
        }
        UserProtobuf agent = JSON.parseObject(thread.getAgent(), UserProtobuf.class);
        // && messageProtobuf.getUser().getType().equals(UserTypeEnum.VISITOR.name())
        if (agent.getType().equals(UserTypeEnum.ROBOT.name())) {
            log.info("robot thread reply");
            RobotEntity robot = robotRestService.findByUid(agent.getUid())
                    .orElseThrow(() -> new RuntimeException("robot " + agent.getUid() + " not found"));
            // 
            MessageProtobuf message = RobotMessageUtils.createRobotMessage(thread, threadProtobuf, robot, messageProtobuf);
            // 
            MessageProtobuf clonedMessage = SerializationUtils.clone(message);
            clonedMessage.setUid(uidUtils.getUid());
            clonedMessage.setType(MessageTypeEnum.PROCESSING);
            messageSendService.sendProtobufMessage(clonedMessage);
            //
            if (robot.getLlm().getProvider().equals(LlmProviderConsts.OLLAMA)) {
                springAIOllamaService.ifPresent(service -> service.sendSseMessage(query, robot, message, emitter));
            } else if (robot.getLlm().getProvider().equals(LlmProviderConsts.DEEPSEEK)) {
                springAIDeepseekService.ifPresent(service -> service.sendSseMessage(query, robot, message, emitter));
            } else if (robot.getLlm().getProvider().equals(LlmProviderConsts.DASHSCOPE)) {
                springAIDashscopeService.ifPresent(service -> service.sendSseMessage(query, robot, message, emitter));
            } else if (robot.getLlm().getProvider().equals(LlmProviderConsts.ZHIPU)) {
                springAIZhipuaiService.ifPresent(service -> service.sendSseMessage(query, robot, message, emitter));
            } else {
                // 默认使用智谱AI
                springAIZhipuaiService.ifPresent(service -> service.sendSseMessage(query, robot, message, emitter));
            }
        }
    }
    
}
