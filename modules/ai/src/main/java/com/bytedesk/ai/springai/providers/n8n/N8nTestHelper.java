/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-08-23 07:32:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-23 07:32:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.providers.n8n;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * RAGFlow 功能测试工具
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "bytedesk.n8n.enabled", havingValue = "true", matchIfMissing = false)
public class N8nTestHelper {

    private final N8nChatService n8nChatService;

    /**
     * 测试聊天完成功能
     */
    public void testChatCompletion() {
        try {
            log.info("开始测试 RAGFlow 聊天完成功能...");
            
            String chatId = "test-chat-123";
            String model = "model";
            
            // 构建消息列表
            Map<String, Object> message = new HashMap<>();
            message.put("role", "user");
            message.put("content", "你好，这是一个测试！");
            
            List<Map<String, Object>> messages = List.of(message);
            
            // 发送聊天完成请求
            String response = n8nChatService.createChatCompletion(
                chatId, model, messages, false
            );
            
            log.info("聊天完成响应: {}", response);
            
            // 检查是否有错误
            if (n8nChatService.isError(response)) {
                String errorMessage = n8nChatService.getErrorMessage(response);
                int errorCode = n8nChatService.getErrorCode(response);
                log.error("RAGFlow API 错误 [{}]: {}", errorCode, errorMessage);
            } else {
                // 解析响应内容
                String messageContent = n8nChatService.extractMessageContent(response);
                String finishReason = n8nChatService.extractFinishReason(response);
                Map<String, Object> usage = n8nChatService.extractUsage(response);
                
                log.info("消息内容: {}", messageContent);
                log.info("完成原因: {}", finishReason);
                log.info("使用情况: {}", usage);
            }
            
            log.info("RAGFlow 聊天完成功能测试完成！");
            
        } catch (Exception e) {
            log.error("RAGFlow 聊天完成功能测试失败", e);
        }
    }

    /**
     * 测试代理完成功能
     */
    public void testAgentCompletion() {
        try {
            log.info("开始测试 RAGFlow 代理完成功能...");
            
            String agentId = "test-agent-123";
            String model = "model";
            
            // 构建消息列表
            Map<String, Object> message = new HashMap<>();
            message.put("role", "user");
            message.put("content", "请介绍一下你的功能");
            
            List<Map<String, Object>> messages = List.of(message);
            
            // 发送代理完成请求
            String response = n8nChatService.createAgentCompletion(
                agentId, model, messages, false
            );
            
            log.info("代理完成响应: {}", response);
            
            // 检查是否有错误
            if (n8nChatService.isError(response)) {
                String errorMessage = n8nChatService.getErrorMessage(response);
                int errorCode = n8nChatService.getErrorCode(response);
                log.error("RAGFlow API 错误 [{}]: {}", errorCode, errorMessage);
            } else {
                // 解析响应内容
                String messageContent = n8nChatService.extractMessageContent(response);
                String finishReason = n8nChatService.extractFinishReason(response);
                Map<String, Object> usage = n8nChatService.extractUsage(response);
                
                log.info("消息内容: {}", messageContent);
                log.info("完成原因: {}", finishReason);
                log.info("使用情况: {}", usage);
            }
            
            log.info("RAGFlow 代理完成功能测试完成！");
            
        } catch (Exception e) {
            log.error("RAGFlow 代理完成功能测试失败", e);
        }
    }

    /**
     * 测试简单消息功能
     */
    public void testSimpleMessage() {
        try {
            log.info("开始测试 RAGFlow 简单消息功能...");
            
            String chatId = "test-chat-simple";
            String content = "你能帮我写一首诗吗？";
            
            // 发送简单消息
            String response = n8nChatService.sendMessage(chatId, content, false);
            
            log.info("简单消息响应: {}", response);
            
            // 检查是否有错误
            if (n8nChatService.isError(response)) {
                String errorMessage = n8nChatService.getErrorMessage(response);
                int errorCode = n8nChatService.getErrorCode(response);
                log.error("RAGFlow API 错误 [{}]: {}", errorCode, errorMessage);
            } else {
                // 解析响应内容
                String messageContent = n8nChatService.extractMessageContent(response);
                log.info("消息内容: {}", messageContent);
            }
            
            log.info("RAGFlow 简单消息功能测试完成！");
            
        } catch (Exception e) {
            log.error("RAGFlow 简单消息功能测试失败", e);
        }
    }

    /**
     * 测试流式响应功能
     */
    public void testStreamResponse() {
        try {
            log.info("开始测试 RAGFlow 流式响应功能...");
            
            String chatId = "test-chat-stream";
            String content = "请详细介绍一下人工智能的发展历史";
            
            // 发送流式消息
            String response = n8nChatService.sendMessage(chatId, content, true);
            
            log.info("流式响应: {}", response);
            log.info("注意：流式响应的内容通常是分块返回的，实际应用中需要逐块处理");
            
            log.info("RAGFlow 流式响应功能测试完成！");
            
        } catch (Exception e) {
            log.error("RAGFlow 流式响应功能测试失败", e);
        }
    }

    /**
     * 运行所有测试
     */
    public void runAllTests() {
        log.info("开始运行 RAGFlow 所有功能测试...");
        
        testChatCompletion();
        testAgentCompletion();
        testSimpleMessage();
        testStreamResponse();
        
        log.info("所有 RAGFlow 功能测试完成！");
    }
}
