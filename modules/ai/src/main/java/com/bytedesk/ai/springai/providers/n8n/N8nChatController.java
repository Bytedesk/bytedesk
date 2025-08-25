package com.bytedesk.ai.springai.providers.n8n;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.bytedesk.core.utils.JsonResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * RAGFlow Chat Controller
 * https://n8n.io/docs/dev/http_api_reference
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/n8n")
@AllArgsConstructor
@ConditionalOnProperty(name = "bytedesk.n8n.enabled", havingValue = "true", matchIfMissing = false)
@Tag(name = "RAGFlow API", description = "RAGFlow 对话接口")
public class N8nChatController {

    private final N8nChatService n8nChatService;

    /**
     * 创建聊天完成 - OpenAI 兼容接口
     */
    @PostMapping("/chats/{chatId}/completions")
    @Operation(summary = "创建聊天完成", description = "使用 RAGFlow 聊天 API 创建对话完成")
    public ResponseEntity<?> createChatCompletion(@PathVariable String chatId,
                                                @RequestBody N8nChatRequest request) {
        try {
            log.info("Chat completion request for chatId: {}", chatId);
            
            String response = n8nChatService.createChatCompletion(
                chatId,
                request.getModel(),
                request.getMessages(),
                request.getStream()
            );
            
            // 检查是否有错误
            if (n8nChatService.isError(response)) {
                String errorMessage = n8nChatService.getErrorMessage(response);
                int errorCode = n8nChatService.getErrorCode(response);
                return ResponseEntity.ok(JsonResult.error("RAGFlow API 错误 [" + errorCode + "]: " + errorMessage));
            }
            
            return ResponseEntity.ok(JsonResult.success("聊天完成成功", response));
            
        } catch (Exception e) {
            log.error("Chat completion error", e);
            return ResponseEntity.ok(JsonResult.error("聊天完成失败: " + e.getMessage()));
        }
    }

    /**
     * 创建代理完成 - OpenAI 兼容接口
     */
    @PostMapping("/agents/{agentId}/completions")
    @Operation(summary = "创建代理完成", description = "使用 RAGFlow 代理 API 创建对话完成")
    public ResponseEntity<?> createAgentCompletion(@PathVariable String agentId,
                                                 @RequestBody N8nChatRequest request) {
        try {
            log.info("Agent completion request for agentId: {}", agentId);
            
            String response = n8nChatService.createAgentCompletion(
                agentId,
                request.getModel(),
                request.getMessages(),
                request.getStream()
            );
            
            // 检查是否有错误
            if (n8nChatService.isError(response)) {
                String errorMessage = n8nChatService.getErrorMessage(response);
                int errorCode = n8nChatService.getErrorCode(response);
                return ResponseEntity.ok(JsonResult.error("RAGFlow API 错误 [" + errorCode + "]: " + errorMessage));
            }
            
            return ResponseEntity.ok(JsonResult.success("代理完成成功", response));
            
        } catch (Exception e) {
            log.error("Agent completion error", e);
            return ResponseEntity.ok(JsonResult.error("代理完成失败: " + e.getMessage()));
        }
    }

    /**
     * 发送简单聊天消息
     */
    @PostMapping("/chats/{chatId}/message")
    @Operation(summary = "发送聊天消息", description = "发送简单的聊天消息到 RAGFlow")
    public ResponseEntity<?> sendChatMessage(@PathVariable String chatId,
                                           @RequestBody N8nMessageRequest request) {
        try {
            log.info("Send message request for chatId: {}", chatId);
            
            String response = n8nChatService.sendMessage(
                chatId,
                request.getContent(),
                request.getStream()
            );
            
            // 检查是否有错误
            if (n8nChatService.isError(response)) {
                String errorMessage = n8nChatService.getErrorMessage(response);
                int errorCode = n8nChatService.getErrorCode(response);
                return ResponseEntity.ok(JsonResult.error("RAGFlow API 错误 [" + errorCode + "]: " + errorMessage));
            }
            
            // 解析响应内容
            String messageContent = n8nChatService.extractMessageContent(response);
            String finishReason = n8nChatService.extractFinishReason(response);
            Map<String, Object> usage = n8nChatService.extractUsage(response);
            
            Map<String, Object> result = new HashMap<>();
            result.put("content", messageContent);
            result.put("finishReason", finishReason);
            result.put("usage", usage);
            result.put("fullResponse", response);
            
            return ResponseEntity.ok(JsonResult.success("消息发送成功", result));
            
        } catch (Exception e) {
            log.error("Send message error", e);
            return ResponseEntity.ok(JsonResult.error("消息发送失败: " + e.getMessage()));
        }
    }

