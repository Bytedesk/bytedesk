/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-08-22 13:02:48
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-22 13:04:05
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.robot;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class RobotController {

    @Autowired(required = false)
    private ChatModel primaryChatModel;
    
    private final ObjectMapper objectMapper;

    /**
     * OpenAI兼容的chat/completions接口
     * 完全兼容OpenAI API格式，支持流式和非流式响应
     */
    @PostMapping("/chat/completions")
    public ResponseEntity<?> chatCompletions(@RequestBody OpenAIChatCompletionRequest request) {
        log.info("Chat completions request: model={}, messages={}, stream={}", 
                request.getModel(), request.getMessages().size(), request.getStream());

        if (primaryChatModel == null) {
            return ResponseEntity.ok(createErrorResponse("No chat model available", "service_unavailable"));
        }

        try {
            // 转换消息格式
            List<Message> messages = convertMessages(request.getMessages());
            Prompt prompt = new Prompt(messages);

            // 检查是否为流式请求
            if (Boolean.TRUE.equals(request.getStream())) {
                return handleStreamingRequest(prompt, request);
            } else {
                return handleNonStreamingRequest(prompt, request);
            }

        } catch (Exception e) {
            log.error("Error processing chat completions request", e);
            return ResponseEntity.ok(createErrorResponse(e.getMessage(), "internal_error"));
        }
    }

    /**
     * 处理非流式请求
     */
    private ResponseEntity<?> handleNonStreamingRequest(Prompt prompt, OpenAIChatCompletionRequest request) {
        try {
            ChatResponse response = primaryChatModel.call(prompt);
            OpenAIChatCompletionResponse openaiResponse = convertToOpenAIResponse(response, request);
            return ResponseEntity.ok(openaiResponse);
        } catch (Exception e) {
            log.error("Error in non-streaming request", e);
            return ResponseEntity.ok(createErrorResponse(e.getMessage(), "internal_error"));
        }
    }

    /**
     * 处理流式请求
     */
    private ResponseEntity<?> handleStreamingRequest(Prompt prompt, OpenAIChatCompletionRequest request) {
        SseEmitter emitter = new SseEmitter(300_000L); // 5分钟超时

        Flux<ChatResponse> responseFlux = primaryChatModel.stream(prompt);
        
        responseFlux.subscribe(
            chatResponse -> {
                try {
                    OpenAIChatCompletionChunk chunk = convertToOpenAIChunk(chatResponse, request);
                    emitter.send(SseEmitter.event()
                            .name("data")
                            .data("data: " + chunk.toJsonString() + "\n\n"));
                } catch (Exception e) {
                    log.error("Error sending streaming data", e);
                    emitter.completeWithError(e);
                }
            },
            error -> {
                log.error("Error in streaming response", error);
                try {
                    emitter.send(SseEmitter.event()
                            .name("error")
                            .data("data: " + createErrorResponse(error.getMessage(), "stream_error").toString() + "\n\n"));
                } catch (Exception e) {
                    log.error("Error sending error event", e);
                }
                emitter.completeWithError(error);
            },
            () -> {
                try {
                    emitter.send(SseEmitter.event()
                            .name("data")
                            .data("data: [DONE]\n\n"));
                    emitter.complete();
                } catch (Exception e) {
                    log.error("Error completing stream", e);
                    emitter.completeWithError(e);
                }
            }
        );

        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .body(emitter);
    }

    /**
     * 转换消息格式
     */
    private List<Message> convertMessages(List<OpenAIChatMessage> openaiMessages) {
        List<Message> messages = new ArrayList<>();
        
        for (OpenAIChatMessage msg : openaiMessages) {
            switch (msg.getRole()) {
                case "system":
                    messages.add(new SystemMessage(msg.getContent()));
                    break;
                case "user":
                    messages.add(new UserMessage(msg.getContent()));
                    break;
                case "assistant":
                    messages.add(new AssistantMessage(msg.getContent()));
                    break;
                default:
                    log.warn("Unknown message role: {}", msg.getRole());
                    messages.add(new UserMessage(msg.getContent()));
                    break;
            }
        }
        
        return messages;
    }

    /**
     * 转换为OpenAI响应格式
     */
    private OpenAIChatCompletionResponse convertToOpenAIResponse(ChatResponse response, OpenAIChatCompletionRequest request) {
        OpenAIChatCompletionResponse openaiResponse = new OpenAIChatCompletionResponse();
        openaiResponse.setId("chatcmpl-" + UUID.randomUUID().toString());
        openaiResponse.setObject("chat.completion");
        openaiResponse.setCreated(Instant.now().getEpochSecond());
        openaiResponse.setModel(request.getModel() != null ? request.getModel() : "bytedesk-ai");

        List<OpenAIChoice> choices = new ArrayList<>();
        for (int i = 0; i < response.getResults().size(); i++) {
            Generation generation = response.getResults().get(i);
            OpenAIChoice choice = new OpenAIChoice();
            choice.setIndex(i);
            choice.setFinishReason("stop");
            
            OpenAIChatMessage message = new OpenAIChatMessage();
            message.setRole("assistant");
            message.setContent(generation.getOutput().getText());
            choice.setMessage(message);
            
            choices.add(choice);
        }
        
        openaiResponse.setChoices(choices);

        // 设置token使用情况（如果可用）
        if (response.getMetadata() != null && response.getMetadata().getUsage() != null) {
            OpenAIUsage usage = new OpenAIUsage();
            usage.setPromptTokens(response.getMetadata().getUsage().getPromptTokens().intValue());
            // 尝试获取completion tokens，不同的provider可能有不同的方法名
            try {
                // 尝试获取generation tokens 或 completion tokens
                int completionTokens = response.getMetadata().getUsage().getTotalTokens().intValue() 
                    - response.getMetadata().getUsage().getPromptTokens().intValue();
                usage.setCompletionTokens(completionTokens);
            } catch (Exception e) {
                // 如果获取失败，设置为0
                usage.setCompletionTokens(0);
            }
            usage.setTotalTokens(response.getMetadata().getUsage().getTotalTokens().intValue());
            openaiResponse.setUsage(usage);
        }

        return openaiResponse;
    }

    /**
     * 转换为OpenAI流式响应块
     */
    private OpenAIChatCompletionChunk convertToOpenAIChunk(ChatResponse response, OpenAIChatCompletionRequest request) {
        OpenAIChatCompletionChunk chunk = new OpenAIChatCompletionChunk();
        chunk.setId("chatcmpl-" + UUID.randomUUID().toString());
        chunk.setObject("chat.completion.chunk");
        chunk.setCreated(Instant.now().getEpochSecond());
        chunk.setModel(request.getModel() != null ? request.getModel() : "bytedesk-ai");

        List<OpenAIStreamChoice> choices = new ArrayList<>();
        for (int i = 0; i < response.getResults().size(); i++) {
            Generation generation = response.getResults().get(i);
            OpenAIStreamChoice choice = new OpenAIStreamChoice();
            choice.setIndex(i);
            choice.setFinishReason(null); // 流式响应中通常为null，直到结束
            
            OpenAIStreamDelta delta = new OpenAIStreamDelta();
            delta.setContent(generation.getOutput().getText());
            choice.setDelta(delta);
            
            choices.add(choice);
        }
        
        chunk.setChoices(choices);
        chunk.setObjectMapper(objectMapper); // 传递ObjectMapper实例
        return chunk;
    }

    /**
     * 创建错误响应
     */
    private OpenAIErrorResponse createErrorResponse(String message, String type) {
        OpenAIErrorResponse errorResponse = new OpenAIErrorResponse();
        OpenAIError error = new OpenAIError();
        error.setMessage(message);
        error.setType(type);
        error.setCode(null);
        errorResponse.setError(error);
        return errorResponse;
    }

    // OpenAI兼容的数据模型类

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class OpenAIChatCompletionRequest {
        private String model;
        private List<OpenAIChatMessage> messages;
        
        @JsonProperty("max_tokens")
        private Integer maxTokens;
        
        private Double temperature;
        
        @JsonProperty("top_p")
        private Double topP;
        
        private Integer n;
        private Boolean stream;
        private List<String> stop;
        
        @JsonProperty("presence_penalty")
        private Double presencePenalty;
        
        @JsonProperty("frequency_penalty")
        private Double frequencyPenalty;
        
        @JsonProperty("logit_bias")
        private Map<String, Double> logitBias;
        
        private String user;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class OpenAIChatMessage {
        private String role;
        private String content;
        private String name;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class OpenAIChatCompletionResponse {
        private String id;
        private String object;
        private Long created;
        private String model;
        private List<OpenAIChoice> choices;
        private OpenAIUsage usage;
        
        @JsonProperty("system_fingerprint")
        private String systemFingerprint;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class OpenAIChoice {
        private Integer index;
        private OpenAIChatMessage message;
        
        @JsonProperty("finish_reason")
        private String finishReason;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class OpenAIUsage {
        @JsonProperty("prompt_tokens")
        private Integer promptTokens;
        
        @JsonProperty("completion_tokens")
        private Integer completionTokens;
        
        @JsonProperty("total_tokens")
        private Integer totalTokens;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class OpenAIChatCompletionChunk {
        private String id;
        private String object;
        private Long created;
        private String model;
        private List<OpenAIStreamChoice> choices;
        
        @JsonProperty("system_fingerprint")
        private String systemFingerprint;
        
        // 非序列化字段
        private transient ObjectMapper objectMapper;

        public void setObjectMapper(ObjectMapper objectMapper) {
            this.objectMapper = objectMapper;
        }

        public String toJsonString() {
            try {
                ObjectMapper mapper = this.objectMapper != null ? this.objectMapper : new ObjectMapper();
                return mapper.writeValueAsString(this);
            } catch (Exception e) {
                log.error("Error serializing chunk to JSON", e);
                return "{}";
            }
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class OpenAIStreamChoice {
        private Integer index;
        private OpenAIStreamDelta delta;
        
        @JsonProperty("finish_reason")
        private String finishReason;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class OpenAIStreamDelta {
        private String role;
        private String content;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class OpenAIErrorResponse {
        private OpenAIError error;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class OpenAIError {
        private String message;
        private String type;
        private String param;
        private String code;
    }
}
