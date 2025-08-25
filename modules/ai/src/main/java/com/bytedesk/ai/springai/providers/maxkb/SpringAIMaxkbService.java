/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-08-25 14:35:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-25 14:35:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.providers.maxkb;

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
 * SpringAI MaxKB Service - 基于 MaxKB API 的SpringAI服务实现
 * 继承BaseSpringAIService，提供统一的AI服务接口
 */
@Slf4j
@Service
@ConditionalOnProperty(name = "bytedesk.maxkb.enabled", havingValue = "true", matchIfMissing = false)
public class SpringAIMaxkbService extends BaseSpringAIService {

    @Autowired
    private MaxkbChatService maxkbChatService;

    @Override
    protected void processPromptWebsocket(Prompt prompt, RobotProtobuf robot, MessageProtobuf messageProtobufQuery,
            MessageProtobuf messageProtobufReply, String fullPromptContent) {
        try {
            log.info("Processing MaxKB prompt via WebSocket for robot: {}", robot.getUid());
            
            String content = fullPromptContent;
            String applicationId = getApplicationId(robot);
            
            // 使用MaxKB Chat API发送消息
            String response = maxkbChatService.sendMessage(applicationId, content, false);
            
            // 解析响应并发送
            String answer = maxkbChatService.extractMessageContent(response);
            if (answer != null && !answer.isEmpty()) {
                messageProtobufReply.setContent(answer);
                messageSendService.sendProtobufMessage(messageProtobufReply);
                
                // 记录token使用情况
                recordTokenUsage(robot, response, content.length(), answer.length());
            }
            
        } catch (Exception e) {
            log.error("Error processing MaxKB prompt via WebSocket", e);
            messageProtobufReply.setContent("处理请求时发生错误：" + e.getMessage());
            messageSendService.sendProtobufMessage(messageProtobufReply);
        }
    }

    @Override
    protected String processPromptSync(String message, RobotProtobuf robot, String fullPromptContent) {
        try {
            log.info("Processing MaxKB prompt synchronously for robot: {}", robot.getUid());
            
            String content = fullPromptContent;
            String applicationId = getApplicationId(robot);
            
            // 使用MaxKB Chat API发送消息
            String response = maxkbChatService.sendMessage(applicationId, content, false);
            
            // 解析并返回响应
            String answer = maxkbChatService.extractMessageContent(response);
            if (answer != null && !answer.isEmpty()) {
                // 记录token使用情况
                recordTokenUsage(robot, response, content.length(), answer.length());
                return answer;
            }
            
            return "抱歉，没有收到有效的响应。";
            
        } catch (Exception e) {
            log.error("Error processing MaxKB prompt synchronously", e);
            return "处理请求时发生错误：" + e.getMessage();
        }
    }

    @Override
    protected void processPromptSse(Prompt prompt, RobotProtobuf robot, MessageProtobuf messageProtobufQuery,
            MessageProtobuf messageProtobufReply, SseEmitter emitter, String fullPromptContent) {
        try {
            log.info("Processing MaxKB prompt via SSE for robot: {}", robot.getUid());
            
            String content = fullPromptContent;
            String applicationId = getApplicationId(robot);
            
            // 对于SSE，我们使用非流式模式然后发送完整响应
            // MaxKB支持streaming，但这里简化处理
            String response = maxkbChatService.sendMessage(applicationId, content, false);
            
            // 解析响应并通过SSE发送
            String answer = maxkbChatService.extractMessageContent(response);
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
                recordTokenUsage(robot, response, content.length(), answer.length());
            }
            
        } catch (Exception e) {
            log.error("Error processing MaxKB prompt via SSE", e);
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
     * 获取应用ID
     * 可以从robot配置中获取，或者使用默认值
     * 
     * @param robot 机器人信息
     * @return 应用ID
     */
    private String getApplicationId(RobotProtobuf robot) {
        // 这里可以从robot的配置中获取applicationId
        // 暂时使用robot的UID作为applicationId
        return robot.getUid();
    }

    /**
     * 记录token使用情况
     * 
     * @param robot 机器人信息
     * @param response API响应
     * @param inputLength 输入长度
     * @param outputLength 输出长度
     */
    private void recordTokenUsage(RobotProtobuf robot, String response, int inputLength, int outputLength) {
        try {
            // 尝试从MaxKB响应中提取实际的token使用情况
            java.util.Map<String, Object> usage = maxkbChatService.extractUsage(response);
            
            int promptTokens = 0;
            int completionTokens = 0;
            
            if (usage != null && !usage.isEmpty()) {
                promptTokens = (Integer) usage.getOrDefault("prompt_tokens", 0);
                completionTokens = (Integer) usage.getOrDefault("completion_tokens", 0);
            } else {
                // 如果无法从响应中提取，则使用估算
                promptTokens = inputLength / 4;
                completionTokens = outputLength / 4;
            }
            
            // 发布token使用事件
            LlmTokenUsageEvent event = new LlmTokenUsageEvent(
                this, // source
                robot.getOrgUid(), // orgUid
                LlmConsts.MAXKB, // aiProvider
                "maxkb-default", // aiModelType
                promptTokens, // promptTokens
                completionTokens, // completionTokens
                true, // success
                0L, // responseTime (暂时设为0)
                java.math.BigDecimal.ZERO // tokenUnitPrice (暂时设为0)
            );
            
            applicationEventPublisher.publishEvent(event);
            
        } catch (Exception e) {
            log.warn("Failed to record token usage for MaxKB", e);
        }
    }
}
