/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-08-23 07:12:31
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-23 07:17:09
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.bytedesk.core.utils.JsonResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * Dify Chat Controller
 * https://docs.dify.ai/zh-hans/guides/application-publishing/developing-with-apis
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/dify")
@AllArgsConstructor
@ConditionalOnProperty(name = "bytedesk.dify.enabled", havingValue = "true", matchIfMissing = false)
@Tag(name = "Dify API", description = "Dify 对话接口")
public class DifyChatController {

    private final DifyChatService difyChatService;

    /**
     * 发送对话消息
     */
    @PostMapping("/chat")
    @Operation(summary = "发送对话消息", description = "与 Dify 机器人对话，支持上下文会话")
    public ResponseEntity<?> chat(@RequestBody DifyRequest request) {
        try {
            log.info("Chat request: {}", request);
            
            String response = difyChatService.sendChatMessage(
                request.getQuery(),
                request.getConversationId(),
                request.getUser(),
                request.getInputs(),
                request.getResponseMode()
            );
            
            return ResponseEntity.ok(JsonResult.success("对话成功", response));
            
        } catch (Exception e) {
            log.error("Chat error", e);
            return ResponseEntity.ok(JsonResult.error("对话失败: " + e.getMessage()));
        }
    }

    /**
     * 发送文本补全消息
     */
    @PostMapping("/completion")
    @Operation(summary = "文本补全", description = "发送文本补全请求到 Dify")
    public ResponseEntity<?> completion(@RequestBody DifyRequest request) {
        try {
            log.info("Completion request: {}", request);
            
            String response = difyChatService.sendCompletionMessage(
                request.getInputs(),
                request.getUser(),
                request.getResponseMode()
            );
            
            return ResponseEntity.ok(JsonResult.success("文本补全成功", response));
            
        } catch (Exception e) {
            log.error("Completion error", e);
            return ResponseEntity.ok(JsonResult.error("文本补全失败: " + e.getMessage()));
        }
    }

    /**
     * 获取对话历史
     */
    @GetMapping("/messages")
    @Operation(summary = "获取对话历史", description = "获取指定对话的历史消息")
    public ResponseEntity<?> getMessages(@RequestParam String conversationId,
                                       @RequestParam String user,
                                       @RequestParam(required = false) String firstId,
                                       @RequestParam(required = false) Integer limit) {
        try {
            String response = difyChatService.getConversationMessages(
                conversationId, user, firstId, limit
            );
            
            return ResponseEntity.ok(JsonResult.success("获取对话历史成功", response));
            
        } catch (Exception e) {
            log.error("Get messages error", e);
            return ResponseEntity.ok(JsonResult.error("获取对话历史失败: " + e.getMessage()));
        }
    }

    /**
     * 获取对话列表
     */
    @GetMapping("/conversations")
    @Operation(summary = "获取对话列表", description = "获取用户的对话列表")
    public ResponseEntity<?> getConversations(@RequestParam String user,
                                            @RequestParam(required = false) String lastId,
                                            @RequestParam(required = false) Integer limit,
                                            @RequestParam(required = false) Boolean pinned) {
        try {
            String response = difyChatService.getConversations(user, lastId, limit, pinned);
            
            return ResponseEntity.ok(JsonResult.success("获取对话列表成功", response));
            
        } catch (Exception e) {
            log.error("Get conversations error", e);
            return ResponseEntity.ok(JsonResult.error("获取对话列表失败: " + e.getMessage()));
        }
    }

    /**
     * 删除对话
     */
    @DeleteMapping("/conversations/{conversationId}")
    @Operation(summary = "删除对话", description = "删除指定的对话")
    public ResponseEntity<?> deleteConversation(@PathVariable String conversationId,
                                              @RequestParam String user) {
        try {
            String response = difyChatService.deleteConversation(conversationId, user);
            
            return ResponseEntity.ok(JsonResult.success("删除对话成功", response));
            
        } catch (Exception e) {
            log.error("Delete conversation error", e);
            return ResponseEntity.ok(JsonResult.error("删除对话失败: " + e.getMessage()));
        }
    }

    /**
     * 重命名对话
     */
    @PostMapping("/conversations/{conversationId}/name")
    @Operation(summary = "重命名对话", description = "修改对话的名称")
    public ResponseEntity<?> renameConversation(@PathVariable String conversationId,
                                              @RequestBody DifyRequest request) {
        try {
            String response = difyChatService.renameConversation(
                conversationId, request.getName(), request.getUser()
            );
            
            return ResponseEntity.ok(JsonResult.success("重命名对话成功", response));
            
        } catch (Exception e) {
            log.error("Rename conversation error", e);
            return ResponseEntity.ok(JsonResult.error("重命名对话失败: " + e.getMessage()));
        }
    }

    /**
     * 简单对话接口（兼容旧版本）
     */
    @GetMapping("/chat")
    @Operation(summary = "简单对话", description = "简单的对话接口，用于快速测试")
    public ResponseEntity<?> simpleChat(@RequestParam String query,
                                      @RequestParam(required = false) String user,
                                      @RequestParam(required = false) String conversationId) {
        try {
            if (user == null) {
                user = "default-user";
            }
            
            Map<String, Object> inputs = new HashMap<>();
            
            String response = difyChatService.sendChatMessage(
                query, conversationId, user, inputs, "blocking"
            );
            
            // 解析响应内容
            String messageContent = difyChatService.extractMessageContent(response);
            String newConversationId = difyChatService.extractConversationId(response);
            
            Map<String, Object> result = new HashMap<>();
            result.put("answer", messageContent);
            result.put("conversationId", newConversationId);
            result.put("fullResponse", response);
            
            return ResponseEntity.ok(JsonResult.success("对话成功", result));
            
        } catch (Exception e) {
            log.error("Simple chat error", e);
            return ResponseEntity.ok(JsonResult.error("对话失败: " + e.getMessage()));
        }
    }
}
