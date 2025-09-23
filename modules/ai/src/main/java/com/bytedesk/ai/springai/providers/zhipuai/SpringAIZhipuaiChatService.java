/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-26 16:58:56
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-21 12:47:17
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.providers.zhipuai;

import java.util.List;

import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.zhipuai.ZhiPuAiChatModel;
import org.springframework.ai.zhipuai.ZhiPuAiChatOptions;
import org.springframework.ai.zhipuai.api.ZhiPuAiApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.bytedesk.ai.robot.RobotLlm;
import com.bytedesk.ai.robot.RobotProtobuf;
import com.bytedesk.ai.springai.service.BaseSpringAIService;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.llm.LlmProviderConstants;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.MessageTypeEnum;
import com.bytedesk.ai.springai.service.ChatTokenUsage;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

/**
 * Spring AI ZhiPuAI聊天服务
 */
@Slf4j
@Service
@ConditionalOnProperty(prefix = "spring.ai.zhipuai.chat", name = "enabled", havingValue = "true", matchIfMissing = false)
public class SpringAIZhipuaiChatService extends BaseSpringAIService {

    @Autowired
    @Qualifier("bytedeskZhipuaiChatModel")
    private ZhiPuAiChatModel bytedeskZhipuaiChatModel;

    @Autowired
    @Qualifier("bytedeskZhipuaiApi")
    private ZhiPuAiApi bytedeskZhipuaiApi;

    
    public SpringAIZhipuaiChatService() {
        super(); // 调用基类的无参构造函数
    }

    /**
     * 根据机器人配置创建动态的ZhiPuAiChatOptions
     * 
     * @param llm 机器人LLM配置
     * @return 根据机器人配置创建的选项
     */
    private ZhiPuAiChatOptions createDynamicOptions(RobotLlm llm) {
        return super.createDynamicOptions(llm, robotLlm -> 
            ZhiPuAiChatOptions.builder()
                .model(robotLlm.getTextModel())
                .temperature(robotLlm.getTemperature())
                .topP(robotLlm.getTopP())
                .build()
        );
    }

    /**
     * 根据机器人配置创建动态的ZhiPuAiChatModel
     * 
     * @param llm 机器人LLM配置
     * @return 配置了特定模型的ZhiPuAiChatModel
     */
    private ZhiPuAiChatModel createDynamicChatModel(RobotLlm llm) {
        if (llm == null || !StringUtils.hasText(llm.getTextModel())) {
            // 如果没有指定模型或设置，使用默认配置
            return bytedeskZhipuaiChatModel;
        }

        try {
            ZhiPuAiChatOptions options = createDynamicOptions(llm);
            if (options == null) {
                return bytedeskZhipuaiChatModel;
            }

            return new ZhiPuAiChatModel(bytedeskZhipuaiApi, options);
        } catch (Exception e) {
            log.error("Error creating dynamic chat model for model {}", llm.getTextModel(), e);
            return bytedeskZhipuaiChatModel;
        }
    }

    /**
     * 方式1：异步流式调用（带prompt参数）
     */
    @Override
    protected void processPromptWebsocket(Prompt prompt, RobotProtobuf robot, MessageProtobuf messageProtobufQuery,
            MessageProtobuf messageProtobufReply, String fullPromptContent) {
        // 从robot中获取llm配置
        RobotLlm llm = robot.getLlm();
        log.info("Zhipuai API websocket fullPromptContent: {}", fullPromptContent);

        // 获取适当的模型实例
        ZhiPuAiChatModel chatModel = (llm != null) ? createDynamicChatModel(llm) : bytedeskZhipuaiChatModel;

        long startTime = System.currentTimeMillis();
        final boolean[] success = {false};
        final ChatTokenUsage[] tokenUsage = {new ChatTokenUsage(0, 0, 0)};

        chatModel.stream(prompt).subscribe(
                response -> {
                    if (response != null) {
                        log.info("Zhipuai API response metadata: {}", response.getMetadata());
                        List<Generation> generations = response.getResults();
                        for (Generation generation : generations) {
                            AssistantMessage assistantMessage = generation.getOutput();
                            String textContent = assistantMessage.getText();

                            sendMessageWebsocket(MessageTypeEnum.ROBOT_STREAM, textContent, messageProtobufReply);
                        }
                        // 提取token使用情况
                        tokenUsage[0] = extractZhipuaiTokenUsage(response);
                        log.info("Zhipuai API websocket tokenUsage after manual extraction: {}", tokenUsage[0]);
                        success[0] = true;
                    }
                },
                error -> {
                    log.error("Zhipuai API error: ", error);
                    sendMessageWebsocket(MessageTypeEnum.ERROR, I18Consts.I18N_SERVICE_TEMPORARILY_UNAVAILABLE, messageProtobufReply);
                    success[0] = false;
                },
                () -> {
                    log.info("Chat stream completed");
                    // 记录token使用情况
                    long responseTime = System.currentTimeMillis() - startTime;
                    String modelType = (llm != null && StringUtils.hasText(llm.getTextModel())) ? llm.getTextModel() : "glm-3-turbo";
                    log.info("Zhipuai API websocket recording token usage - prompt: {}, completion: {}, total: {}, model: {}", 
                            tokenUsage[0].getPromptTokens(), tokenUsage[0].getCompletionTokens(), tokenUsage[0].getTotalTokens(), modelType);
                    recordAiTokenUsage(robot, LlmProviderConstants.ZHIPUAI, modelType, 
                            tokenUsage[0].getPromptTokens(), tokenUsage[0].getCompletionTokens(), success[0], responseTime);
                });
    }

