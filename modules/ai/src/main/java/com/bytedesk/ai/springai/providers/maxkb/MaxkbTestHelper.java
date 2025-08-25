/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-08-23 07:40:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-23 07:40:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.providers.maxkb;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MaxKB 测试工具类
 * 用于验证 MaxKB API 集成功能
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "bytedesk.maxkb.enabled", havingValue = "true", matchIfMissing = false)
public class MaxkbTestHelper {

    private final MaxkbChatService maxkbChatService;

    // 测试用的应用ID（需要替换为实际的应用ID）
    private static final String TEST_APPLICATION_ID = "xxxxxxxx-8c56-11ef-a99e-0242ac140003";

    /**
     * 运行所有测试
     */
    public void runAllTests() {
        log.info("开始运行 MaxKB 集成测试...");
        
        try {
            testChatCompletion();
            testSimpleMessage();
            testMessageWithSystemRole();
            testErrorHandling();
            testResponseParsing();
            
            log.info("MaxKB 集成测试全部完成！");
        } catch (Exception e) {
            log.error("MaxKB 集成测试失败", e);
        }
    }

    /**
     * 测试聊天完成功能
     */
    public void testChatCompletion() {
        log.info("测试聊天完成功能...");
        
        try {
            // 构建消息列表
            Map<String, Object> systemMessage = new HashMap<>();
            systemMessage.put("role", "system");
            systemMessage.put("content", "你是 MaxKB 知识库问答系统的智能小助手。");
            
            Map<String, Object> userMessage = new HashMap<>();
            userMessage.put("role", "user");
            userMessage.put("content", "MaxKB 是什么？");
            
            List<Map<String, Object>> messages = List.of(systemMessage, userMessage);
            
            // 测试非流式响应
            String response = maxkbChatService.createChatCompletion(
                TEST_APPLICATION_ID, "gpt-3.5-turbo", messages, false);
            
            if (maxkbChatService.isError(response)) {
                log.error("聊天完成测试失败: {}", maxkbChatService.getErrorMessage(response));
            } else {
                String content = maxkbChatService.extractMessageContent(response);
                log.info("聊天完成测试成功，回复: {}", content);
            }
            
        } catch (Exception e) {
            log.error("聊天完成测试异常", e);
        }
    }

    /**
     * 测试简单消息功能
     */
    public void testSimpleMessage() {
        log.info("测试简单消息功能...");
        
        try {
            String response = maxkbChatService.sendMessage(
                TEST_APPLICATION_ID, "请介绍一下 MaxKB 的主要功能", false);
            
            if (maxkbChatService.isError(response)) {
                log.error("简单消息测试失败: {}", maxkbChatService.getErrorMessage(response));
            } else {
                String content = maxkbChatService.extractMessageContent(response);
                String finishReason = maxkbChatService.extractFinishReason(response);
                Map<String, Object> usage = maxkbChatService.extractUsage(response);
                
                log.info("简单消息测试成功");
                log.info("回复内容: {}", content);
                log.info("完成原因: {}", finishReason);
                log.info("使用情况: {}", usage);
            }
            
        } catch (Exception e) {
            log.error("简单消息测试异常", e);
        }
    }

    /**
     * 测试带系统角色的消息功能
     */
    public void testMessageWithSystemRole() {
        log.info("测试带系统角色的消息功能...");
        
        try {
            String systemRole = "你是杭州飞致云信息科技有限公司旗下产品 MaxKB 知识库问答系统的智能小助手，你的工作是帮助 MaxKB 用户解答使用中遇到的问题，用户找你回答问题时，你要把主题放在 MaxKB 知识库问答系统身上。";
            
            String response = maxkbChatService.sendMessageWithSystemRole(
                TEST_APPLICATION_ID, systemRole, "MaxKB 有哪些优势？", false);
            
            if (maxkbChatService.isError(response)) {
                log.error("系统角色消息测试失败: {}", maxkbChatService.getErrorMessage(response));
            } else {
                String content = maxkbChatService.extractMessageContent(response);
                log.info("系统角色消息测试成功，回复: {}", content);
            }
            
        } catch (Exception e) {
            log.error("系统角色消息测试异常", e);
        }
    }

    /**
     * 测试流式响应功能
     */
    public void testStreamResponse() {
        log.info("测试流式响应功能...");
        
        try {
            String response = maxkbChatService.sendMessage(
                TEST_APPLICATION_ID, "请详细介绍 MaxKB 的安装和部署方法", true);
            
            if (maxkbChatService.isError(response)) {
                log.error("流式响应测试失败: {}", maxkbChatService.getErrorMessage(response));
            } else {
                log.info("流式响应测试成功");
                log.info("响应内容: {}", response);
            }
            
        } catch (Exception e) {
            log.error("流式响应测试异常", e);
        }
    }

