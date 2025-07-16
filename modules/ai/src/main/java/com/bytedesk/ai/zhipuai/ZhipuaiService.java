/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-19 09:39:15
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-16 10:24:39
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
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.bytedesk.ai.robot.RobotLlm;
import com.bytedesk.ai.robot.RobotProtobuf;
import com.bytedesk.ai.springai.service.BaseSpringAIService;
import com.bytedesk.core.constant.LlmConsts;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.MessageTypeEnum;
import com.zhipu.oapi.ClientV4;
import com.zhipu.oapi.Constants;
import com.zhipu.oapi.service.v4.model.ChatCompletionRequest;
import com.zhipu.oapi.service.v4.model.ChatMessage;
import com.zhipu.oapi.service.v4.model.ChatMessageRole;
import com.zhipu.oapi.service.v4.model.ChatMeta;
import com.zhipu.oapi.service.v4.model.ModelApiResponse;
import com.zhipu.oapi.service.v4.model.ChatMessageAccumulator;
import com.zhipu.oapi.service.v4.model.ChatTool;
import com.zhipu.oapi.service.v4.model.ChatToolType;
import com.zhipu.oapi.service.v4.model.ChatFunction;
import com.zhipu.oapi.service.v4.model.ModelData;
import com.zhipu.oapi.service.v4.model.QueryModelResultRequest;
import com.zhipu.oapi.service.v4.model.QueryModelResultResponse;
import com.zhipu.oapi.service.v4.model.WebSearch;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

/**
 * 智谱AI服务类
 * 使用 oapi-java-sdk 实现聊天功能，继承自BaseSpringAIService
 * https://github.com/MetaGLM/zhipuai-sdk-java-v4
 */
@Slf4j
@Service
@ConditionalOnProperty(prefix = "spring.ai.zhipuai.chat", name = "enabled", havingValue = "true", matchIfMissing = false)
public class ZhipuaiService extends BaseSpringAIService {

    @Autowired
    @Qualifier("zhipuaiChatClient")
    private ClientV4 client;

    @Autowired
    private ZhipuaiChatConfig zhipuaiChatConfig;

    /**
     * 构造函数
     */
    public ZhipuaiService() {
        super();
    }

    /**
     * 根据机器人配置创建动态的聊天选项
     */
    private ChatCompletionRequest createDynamicRequest(RobotLlm llm, String message, boolean stream) {
        List<ChatMessage> messages = new ArrayList<>();
        ChatMessage chatMessage = new ChatMessage(ChatMessageRole.USER.value(), message);
        messages.add(chatMessage);

        String requestId = String.format("zhipuai-%d", System.currentTimeMillis());
        
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model(llm != null && llm.getModel() != null ? llm.getModel() : zhipuaiChatConfig.getModel())
                .stream(stream)
                .invokeMethod(Constants.invokeMethod)
                .messages(messages)
                .requestId(requestId)
                .temperature(llm != null ? llm.getTemperature().floatValue() : (float) zhipuaiChatConfig.getTemperature())
                .build();

        // 添加请求日志
        log.info("Zhipuai API creating request - model: {}, stream: {}, requestId: {}, temperature: {}, message length: {}", 
                chatCompletionRequest.getModel(), stream, requestId, chatCompletionRequest.getTemperature(), message.length());

        return chatCompletionRequest;
    }

    /**
     * 手动提取智谱AI的token使用情况
     * 从ModelApiResponse中提取token使用信息
     * 
     * @param response ModelApiResponse对象
     * @return TokenUsage对象
     */
    private TokenUsage extractZhipuaiTokenUsage(ModelApiResponse response) {
        try {
            if (response == null) {
                log.warn("Zhipuai API response is null");
                return new TokenUsage(0, 0, 0);
            }
            
            log.info("Zhipuai API manual token extraction - response success: {}, has data: {}", 
                    response.isSuccess(), response.getData() != null);
            
            if (!response.isSuccess() || response.getData() == null) {
                log.warn("Zhipuai API response is not successful or has no data");
                return new TokenUsage(0, 0, 0);
            }
            
            // 尝试从response.getData()中获取usage信息
            var data = response.getData();
            log.info("Zhipuai API response data class: {}", data.getClass().getName());
            
            // 检查是否有usage字段
            try {
                java.lang.reflect.Field usageField = data.getClass().getDeclaredField("usage");
                usageField.setAccessible(true);
                Object usage = usageField.get(data);
                
                if (usage != null) {
                    log.info("Zhipuai API found usage field: {}", usage);
                    
                    if (usage instanceof java.util.Map) {
                        java.util.Map<?, ?> usageMap = (java.util.Map<?, ?>) usage;
                        
                        long promptTokens = 0;
                        long completionTokens = 0;
                        long totalTokens = 0;
                        
                        // 提取prompt_tokens
                        Object promptObj = usageMap.get("prompt_tokens");
                        if (promptObj instanceof Number) {
                            promptTokens = ((Number) promptObj).longValue();
                        }
                        
                        // 提取completion_tokens
                        Object completionObj = usageMap.get("completion_tokens");
                        if (completionObj instanceof Number) {
                            completionTokens = ((Number) completionObj).longValue();
                        }
                        
                        // 提取total_tokens
                        Object totalObj = usageMap.get("total_tokens");
                        if (totalObj instanceof Number) {
                            totalTokens = ((Number) totalObj).longValue();
                        }
                        
                        // 如果没有total_tokens，计算它
                        if (totalTokens == 0 && (promptTokens > 0 || completionTokens > 0)) {
                            totalTokens = promptTokens + completionTokens;
                        }
                        
                        log.info("Zhipuai API manual token extraction result - prompt: {}, completion: {}, total: {}", 
                                promptTokens, completionTokens, totalTokens);
                        
                        return new TokenUsage(promptTokens, completionTokens, totalTokens);
                    }
                }
            } catch (Exception e) {
                log.debug("Could not get usage field via reflection: {}", e.getMessage());
            }
            
            // 如果无法通过反射获取，尝试从toString()中解析
            String dataStr = data.toString();
            log.info("Zhipuai API parsing data string: {}", dataStr);
            
            // 查找usage信息
            if (dataStr.contains("usage") || dataStr.contains("tokens")) {
                // 提取prompt_tokens
                int promptStart = dataStr.indexOf("prompt_tokens=");
                if (promptStart == -1) promptStart = dataStr.indexOf("promptTokens=");
                int promptEnd = -1;
                if (promptStart > 0) {
                    promptEnd = dataStr.indexOf(",", promptStart);
                    if (promptEnd == -1) promptEnd = dataStr.indexOf("}", promptStart);
                }
                
                // 提取completion_tokens
                int completionStart = dataStr.indexOf("completion_tokens=");
                if (completionStart == -1) completionStart = dataStr.indexOf("completionTokens=");
                int completionEnd = -1;
                if (completionStart > 0) {
                    completionEnd = dataStr.indexOf(",", completionStart);
                    if (completionEnd == -1) completionEnd = dataStr.indexOf("}", completionStart);
                }
                
                // 提取total_tokens
                int totalStart = dataStr.indexOf("total_tokens=");
                if (totalStart == -1) totalStart = dataStr.indexOf("totalTokens=");
                int totalEnd = -1;
                if (totalStart > 0) {
                    totalEnd = dataStr.indexOf("}", totalStart);
                }
                
                long promptTokens = 0;
                long completionTokens = 0;
                long totalTokens = 0;
                
                if (promptStart > 0 && promptEnd > promptStart) {
                    try {
                        String promptStr = dataStr.substring(promptStart + 13, promptEnd);
                        promptTokens = Long.parseLong(promptStr);
                    } catch (NumberFormatException e) {
                        log.warn("Could not parse prompt_tokens from: {}", dataStr.substring(promptStart + 13, promptEnd));
                    }
                }
                
                if (completionStart > 0 && completionEnd > completionStart) {
                    try {
                        String completionStr = dataStr.substring(completionStart + 17, completionEnd);
                        completionTokens = Long.parseLong(completionStr);
                    } catch (NumberFormatException e) {
                        log.warn("Could not parse completion_tokens from: {}", dataStr.substring(completionStart + 17, completionEnd));
                    }
                }
                
                if (totalStart > 0 && totalEnd > totalStart) {
                    try {
                        String totalStr = dataStr.substring(totalStart + 12, totalEnd);
                        totalTokens = Long.parseLong(totalStr);
                    } catch (NumberFormatException e) {
                        log.warn("Could not parse total_tokens from: {}", dataStr.substring(totalStart + 12, totalEnd));
                    }
                }
                
                // 如果没有total_tokens，计算它
                if (totalTokens == 0 && (promptTokens > 0 || completionTokens > 0)) {
                    totalTokens = promptTokens + completionTokens;
                }
                
                log.info("Zhipuai API manual token extraction result from string parsing - prompt: {}, completion: {}, total: {}", 
                        promptTokens, completionTokens, totalTokens);
                
                return new TokenUsage(promptTokens, completionTokens, totalTokens);
            }
            
            // 如果所有方法都失败，尝试估算token使用量
            log.info("Zhipuai API all extraction methods failed, attempting to estimate token usage");
            return estimateTokenUsageFromResponse(response);
            
        } catch (Exception e) {
            log.error("Error in manual Zhipuai token extraction", e);
            return new TokenUsage(0, 0, 0);
        }
    }

