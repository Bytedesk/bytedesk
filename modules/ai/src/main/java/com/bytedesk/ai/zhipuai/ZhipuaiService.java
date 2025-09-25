/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-19 09:39:15
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-25 09:30:27
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
import java.util.Optional;

import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
// import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.bytedesk.ai.provider.LlmProviderEntity;
import com.bytedesk.ai.provider.LlmProviderRestService;
import com.bytedesk.ai.robot.RobotLlm;
import com.bytedesk.ai.robot.RobotProtobuf;
import com.bytedesk.ai.springai.service.BaseSpringAIService;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.llm.LlmProviderConstants;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.MessageTypeEnum;
import com.bytedesk.core.message.content.StreamContent;
import com.zhipu.oapi.ClientV4;
import com.zhipu.oapi.Constants;
import com.zhipu.oapi.service.v4.model.ChatCompletionRequest;
import com.zhipu.oapi.service.v4.model.ChatMessage;
import com.zhipu.oapi.service.v4.model.ChatMessageRole;
import com.zhipu.oapi.service.v4.model.ModelApiResponse;
import com.zhipu.oapi.service.v4.model.ChatMessageAccumulator;
import com.zhipu.oapi.service.v4.model.ModelData;
import lombok.extern.slf4j.Slf4j;
import com.bytedesk.ai.springai.service.ChatTokenUsage;

/**
 * 智谱AI服务类
 * 使用 oapi-java-sdk 实现聊天功能，继承自BaseSpringAIService
 * https://github.com/MetaGLM/zhipuai-sdk-java-v4
 */
@Slf4j
@Service
public class ZhipuaiService extends BaseSpringAIService {

    @Autowired
    private LlmProviderRestService llmProviderRestService;

    @Autowired(required = false)
    @Qualifier("zhipuaiChatClient")
    private ClientV4 defaultClient;

    /**
     * 构造函数
     */
    public ZhipuaiService() {
        super();
    }

    /**
     * 根据机器人配置创建动态的ClientV4实例
     * 
     * @param llm 机器人LLM配置
     * @return 配置了特定参数的ClientV4
     */
    private ClientV4 createDynamicClient(RobotLlm llm) {
        if (llm == null || llm.getTextProviderUid() == null) {
            log.warn("RobotLlm or textProviderUid is null, using default client");
            return defaultClient;
        }

        Optional<LlmProviderEntity> llmProviderOptional = llmProviderRestService.findByUid(llm.getTextProviderUid());
        if (llmProviderOptional.isEmpty()) {
            log.warn("LlmProvider with uid {} not found, using default client", llm.getTextProviderUid());
            return defaultClient;
        }

        LlmProviderEntity provider = llmProviderOptional.get();
        String apiKey = provider.getApiKey();
        
        if (apiKey == null || apiKey.trim().isEmpty()) {
            log.warn("API key is not configured for provider {}, using default client", provider.getUid());
            return defaultClient;
        }

        try {
            log.info("Creating dynamic Zhipuai client with provider: {} ({})", provider.getType(), provider.getUid());
            
            return new ClientV4.Builder(apiKey)
                    .enableTokenCache()
                    .build();
        } catch (Exception e) {
            log.error("Failed to create dynamic Zhipuai client for provider {}, using default client", provider.getUid(), e);
            return defaultClient;
        }
    }

