/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-19 09:39:15
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-23 11:45:27
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.zhipuai;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhipu.oapi.ClientV4;
import com.zhipu.oapi.Constants;
import com.zhipu.oapi.service.v4.deserialize.MessageDeserializeFactory;
import com.zhipu.oapi.service.v4.model.ChatCompletionRequest;
import com.zhipu.oapi.service.v4.model.ChatMessage;
import com.zhipu.oapi.service.v4.model.ChatMessageRole;
import com.zhipu.oapi.service.v4.model.ChatMeta;
import com.zhipu.oapi.service.v4.model.ModelApiResponse;
import com.zhipu.oapi.service.v4.model.ModelData;
import com.zhipu.oapi.service.v4.model.ChatMessageAccumulator;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

/**
 * 智谱AI服务类
 * 使用 oapi-java-sdk 实现聊天功能
 * https://github.com/MetaGLM/zhipuai-sdk-java-v4
 */
@Slf4j
@Service
@ConditionalOnProperty(prefix = "zhipuai", name = "enabled", havingValue = "true", matchIfMissing = false)
public class ZhipuaiService {

    @Autowired
    @Qualifier("zhipuaiClient")
    private ClientV4 client;

    @Autowired
    private ZhipuaiConfig zhipuaiConfig;

    private final ObjectMapper objectMapper = MessageDeserializeFactory.defaultObjectMapper();

    /**
     * 同步聊天
     */
    public String chatSync(String message) {
        return chatSync(message, null, null);
    }

    /**
     * 同步聊天（带自定义参数）
     */
    public String chatSync(String message, String model, Double temperature) {
        try {
            if (client == null) {
                return "Zhipuai client is not available";
            }

            List<ChatMessage> messages = new ArrayList<>();
            ChatMessage chatMessage = new ChatMessage(ChatMessageRole.USER.value(), message);
            messages.add(chatMessage);

            String requestId = String.format("sync-%d", System.currentTimeMillis());
            
            ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                    .model(model != null ? model : zhipuaiConfig.getModel())
                    .stream(Boolean.FALSE)
                    .invokeMethod(Constants.invokeMethod)
                    .messages(messages)
                    .requestId(requestId)
                    .temperature(temperature != null ? temperature.floatValue() : (float) zhipuaiConfig.getTemperature())
                    .build();
            
            ModelApiResponse response = client.invokeModelApi(chatCompletionRequest);
            
            if (response.isSuccess() && response.getData() != null) {
                Object content = response.getData().getChoices().get(0).getMessage().getContent();
                return content != null ? content.toString() : null;
            } else {
                log.error("Zhipuai API error: {}", response.getError());
                return "Error: " + (response.getError() != null ? response.getError().getMessage() : "Unknown error");
            }
        } catch (Exception e) {
            log.error("Error in chatSync", e);
            return "Error: " + e.getMessage();
        }
    }

    /**
     * 流式聊天
     */
    public Flux<String> chatStream(String message) {
        return chatStream(message, null, null);
    }

    /**
     * 流式聊天（带自定义参数）
     */
    public Flux<String> chatStream(String message, String model, Double temperature) {
        try {
            if (client == null) {
                return Flux.just("Zhipuai client is not available");
            }

            List<ChatMessage> messages = new ArrayList<>();
            ChatMessage chatMessage = new ChatMessage(ChatMessageRole.USER.value(), message);
            messages.add(chatMessage);

            String requestId = String.format("stream-%d", System.currentTimeMillis());
            
            ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                    .model(model != null ? model : zhipuaiConfig.getModel())
                    .stream(Boolean.TRUE)
                    .messages(messages)
                    .requestId(requestId)
                    .temperature(temperature != null ? temperature.floatValue() : (float) zhipuaiConfig.getTemperature())
                    .build();
            
            ModelApiResponse response = client.invokeModelApi(chatCompletionRequest);
            
            if (response.isSuccess()) {
                return Flux.from(response.getFlowable().map(accumulator -> {
                    Object delta = accumulator.getDelta();
                    if (delta instanceof com.zhipu.oapi.service.v4.model.ChatMessage) {
                        Object content = ((com.zhipu.oapi.service.v4.model.ChatMessage) delta).getContent();
                        return content != null ? content.toString() : "";
                    } else if (delta != null) {
                        return delta.toString();
                    }
                    return "";
                }));
            } else {
                log.error("Zhipuai API error: {}", response.getError());
                return Flux.just("Error: " + (response.getError() != null ? response.getError().getMessage() : "Unknown error"));
            }
        } catch (Exception e) {
            log.error("Error in chatStream", e);
            return Flux.just("Error: " + e.getMessage());
        }
    }

    /**
     * SSE聊天
     */
    public void chatSSE(String message, SseEmitter emitter) {
        chatSSE(message, null, null, emitter);
    }