    /**
     * 估算token使用量（当无法从API获取实际token信息时使用）
     * 
     * @param response ModelApiResponse对象
     * @return 估算的TokenUsage对象
     */
    private TokenUsage estimateTokenUsageFromResponse(ModelApiResponse response) {
        try {
            if (response == null || response.getData() == null) {
                return new TokenUsage(0, 0, 0);
            }
            
            // 获取输出文本
            String outputText = "";
            if (response.getData().getChoices() != null && !response.getData().getChoices().isEmpty()) {
                var choice = response.getData().getChoices().get(0);
                if (choice.getMessage() != null && choice.getMessage().getContent() != null) {
                    outputText = choice.getMessage().getContent().toString();
                }
            }
            
            // 由于无法从ModelApiResponse直接获取输入文本，我们使用一个保守的估算
            // 假设输入文本长度约为输出文本的30%（这是一个常见的比例）
            long completionTokens = estimateTokens(outputText);
            long promptTokens = (long) (completionTokens * 0.3);
            long totalTokens = promptTokens + completionTokens;
            
            log.info("Zhipuai API estimated tokens - output: {} chars -> {} tokens, estimated prompt: {} tokens, total: {} tokens", 
                    outputText.length(), completionTokens, promptTokens, totalTokens);
            
            return new TokenUsage(promptTokens, completionTokens, totalTokens);
            
        } catch (Exception e) {
            log.error("Error estimating token usage", e);
            return new TokenUsage(0, 0, 0);
        }
    }
    
    /**
     * 估算文本的token数量
     * 
     * @param text 输入文本
     * @return 估算的token数量
     */
    private long estimateTokens(String text) {
        if (text == null || text.isEmpty()) {
            return 0;
        }
        
        // 简单的token估算算法
        // 中文约1.5字符/token，英文约4字符/token
        int chineseChars = 0;
        int englishChars = 0;
        
        for (char c : text.toCharArray()) {
            if (Character.UnicodeScript.of(c) == Character.UnicodeScript.HAN) {
                chineseChars++;
            } else if (Character.isLetterOrDigit(c)) {
                englishChars++;
            }
        }
        
        // 计算估算的token数量
        long estimatedTokens = (long) (chineseChars / 1.5 + englishChars / 4.0);
        
        // 确保至少返回1个token
        return Math.max(1, estimatedTokens);
    }

    /**
     * 从消息估算token使用量
     * 
     * @param message 输入消息
     * @return 估算的TokenUsage对象
     */
    private TokenUsage estimateTokenUsageFromMessage(String message) {
        try {
            if (message == null || message.isEmpty()) {
                return new TokenUsage(0, 0, 0);
            }
            
            // 估算输入token数量
            long promptTokens = estimateTokens(message);
            
            // 假设输出长度约为输入的2倍（这是一个常见的比例）
            long completionTokens = promptTokens * 2;
            long totalTokens = promptTokens + completionTokens;
            
            log.info("Zhipuai API estimated token usage from message - input: {} chars -> {} tokens, estimated output: {} tokens, total: {} tokens", 
                    message.length(), promptTokens, completionTokens, totalTokens);
            
            return new TokenUsage(promptTokens, completionTokens, totalTokens);
            
        } catch (Exception e) {
            log.error("Error estimating token usage from message", e);
            return new TokenUsage(0, 0, 0);
        }
    }