    /**
     * 根据机器人配置创建动态的聊天选项
     */
    private ChatCompletionRequest createDynamicRequest(RobotLlm llm, String message, boolean stream) {
        if (llm == null || llm.getTextModel() == null) {
            log.warn("RobotLlm or textModel is null, using default model");
            throw new IllegalArgumentException("RobotLlm or textModel cannot be null");
        }
        List<ChatMessage> messages = new ArrayList<>();
        ChatMessage chatMessage = new ChatMessage(ChatMessageRole.USER.value(), message);
        messages.add(chatMessage);

        String requestId = String.format("zhipuai-%d", System.currentTimeMillis());
        
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model(llm.getTextModel())
                .stream(stream)
                .invokeMethod(Constants.invokeMethod)
                .messages(messages)
                .requestId(requestId)
                .temperature(llm.getTemperature().floatValue())
                .build();

        // 添加请求日志
        log.info("Zhipuai API creating request - model: {}, stream: {}, requestId: {}, temperature: {}, message length: {}", 
                chatCompletionRequest.getModel(), stream, requestId, chatCompletionRequest.getTemperature(), message.length());

        return chatCompletionRequest;
    }

    /**
     * 根据机器人配置创建动态的聊天选项（支持完整的Prompt对象）
     */
    private ChatCompletionRequest createDynamicRequestFromPrompt(RobotLlm llm, Prompt prompt, boolean stream) {
        List<ChatMessage> messages = new ArrayList<>();
        
        // 将Spring AI的Message转换为智谱AI的ChatMessage
        if (prompt != null && prompt.getInstructions() != null) {
            for (Message message : prompt.getInstructions()) {
                String role;
                String content = message.getText();
                
                if (message instanceof SystemMessage) {
                    role = ChatMessageRole.SYSTEM.value();
                } else if (message instanceof UserMessage) {
                    role = ChatMessageRole.USER.value();
                } else if (message instanceof AssistantMessage) {
                    role = ChatMessageRole.ASSISTANT.value();
                } else {
                    // 对于其他类型的消息，默认作为系统消息处理
                    role = ChatMessageRole.SYSTEM.value();
                }
                
                ChatMessage chatMessage = new ChatMessage(role, content);
                messages.add(chatMessage);
            }
        }

        String requestId = String.format("zhipuai-%d", System.currentTimeMillis());
        
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model(llm.getTextModel())
                .stream(stream)
                .invokeMethod(Constants.invokeMethod)
                .messages(messages)
                .requestId(requestId)
                .temperature(llm.getTemperature().floatValue())
                .build();

        // 添加请求日志
        log.info("Zhipuai API creating request from prompt - model: {}, stream: {}, requestId: {}, temperature: {}, messages count: {}", 
                chatCompletionRequest.getModel(), stream, requestId, chatCompletionRequest.getTemperature(), messages.size());

        return chatCompletionRequest;
    }