    /**
     * 方式2：同步调用（带prompt参数）
     */
    @Override
    protected String processPromptSync(String message, RobotProtobuf robot, String fullPromptContent) {
        long startTime = System.currentTimeMillis();
        boolean success = false;
        ChatTokenUsage tokenUsage = new ChatTokenUsage(0, 0, 0);
        
        log.info("Zhipuai API sync fullPromptContent: {}", fullPromptContent);
        
        try {
            if (bytedeskZhipuaiChatModel == null) {
                return "Zhipuai service is not available";
            }

            try {
                // 如果有robot参数，尝试创建自定义选项
                if (robot != null && robot.getLlm() != null) {
                    // 创建动态的模型实例
                    ZhiPuAiChatModel chatModel = createDynamicChatModel(robot.getLlm());
                    if (chatModel != null) {
                        // 创建Prompt对象以获取ChatResponse
                        Prompt prompt = new Prompt(message);
                        var response = chatModel.call(prompt);
                        
                        // 添加详细的调试信息
                        log.info("Zhipuai API sync response class: {}", response.getClass().getName());
                        log.info("Zhipuai API sync response metadata: {}", response.getMetadata());
                        log.info("Zhipuai API sync response metadata class: {}", response.getMetadata() != null ? response.getMetadata().getClass().getName() : "null");
                        
                        // 检查metadata的所有字段
                        if (response.getMetadata() != null) {
                            log.info("Zhipuai API sync response metadata keys: {}", response.getMetadata().keySet());
                            for (String key : response.getMetadata().keySet()) {
                                Object value = response.getMetadata().get(key);
                                log.info("Zhipuai API sync response metadata [{}]: {} (class: {})", 
                                        key, value, value != null ? value.getClass().getName() : "null");
                            }
                        }
                        
                        tokenUsage = extractZhipuaiTokenUsage(response);
                        log.info("Zhipuai API sync tokenUsage after manual extraction: {}", tokenUsage);
                        success = true;
                        return extractTextFromResponse(response);
                    }
                }
                
                // 创建Prompt对象以获取ChatResponse
                Prompt prompt = new Prompt(message);
                var response = bytedeskZhipuaiChatModel.call(prompt);
                
                // 添加详细的调试信息
                log.info("Zhipuai API sync response class: {}", response.getClass().getName());
                log.info("Zhipuai API sync response metadata: {}", response.getMetadata());
                log.info("Zhipuai API sync response metadata class: {}", response.getMetadata() != null ? response.getMetadata().getClass().getName() : "null");
                
                // 检查metadata的所有字段
                if (response.getMetadata() != null) {
                    log.info("Zhipuai API sync response metadata keys: {}", response.getMetadata().keySet());
                    for (String key : response.getMetadata().keySet()) {
                        Object value = response.getMetadata().get(key);
                        log.info("Zhipuai API sync response metadata [{}]: {} (class: {})", 
                                key, value, value != null ? value.getClass().getName() : "null");
                    }
                }
                
                tokenUsage = extractZhipuaiTokenUsage(response);
                log.info("Zhipuai API sync tokenUsage after manual extraction: {}", tokenUsage);
                success = true;
                return extractTextFromResponse(response);
            } catch (Exception e) {
                log.error("Zhipuai API call error: ", e);
                success = false;
                return I18Consts.I18N_SERVICE_TEMPORARILY_UNAVAILABLE;
            }
        } catch (Exception e) {
            log.error("Zhipuai API sync error: ", e);
            success = false;
            return I18Consts.I18N_SERVICE_TEMPORARILY_UNAVAILABLE;
        } finally {
            // 记录token使用情况
            long responseTime = System.currentTimeMillis() - startTime;
            String modelType = (robot != null && robot.getLlm() != null && StringUtils.hasText(robot.getLlm().getTextModel())) 
                    ? robot.getLlm().getTextModel() : "glm-3-turbo";
            log.info("Zhipuai API sync recording token usage - prompt: {}, completion: {}, total: {}, model: {}", 
                    tokenUsage.getPromptTokens(), tokenUsage.getCompletionTokens(), tokenUsage.getTotalTokens(), modelType);
            recordAiTokenUsage(robot, LlmProviderConstants.ZHIPUAI, modelType, 
                    tokenUsage.getPromptTokens(), tokenUsage.getCompletionTokens(), success, responseTime);
        }
    }