    /**
     * 测试错误处理功能
     */
    public void testErrorHandling() {
        log.info("测试错误处理功能...");
        
        try {
            // 使用无效的应用ID测试错误处理
            String response = maxkbChatService.sendMessage(
                "invalid-application-id", "测试错误处理", false);
            
            if (maxkbChatService.isError(response)) {
                String errorMessage = maxkbChatService.getErrorMessage(response);
                int errorCode = maxkbChatService.getErrorCode(response);
                log.info("错误处理测试成功 - 错误代码: {}, 错误信息: {}", errorCode, errorMessage);
            } else {
                log.warn("错误处理测试：期望返回错误，但实际成功");
            }
            
        } catch (Exception e) {
            log.info("错误处理测试成功 - 捕获到预期异常: {}", e.getMessage());
        }
    }

    /**
     * 测试响应解析功能
     */
    public void testResponseParsing() {
        log.info("测试响应解析功能...");
        
        try {
            String response = maxkbChatService.sendMessage(
                TEST_APPLICATION_ID, "测试响应解析", false);
            
            if (!maxkbChatService.isError(response)) {
                // 测试各种解析方法
                String content = maxkbChatService.extractMessageContent(response);
                String finishReason = maxkbChatService.extractFinishReason(response);
                Map<String, Object> usage = maxkbChatService.extractUsage(response);
                String model = maxkbChatService.extractModel(response);
                String id = maxkbChatService.extractId(response);
                String object = maxkbChatService.extractObject(response);
                Long created = maxkbChatService.extractCreated(response);
                
                log.info("响应解析测试成功:");
                log.info("- 内容: {}", content);
                log.info("- 完成原因: {}", finishReason);
                log.info("- 使用情况: {}", usage);
                log.info("- 模型: {}", model);
                log.info("- ID: {}", id);
                log.info("- 对象类型: {}", object);
                log.info("- 创建时间: {}", created);
            } else {
                log.error("响应解析测试失败: {}", maxkbChatService.getErrorMessage(response));
            }
            
        } catch (Exception e) {
            log.error("响应解析测试异常", e);
        }
    }

    /**
     * 测试不同模型
     */
    public void testDifferentModels() {
        log.info("测试不同模型...");
        
        String[] models = {"gpt-3.5-turbo", "gpt-4", "claude-3"};
        
        for (String model : models) {
            try {
                Map<String, Object> message = new HashMap<>();
                message.put("role", "user");
                message.put("content", "你好，测试模型: " + model);
                
                List<Map<String, Object>> messages = List.of(message);
                
                String response = maxkbChatService.createChatCompletion(
                    TEST_APPLICATION_ID, model, messages, false);
                
                if (maxkbChatService.isError(response)) {
                    log.warn("模型 {} 测试失败: {}", model, maxkbChatService.getErrorMessage(response));
                } else {
                    String content = maxkbChatService.extractMessageContent(response);
                    log.info("模型 {} 测试成功，回复: {}", model, content);
                }
                
            } catch (Exception e) {
                log.error("模型 {} 测试异常", model, e);
            }
        }
    }

    /**
     * 性能测试
     */
    public void performanceTest() {
        log.info("开始性能测试...");
        
        int testCount = 10;
        long startTime = System.currentTimeMillis();
        int successCount = 0;
        
        for (int i = 0; i < testCount; i++) {
            try {
                String response = maxkbChatService.sendMessage(
                    TEST_APPLICATION_ID, "性能测试 " + (i + 1), false);
                
                if (!maxkbChatService.isError(response)) {
                    successCount++;
                }
                
            } catch (Exception e) {
                log.warn("性能测试第 {} 次请求失败", i + 1, e);
            }
        }
        
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        
        log.info("性能测试完成:");
        log.info("- 总请求数: {}", testCount);
        log.info("- 成功请求数: {}", successCount);
        log.info("- 成功率: {}%", (successCount * 100.0 / testCount));
        log.info("- 总耗时: {} ms", totalTime);
        log.info("- 平均耗时: {} ms", totalTime / testCount);
    }

    /**
     * 打印配置信息
     */
    public void printConfiguration() {
        log.info("MaxKB 配置信息:");
        log.info("- 测试应用ID: {}", TEST_APPLICATION_ID);
        log.info("- 当前时间: {}", System.currentTimeMillis());
    }
}