    /**
     * 方式1：异步流式调用 - 实现BaseSpringAIService的抽象方法（带prompt参数）
     */
    @Override
    protected void processPromptWebsocket(Prompt prompt, RobotProtobuf robot, MessageProtobuf messageProtobufQuery, MessageProtobuf messageProtobufReply) {
        // 从robot中获取llm配置
        RobotLlm llm = robot.getLlm();
        log.info("Zhipuai API websocket prompt: {}", prompt);

        long startTime = System.currentTimeMillis();
        final boolean[] success = {false};
        final ChatTokenUsage[] tokenUsage = {new ChatTokenUsage(0, 0, 0)};
        final ChatMessageAccumulator[] finalAccumulator = {null};

        // 获取适当的client实例
        ClientV4 chatClient = createDynamicClient(llm);
        if (chatClient == null) {
            log.error("Failed to create Zhipuai client and no default client available, cannot process websocket request");
            sendMessageWebsocket(MessageTypeEnum.ERROR, I18Consts.I18N_SERVICE_TEMPORARILY_UNAVAILABLE, messageProtobufReply);
            return;
        }

        try {
            ChatCompletionRequest chatCompletionRequest = createDynamicRequestFromPrompt(llm, prompt, true);
            
            log.info("Zhipuai API invoking model with requestId: {}", chatCompletionRequest.getRequestId());
            ModelApiResponse response = chatClient.invokeModelApi(chatCompletionRequest);
            
            if (response.isSuccess()) {
                log.info("Zhipuai API websocket response success, starting stream processing");
                
                // 使用AtomicBoolean来标记是否是第一个消息，参考官方示例
                java.util.concurrent.atomic.AtomicBoolean isFirst = new java.util.concurrent.atomic.AtomicBoolean(true);
                
                // 使用mapStreamToAccumulator方法处理流式响应，参考官方示例
                mapStreamToAccumulator(response.getFlowable())
                        .doOnNext(accumulator -> {
                            try {
                                if (isFirst.getAndSet(false)) {
                                    log.info("Zhipuai API websocket Response: ");
                                }
                                
                                log.info("Zhipuai API websocket accumulator received: {}", accumulator);
                                log.info("Zhipuai API websocket accumulator class: {}", accumulator.getClass().getName());
                                
                                // 保存最新的accumulator用于token统计
                                finalAccumulator[0] = accumulator;
                                
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
                                            sendMessageWebsocket(MessageTypeEnum.ROBOT_STREAM, contentStr, messageProtobufReply);
                                        }
                                    }
                                } else {
                                    log.info("Zhipuai API websocket delta is not ChatMessage instance, delta type: {}", 
                                            delta != null ? delta.getClass().getName() : "null");
                                    // 尝试解析delta字符串为JSON并提取content
                                    if (delta != null) {
                                        String deltaStr = delta.toString();
                                        log.info("Zhipuai API websocket delta as string: {}", deltaStr);
                                        
                                        // 尝试从JSON字符串中提取content字段
                                        String extractedContent = extractContentFromDeltaString(deltaStr);
                                        if (extractedContent != null && !extractedContent.isEmpty()) {
                                            log.info("Zhipuai API websocket extracted content: {}", extractedContent);
                                            sendMessageWebsocket(MessageTypeEnum.ROBOT_STREAM, extractedContent, messageProtobufReply);
                                        } else if (!deltaStr.isEmpty() && !deltaStr.equals("null") && !isEmptyAssistantMessage(deltaStr)) {
                                            // 如果无法提取content，且不是空的助手消息，则发送原始delta字符串
                                            sendMessageWebsocket(MessageTypeEnum.ROBOT_STREAM, deltaStr, messageProtobufReply);
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
                                String promptText = extractTextFromPrompt(prompt);
                                tokenUsage[0] = estimateTokenUsageFromMessage(promptText);
                                log.info("Zhipuai API websocket estimated tokenUsage: {}", tokenUsage[0]);
                            }
                            
                            // 记录token使用情况
                            long responseTime = System.currentTimeMillis() - startTime;
                            String modelType = llm.getTextModel();
                            log.info("Zhipuai API websocket recording token usage - prompt: {}, completion: {}, total: {}, model: {}, responseTime: {}ms", 
                                    tokenUsage[0].getPromptTokens(), tokenUsage[0].getCompletionTokens(), tokenUsage[0].getTotalTokens(), modelType, responseTime);
                            recordAiTokenUsage(robot, LlmProviderConstants.ZHIPUAI, modelType, 
                                    tokenUsage[0].getPromptTokens(), tokenUsage[0].getCompletionTokens(), success[0], responseTime);
                        })
                        .doOnError(error -> {
                            log.error("Zhipuai API websocket stream error: ", error);
                            sendMessageWebsocket(MessageTypeEnum.ERROR, I18Consts.I18N_SERVICE_TEMPORARILY_UNAVAILABLE, messageProtobufReply);
                            success[0] = false;
                        })
                        .lastElement()
                        .blockingGet();
                
            } else {
                log.error("Zhipuai API websocket error: {}", response.getError());
                sendMessageWebsocket(MessageTypeEnum.ERROR, I18Consts.I18N_SERVICE_TEMPORARILY_UNAVAILABLE, messageProtobufReply);
            }
        } catch (Exception e) {
            log.error("Error in processPromptWebsocket", e);
            sendMessageWebsocket(MessageTypeEnum.ERROR, I18Consts.I18N_SERVICE_TEMPORARILY_UNAVAILABLE, messageProtobufReply);
        }
    }