    /**
     * 发送简单代理消息
     */
    @PostMapping("/agents/{agentId}/message")
    @Operation(summary = "发送代理消息", description = "发送简单的消息到 RAGFlow 代理")
    public ResponseEntity<?> sendAgentMessage(@PathVariable String agentId,
                                            @RequestBody N8nMessageRequest request) {
        try {
            log.info("Send agent message request for agentId: {}", agentId);
            
            String response = n8nChatService.sendAgentMessage(
                agentId,
                request.getContent(),
                request.getStream()
            );
            
            // 检查是否有错误
            if (n8nChatService.isError(response)) {
                String errorMessage = n8nChatService.getErrorMessage(response);
                int errorCode = n8nChatService.getErrorCode(response);
                return ResponseEntity.ok(JsonResult.error("RAGFlow API 错误 [" + errorCode + "]: " + errorMessage));
            }
            
            // 解析响应内容
            String messageContent = n8nChatService.extractMessageContent(response);
            String finishReason = n8nChatService.extractFinishReason(response);
            Map<String, Object> usage = n8nChatService.extractUsage(response);
            
            Map<String, Object> result = new HashMap<>();
            result.put("content", messageContent);
            result.put("finishReason", finishReason);
            result.put("usage", usage);
            result.put("fullResponse", response);
            
            return ResponseEntity.ok(JsonResult.success("代理消息发送成功", result));
            
        } catch (Exception e) {
            log.error("Send agent message error", e);
            return ResponseEntity.ok(JsonResult.error("代理消息发送失败: " + e.getMessage()));
        }
    }

    /**
     * 快速聊天接口（GET 方式）
     */
    @GetMapping("/chats/{chatId}/quick")
    @Operation(summary = "快速聊天", description = "快速聊天接口，用于简单测试")
    public ResponseEntity<?> quickChat(@PathVariable String chatId,
                                     @RequestParam String content,
                                     @RequestParam(required = false, defaultValue = "false") Boolean stream) {
        try {
            String response = n8nChatService.sendMessage(chatId, content, stream);
            
            // 检查是否有错误
            if (n8nChatService.isError(response)) {
                String errorMessage = n8nChatService.getErrorMessage(response);
                int errorCode = n8nChatService.getErrorCode(response);
                return ResponseEntity.ok(JsonResult.error("RAGFlow API 错误 [" + errorCode + "]: " + errorMessage));
            }
            
            // 解析响应内容
            String messageContent = n8nChatService.extractMessageContent(response);
            
            Map<String, Object> result = new HashMap<>();
            result.put("content", messageContent);
            result.put("fullResponse", response);
            
            return ResponseEntity.ok(JsonResult.success("快速聊天成功", result));
            
        } catch (Exception e) {
            log.error("Quick chat error", e);
            return ResponseEntity.ok(JsonResult.error("快速聊天失败: " + e.getMessage()));
        }
    }

    /**
     * 快速代理聊天接口（GET 方式）
     */
    @GetMapping("/agents/{agentId}/quick")
    @Operation(summary = "快速代理聊天", description = "快速代理聊天接口，用于简单测试")
    public ResponseEntity<?> quickAgentChat(@PathVariable String agentId,
                                          @RequestParam String content,
                                          @RequestParam(required = false, defaultValue = "false") Boolean stream) {
        try {
            String response = n8nChatService.sendAgentMessage(agentId, content, stream);
            
            // 检查是否有错误
            if (n8nChatService.isError(response)) {
                String errorMessage = n8nChatService.getErrorMessage(response);
                int errorCode = n8nChatService.getErrorCode(response);
                return ResponseEntity.ok(JsonResult.error("RAGFlow API 错误 [" + errorCode + "]: " + errorMessage));
            }
            
            // 解析响应内容
            String messageContent = n8nChatService.extractMessageContent(response);
            
            Map<String, Object> result = new HashMap<>();
            result.put("content", messageContent);
            result.put("fullResponse", response);
            
            return ResponseEntity.ok(JsonResult.success("快速代理聊天成功", result));
            
        } catch (Exception e) {
            log.error("Quick agent chat error", e);
            return ResponseEntity.ok(JsonResult.error("快速代理聊天失败: " + e.getMessage()));
        }
    }

    /**
     * RAGFlow 聊天请求数据结构
     */
    public static class N8nChatRequest {
        private String model;
        private List<Map<String, Object>> messages;
        private Boolean stream;

        // Getters and Setters
        public String getModel() { return model; }
        public void setModel(String model) { this.model = model; }
        
        public List<Map<String, Object>> getMessages() { return messages; }
        public void setMessages(List<Map<String, Object>> messages) { this.messages = messages; }
        
        public Boolean getStream() { return stream; }
        public void setStream(Boolean stream) { this.stream = stream; }
    }

    /**
     * RAGFlow 简单消息请求数据结构
     */
    public static class N8nMessageRequest {
        private String content;
        private Boolean stream;

        // Getters and Setters
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        
        public Boolean getStream() { return stream; }
        public void setStream(Boolean stream) { this.stream = stream; }
    }
}