    /**
     * 方式3：SSE方式调用（带prompt参数）
     */
    @Override
    protected void processPromptSse(Prompt prompt, RobotProtobuf robot, MessageProtobuf messageProtobufQuery,
            MessageProtobuf messageProtobufReply, SseEmitter emitter, String fullPromptContent) {
        // 从robot中获取llm配置
        RobotLlm llm = robot.getLlm();
        log.info("Zhipuai API SSE fullPromptContent: {}", fullPromptContent);

        // 获取适当的模型实例
        ZhiPuAiChatModel chatModel = (llm != null) ? createDynamicChatModel(llm) : bytedeskZhipuaiChatModel;
        
        // 发送起始消息
        sendStreamStartMessage(messageProtobufReply, emitter, I18Consts.I18N_THINKING);

        long startTime = System.currentTimeMillis();
        final boolean[] success = {false};
        final ChatTokenUsage[] tokenUsage = {new ChatTokenUsage(0, 0, 0)};
        final ChatResponse[] lastResponse = {null};

        Flux<ChatResponse> responseFlux = chatModel.stream(prompt);

        responseFlux.subscribe(
                response -> {
                    try {
                        if (response != null) {
                            // 保存最后一个响应，用于提取token信息
                            lastResponse[0] = response;
                            
                            // 添加详细的调试信息
                            log.info("Zhipuai API SSE response class: {}", response.getClass().getName());
                            log.info("Zhipuai API SSE response metadata: {}", response.getMetadata());
                            log.info("Zhipuai API SSE response metadata class: {}", response.getMetadata() != null ? response.getMetadata().getClass().getName() : "null");
                            
                            // 检查metadata的所有字段
                            if (response.getMetadata() != null) {
                                log.info("Zhipuai API SSE response metadata keys: {}", response.getMetadata().keySet());
                                for (String key : response.getMetadata().keySet()) {
                                    Object value = response.getMetadata().get(key);
                                    log.info("Zhipuai API SSE response metadata [{}]: {} (class: {})", 
                                            key, value, value != null ? value.getClass().getName() : "null");
                                }
                            }
                            
                            List<Generation> generations = response.getResults();
                            for (Generation generation : generations) {
                                AssistantMessage assistantMessage = generation.getOutput();
                                String textContent = assistantMessage.getText();
                                log.info("Zhipuai API SSE generation metadata {}, textContent {}", 
                                    generation.getMetadata(), textContent);
                                
                                sendStreamMessage(messageProtobufQuery, messageProtobufReply, emitter, textContent);
                            }
                            success[0] = true;
                        }
                    } catch (Exception e) {
                        log.error("Zhipuai API Error sending SSE event 1：", e);
                        handleSseError(e, messageProtobufQuery, messageProtobufReply, emitter);
                        success[0] = false;
                    }
                },
                error -> {
                    log.error("Zhipuai API SSE error 2:", error);
                    handleSseError(error, messageProtobufQuery, messageProtobufReply, emitter);
                    success[0] = false;
                },
                () -> {
                    log.info("Zhipuai API SSE complete");
                    
                    // 在流结束时，从最后一个响应中提取token使用情况
                    if (lastResponse[0] != null) {
                        tokenUsage[0] = extractZhipuaiTokenUsage(lastResponse[0]);
                        log.info("Zhipuai API SSE final tokenUsage after manual extraction: {}", tokenUsage[0]);
                    }
                    
                    // 发送流结束消息，包含token使用情况和prompt内容
                    sendStreamEndMessage(messageProtobufQuery, messageProtobufReply, emitter, 
                            tokenUsage[0].getPromptTokens(), tokenUsage[0].getCompletionTokens(), tokenUsage[0].getTotalTokens(), fullPromptContent, LlmProviderConstants.ZHIPUAI, (llm != null && StringUtils.hasText(llm.getTextModel())) ? llm.getTextModel() : "glm-3-turbo");
                    // 记录token使用情况
                    long responseTime = System.currentTimeMillis() - startTime;
                    String modelType = (llm != null && StringUtils.hasText(llm.getTextModel())) ? llm.getTextModel() : "glm-3-turbo";
                    log.info("Zhipuai API SSE recording token usage - prompt: {}, completion: {}, total: {}, model: {}", 
                            tokenUsage[0].getPromptTokens(), tokenUsage[0].getCompletionTokens(), tokenUsage[0].getTotalTokens(), modelType);
                    recordAiTokenUsage(robot, LlmProviderConstants.ZHIPUAI, modelType, 
                            tokenUsage[0].getPromptTokens(), tokenUsage[0].getCompletionTokens(), success[0], responseTime);
                });
    }