    /**
     * 方式1：异步流式调用 - 实现BaseSpringAIService的抽象方法
     */
    @Override
    protected void processPromptWebsocket(Prompt prompt, RobotProtobuf robot, MessageProtobuf messageProtobufQuery,
            MessageProtobuf messageProtobufReply) {
        // 从robot中获取llm配置
        RobotLlm llm = robot.getLlm();
        String message = extractTextFromPrompt(prompt);
        log.info("Zhipuai API websocket message: {}", message);

        // 添加请求日志
        log.info("Zhipuai API websocket request - model: {}, message length: {}, robot: {}", 
                (llm != null && llm.getModel() != null) ? llm.getModel() : zhipuaiChatConfig.getModel(), 
                message.length(), robot != null ? robot.getUid() : "null");

        long startTime = System.currentTimeMillis();
        final boolean[] success = {false};
        final TokenUsage[] tokenUsage = {new TokenUsage(0, 0, 0)};
        final ChatMessageAccumulator[] finalAccumulator = {null};

        try {
            if (client == null) {
                log.error("Zhipuai API client is null");
                sendMessageWebsocket(MessageTypeEnum.ERROR, "Zhipuai client is not available", messageProtobufReply);
                return;
            }

            ChatCompletionRequest chatCompletionRequest = createDynamicRequest(llm, message, true);
            
            log.info("Zhipuai API invoking model with requestId: {}", chatCompletionRequest.getRequestId());
            ModelApiResponse response = client.invokeModelApi(chatCompletionRequest);
            
            if (response.isSuccess()) {
                log.info("Zhipuai API websocket response success, starting stream processing");
                
                // 使用AtomicBoolean来标记是否是第一个消息，参考官方示例
                java.util.concurrent.atomic.AtomicBoolean isFirst = new java.util.concurrent.atomic.AtomicBoolean(true);
                
                // 使用mapStreamToAccumulator方法处理流式响应，参考官方示例
                ChatMessageAccumulator chatMessageAccumulator = mapStreamToAccumulator(response.getFlowable())
                        .doOnNext(accumulator -> {
                            try {
                                if (isFirst.getAndSet(false)) {
                                    log.info("Zhipuai API websocket Response: ");
                                }
                                
                                log.info("Zhipuai API websocket accumulator received: {}", accumulator);
                                log.info("Zhipuai API websocket accumulator class: {}", accumulator.getClass().getName());
                                
                                Object delta = accumulator.getDelta();
                                log.info("Zhipuai API websocket delta: {}", delta);
                                log.info("Zhipuai API websocket delta class: {}", delta != null ? delta.getClass().getName() : "null");
                                
                                // 处理tool_calls（如果有的话）
                                if (delta != null && delta instanceof com.zhipu.oapi.service.v4.model.ChatMessage) {
                                    com.zhipu.oapi.service.v4.model.ChatMessage chatMessage = (com.zhipu.oapi.service.v4.model.ChatMessage) delta;
                                    if (chatMessage.getTool_calls() != null) {
                                        log.info("Zhipuai API websocket tool_calls: {}", chatMessage.getTool_calls());
                                    }
                                }
                                
                                // 处理content
                                if (delta != null && delta instanceof com.zhipu.oapi.service.v4.model.ChatMessage) {
                                    Object content = ((com.zhipu.oapi.service.v4.model.ChatMessage) delta).getContent();
                                    log.info("Zhipuai API websocket content: {}", content);
                                    if (content != null) {
                                        String contentStr = content.toString();
                                        if (!contentStr.isEmpty() && !contentStr.equals("null")) {
                                            sendMessageWebsocket(MessageTypeEnum.STREAM, contentStr, messageProtobufReply);
                                        }
                                    }
                                } else {
                                    log.info("Zhipuai API websocket delta is not ChatMessage instance, delta type: {}", 
                                            delta != null ? delta.getClass().getName() : "null");
                                    // 尝试直接处理delta
                                    if (delta != null) {
                                        String deltaStr = delta.toString();
                                        log.info("Zhipuai API websocket delta as string: {}", deltaStr);
                                        if (!deltaStr.isEmpty() && !deltaStr.equals("null")) {
                                            sendMessageWebsocket(MessageTypeEnum.STREAM, deltaStr, messageProtobufReply);
                                        }
                                    }
                                }
                                success[0] = true;
                            } catch (Exception e) {
                                log.error("Error processing stream response", e);
                                success[0] = false;
                            }
                        })
                        .doOnComplete(() -> {
                            log.info("Zhipuai API websocket stream completed");
                            
                            // 从最终的accumulator中提取token使用情况
                            if (finalAccumulator[0] != null) {
                                tokenUsage[0] = extractTokenUsageFromAccumulator(finalAccumulator[0]);
                                log.info("Zhipuai API websocket tokenUsage from accumulator: {}", tokenUsage[0]);
                            } else {
                                // 如果无法获取实际token信息，使用估算
                                tokenUsage[0] = estimateTokenUsageFromMessage(message);
                                log.info("Zhipuai API websocket estimated tokenUsage: {}", tokenUsage[0]);
                            }
                            
                            // 记录token使用情况
                            long responseTime = System.currentTimeMillis() - startTime;
                            String modelType = (llm != null && llm.getModel() != null) ? llm.getModel() : zhipuaiChatConfig.getModel();
                            log.info("Zhipuai API websocket recording token usage - prompt: {}, completion: {}, total: {}, model: {}, responseTime: {}ms", 
                                    tokenUsage[0].getPromptTokens(), tokenUsage[0].getCompletionTokens(), tokenUsage[0].getTotalTokens(), modelType, responseTime);
                            recordAiTokenUsage(robot, LlmConsts.ZHIPUAI, modelType, 
                                    tokenUsage[0].getPromptTokens(), tokenUsage[0].getCompletionTokens(), success[0], responseTime);
                        })
                        .doOnError(error -> {
                            log.error("Zhipuai API websocket stream error: ", error);
                            sendMessageWebsocket(MessageTypeEnum.ERROR, "服务暂时不可用，请稍后重试", messageProtobufReply);
                            success[0] = false;
                        })
                        .lastElement()
                        .blockingGet();
                
                // 保存最终的accumulator用于token统计
                finalAccumulator[0] = chatMessageAccumulator;
                
            } else {
                log.error("Zhipuai API websocket error: {}", response.getError());
                sendMessageWebsocket(MessageTypeEnum.ERROR, "服务暂时不可用，请稍后重试", messageProtobufReply);
            }
        } catch (Exception e) {
            log.error("Error in processPromptWebsocket", e);
            sendMessageWebsocket(MessageTypeEnum.ERROR, "服务暂时不可用，请稍后重试", messageProtobufReply);
        }
    }

    /**
     * 方式2：同步调用 - 实现BaseSpringAIService的抽象方法
     */
    @Override
    protected String processPromptSync(String message, RobotProtobuf robot) {
        // 添加请求日志
        RobotLlm llm = robot != null ? robot.getLlm() : null;
        log.info("Zhipuai API sync request - model: {}, message length: {}, robot: {}", 
                (llm != null && llm.getModel() != null) ? llm.getModel() : zhipuaiChatConfig.getModel(), 
                message.length(), robot != null ? robot.getUid() : "null");

        long startTime = System.currentTimeMillis();
        boolean success = false;
        TokenUsage tokenUsage = new TokenUsage(0, 0, 0);
        
        try {
            if (client == null) {
                log.error("Zhipuai API client is null");
                return "Zhipuai client is not available";
            }

            ChatCompletionRequest chatCompletionRequest = createDynamicRequest(llm, message, false);
            
            log.info("Zhipuai API invoking sync model with requestId: {}", chatCompletionRequest.getRequestId());
            ModelApiResponse response = client.invokeModelApi(chatCompletionRequest);
            
            if (response.isSuccess() && response.getData() != null) {
                log.info("Zhipuai API sync response success");
                
                // 提取token使用情况
                tokenUsage = extractZhipuaiTokenUsage(response);
                log.info("Zhipuai API sync tokenUsage after extraction: {}", tokenUsage);
                
                Object content = response.getData().getChoices().get(0).getMessage().getContent();
                success = true;
                return content != null ? content.toString() : null;
            } else {
                log.error("Zhipuai API sync error: {}", response.getError());
                return "Error: " + (response.getError() != null ? response.getError().getMessage() : "Unknown error");
            }
        } catch (Exception e) {
            log.error("Error in processPromptSync", e);
            return "Error: " + e.getMessage();
        } finally {
            // 记录token使用情况
            long responseTime = System.currentTimeMillis() - startTime;
            String modelType = (llm != null && llm.getModel() != null) ? llm.getModel() : zhipuaiChatConfig.getModel();
            log.info("Zhipuai API sync recording token usage - prompt: {}, completion: {}, total: {}, model: {}, responseTime: {}ms", 
                    tokenUsage.getPromptTokens(), tokenUsage.getCompletionTokens(), tokenUsage.getTotalTokens(), modelType, responseTime);
            recordAiTokenUsage(robot, LlmConsts.ZHIPUAI, modelType, 
                    tokenUsage.getPromptTokens(), tokenUsage.getCompletionTokens(), success, responseTime);
        }
    }

