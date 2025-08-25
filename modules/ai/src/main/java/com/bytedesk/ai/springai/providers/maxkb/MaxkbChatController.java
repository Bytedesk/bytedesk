/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-08-23 07:22:47
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-23 07:22:52
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.context.annotation.Description;

import com.bytedesk.core.utils.JsonResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * MaxKB Chat Controller - MaxKB 对话 API 控制器
 * https://maxkb.cn/docs/v1/dev_manual/APIKey_chat/#1-openai-api
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/maxkb")
@RequiredArgsConstructor
@ConditionalOnProperty(name = "bytedesk.maxkb.enabled", havingValue = "true", matchIfMissing = false)
@Tag(name = "MaxKB Chat API", description = "MaxKB 知识库问答系统 API - OpenAI 兼容接口")
@Description("MaxKB Chat Controller - MaxKB knowledge base chat API with OpenAI compatibility")
public class MaxkbChatController {

    private final MaxkbChatService maxkbChatService;

    /**
     * 聊天完成接口 - OpenAI 兼容 API
     */
    @PostMapping("/applications/{applicationId}/chat/completions")
    @Operation(summary = "Chat Completions", description = "Create a chat completion using MaxKB application - OpenAI compatible API")
    public ResponseEntity<?> createChatCompletion(
            @Parameter(description = "MaxKB 应用ID", required = true)
            @PathVariable String applicationId,
            @RequestBody MaxkbChatRequest request) {
        
        try {
            log.info("Creating chat completion for application: {}", applicationId);
            
            String response = maxkbChatService.createChatCompletion(
                applicationId, 
                request.getModel(), 
                request.getMessages(), 
                request.getStream()
            );
            
            // 检查是否有错误
            if (maxkbChatService.isError(response)) {
                String errorMessage = maxkbChatService.getErrorMessage(response);
                int errorCode = maxkbChatService.getErrorCode(response);
                log.error("MaxKB API error [{}]: {}", errorCode, errorMessage);
                return ResponseEntity.badRequest().body(JsonResult.error(errorMessage, errorCode));
            }

            log.info("Chat completion successful for application: {}", applicationId);
            
            // 直接返回 MaxKB API 的原始响应（OpenAI 格式）
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error in chat completion for application: " + applicationId, e);
            return ResponseEntity.internalServerError()
                .body(JsonResult.error("服务内部错误: " + e.getMessage()));
        }
    }

    /**
     * 发送简单消息接口
     */
    @PostMapping("/applications/{applicationId}/message")
    @Operation(summary = "Send Simple Message", description = "Send a simple message to MaxKB application")
    public ResponseEntity<?> sendMessage(
            @Parameter(description = "MaxKB 应用ID", required = true)
            @PathVariable String applicationId,
            @RequestBody MaxkbMessageRequest request) {
        
        try {
            log.info("Sending message to application: {}", applicationId);
            
            String response = maxkbChatService.sendMessage(
                applicationId, 
                request.getContent(), 
                request.getStream()
            );
            
            // 检查是否有错误
            if (maxkbChatService.isError(response)) {
                String errorMessage = maxkbChatService.getErrorMessage(response);
                int errorCode = maxkbChatService.getErrorCode(response);
                log.error("MaxKB API error [{}]: {}", errorCode, errorMessage);
                return ResponseEntity.badRequest().body(JsonResult.error(errorMessage, errorCode));
            }

            // 解析响应内容
            String content = maxkbChatService.extractMessageContent(response);
            String finishReason = maxkbChatService.extractFinishReason(response);
            Map<String, Object> usage = maxkbChatService.extractUsage(response);
            
            // 构建响应数据
            Map<String, Object> result = new HashMap<>();
            result.put("content", content);
            result.put("finish_reason", finishReason);
            result.put("usage", usage);
            result.put("model", maxkbChatService.extractModel(response));
            result.put("id", maxkbChatService.extractId(response));
            result.put("object", maxkbChatService.extractObject(response));
            result.put("created", maxkbChatService.extractCreated(response));
            result.put("raw_response", response);

            log.info("Message sent successfully to application: {}", applicationId);
            return ResponseEntity.ok(JsonResult.success(result));

        } catch (Exception e) {
            log.error("Error sending message to application: " + applicationId, e);
            return ResponseEntity.internalServerError()
                .body(JsonResult.error("服务内部错误: " + e.getMessage()));
        }
    }