    /**
     * 方式2：同步调用 - 实现BaseSpringAIService的抽象方法（带prompt参数）
     */
    @Override
    protected String processPromptSync(String message, RobotProtobuf robot) {
        if (robot == null || robot.getLlm() == null || robot.getLlm().getTextModel() == null) {
            log.error("Robot or RobotLlm is null, cannot process prompt sync");
            return "Error: Robot or RobotLlm is not configured";
        }
        // 从robot中获取llm配置
        RobotLlm llm = robot.getLlm();
        // 
        long startTime = System.currentTimeMillis();
        boolean success = false;
        ChatTokenUsage tokenUsage = new ChatTokenUsage(0, 0, 0);
        
        // 获取适当的client实例
        ClientV4 chatClient = createDynamicClient(llm);
        if (chatClient == null) {
            log.error("Failed to create Zhipuai client and no default client available, cannot process sync request");
            return I18Consts.I18N_SERVICE_TEMPORARILY_UNAVAILABLE;
        }
        
        try {
            ChatCompletionRequest chatCompletionRequest = createDynamicRequest(llm, message, false);
            
            log.info("Zhipuai API invoking sync model with requestId: {}", chatCompletionRequest.getRequestId());
            ModelApiResponse response = chatClient.invokeModelApi(chatCompletionRequest);
            
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
            String modelType = llm.getTextModel();
            log.info("Zhipuai API sync recording token usage - prompt: {}, completion: {}, total: {}, model: {}, responseTime: {}ms", 
                    tokenUsage.getPromptTokens(), tokenUsage.getCompletionTokens(), tokenUsage.getTotalTokens(), modelType, responseTime);
            recordAiTokenUsage(robot, LlmProviderConstants.ZHIPUAI, modelType, 
                    tokenUsage.getPromptTokens(), tokenUsage.getCompletionTokens(), success, responseTime);
        }
    }