    /**
     * 方式3：SSE方式调用 - 实现BaseSpringAIService的抽象方法
     */
    @Override
    protected void processPromptSse(Prompt prompt, RobotProtobuf robot, MessageProtobuf messageProtobufQuery,
            MessageProtobuf messageProtobufReply, SseEmitter emitter) {
        // 从robot中获取llm配置
        RobotLlm llm = robot.getLlm();
        String message = extractTextFromPrompt(prompt);
        log.info("Zhipuai API SSE message: {}", message);
        
        // 添加请求日志
        log.info("Zhipuai API SSE request - model: {}, message length: {}, robot: {}", 
                (llm != null && llm.getModel() != null) ? llm.getModel() : zhipuaiChatConfig.getModel(), 
                message.length(), robot != null ? robot.getUid() : "null");
        
        // 发送起始消息
        sendStreamStartMessage(messageProtobufReply, emitter, "正在思考中...");

        long startTime = System.currentTimeMillis();
        final boolean[] success = {false};
        final TokenUsage[] tokenUsage = {new TokenUsage(0, 0, 0)};
        final ChatMessageAccumulator[] finalAccumulator = {null};

        try {
            if (client == null) {
                log.error("Zhipuai API client is null");
                handleSseError(new Exception("Zhipuai client is not available"), messageProtobufQuery, messageProtobufReply, emitter);
                return;
            }

            ChatCompletionRequest chatCompletionRequest = createDynamicRequest(llm, message, true);
            
            log.info("Zhipuai API invoking SSE model with requestId: {}", chatCompletionRequest.getRequestId());
            ModelApiResponse response = client.invokeModelApi(chatCompletionRequest);
            
            if (response.isSuccess()) {
                log.info("Zhipuai API SSE response success, starting stream processing");
                
                // 使用AtomicBoolean来标记是否是第一个消息，参考官方示例
                java.util.concurrent.atomic.AtomicBoolean isFirst = new java.util.concurrent.atomic.AtomicBoolean(true);
                
                // 使用mapStreamToAccumulator方法处理流式响应，参考官方示例
                ChatMessageAccumulator chatMessageAccumulator = mapStreamToAccumulator(response.getFlowable())
                        .doOnNext(accumulator -> {
                            try {
                                if (isFirst.getAndSet(false)) {
                                    log.info("Zhipuai API SSE Response: ");
                                }
                                
                                log.info("Zhipuai API SSE accumulator received: {}", accumulator);
                                log.info("Zhipuai API SSE accumulator class: {}", accumulator.getClass().getName());
                                
                                Object delta = accumulator.getDelta();
                                log.info("Zhipuai API SSE delta: {}", delta);
                                log.info("Zhipuai API SSE delta class: {}", delta != null ? delta.getClass().getName() : "null");
                                
                                // 处理tool_calls（如果有的话）
                                if (delta != null && delta instanceof com.zhipu.oapi.service.v4.model.ChatMessage) {
                                    com.zhipu.oapi.service.v4.model.ChatMessage chatMessage = (com.zhipu.oapi.service.v4.model.ChatMessage) delta;
                                    if (chatMessage.getTool_calls() != null) {
                                        log.info("Zhipuai API SSE tool_calls: {}", chatMessage.getTool_calls());
                                    }
                                }
                                
                                // 处理content
                                if (delta != null && delta instanceof com.zhipu.oapi.service.v4.model.ChatMessage) {
                                    Object content = ((com.zhipu.oapi.service.v4.model.ChatMessage) delta).getContent();
                                    log.info("Zhipuai API SSE content: {}", content);
                                    if (content != null) {
                                        String contentStr = content.toString();
                                        if (!contentStr.isEmpty() && !contentStr.equals("null")) {
                                            sendStreamMessage(messageProtobufQuery, messageProtobufReply, emitter, contentStr);
                                        }
                                    }
                                } else {
                                    log.info("Zhipuai API SSE delta is not ChatMessage instance, delta type: {}", 
                                            delta != null ? delta.getClass().getName() : "null");
                                    // 尝试直接处理delta
                                    if (delta != null) {
                                        String deltaStr = delta.toString();
                                        log.info("Zhipuai API SSE delta as string: {}", deltaStr);
                                        if (!deltaStr.isEmpty() && !deltaStr.equals("null")) {
                                            sendStreamMessage(messageProtobufQuery, messageProtobufReply, emitter, deltaStr);
                                        }
                                    }
                                }
                                success[0] = true;
                            } catch (Exception e) {
                                log.error("Zhipuai API SSE error sending data: ", e);
                                success[0] = false;
                            }
                        })
                        .doOnComplete(() -> {
                            try {
                                log.info("Zhipuai API SSE stream completed");
                                
                                // 从最终的accumulator中提取token使用情况
                                if (finalAccumulator[0] != null) {
                                    tokenUsage[0] = extractTokenUsageFromAccumulator(finalAccumulator[0]);
                                    log.info("Zhipuai API SSE tokenUsage from accumulator: {}", tokenUsage[0]);
                                } else {
                                    // 如果无法获取实际token信息，使用估算
                                    tokenUsage[0] = estimateTokenUsageFromMessage(message);
                                    log.info("Zhipuai API SSE estimated tokenUsage: {}", tokenUsage[0]);
                                }
                                
                                sendStreamEndMessage(messageProtobufQuery, messageProtobufReply, emitter, 
                                        tokenUsage[0].getPromptTokens(), tokenUsage[0].getCompletionTokens(), tokenUsage[0].getTotalTokens());
                            } catch (Exception e) {
                                log.error("Zhipuai API SSE error completing stream: ", e);
                            }
                        })
                        .doOnError(error -> {
                            log.error("Zhipuai API SSE stream error: ", error);
                            handleSseError(error, messageProtobufQuery, messageProtobufReply, emitter);
                            success[0] = false;
                        })
                        .lastElement()
                        .blockingGet();
                
                // 保存最终的accumulator用于token统计
                finalAccumulator[0] = chatMessageAccumulator;
                
            } else {
                log.error("Zhipuai API SSE error: {}", response.getError());
                handleSseError(new Exception(response.getError() != null ? response.getError().getMessage() : "Unknown error"), 
                        messageProtobufQuery, messageProtobufReply, emitter);
            }
        } catch (Exception e) {
            log.error("Zhipuai API SSE error in processPromptSse: ", e);
            handleSseError(e, messageProtobufQuery, messageProtobufReply, emitter);
        } finally {
            // 记录token使用情况
            long responseTime = System.currentTimeMillis() - startTime;
            String modelType = (llm != null && llm.getModel() != null) ? llm.getModel() : zhipuaiChatConfig.getModel();
            log.info("Zhipuai API SSE recording token usage - prompt: {}, completion: {}, total: {}, model: {}, responseTime: {}ms", 
                    tokenUsage[0].getPromptTokens(), tokenUsage[0].getCompletionTokens(), tokenUsage[0].getTotalTokens(), modelType, responseTime);
            recordAiTokenUsage(robot, LlmConsts.ZHIPUAI, modelType, 
                    tokenUsage[0].getPromptTokens(), tokenUsage[0].getCompletionTokens(), success[0], responseTime);
        }
    }