    /**
     * 发送带系统角色的消息接口
     */
    @PostMapping("/applications/{applicationId}/message/with-role")
    @Operation(summary = "Send Message with System Role", description = "Send a message with system role to MaxKB application")
    public ResponseEntity<?> sendMessageWithSystemRole(
            @Parameter(description = "MaxKB 应用ID", required = true)
            @PathVariable String applicationId,
            @RequestBody MaxkbSystemRoleRequest request) {
        
        try {
            log.info("Sending message with system role to application: {}", applicationId);
            
            String response = maxkbChatService.sendMessageWithSystemRole(
                applicationId, 
                request.getSystemRole(),
                request.getContent(), 
                request.getStream()
            );
            
            // 检查是否有错误
            if (maxkbChatService.isError(response)) {
                String errorMessage = maxkbChatService.getErrorMessage(response);
                int errorCode = maxkbChatService.getErrorCode(response);
                log.error("MaxKB API error [{}]: {}", errorCode, errorMessage);
                return ResponseEntity.badRequest().body(JsonResult.error(errorMessage, errorCode));
            }

            // 解析响应内容
            String content = maxkbChatService.extractMessageContent(response);
            String finishReason = maxkbChatService.extractFinishReason(response);
            Map<String, Object> usage = maxkbChatService.extractUsage(response);
            
            // 构建响应数据
            Map<String, Object> result = new HashMap<>();
            result.put("content", content);
            result.put("finish_reason", finishReason);
            result.put("usage", usage);
            result.put("model", maxkbChatService.extractModel(response));
            result.put("id", maxkbChatService.extractId(response));
            result.put("object", maxkbChatService.extractObject(response));
            result.put("created", maxkbChatService.extractCreated(response));
            result.put("raw_response", response);

            log.info("Message with system role sent successfully to application: {}", applicationId);
            return ResponseEntity.ok(JsonResult.success(result));

        } catch (Exception e) {
            log.error("Error sending message with system role to application: " + applicationId, e);
            return ResponseEntity.internalServerError()
                .body(JsonResult.error("服务内部错误: " + e.getMessage()));
        }
    }

    /**
     * 快速测试接口
     */
    @GetMapping("/applications/{applicationId}/quick")
    @Operation(summary = "Quick Test", description = "Quick test MaxKB application with simple parameters")
    public ResponseEntity<?> quickTest(
            @Parameter(description = "MaxKB 应用ID", required = true)
            @PathVariable String applicationId,
            @Parameter(description = "消息内容", required = true)
            @RequestParam String content,
            @Parameter(description = "是否流式响应", required = false)
            @RequestParam(defaultValue = "false") Boolean stream) {
        
        try {
            log.info("Quick test for application: {} with content: {}", applicationId, content);
            
            String response = maxkbChatService.sendMessage(applicationId, content, stream);
            
            // 检查是否有错误
            if (maxkbChatService.isError(response)) {
                String errorMessage = maxkbChatService.getErrorMessage(response);
                int errorCode = maxkbChatService.getErrorCode(response);
                log.error("MaxKB API error [{}]: {}", errorCode, errorMessage);
                return ResponseEntity.badRequest().body(JsonResult.error(errorMessage, errorCode));
            }

            // 解析响应内容
            String responseContent = maxkbChatService.extractMessageContent(response);
            
            // 构建简化响应数据
            Map<String, Object> result = new HashMap<>();
            result.put("question", content);
            result.put("answer", responseContent);
            result.put("application_id", applicationId);
            result.put("stream", stream);
            result.put("model", maxkbChatService.extractModel(response));
            result.put("usage", maxkbChatService.extractUsage(response));

            log.info("Quick test successful for application: {}", applicationId);
            return ResponseEntity.ok(JsonResult.success(result));

        } catch (Exception e) {
            log.error("Error in quick test for application: " + applicationId, e);
            return ResponseEntity.internalServerError()
                .body(JsonResult.error("服务内部错误: " + e.getMessage()));
        }
    }

    /**
     * 健康检查接口
     */
    @GetMapping("/health")
    @Operation(summary = "Health Check", description = "Check MaxKB service health status")
    public ResponseEntity<?> healthCheck() {
        
        try {
            Map<String, Object> health = new HashMap<>();
            health.put("service", "MaxKB Chat Service");
            health.put("status", "UP");
            health.put("timestamp", System.currentTimeMillis());
            health.put("version", "1.0.0");
            
            log.debug("MaxKB service health check passed");
            return ResponseEntity.ok(JsonResult.success(health));

        } catch (Exception e) {
            log.error("MaxKB service health check failed", e);
            return ResponseEntity.internalServerError()
                .body(JsonResult.error("服务健康检查失败: " + e.getMessage()));
        }
    }
}
