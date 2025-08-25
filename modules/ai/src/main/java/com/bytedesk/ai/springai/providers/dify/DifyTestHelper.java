/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-08-23 07:25:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-23 07:25:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.providers.dify;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * Dify 功能测试工具
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "bytedesk.dify.enabled", havingValue = "true", matchIfMissing = false)
public class DifyTestHelper {

    private final DifyChatService difyChatService;

    /**
     * 测试对话功能
     */
    public void testChat() {
        try {
            log.info("开始测试 Dify 对话功能...");
            
            String query = "你好，你能做什么？";
            String user = "test-user-123";
            Map<String, Object> inputs = new HashMap<>();
            
            // 发送第一条消息
            String response1 = difyChatService.sendChatMessage(
                query, null, user, inputs, "blocking"
            );
            
            log.info("第一条消息响应: {}", response1);
            
            // 提取对话ID
            String conversationId = difyChatService.extractConversationId(response1);
            String answer1 = difyChatService.extractMessageContent(response1);
            
            log.info("对话ID: {}", conversationId);
            log.info("回答: {}", answer1);
            
            // 发送第二条消息（继续对话）
            String query2 = "请详细介绍一下你的功能";
            String response2 = difyChatService.sendChatMessage(
                query2, conversationId, user, inputs, "blocking"
            );
            
            String answer2 = difyChatService.extractMessageContent(response2);
            log.info("第二条消息回答: {}", answer2);
            
            // 获取对话历史
            String messages = difyChatService.getConversationMessages(
                conversationId, user, null, 10
            );
            log.info("对话历史: {}", messages);
            
            log.info("Dify 对话功能测试完成！");
            
        } catch (Exception e) {
            log.error("Dify 对话功能测试失败", e);
        }
    }

    /**
     * 测试文本补全功能
     */
    public void testCompletion() {
        try {
            log.info("开始测试 Dify 文本补全功能...");
            
            Map<String, Object> inputs = new HashMap<>();
            inputs.put("text", "今天是个好天气，适合");
            
            String user = "test-user-123";
            
            String response = difyChatService.sendCompletionMessage(
                inputs, user, "blocking"
            );
            
            log.info("文本补全响应: {}", response);
            
            String answer = difyChatService.extractMessageContent(response);
            log.info("补全结果: {}", answer);
            
            log.info("Dify 文本补全功能测试完成！");
            
        } catch (Exception e) {
            log.error("Dify 文本补全功能测试失败", e);
        }
    }

    /**
     * 测试对话管理功能
     */
    public void testConversationManagement() {
        try {
            log.info("开始测试 Dify 对话管理功能...");
            
            String user = "test-user-123";
            
            // 获取对话列表
            String conversations = difyChatService.getConversations(
                user, null, 10, null
            );
            log.info("对话列表: {}", conversations);
            
            log.info("Dify 对话管理功能测试完成！");
            
        } catch (Exception e) {
            log.error("Dify 对话管理功能测试失败", e);
        }
    }

    /**
     * 运行所有测试
     */
    public void runAllTests() {
        log.info("开始运行 Dify 所有功能测试...");
        
        testChat();
        testCompletion();
        testConversationManagement();
        
        log.info("所有 Dify 功能测试完成！");
    }
}