    /**
     * 从Prompt中提取文本内容
     */
    private String extractTextFromPrompt(Prompt prompt) {
        if (prompt == null || prompt.getInstructions() == null) {
            return "";
        }
        // Spring AI Prompt的getInstructions()返回的是List<Message>
        // 我们需要找到UserMessage并提取其内容
        for (org.springframework.ai.chat.messages.Message message : prompt.getInstructions()) {
            if (message instanceof org.springframework.ai.chat.messages.UserMessage) {
                return ((org.springframework.ai.chat.messages.UserMessage) message).getText();
            }
        }
        return "";
    }

    /**
     * 角色扮演聊天
     */
    public String rolePlayChat(String message, String userInfo, String botInfo, String botName, String userName) {
        // 添加请求日志
        log.info("Zhipuai API roleplay request - message length: {}, userInfo: {}, botInfo: {}, botName: {}, userName: {}", 
                message.length(), userInfo, botInfo, botName, userName);
        
        long startTime = System.currentTimeMillis();
        
        try {
            if (client == null) {
                log.error("Zhipuai API client is null");
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
            
            log.info("Zhipuai API roleplay invoking model with requestId: {}", requestId);
            ModelApiResponse response = client.invokeModelApi(chatCompletionRequest);
            
            if (response.isSuccess() && response.getData() != null) {
                log.info("Zhipuai API roleplay response success");
                
                // 提取token使用情况
                TokenUsage tokenUsage = extractZhipuaiTokenUsage(response);
                log.info("Zhipuai API roleplay tokenUsage: {}", tokenUsage);
                
                Object content = response.getData().getChoices().get(0).getMessage().getContent();
                return content != null ? content.toString() : null;
            } else {
                log.error("Zhipuai API roleplay error: {}", response.getError());
                return "Error: " + (response.getError() != null ? response.getError().getMessage() : "Unknown error");
            }
        } catch (Exception e) {
            log.error("Zhipuai API roleplay error: ", e);
            return "Error: " + e.getMessage();
        } finally {
            long responseTime = System.currentTimeMillis() - startTime;
            log.info("Zhipuai API roleplay completed in {}ms", responseTime);
        }
    }

    /**
     * Function Calling 聊天
     */
    public String functionCallingChat(String message, List<ChatFunction> functions) {
        return functionCallingChat(message, null, null, functions);
    }

    /**
     * Function Calling 聊天（带自定义参数）
     */
    public String functionCallingChat(String message, String model, Double temperature, List<ChatFunction> functions) {
        // 添加请求日志
        log.info("Zhipuai API function calling request - message length: {}, model: {}, temperature: {}, functions count: {}", 
                message.length(), model, temperature, functions != null ? functions.size() : 0);
        
        long startTime = System.currentTimeMillis();
        
        try {
            if (client == null) {
                log.error("Zhipuai API client is null");
                return "Zhipuai client is not available";
            }

            List<ChatMessage> messages = new ArrayList<>();
            ChatMessage chatMessage = new ChatMessage(ChatMessageRole.USER.value(), message);
            messages.add(chatMessage);

            List<ChatTool> chatToolList = new ArrayList<>();
            for (ChatFunction function : functions) {
                ChatTool chatTool = new ChatTool();
                chatTool.setType(ChatToolType.FUNCTION.value());
                chatTool.setFunction(function);
                chatToolList.add(chatTool);
            }

            String requestId = String.format("function-%d", System.currentTimeMillis());
            
            ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                    .model(model != null ? model : zhipuaiChatConfig.getModel())
                    .stream(Boolean.FALSE)
                    .invokeMethod(Constants.invokeMethod)
                    .messages(messages)
                    .requestId(requestId)
                    .temperature(temperature != null ? temperature.floatValue() : (float) zhipuaiChatConfig.getTemperature())
                    .tools(chatToolList)
                    .toolChoice("auto")
                    .build();
            
            log.info("Zhipuai API function calling invoking model with requestId: {}", requestId);
            ModelApiResponse response = client.invokeModelApi(chatCompletionRequest);
            
            if (response.isSuccess() && response.getData() != null) {
                log.info("Zhipuai API function calling response success");
                
                // 提取token使用情况
                TokenUsage tokenUsage = extractZhipuaiTokenUsage(response);
                log.info("Zhipuai API function calling tokenUsage: {}", tokenUsage);
                
                Object content = response.getData().getChoices().get(0).getMessage().getContent();
                return content != null ? content.toString() : null;
            } else {
                log.error("Zhipuai API function calling error: {}", response.getError());
                return "Error: " + (response.getError() != null ? response.getError().getMessage() : "Unknown error");
            }
        } catch (Exception e) {
            log.error("Zhipuai API function calling error: ", e);
            return "Error: " + e.getMessage();
        } finally {
            long responseTime = System.currentTimeMillis() - startTime;
            log.info("Zhipuai API function calling completed in {}ms", responseTime);
        }
    }

    /**
     * 流式 Function Calling 聊天
     */
    public Flux<String> functionCallingChatStream(String message, List<ChatFunction> functions) {
        return functionCallingChatStream(message, null, null, functions);
    }

    /**
     * 流式 Function Calling 聊天（带自定义参数）
     */
    public Flux<String> functionCallingChatStream(String message, String model, Double temperature, List<ChatFunction> functions) {
        try {
            if (client == null) {
                return Flux.just("Zhipuai client is not available");
            }

            List<ChatMessage> messages = new ArrayList<>();
            ChatMessage chatMessage = new ChatMessage(ChatMessageRole.USER.value(), message);
            messages.add(chatMessage);

            List<ChatTool> chatToolList = new ArrayList<>();
            for (ChatFunction function : functions) {
                ChatTool chatTool = new ChatTool();
                chatTool.setType(ChatToolType.FUNCTION.value());
                chatTool.setFunction(function);
                chatToolList.add(chatTool);
            }

            String requestId = String.format("function-stream-%d", System.currentTimeMillis());
            
            ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                    .model(model != null ? model : zhipuaiChatConfig.getModel())
                    .stream(Boolean.TRUE)
                    .messages(messages)
                    .requestId(requestId)
                    .temperature(temperature != null ? temperature.floatValue() : (float) zhipuaiChatConfig.getTemperature())
                    .tools(chatToolList)
                    .toolChoice("auto")
                    .build();
            
            ModelApiResponse response = client.invokeModelApi(chatCompletionRequest);
            
            if (response.isSuccess()) {
                return Flux.from(mapStreamToAccumulator(response.getFlowable()).map(accumulator -> {
                    log.info("Zhipuai API function calling accumulator received: {}", accumulator);
                    log.info("Zhipuai API function calling accumulator class: {}", accumulator.getClass().getName());
                    
                    Object delta = accumulator.getDelta();
                    log.info("Zhipuai API function calling delta: {}", delta);
                    log.info("Zhipuai API function calling delta class: {}", delta != null ? delta.getClass().getName() : "null");
                    
                    // 处理tool_calls（如果有的话）
                    if (delta != null && delta instanceof com.zhipu.oapi.service.v4.model.ChatMessage) {
                        com.zhipu.oapi.service.v4.model.ChatMessage deltaMessage = (com.zhipu.oapi.service.v4.model.ChatMessage) delta;
                        if (deltaMessage.getTool_calls() != null) {
                            log.info("Zhipuai API function calling tool_calls: {}", deltaMessage.getTool_calls());
                        }
                    }
                    
                    if (delta instanceof com.zhipu.oapi.service.v4.model.ChatMessage) {
                        Object content = ((com.zhipu.oapi.service.v4.model.ChatMessage) delta).getContent();
                        log.info("Zhipuai API function calling content: {}", content);
                        return content != null ? content.toString() : "";
                    } else if (delta != null) {
                        String deltaStr = delta.toString();
                        log.info("Zhipuai API function calling delta as string: {}", deltaStr);
                        return deltaStr;
                    }
                    return "";
                }));
            } else {
                log.error("Zhipuai API error: {}", response.getError());
                return Flux.just("Error: " + (response.getError() != null ? response.getError().getMessage() : "Unknown error"));
            }
        } catch (Exception e) {
            log.error("Error in functionCallingChatStream", e);
            return Flux.just("Error: " + e.getMessage());
        }
    }

    /**
     * 图像生成 - 暂不支持，需要等待SDK更新
     */
    public String generateImage(String prompt) {
        return "Image generation is not supported in current SDK version";
    }

    /**
     * 图像生成（带请求ID）- 暂不支持，需要等待SDK更新
     */
    public String generateImage(String prompt, String requestId) {
        return "Image generation is not supported in current SDK version";
    }

    /**
     * 向量嵌入 - 暂不支持，需要等待SDK更新
     */
    public List<Double> getEmbedding(String text) {
        log.warn("Embedding is not supported in current SDK version");
        return new ArrayList<>();
    }

    /**
     * 批量向量嵌入 - 暂不支持，需要等待SDK更新
     */
    public List<List<Double>> getEmbeddings(List<String> texts) {
        log.warn("Embeddings is not supported in current SDK version");
        return new ArrayList<>();
    }

    /**
     * 语音合成 - 暂不支持，需要等待SDK更新
     */
    public File generateSpeech(String text, String voice, String responseFormat) {
        log.warn("Speech synthesis is not supported in current SDK version");
        return null;
    }

    /**
     * 自定义语音合成 - 暂不支持，需要等待SDK更新
     */
    public File generateCustomSpeech(String text, String voiceText, File voiceData, String responseFormat) {
        log.warn("Custom speech synthesis is not supported in current SDK version");
        return null;
    }

    /**
     * 文件上传 - 暂不支持，需要等待SDK更新
     */
    public String uploadFile(String filePath, String purpose) {
        return "File upload is not supported in current SDK version";
    }

    /**
     * 查询文件列表 - 暂不支持，需要等待SDK更新
     */
    public List<Map<String, Object>> queryFiles() {
        log.warn("File query is not supported in current SDK version");
        return new ArrayList<>();
    }

    /**
     * 下载文件内容 - 暂不支持，需要等待SDK更新
     */
    public File downloadFile(String fileId, String outputPath) {
        log.warn("File download is not supported in current SDK version");
        return null;
    }

    /**
     * 创建微调任务 - 暂不支持，需要等待SDK更新
     */
    public String createFineTuningJob(String model, String trainingFile) {
        return "Fine-tuning is not supported in current SDK version";
    }

    /**
     * 查询微调任务 - 暂不支持，需要等待SDK更新
     */
    public Map<String, Object> queryFineTuningJob(String jobId) {
        log.warn("Fine-tuning query is not supported in current SDK version");
        return new HashMap<>();
    }

    /**
     * 异步聊天
     */
    public String chatAsync(String message) {
        // 添加请求日志
        log.info("Zhipuai API async request - message length: {}", message.length());
        
        long startTime = System.currentTimeMillis();
        
        try {
            if (client == null) {
                log.error("Zhipuai API client is null");
                return "Zhipuai client is not available";
            }

            List<ChatMessage> messages = new ArrayList<>();
            ChatMessage chatMessage = new ChatMessage(ChatMessageRole.USER.value(), message);
            messages.add(chatMessage);

            ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                    .model(zhipuaiChatConfig.getModel())
                    .stream(Boolean.FALSE)
                    .invokeMethod(Constants.invokeMethodAsync)
                    .messages(messages)
                    .build();
            
            log.info("Zhipuai API async invoking model");
            ModelApiResponse response = client.invokeModelApi(chatCompletionRequest);
            
            if (response.isSuccess() && response.getData() != null) {
                String taskId = response.getData().getId();
                log.info("Zhipuai API async task created with taskId: {}", taskId);
                
                // 轮询获取结果
                return pollAsyncResult(taskId);
            } else {
                log.error("Zhipuai API async error: {}", response.getError());
                return "Error: " + (response.getError() != null ? response.getError().getMessage() : "Unknown error");
            }
        } catch (Exception e) {
            log.error("Zhipuai API async error: ", e);
            return "Error: " + e.getMessage();
        } finally {
            long responseTime = System.currentTimeMillis() - startTime;
            log.info("Zhipuai API async completed in {}ms", responseTime);
        }
    }

    /**
     * 轮询异步结果
     */
    private String pollAsyncResult(String taskId) {
        log.info("Zhipuai API starting async result polling for taskId: {}", taskId);
        
        try {
            int maxAttempts = 30; // 最多轮询30次
            int attempt = 0;
            
            while (attempt < maxAttempts) {
                log.debug("Zhipuai API polling attempt {}/{} for taskId: {}", attempt + 1, maxAttempts, taskId);
                
                QueryModelResultRequest request = new QueryModelResultRequest();
                request.setTaskId(taskId);
                
                QueryModelResultResponse response = client.queryModelResult(request);
                
                if (response.isSuccess() && response.getData() != null) {
                    Object taskStatus = response.getData().getTaskStatus();
                    log.debug("Zhipuai API task status: {} for taskId: {}", taskStatus, taskId);
                    
                    if ("SUCCESS".equals(taskStatus.toString())) {
                        log.info("Zhipuai API async task completed successfully for taskId: {}", taskId);
                        Object content = response.getData().getChoices().get(0).getMessage().getContent();
                        return content != null ? content.toString() : null;
                    } else if ("FAILED".equals(taskStatus.toString())) {
                        log.error("Zhipuai API async task failed for taskId: {}", taskId);
                        return "Task failed";
                    }
                } else {
                    log.warn("Zhipuai API async polling response not successful for taskId: {}", taskId);
                }
                
                attempt++;
                Thread.sleep(2000); // 等待2秒后重试
            }
            
            log.error("Zhipuai API async task timeout after {} attempts for taskId: {}", maxAttempts, taskId);
            return "Task timeout after " + maxAttempts + " attempts";
        } catch (Exception e) {
            log.error("Zhipuai API error polling async result for taskId: {}", taskId, e);
            return "Error: " + e.getMessage();
        }
    }

    /**
     * 带Web搜索的聊天
     */
    public String chatWithWebSearch(String message, String searchQuery) {
        // 添加请求日志
        log.info("Zhipuai API web search request - message length: {}, searchQuery: {}", 
                message.length(), searchQuery);
        
        long startTime = System.currentTimeMillis();
        
        try {
            if (client == null) {
                log.error("Zhipuai API client is null");
                return "Zhipuai client is not available";
            }

            List<ChatMessage> messages = new ArrayList<>();
            ChatMessage chatMessage = new ChatMessage(ChatMessageRole.USER.value(), message);
            messages.add(chatMessage);

            List<ChatTool> chatToolList = new ArrayList<>();
            
            // 添加Web搜索工具
            ChatTool webSearchTool = new ChatTool();
            webSearchTool.setType(ChatToolType.WEB_SEARCH.value());
            WebSearch webSearch = new WebSearch();
            webSearch.setSearch_query(searchQuery);
            webSearch.setSearch_result(true);
            webSearch.setEnable(true);
            webSearchTool.setWeb_search(webSearch);
            chatToolList.add(webSearchTool);

            String requestId = String.format("websearch-%d", System.currentTimeMillis());
            
            ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                    .model(zhipuaiChatConfig.getModel())
                    .stream(Boolean.FALSE)
                    .invokeMethod(Constants.invokeMethod)
                    .messages(messages)
                    .requestId(requestId)
                    .tools(chatToolList)
                    .toolChoice("auto")
                    .build();
            
            log.info("Zhipuai API web search invoking model with requestId: {}", requestId);
            ModelApiResponse response = client.invokeModelApi(chatCompletionRequest);
            
            if (response.isSuccess() && response.getData() != null) {
                log.info("Zhipuai API web search response success");
                
                // 提取token使用情况
                TokenUsage tokenUsage = extractZhipuaiTokenUsage(response);
                log.info("Zhipuai API web search tokenUsage: {}", tokenUsage);
                
                Object content = response.getData().getChoices().get(0).getMessage().getContent();
                return content != null ? content.toString() : null;
            } else {
                log.error("Zhipuai API web search error: {}", response.getError());
                return "Error: " + (response.getError() != null ? response.getError().getMessage() : "Unknown error");
            }
        } catch (Exception e) {
            log.error("Zhipuai API web search error: ", e);
            return "Error: " + e.getMessage();
        } finally {
            long responseTime = System.currentTimeMillis() - startTime;
            log.info("Zhipuai API web search completed in {}ms", responseTime);
        }
    }

    /**
     * 语音模型聊天 - 暂不支持，需要等待SDK更新
     */
    public String chatWithVoice(String message) {
        log.warn("Voice chat is not supported in current SDK version");
        return "Voice chat is not supported in current SDK version";
    }

    /**
     * 将流式响应转换为Accumulator，参考官方示例
     */
    private io.reactivex.Flowable<ChatMessageAccumulator> mapStreamToAccumulator(io.reactivex.Flowable<ModelData> flowable) {
        return flowable.map(chunk -> {
            return new ChatMessageAccumulator(
                chunk.getChoices().get(0).getDelta(), 
                null, 
                chunk.getChoices().get(0), 
                chunk.getUsage(), 
                chunk.getCreated(), 
                chunk.getId()
            );
        });
    }

    /**
     * 从ChatMessageAccumulator中提取token使用情况
     */
    private TokenUsage extractTokenUsageFromAccumulator(ChatMessageAccumulator accumulator) {
        try {
            if (accumulator == null || accumulator.getUsage() == null) {
                log.warn("Zhipuai API accumulator or usage is null");
                return new TokenUsage(0, 0, 0);
            }
            
            var usage = accumulator.getUsage();
            log.info("Zhipuai API accumulator usage: {}", usage);
            
            long promptTokens = 0;
            long completionTokens = 0;
            long totalTokens = 0;
            
            // 尝试从usage中提取token信息
            try {
                java.lang.reflect.Field promptField = usage.getClass().getDeclaredField("promptTokens");
                promptField.setAccessible(true);
                Object promptObj = promptField.get(usage);
                if (promptObj instanceof Number) {
                    promptTokens = ((Number) promptObj).longValue();
                }
            } catch (Exception e) {
                log.debug("Could not get promptTokens field: {}", e.getMessage());
            }
            
            try {
                java.lang.reflect.Field completionField = usage.getClass().getDeclaredField("completionTokens");
                completionField.setAccessible(true);
                Object completionObj = completionField.get(usage);
                if (completionObj instanceof Number) {
                    completionTokens = ((Number) completionObj).longValue();
                }
            } catch (Exception e) {
                log.debug("Could not get completionTokens field: {}", e.getMessage());
            }
            
            try {
                java.lang.reflect.Field totalField = usage.getClass().getDeclaredField("totalTokens");
                totalField.setAccessible(true);
                Object totalObj = totalField.get(usage);
                if (totalObj instanceof Number) {
                    totalTokens = ((Number) totalObj).longValue();
                }
            } catch (Exception e) {
                log.debug("Could not get totalTokens field: {}", e.getMessage());
            }
            
            // 如果没有total_tokens，计算它
            if (totalTokens == 0 && (promptTokens > 0 || completionTokens > 0)) {
                totalTokens = promptTokens + completionTokens;
            }
            
            log.info("Zhipuai API accumulator token extraction result - prompt: {}, completion: {}, total: {}", 
                    promptTokens, completionTokens, totalTokens);
            
            return new TokenUsage(promptTokens, completionTokens, totalTokens);
            
        } catch (Exception e) {
            log.error("Error extracting token usage from accumulator", e);
            return new TokenUsage(0, 0, 0);
        }
    }

    /**
     * 将流式响应转换为Flux
     */
    private Flux<String> mapStreamToFlux(io.reactivex.Flowable<ChatMessageAccumulator> flowable) {
        return Flux.from(flowable.map(accumulator -> {
            log.info("Zhipuai API mapStreamToFlux accumulator received: {}", accumulator);
            log.info("Zhipuai API mapStreamToFlux accumulator class: {}", accumulator.getClass().getName());
            
            if (accumulator.getDelta() != null && accumulator.getDelta().getContent() != null) {
                Object content = accumulator.getDelta().getContent();
                log.info("Zhipuai API mapStreamToFlux content: {}", content);
                return content.toString();
            }
            log.info("Zhipuai API mapStreamToFlux no content found");
            return "";
        }));
    }

    /**
     * 测试token提取功能
     * 用于调试token计数问题
     */
    public void testTokenExtraction() {
        try {
            log.info("Zhipuai API testing token extraction...");
            
            if (client == null) {
                log.error("Zhipuai API client is null");
                return;
            }
            
            // 创建一个简单的测试请求
            String testMessage = "Hello, this is a test message for token counting.";
            ChatCompletionRequest chatCompletionRequest = createDynamicRequest(null, testMessage, false);
            
            log.info("Zhipuai API making test call with message: {}", testMessage);
            
            // 调用API
            ModelApiResponse response = client.invokeModelApi(chatCompletionRequest);
            
            log.info("Zhipuai API test response success: {}", response.isSuccess());
            log.info("Zhipuai API test response has data: {}", response.getData() != null);
            
            if (response.getData() != null) {
                log.info("Zhipuai API test response data class: {}", response.getData().getClass().getName());
                log.info("Zhipuai API test response data: {}", response.getData());
            }
            
            // 测试手动token提取
            TokenUsage manualUsage = extractZhipuaiTokenUsage(response);
            log.info("Zhipuai API test manual token extraction result: {}", manualUsage);
            
            // 测试token估算功能
            TokenUsage estimatedUsage = estimateTokenUsageFromResponse(response);
            log.info("Zhipuai API test estimated token usage result: {}", estimatedUsage);
            
            // 测试token估算算法
            String testText = "这是一个测试文本，包含中英文混合内容。This is a test text with mixed Chinese and English content.";
            long estimatedTokens = estimateTokens(testText);
            log.info("Zhipuai API test token estimation for text '{}': {} tokens", testText, estimatedTokens);
            
            // 提取文本内容
            String textContent = "";
            if (response.isSuccess() && response.getData() != null && 
                response.getData().getChoices() != null && !response.getData().getChoices().isEmpty()) {
                var choice = response.getData().getChoices().get(0);
                if (choice.getMessage() != null && choice.getMessage().getContent() != null) {
                    textContent = choice.getMessage().getContent().toString();
                }
            }
            log.info("Zhipuai API test response text: {}", textContent);
            
        } catch (Exception e) {
            log.error("Zhipuai API test token extraction error", e);
        }
    }

    /**
     * 测试流式响应功能
     * 用于调试流式响应问题
     */
    public void testStreamResponse() {
        try {
            log.info("Zhipuai API testing stream response...");
            
            if (client == null) {
                log.error("Zhipuai API client is null");
                return;
            }
            
            // 创建一个简单的测试请求
            String testMessage = "Hello, this is a test message for stream response.";
            ChatCompletionRequest chatCompletionRequest = createDynamicRequest(null, testMessage, true);
            
            log.info("Zhipuai API making stream test call with message: {}", testMessage);
            
            // 调用API
            ModelApiResponse response = client.invokeModelApi(chatCompletionRequest);
            
            log.info("Zhipuai API stream test response success: {}", response.isSuccess());
            
            if (response.isSuccess()) {
                log.info("Zhipuai API stream test starting flowable processing");
                
                final int[] messageCount = {0};
                
                // 使用AtomicBoolean来标记是否是第一个消息，参考官方示例
                java.util.concurrent.atomic.AtomicBoolean isFirst = new java.util.concurrent.atomic.AtomicBoolean(true);
                
                mapStreamToAccumulator(response.getFlowable())
                        .doOnNext(accumulator -> {
                            messageCount[0]++;
                            log.info("Zhipuai API stream test message #{}: accumulator={}", messageCount[0], accumulator);
                            log.info("Zhipuai API stream test message #{}: accumulator class={}", messageCount[0], accumulator.getClass().getName());
                            
                            Object delta = accumulator.getDelta();
                            log.info("Zhipuai API stream test message #{}: delta={}", messageCount[0], delta);
                            log.info("Zhipuai API stream test message #{}: delta class={}", messageCount[0], delta != null ? delta.getClass().getName() : "null");
                            
                            if (delta instanceof com.zhipu.oapi.service.v4.model.ChatMessage) {
                                Object content = ((com.zhipu.oapi.service.v4.model.ChatMessage) delta).getContent();
                                log.info("Zhipuai API stream test message #{}: content={}", messageCount[0], content);
                            } else {
                                log.info("Zhipuai API stream test message #{}: delta is not ChatMessage", messageCount[0]);
                            }
                        })
                        .doOnComplete(() -> {
                            log.info("Zhipuai API stream test completed, total messages: {}", messageCount[0]);
                        })
                        .doOnError(error -> {
                            log.error("Zhipuai API stream test error: ", error);
                        })
                        .subscribe();
            } else {
                log.error("Zhipuai API stream test failed: {}", response.getError());
            }
            
        } catch (Exception e) {
            log.error("Zhipuai API test stream response error", e);
        }
    }

    /**
     * 简单流式测试 - 完全按照官方示例代码实现
     * 用于调试流式响应问题
     */
    public void testSimpleStream() {
        try {
            log.info("Zhipuai API testing simple stream response...");
            
            if (client == null) {
                log.error("Zhipuai API client is null");
                return;
            }
            
            // 完全按照官方示例代码实现
            List<ChatMessage> messages = new ArrayList<>();
            ChatMessage chatMessage = new ChatMessage(ChatMessageRole.USER.value(), "What is the relationship between ZhipuAI and ChatGLM?");
            messages.add(chatMessage);
            
            String requestId = String.format("your-request-id-%d", System.currentTimeMillis());
            ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                    .model(Constants.ModelChatGLM4)
                    .stream(Boolean.TRUE)
                    .messages(messages)
                    .requestId(requestId)
                    .build();
            
            log.info("Zhipuai API making simple stream test call with requestId: {}", requestId);
            
            ModelApiResponse sseModelApiResp = client.invokeModelApi(chatCompletionRequest);
            
            if (sseModelApiResp.isSuccess()) {
                log.info("Zhipuai API simple stream test response success");
                
                java.util.concurrent.atomic.AtomicBoolean isFirst = new java.util.concurrent.atomic.AtomicBoolean(true);
                final int[] messageCount = {0};
                
                mapStreamToAccumulator(sseModelApiResp.getFlowable())
                        .doOnNext(accumulator -> {
                            messageCount[0]++;
                            log.info("Zhipuai API simple stream test message #{}: accumulator: {}", messageCount[0], accumulator);
                            
                            Object delta = accumulator.getDelta();
                            log.info("Zhipuai API simple stream test message #{}: delta: {}", messageCount[0], delta);
                            
                            if (delta instanceof com.zhipu.oapi.service.v4.model.ChatMessage) {
                                Object content = ((com.zhipu.oapi.service.v4.model.ChatMessage) delta).getContent();
                                log.info("Zhipuai API simple stream test message #{}: content: {}", messageCount[0], content);
                            }
                        })
                        .doOnComplete(() -> {
                            log.info("Zhipuai API simple stream test completed, total messages: {}", messageCount[0]);
                        })
                        .doOnError(error -> {
                            log.error("Zhipuai API simple stream test error: ", error);
                        })
                        .subscribe();
            } else {
                log.error("Zhipuai API simple stream test failed: {}", sseModelApiResp.getError());
            }
            
        } catch (Exception e) {
            log.error("Zhipuai API test simple stream error", e);
        }
    }

    /**
     * 检查服务健康状态
     */
    public boolean isHealthy() {
        log.info("Zhipuai API health check started");
        
        try {
            if (client == null) {
                log.error("Zhipuai API health check failed: client is null");
                return false;
            }
            
            // 发送一个简单的测试请求
            String response = processPromptSync("Hello", null);
            boolean isHealthy = response != null && !response.startsWith("Error");
            
            log.info("Zhipuai API health check result: {}", isHealthy);
            return isHealthy;
        } catch (Exception e) {
            log.error("Zhipuai API health check failed", e);
            return false;
        }
    }
} 