    /**
     * 手动提取智谱AI的token使用情况
     * 由于Spring AI ZhiPuAI集成可能无法正确解析token使用情况，我们手动提取
     * 
     * @param response ChatResponse对象
     * @return TokenUsage对象
     */
    private ChatTokenUsage extractZhipuaiTokenUsage(org.springframework.ai.chat.model.ChatResponse response) {
        try {
            if (response == null) {
                return new ChatTokenUsage(0, 0, 0);
            }
            
            var metadata = response.getMetadata();
            if (metadata == null) {
                log.warn("Zhipuai API response metadata is null");
                return new ChatTokenUsage(0, 0, 0);
            }
            
            log.info("Zhipuai API manual token extraction - metadata: {}", metadata);
            
            // 智谱AI的token使用情况通常在以下字段中：
            // 1. usage.prompt_tokens
            // 2. usage.completion_tokens  
            // 3. usage.total_tokens
            
            // 尝试从metadata中直接获取usage对象
            Object usage = null;
            
            // 方法1: 尝试通过反射获取usage字段
            try {
                java.lang.reflect.Field usageField = metadata.getClass().getDeclaredField("usage");
                usageField.setAccessible(true);
                usage = usageField.get(metadata);
                log.info("Zhipuai API found usage field via reflection: {}", usage);
            } catch (Exception e) {
                log.debug("Could not get usage field via reflection: {}", e.getMessage());
            }
            
            // 方法2: 如果反射失败，尝试从metadata的toString()中解析
            if (usage == null) {
                String metadataStr = metadata.toString();
                log.info("Zhipuai API parsing metadata string: {}", metadataStr);
                
                // 查找usage信息，格式通常是: DefaultUsage{promptTokens=16, completionTokens=14, totalTokens=30}
                if (metadataStr.contains("DefaultUsage{") || metadataStr.contains("usage:")) {
                    // 提取promptTokens
                    int promptStart = metadataStr.indexOf("promptTokens=");
                    int promptEnd = metadataStr.indexOf(",", promptStart);
                    if (promptEnd == -1) promptEnd = metadataStr.indexOf("}", promptStart);
                    
                    // 提取completionTokens
                    int completionStart = metadataStr.indexOf("completionTokens=");
                    int completionEnd = metadataStr.indexOf(",", completionStart);
                    if (completionEnd == -1) completionEnd = metadataStr.indexOf("}", completionStart);
                    
                    // 提取totalTokens
                    int totalStart = metadataStr.indexOf("totalTokens=");
                    int totalEnd = metadataStr.indexOf("}", totalStart);
                    
                    long promptTokens = 0;
                    long completionTokens = 0;
                    long totalTokens = 0;
                    
                    if (promptStart > 0 && promptEnd > promptStart) {
                        try {
                            promptTokens = Long.parseLong(metadataStr.substring(promptStart + 13, promptEnd));
                        } catch (NumberFormatException e) {
                            log.warn("Could not parse promptTokens from: {}", metadataStr.substring(promptStart + 13, promptEnd));
                        }
                    }
                    
                    if (completionStart > 0 && completionEnd > completionStart) {
                        try {
                            completionTokens = Long.parseLong(metadataStr.substring(completionStart + 17, completionEnd));
                        } catch (NumberFormatException e) {
                            log.warn("Could not parse completionTokens from: {}", metadataStr.substring(completionStart + 17, completionEnd));
                        }
                    }
                    
                    if (totalStart > 0 && totalEnd > totalStart) {
                        try {
                            totalTokens = Long.parseLong(metadataStr.substring(totalStart + 12, totalEnd));
                        } catch (NumberFormatException e) {
                            log.warn("Could not parse totalTokens from: {}", metadataStr.substring(totalStart + 12, totalEnd));
                        }
                    }
                    
                    // 如果没有total_tokens，计算它
                    if (totalTokens == 0 && (promptTokens > 0 || completionTokens > 0)) {
                        totalTokens = promptTokens + completionTokens;
                    }
                    
                    // 如果只有total_tokens，估算prompt和completion
                    if (totalTokens > 0 && (promptTokens == 0 || completionTokens == 0)) {
                        if (promptTokens == 0) {
                            promptTokens = (long) (totalTokens * 0.3);
                        }
                        if (completionTokens == 0) {
                            completionTokens = totalTokens - promptTokens;
                        }
                    }
                    
                    log.info("Zhipuai API manual token extraction result from string parsing - prompt: {}, completion: {}, total: {}", 
                            promptTokens, completionTokens, totalTokens);
                    
                    return new ChatTokenUsage(promptTokens, completionTokens, totalTokens);
                }
            }
            
            // 方法3: 尝试从usage对象中提取（如果通过反射获取到了）
            if (usage != null) {
                log.info("Zhipuai API found usage field: {}", usage);
                
                if (usage instanceof java.util.Map) {
                    java.util.Map<?, ?> usageMap = (java.util.Map<?, ?>) usage;
                    
                    long promptTokens = 0;
                    long completionTokens = 0;
                    long totalTokens = 0;
                    
                    // 提取prompt_tokens
                    Object promptObj = usageMap.get("promptTokens");
                    if (promptObj instanceof Number) {
                        promptTokens = ((Number) promptObj).longValue();
                    }
                    
                    // 提取completion_tokens
                    Object completionObj = usageMap.get("completionTokens");
                    if (completionObj instanceof Number) {
                        completionTokens = ((Number) completionObj).longValue();
                    }
                    
                    // 提取total_tokens
                    Object totalObj = usageMap.get("totalTokens");
                    if (totalObj instanceof Number) {
                        totalTokens = ((Number) totalObj).longValue();
                    }
                    
                    // 如果没有total_tokens，计算它
                    if (totalTokens == 0 && (promptTokens > 0 || completionTokens > 0)) {
                        totalTokens = promptTokens + completionTokens;
                    }
                    
                    // 如果只有total_tokens，估算prompt和completion
                    if (totalTokens > 0 && (promptTokens == 0 || completionTokens == 0)) {
                        if (promptTokens == 0) {
                            promptTokens = (long) (totalTokens * 0.3);
                        }
                        if (completionTokens == 0) {
                            completionTokens = totalTokens - promptTokens;
                        }
                    }
                    
                    log.info("Zhipuai API manual token extraction result - prompt: {}, completion: {}, total: {}", 
                            promptTokens, completionTokens, totalTokens);
                    
                    return new ChatTokenUsage(promptTokens, completionTokens, totalTokens);
                }
            }
            
            // 如果usage字段不存在，尝试其他可能的字段
            log.info("Zhipuai API usage field not found, checking other fields");
            for (String key : metadata.keySet()) {
                Object value = metadata.get(key);
                log.info("Zhipuai API metadata [{}]: {} (class: {})", 
                        key, value, value != null ? value.getClass().getName() : "null");
            }
            
            // 方法4: 如果手动提取失败，尝试使用原始的extractTokenUsage方法作为后备
            log.info("Zhipuai API manual extraction failed, trying original extractTokenUsage method");
            ChatTokenUsage fallbackUsage = extractTokenUsage(response);
            if (fallbackUsage.getTotalTokens() > 0) {
                log.info("Zhipuai API fallback extraction successful: {}", fallbackUsage);
                return fallbackUsage;
            }
            
            // 方法5: 如果所有方法都失败，尝试估算token使用量
            log.info("Zhipuai API all extraction methods failed, attempting to estimate token usage");
            ChatTokenUsage estimatedUsage = estimateTokenUsageFromResponse(response);
            if (estimatedUsage.getTotalTokens() > 0) {
                log.info("Zhipuai API estimated token usage: {}", estimatedUsage);
                return estimatedUsage;
            }
            
        } catch (Exception e) {
            log.error("Error in manual Zhipuai token extraction", e);
            
            // 如果手动提取出错，尝试使用原始的extractTokenUsage方法作为后备
            try {
                log.info("Zhipuai API manual extraction error, trying original extractTokenUsage method");
                ChatTokenUsage fallbackUsage = extractTokenUsage(response);
                if (fallbackUsage.getTotalTokens() > 0) {
                    log.info("Zhipuai API fallback extraction successful after error: {}", fallbackUsage);
                    return fallbackUsage;
                }
            } catch (Exception fallbackError) {
                log.error("Error in fallback token extraction", fallbackError);
            }
        }
        
        log.warn("Zhipuai API all token extraction methods failed, returning zeros");
        return new ChatTokenUsage(0, 0, 0);
    }

