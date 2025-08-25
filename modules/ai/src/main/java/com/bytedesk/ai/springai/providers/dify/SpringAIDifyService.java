/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-08-25 14:30:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-25 14:30:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.providers.dify;

import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.bytedesk.ai.robot.RobotProtobuf;
import com.bytedesk.ai.springai.event.LlmTokenUsageEvent;
import com.bytedesk.ai.springai.service.BaseSpringAIService;
import com.bytedesk.core.constant.LlmConsts;
import com.bytedesk.core.message.MessageProtobuf;

import lombok.extern.slf4j.Slf4j;

/**
 * SpringAI Dify Service - 基于 Dify API 的SpringAI服务实现
 * 继承BaseSpringAIService，提供统一的AI服务接口
 */
@Slf4j
@Service
@ConditionalOnProperty(name = "bytedesk.dify.enabled", havingValue = "true", matchIfMissing = false)
public class SpringAIDifyService extends BaseSpringAIService {

    @Autowired
    private DifyChatService difyChatService;

    @Override
    protected void processPromptWebsocket(Prompt prompt, RobotProtobuf robot, MessageProtobuf messageProtobufQuery,
            MessageProtobuf messageProtobufReply, String fullPromptContent) {
        try {
            log.info("Processing Dify prompt via WebSocket for robot: {}", robot.getUid());
            
            String content = fullPromptContent;
            String conversationId = getConversationId(robot, messageProtobufQuery);
            String userId = messageProtobufQuery.getUser().getUid();
            
            // 使用Dify Chat API发送消息
            String response = difyChatService.sendChatMessage(
                content,
                conversationId,
                userId,
                null, // inputs
                "blocking" // response_mode
            );
            
            // 解析响应并发送
            String answer = difyChatService.extractMessageContent(response);
            if (answer != null && !answer.isEmpty()) {
                messageProtobufReply.setContent(answer);
                messageSendService.sendProtobufMessage(messageProtobufReply);
                
                // 记录token使用情况
                recordTokenUsage(robot, content.length(), answer.length());
            }
            
        } catch (Exception e) {
            log.error("Error processing Dify prompt via WebSocket", e);
            messageProtobufReply.setContent("处理请求时发生错误：" + e.getMessage());
            messageSendService.sendProtobufMessage(messageProtobufReply);
        }
    }

    @Override
    protected String processPromptSync(String message, RobotProtobuf robot, String fullPromptContent) {
        try {
            log.info("Processing Dify prompt synchronously for robot: {}", robot.getUid());
            
            String content = fullPromptContent;
            String conversationId = robot.getUid(); // 使用robot UID作为conversationId
            String userId = "system"; // 同步调用使用系统用户
            
            // 使用Dify Chat API发送消息
            String response = difyChatService.sendChatMessage(
                content,
                conversationId,
                userId,
                null, // inputs
                "blocking" // response_mode
            );
            
            // 解析并返回响应
            String answer = difyChatService.extractMessageContent(response);
            if (answer != null && !answer.isEmpty()) {
                // 记录token使用情况
                recordTokenUsage(robot, content.length(), answer.length());
                return answer;
            }
            
            return "抱歉，没有收到有效的响应。";
            
        } catch (Exception e) {
            log.error("Error processing Dify prompt synchronously", e);
            return "处理请求时发生错误：" + e.getMessage();
        }
    }

    @Override
    protected void processPromptSse(Prompt prompt, RobotProtobuf robot, MessageProtobuf messageProtobufQuery,
            MessageProtobuf messageProtobufReply, SseEmitter emitter, String fullPromptContent) {
        try {
            log.info("Processing Dify prompt via SSE for robot: {}", robot.getUid());
            
            String content = fullPromptContent;
            String conversationId = getConversationId(robot, messageProtobufQuery);
            String userId = messageProtobufQuery.getUser().getUid();
            
            // 对于SSE，我们使用blocking模式然后发送完整响应
            // Dify的streaming模式需要特殊处理，这里简化处理
            String response = difyChatService.sendChatMessage(
                content,
                conversationId,
                userId,
                null, // inputs
                "blocking" // response_mode
            );
            
            // 解析响应并通过SSE发送
            String answer = difyChatService.extractMessageContent(response);
            if (answer != null && !answer.isEmpty()) {
                messageProtobufReply.setContent(answer);
                
                try {
                    emitter.send(answer);
                    emitter.complete();
                } catch (Exception sseError) {
                    log.error("Error sending SSE message", sseError);
                    try {
                        emitter.completeWithError(sseError);
                    } catch (Exception completeError) {
                        log.error("Error completing SSE with error", completeError);
                    }
                }
                
                // 记录token使用情况
                recordTokenUsage(robot, content.length(), answer.length());
            }
            
        } catch (Exception e) {
            log.error("Error processing Dify prompt via SSE", e);
            try {
                emitter.send("处理请求时发生错误：" + e.getMessage());
                emitter.complete();
            } catch (Exception sseError) {
                log.error("Error sending SSE error message", sseError);
                try {
                    emitter.completeWithError(e);
                } catch (Exception completeError) {
                    log.error("Error completing SSE with error", completeError);
                }
            }
        }
    }

    /**
     * 获取或生成会话ID
     * 
     * @param robot 机器人信息
     * @param messageProtobuf 消息信息
     * @return 会话ID
     */
    private String getConversationId(RobotProtobuf robot, MessageProtobuf messageProtobuf) {
        // 可以从messageProtobuf中获取thread信息，或者使用robot信息
        // 这里简化处理，使用thread的UID作为conversationId
        return messageProtobuf.getThread().getUid();
    }

    /**
     * 记录token使用情况
     * 
     * @param robot 机器人信息
     * @param inputLength 输入长度
     * @param outputLength 输出长度
     */
    private void recordTokenUsage(RobotProtobuf robot, int inputLength, int outputLength) {
        try {
            // 简化的token计算，实际应该更精确
            int promptTokens = inputLength / 4; // 粗略估算
            int completionTokens = outputLength / 4; // 粗略估算
            
            // 发布token使用事件
            LlmTokenUsageEvent event = new LlmTokenUsageEvent(
                this, // source
                robot.getOrgUid(), // orgUid
                LlmConsts.DIFY, // aiProvider
                "dify-default", // aiModelType
                promptTokens, // promptTokens
                completionTokens, // completionTokens
                true, // success
                0L, // responseTime (暂时设为0)
                java.math.BigDecimal.ZERO // tokenUnitPrice (暂时设为0)
            );
            
            applicationEventPublisher.publishEvent(event);
            
        } catch (Exception e) {
            log.warn("Failed to record token usage for Dify", e);
        }
    }
}