    /**
     * 方式3：SSE方式调用 - 实现BaseSpringAIService的抽象方法（带prompt参数）
     */
    @Override
    protected void processPromptSse(Prompt prompt, RobotProtobuf robot, MessageProtobuf messageProtobufQuery,
            MessageProtobuf messageProtobufReply, List<StreamContent.SourceReference> sourceReferences, SseEmitter emitter) {
        if (robot == null || robot.getLlm() == null || robot.getLlm().getTextModel() == null) {
            log.error("Robot or RobotLlm is null, cannot process prompt SSE");
            sendSseMessage(I18Consts.I18N_SERVICE_TEMPORARILY_UNAVAILABLE, robot, messageProtobufQuery, messageProtobufReply, emitter);
            return;
        }
        // 从robot中获取llm配置
        RobotLlm llm = robot.getLlm();
        log.info("Zhipuai API SSE prompt: {}", prompt);

        // 发送起始消息
        sendStreamStartMessage(messageProtobufReply, emitter, I18Consts.I18N_THINKING);

        long startTime = System.currentTimeMillis();
        final boolean[] success = {false};
        final ChatTokenUsage[] tokenUsage = {new ChatTokenUsage(0, 0, 0)};
        final ChatMessageAccumulator[] finalAccumulator = {null};
        final String modelType = llm.getTextModel();

        // 获取适当的client实例
        ClientV4 chatClient = createDynamicClient(llm);
        if (chatClient == null) {
            log.error("Failed to create Zhipuai client and no default client available, cannot process SSE request");
            handleSseError(new Exception("Failed to create Zhipuai client"), 
                    messageProtobufQuery, messageProtobufReply, emitter);
            return;
        }

        try {
            ChatCompletionRequest chatCompletionRequest = createDynamicRequestFromPrompt(llm, prompt, true);
            
            log.info("Zhipuai API invoking SSE model with requestId: {}", chatCompletionRequest.getRequestId());
            ModelApiResponse response = chatClient.invokeModelApi(chatCompletionRequest);
            
            if (response.isSuccess()) {
                log.info("Zhipuai API SSE response success, starting stream processing");
                
                // 使用AtomicBoolean来标记是否是第一个消息，参考官方示例
                java.util.concurrent.atomic.AtomicBoolean isFirst = new java.util.concurrent.atomic.AtomicBoolean(true);
                
                // 使用mapStreamToAccumulator方法处理流式响应，参考官方示例
                mapStreamToAccumulator(response.getFlowable())
                        .doOnNext(accumulator -> {
                            try {
                                if (isFirst.getAndSet(false)) {
                                    log.info("Zhipuai API SSE Response: ");
                                }
                                
                                // 保存最新的accumulator用于token统计
                                finalAccumulator[0] = accumulator;
                                
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
                                            sendStreamMessage(messageProtobufQuery, messageProtobufReply, emitter, contentStr, null, sourceReferences);
                                        }
                                    }
                                } else {
                                    log.info("Zhipuai API SSE delta is not ChatMessage instance, delta type: {}", 
                                            delta != null ? delta.getClass().getName() : "null");
                                    // 尝试解析delta字符串为JSON并提取content
                                    if (delta != null) {
                                        String deltaStr = delta.toString();
                                        log.info("Zhipuai API SSE delta as string: {}", deltaStr);
                                        
                                        // 尝试从JSON字符串中提取content字段
                                        String extractedContent = extractContentFromDeltaString(deltaStr);
                                        if (extractedContent != null && !extractedContent.isEmpty()) {
                                            log.info("Zhipuai API SSE extracted content: {}", extractedContent);
                                            sendStreamMessage(messageProtobufQuery, messageProtobufReply, emitter, extractedContent, null, sourceReferences);
                                        } else if (!deltaStr.isEmpty() && !deltaStr.equals("null") && !isEmptyAssistantMessage(deltaStr)) {
                                            // 如果无法提取content，且不是空的助手消息，则发送原始delta字符串
                                            sendStreamMessage(messageProtobufQuery, messageProtobufReply, emitter, deltaStr, null, sourceReferences);
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
                                    String promptText = extractTextFromPrompt(prompt);
                                    tokenUsage[0] = estimateTokenUsageFromMessage(promptText);
                                    log.info("Zhipuai API SSE estimated tokenUsage: {}", tokenUsage[0]);
                                }
                                
                                sendStreamEndMessage(messageProtobufQuery, messageProtobufReply, emitter, 
                                        tokenUsage[0].getPromptTokens(), tokenUsage[0].getCompletionTokens(), tokenUsage[0].getTotalTokens(), prompt, LlmProviderConstants.ZHIPUAI, modelType);
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
            log.info("Zhipuai API SSE recording token usage - prompt: {}, completion: {}, total: {}, model: {}, responseTime: {}ms", 
                    tokenUsage[0].getPromptTokens(), tokenUsage[0].getCompletionTokens(), tokenUsage[0].getTotalTokens(), modelType, responseTime);
            recordAiTokenUsage(robot, LlmProviderConstants.ZHIPUAI, modelType, 
                    tokenUsage[0].getPromptTokens(), tokenUsage[0].getCompletionTokens(), success[0], responseTime);
        }
    }

        /**
     * 手动提取智谱AI的token使用情况
     * 从ModelApiResponse中提取token使用信息
     * 
     * @param response ModelApiResponse对象
     * @return TokenUsage对象
     */
    private ChatTokenUsage extractZhipuaiTokenUsage(ModelApiResponse response) {
        try {
            if (response == null) {
                log.warn("Zhipuai API response is null");
                return new ChatTokenUsage(0, 0, 0);
            }
            
            log.info("Zhipuai API manual token extraction - response success: {}, has data: {}", 
                    response.isSuccess(), response.getData() != null);
            
            if (!response.isSuccess() || response.getData() == null) {
                log.warn("Zhipuai API response is not successful or has no data");
                return new ChatTokenUsage(0, 0, 0);
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
                        
                        return new ChatTokenUsage(promptTokens, completionTokens, totalTokens);
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
                
                return new ChatTokenUsage(promptTokens, completionTokens, totalTokens);
            }
            
            // 如果所有方法都失败，尝试估算token使用量
            log.info("Zhipuai API all extraction methods failed, attempting to estimate token usage");
            return estimateTokenUsageFromResponse(response);
            
        } catch (Exception e) {
            log.error("Error in manual Zhipuai token extraction", e);
            return new ChatTokenUsage(0, 0, 0);
        }
    }

    /**
     * 估算token使用量（当无法从API获取实际token信息时使用）
     * 
     * @param response ModelApiResponse对象
     * @return 估算的TokenUsage对象
     */
    private ChatTokenUsage estimateTokenUsageFromResponse(ModelApiResponse response) {
        try {
            if (response == null || response.getData() == null) {
                return new ChatTokenUsage(0, 0, 0);
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
            
            return new ChatTokenUsage(promptTokens, completionTokens, totalTokens);
            
        } catch (Exception e) {
            log.error("Error estimating token usage", e);
            return new ChatTokenUsage(0, 0, 0);
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
    private ChatTokenUsage estimateTokenUsageFromMessage(String message) {
        try {
            if (message == null || message.isEmpty()) {
                return new ChatTokenUsage(0, 0, 0);
            }
            
            // 估算输入token数量
            long promptTokens = estimateTokens(message);
            
            // 假设输出长度约为输入的2倍（这是一个常见的比例）
            long completionTokens = promptTokens * 2;
            long totalTokens = promptTokens + completionTokens;
            
            log.info("Zhipuai API estimated token usage from message - input: {} chars -> {} tokens, estimated output: {} tokens, total: {} tokens", 
                    message.length(), promptTokens, completionTokens, totalTokens);
            
            return new ChatTokenUsage(promptTokens, completionTokens, totalTokens);
            
        } catch (Exception e) {
            log.error("Error estimating token usage from message", e);
            return new ChatTokenUsage(0, 0, 0);
        }
    }


    /**
     * 从Prompt中提取文本内容
     */
    private String extractTextFromPrompt(Prompt prompt) {
        if (prompt == null || prompt.getInstructions() == null) {
            return "";
        }
        
        // 构建完整的提示词内容，包括所有消息
        StringBuilder fullPrompt = new StringBuilder();
        for (org.springframework.ai.chat.messages.Message message : prompt.getInstructions()) {
            String content = message.getText();
            if (content != null && !content.trim().isEmpty()) {
                if (message instanceof org.springframework.ai.chat.messages.SystemMessage) {
                    fullPrompt.append(I18Consts.I18N_SYSTEM_PREFIX).append(content).append("\n");
                } else if (message instanceof org.springframework.ai.chat.messages.UserMessage) {
                    fullPrompt.append(I18Consts.I18N_USER_PREFIX).append(content).append("\n");
                } else if (message instanceof org.springframework.ai.chat.messages.AssistantMessage) {
                    fullPrompt.append(I18Consts.I18N_ASSISTANT_PREFIX).append(content).append("\n");
                } else {
                    fullPrompt.append(content).append("\n");
                }
            }
        }
        
        return fullPrompt.toString().trim();
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
     * 检查是否是空的助手消息
     * 过滤掉 {"role":"assistant","content":"","tool_calls":[]} 这样的空消息
     */
    private boolean isEmptyAssistantMessage(String deltaStr) {
        try {
            if (deltaStr == null || deltaStr.isEmpty()) {
                return true;
            }
            
            // 检查是否是JSON格式的助手消息
            if (deltaStr.trim().startsWith("{") && deltaStr.trim().endsWith("}")) {
                // 检查是否包含 "role":"assistant" 和空的 "content":""
                if (deltaStr.contains("\"role\":\"assistant\"") && 
                    (deltaStr.contains("\"content\":\"\"") || deltaStr.contains("\"content\":null"))) {
                    log.debug("Zhipuai API detected empty assistant message: {}", deltaStr);
                    return true;
                }
            }
            
            return false;
        } catch (Exception e) {
            log.error("Error checking empty assistant message: {}", deltaStr, e);
            return false;
        }
    }

    /**
     * 从delta字符串中提取content字段
     * 处理JSON格式的ChatMessage字符串，如：{"role":"assistant","content":"的","tool_calls":[]}
     */
    private String extractContentFromDeltaString(String deltaStr) {
        try {
            if (deltaStr == null || deltaStr.isEmpty() || deltaStr.equals("null") || isEmptyAssistantMessage(deltaStr)) {
                return null;
            }
            
            // 检查是否是JSON格式
            if (deltaStr.trim().startsWith("{") && deltaStr.trim().endsWith("}")) {
                // 使用简单的字符串解析来提取content字段
                // 查找 "content":" 的位置
                int contentStart = deltaStr.indexOf("\"content\":\"");
                if (contentStart != -1) {
                    contentStart += 11; // 跳过 "content":"
                    int contentEnd = deltaStr.indexOf("\"", contentStart);
                    if (contentEnd != -1) {
                        String content = deltaStr.substring(contentStart, contentEnd);
                        log.debug("Zhipuai API extracted content from delta string: {}", content);
                        return content;
                    }
                }
                
                // 如果上面的方法失败，尝试查找 "content": 的位置（不带引号的情况）
                contentStart = deltaStr.indexOf("\"content\":");
                if (contentStart != -1) {
                    contentStart += 10; // 跳过 "content":
                    // 跳过可能的空格
                    while (contentStart < deltaStr.length() && Character.isWhitespace(deltaStr.charAt(contentStart))) {
                        contentStart++;
                    }
                    
                    // 检查是否以引号开始
                    if (contentStart < deltaStr.length() && deltaStr.charAt(contentStart) == '"') {
                        contentStart++; // 跳过开始的引号
                        int contentEnd = deltaStr.indexOf("\"", contentStart);
                        if (contentEnd != -1) {
                            String content = deltaStr.substring(contentStart, contentEnd);
                            log.debug("Zhipuai API extracted content from delta string (with quotes): {}", content);
                            return content;
                        }
                    } else {
                        // 没有引号的情况，找到下一个逗号或大括号
                        int contentEnd = deltaStr.indexOf(",", contentStart);
                        if (contentEnd == -1) {
                            contentEnd = deltaStr.indexOf("}", contentStart);
                        }
                        if (contentEnd != -1) {
                            String content = deltaStr.substring(contentStart, contentEnd).trim();
                            log.debug("Zhipuai API extracted content from delta string (without quotes): {}", content);
                            return content;
                        }
                    }
                }
            }
            
            log.debug("Zhipuai API could not extract content from delta string: {}", deltaStr);
            return null;
            
        } catch (Exception e) {
            log.error("Error extracting content from delta string: {}", deltaStr, e);
            return null;
        }
    }

    /**
     * 从ChatMessageAccumulator中提取token使用情况
     */
    private ChatTokenUsage extractTokenUsageFromAccumulator(ChatMessageAccumulator accumulator) {
        try {
            if (accumulator == null || accumulator.getUsage() == null) {
                log.warn("Zhipuai API accumulator or usage is null");
                return new ChatTokenUsage(0, 0, 0);
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
            
            return new ChatTokenUsage(promptTokens, completionTokens, totalTokens);
            
        } catch (Exception e) {
            log.error("Error extracting token usage from accumulator", e);
            return new ChatTokenUsage(0, 0, 0);
        }
    }

}