    /**
     * 估算token使用量（当无法从API获取实际token信息时使用）
     * 
     * @param response ChatResponse对象
     * @return 估算的TokenUsage对象
     */
    private ChatTokenUsage estimateTokenUsageFromResponse(org.springframework.ai.chat.model.ChatResponse response) {
        try {
            if (response == null) {
                return new ChatTokenUsage(0, 0, 0);
            }
            
            // 获取输出文本
            String outputText = extractTextFromResponse(response);
            
            // 由于无法从ChatResponse直接获取输入文本，我们使用一个保守的估算
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
     * 测试token提取功能
     * 用于调试token计数问题
     */
    public void testTokenExtraction() {
        try {
            log.info("Zhipuai API testing token extraction...");
            
            if (bytedeskZhipuaiChatModel == null) {
                log.error("Zhipuai API chat model is null");
                return;
            }
            
            // 创建一个简单的测试请求
            String testMessage = "Hello, this is a test message for token counting.";
            Prompt prompt = new Prompt(testMessage);
            
            log.info("Zhipuai API making test call with message: {}", testMessage);
            
            // 调用API
            var response = bytedeskZhipuaiChatModel.call(prompt);
            
            log.info("Zhipuai API test response class: {}", response.getClass().getName());
            log.info("Zhipuai API test response metadata: {}", response.getMetadata());
            
            if (response.getMetadata() != null) {
                log.info("Zhipuai API test response metadata class: {}", response.getMetadata().getClass().getName());
                log.info("Zhipuai API test response metadata keys: {}", response.getMetadata().keySet());
                
                for (String key : response.getMetadata().keySet()) {
                    Object value = response.getMetadata().get(key);
                    log.info("Zhipuai API test response metadata [{}]: {} (class: {})", 
                            key, value, value != null ? value.getClass().getName() : "null");
                }
            }
            
            // 测试手动token提取
            ChatTokenUsage manualUsage = extractZhipuaiTokenUsage(response);
            log.info("Zhipuai API test manual token extraction result: {}", manualUsage);
            
            // 测试原始token提取
            ChatTokenUsage originalUsage = extractTokenUsage(response);
            log.info("Zhipuai API test original token extraction result: {}", originalUsage);
            
            // 测试token估算功能
            ChatTokenUsage estimatedUsage = estimateTokenUsageFromResponse(response);
            log.info("Zhipuai API test estimated token usage result: {}", estimatedUsage);
            
            // 测试token估算算法
            String testText = "这是一个测试文本，包含中英文混合内容。This is a test text with mixed Chinese and English content.";
            long estimatedTokens = estimateTokens(testText);
            log.info("Zhipuai API test token estimation for text '{}': {} tokens", testText, estimatedTokens);
            
            // 提取文本内容
            String textContent = extractTextFromResponse(response);
            log.info("Zhipuai API test response text: {}", textContent);
            
        } catch (Exception e) {
            log.error("Zhipuai API test token extraction error", e);
        }
    }

    public Boolean isServiceHealthy() {
        try {
            // 发送一个简单的测试请求来检测服务是否响应
            String response = processPromptSync("test", null, "");
            return !response.contains("不可用");
        } catch (Exception e) {
            log.error("Error checking Zhipuai service health", e);
            return false;
        }
    }
}