    /**
     * SSE聊天（带自定义参数）
     */
    public void chatSSE(String message, String model, Double temperature, SseEmitter emitter) {
        try {
            if (client == null) {
                emitter.send("Zhipuai client is not available");
                emitter.complete();
                return;
            }

            List<ChatMessage> messages = new ArrayList<>();
            ChatMessage chatMessage = new ChatMessage(ChatMessageRole.USER.value(), message);
            messages.add(chatMessage);

            String requestId = String.format("sse-%d", System.currentTimeMillis());
            
            ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                    .model(model != null ? model : zhipuaiConfig.getModel())
                    .stream(Boolean.TRUE)
                    .messages(messages)
                    .requestId(requestId)
                    .temperature(temperature != null ? temperature.floatValue() : (float) zhipuaiConfig.getTemperature())
                    .build();
            
            ModelApiResponse response = client.invokeModelApi(chatCompletionRequest);
            
            if (response.isSuccess()) {
                AtomicBoolean isFirst = new AtomicBoolean(true);
                response.getFlowable()
                        .doOnNext(accumulator -> {
                            try {
                                if (isFirst.get()) {
                                    emitter.send("data: " + objectMapper.writeValueAsString(accumulator) + "\n\n");
                                    isFirst.set(false);
                                } else {
                                    emitter.send("data: " + objectMapper.writeValueAsString(accumulator) + "\n\n");
                                }
                            } catch (Exception e) {
                                log.error("Error sending SSE data", e);
                            }
                        })
                        .doOnComplete(() -> {
                            try {
                                emitter.send("data: [DONE]\n\n");
                                emitter.complete();
                            } catch (Exception e) {
                                log.error("Error completing SSE", e);
                            }
                        })
                        .doOnError(error -> {
                            log.error("Error in SSE stream", error);
                            try {
                                emitter.send("data: Error: " + error.getMessage() + "\n\n");
                                emitter.complete();
                            } catch (Exception e) {
                                log.error("Error sending error via SSE", e);
                            }
                        })
                        .subscribe();
            } else {
                log.error("Zhipuai API error: {}", response.getError());
                emitter.send("data: Error: " + (response.getError() != null ? response.getError().getMessage() : "Unknown error") + "\n\n");
                emitter.complete();
            }
        } catch (Exception e) {
            log.error("Error in chatSSE", e);
            try {
                emitter.send("data: Error: " + e.getMessage() + "\n\n");
                emitter.complete();
            } catch (Exception ex) {
                log.error("Error sending error via SSE", ex);
            }
        }
    }

    /**
     * 角色扮演聊天
     */
    public String rolePlayChat(String message, String userInfo, String botInfo, String botName, String userName) {
        try {
            if (client == null) {
                return "Zhipuai client is not available";
            }

            List<ChatMessage> messages = new ArrayList<>();
            ChatMessage chatMessage = new ChatMessage(ChatMessageRole.USER.value(), message);
            messages.add(chatMessage);

            ChatMeta meta = new ChatMeta();
            meta.setUser_info(userInfo);
            meta.setBot_info(botInfo);
            meta.setBot_name(botName);
            meta.setUser_name(userName);

            String requestId = String.format("roleplay-%d", System.currentTimeMillis());
            
            ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                    .model(Constants.ModelCharGLM3)
                    .stream(Boolean.FALSE)
                    .invokeMethod(Constants.invokeMethod)
                    .messages(messages)
                    .meta(meta)
                    .requestId(requestId)
                    .build();
            
            ModelApiResponse response = client.invokeModelApi(chatCompletionRequest);
            
            if (response.isSuccess() && response.getData() != null) {
                Object content = response.getData().getChoices().get(0).getMessage().getContent();
                return content != null ? content.toString() : null;
            } else {
                log.error("Zhipuai API error: {}", response.getError());
                return "Error: " + (response.getError() != null ? response.getError().getMessage() : "Unknown error");
            }
        } catch (Exception e) {
            log.error("Error in rolePlayChat", e);
            return "Error: " + e.getMessage();
        }
    }

    /**
     * 将流式响应转换为Flux
     */
    private Flux<String> mapStreamToFlux(io.reactivex.Flowable<ChatMessageAccumulator> flowable) {
        return Flux.from(flowable.map(accumulator -> {
            if (accumulator.getDelta() != null && accumulator.getDelta().getContent() != null) {
                return accumulator.getDelta().getContent();
            }
            return "";
        }));
    }

    /**
     * 检查服务健康状态
     */
    public boolean isHealthy() {
        try {
            if (client == null) {
                return false;
            }
            
            // 发送一个简单的测试请求
            String response = chatSync("Hello");
            return response != null && !response.startsWith("Error");
        } catch (Exception e) {
            log.error("Health check failed", e);
            return false;
        }
    }
} 