/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-28 11:44:03
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-25 09:24:30
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.providers.minimax;

import java.util.List;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.minimax.MiniMaxChatOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.bytedesk.ai.robot_settings.RobotLlmResponse;
import com.bytedesk.ai.robot.RobotProtobuf;
import com.bytedesk.ai.springai.service.BaseSpringAIService;
import com.bytedesk.ai.springai.service.TokenUsageHelper;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.llm.LlmProviderConstants;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.MessageTypeEnum;
import com.bytedesk.core.message.content.RobotContent;
import com.bytedesk.ai.springai.service.ChatTokenUsage;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@ConditionalOnProperty(prefix = "spring.ai.minimax.chat", name = "enabled", havingValue = "true", matchIfMissing = false)
public class SpringAIMinimaxChatService extends BaseSpringAIService {

    @Autowired(required = false)
    @Qualifier("minimaxChatModel")
    private ChatModel minimaxChatModel;

    @Autowired
    private TokenUsageHelper tokenUsageHelper;


    public SpringAIMinimaxChatService() {
        super(); // 调用基类的无参构造函数
    }

    /**
     * 根据机器人配置创建动态的MiniMaxChatOptions
     * 
     * @param llm 机器人LLM配置
     * @return 根据机器人配置创建的选项
     */
    private MiniMaxChatOptions createDynamicOptions(RobotLlmResponse llm) {
        if (llm == null || !StringUtils.hasText(llm.getTextModel())) {
            return null;
        }
        try {
            return MiniMaxChatOptions.builder()
                .model(llm.getTextModel())
                .temperature(llm.getTemperature())
                .topP(llm.getTopP())
                .build();
        } catch (Exception e) {
            log.error("Error creating Minimax options for model {}", llm.getTextModel(), e);
            return null;
        }
    }

    @Override
    protected void processPromptWebsocket(Prompt prompt, RobotProtobuf robot, MessageProtobuf messageProtobufQuery,
            MessageProtobuf messageProtobufReply) {
        log.info("SpringAIMinimaxService processPromptWebsocket with full prompt content");
        // 从robot中获取llm配置
    RobotLlmResponse llm = robot.getLlm();
        
        if (minimaxChatModel == null) {
            sseMessageHelper.sendMessageWebsocket(MessageTypeEnum.ERROR, I18Consts.I18N_SERVICE_TEMPORARILY_UNAVAILABLE, messageProtobufReply);
            return;
        }
        
        // 如果有自定义选项，创建新的Prompt
        Prompt requestPrompt = prompt;
        MiniMaxChatOptions customOptions = createDynamicOptions(llm);
        if (customOptions != null) {
            requestPrompt = new Prompt(prompt.getInstructions(), customOptions);
        }
        
        long startTime = System.currentTimeMillis();
        final boolean[] success = {false};
        final ChatTokenUsage[] tokenUsage = {new ChatTokenUsage(0, 0, 0)};
        
        // 使用同一个ChatModel实例，但传入不同的选项
        minimaxChatModel.stream(requestPrompt).subscribe(
                response -> {
                    if (response != null) {
                        log.info("Minimax API response metadata: {}", response.getMetadata());
                        List<Generation> generations = response.getResults();
                        for (Generation generation : generations) {
                            AssistantMessage assistantMessage = generation.getOutput();
                            String textContent = assistantMessage.getText();

                            sseMessageHelper.sendMessageWebsocket(MessageTypeEnum.ROBOT_STREAM, textContent, messageProtobufReply);
                        }
                        // 提取token使用情况
                        tokenUsage[0] = extractDeepSeekTokenUsage(response);
                        success[0] = true;
                    }
                },
                error -> {
                    log.error("Minimax API error: ", error);
                    sseMessageHelper.sendMessageWebsocket(MessageTypeEnum.ERROR, I18Consts.I18N_SERVICE_TEMPORARILY_UNAVAILABLE, messageProtobufReply);
                    success[0] = false;
                },
                () -> {
                    log.info("Chat stream completed");
                    // 记录token使用情况
                    long responseTime = System.currentTimeMillis() - startTime;
                    String modelType = (llm != null && StringUtils.hasText(llm.getTextModel())) ? llm.getTextModel() : LlmProviderConstants.DEEPSEEK;
            tokenUsageHelper.recordAiTokenUsage(robot, LlmProviderConstants.DEEPSEEK, modelType, 
                            tokenUsage[0].getPromptTokens(), tokenUsage[0].getCompletionTokens(), success[0], responseTime);
                });
    }

    @Override
    protected String processPromptSync(String message, RobotProtobuf robot) {
        log.info("SpringAIMinimaxService processPromptSync with full prompt content");
        long startTime = System.currentTimeMillis();
        boolean success = false;
        ChatTokenUsage tokenUsage = new ChatTokenUsage(0, 0, 0);
        
        try {
            if (minimaxChatModel == null) {
                return "Minimax service is not available";
            }

            try {
                // 如果有robot参数，尝试创建自定义选项
                if (robot != null && robot.getLlm() != null) {
                    // 创建自定义选项
                    MiniMaxChatOptions customOptions = createDynamicOptions(robot.getLlm());
                    if (customOptions != null) {
                        // 使用自定义选项创建Prompt
                        Prompt prompt = new Prompt(message, customOptions);
                        var response = minimaxChatModel.call(prompt);
                        tokenUsage = extractDeepSeekTokenUsage(response);
                        success = true;
                        return promptHelper.extractTextFromResponse(response);
                    }
                }
                
                ChatResponse response = minimaxChatModel.call(new Prompt(message));
                tokenUsage = extractDeepSeekTokenUsage(response);
                success = true;
                return promptHelper.extractTextFromResponse(response);
            } catch (Exception e) {
                log.error("Minimax API call error: ", e);
                success = false;
                return I18Consts.I18N_SERVICE_TEMPORARILY_UNAVAILABLE;
            }
        } catch (Exception e) {
            log.error("Minimax API sync error: ", e);
            success = false;
            return I18Consts.I18N_SERVICE_TEMPORARILY_UNAVAILABLE;
        } finally {
            // 记录token使用情况
            long responseTime = System.currentTimeMillis() - startTime;
            String modelType = (robot != null && robot.getLlm() != null && StringUtils.hasText(robot.getLlm().getTextModel())) 
                    ? robot.getLlm().getTextModel() : LlmProviderConstants.DEEPSEEK;
        tokenUsageHelper.recordAiTokenUsage(robot, LlmProviderConstants.DEEPSEEK, modelType, 
                    tokenUsage.getPromptTokens(), tokenUsage.getCompletionTokens(), success, responseTime);
        }
    }

    @Override
    protected void processPromptSse(Prompt prompt, RobotProtobuf robot, MessageProtobuf messageProtobufQuery,
            MessageProtobuf messageProtobufReply, List<RobotContent.SourceReference> sourceReferences, SseEmitter emitter) {
        log.info("SpringAIMinimaxService processPromptSse with full prompt content");
        // 从robot中获取llm配置
    RobotLlmResponse llm = robot.getLlm();

        if (minimaxChatModel == null) {
            sseMessageHelper.handleSseError(new RuntimeException("Minimax service not available"), messageProtobufQuery, messageProtobufReply, emitter);
            return;
        }

        // 发送起始消息
    sseMessageHelper.sendStreamStartMessage(messageProtobufQuery, messageProtobufReply, emitter, I18Consts.I18N_THINKING);

        // 如果有自定义选项，创建新的Prompt
        Prompt requestPrompt = prompt;
        MiniMaxChatOptions customOptions = createDynamicOptions(llm);
        if (customOptions != null) {
            requestPrompt = new Prompt(prompt.getInstructions(), customOptions);
        }

        long startTime = System.currentTimeMillis();
        final boolean[] success = {false};
        final ChatTokenUsage[] tokenUsage = {new ChatTokenUsage(0, 0, 0)};

        minimaxChatModel.stream(requestPrompt).subscribe(
                response -> {
                    try {
                        if (response != null) {
                            List<Generation> generations = response.getResults();
                            for (Generation generation : generations) {
                                AssistantMessage assistantMessage = generation.getOutput();
                                String textContent = assistantMessage.getText();
                                log.info("Minimax API response metadata: {}, text {}",
                                        response.getMetadata(), textContent);
                                
                                sseMessageHelper.sendStreamMessage(messageProtobufQuery, messageProtobufReply, emitter, textContent, null, sourceReferences);
                            }
                            // 提取token使用情况
                            tokenUsage[0] = extractDeepSeekTokenUsage(response);
                            success[0] = true;
                        }
                    } catch (Exception e) {
                        log.error("Error sending SSE event", e);
                        sseMessageHelper.handleSseError(e, messageProtobufQuery, messageProtobufReply, emitter);
                        success[0] = false;
                    }
                },
                error -> {
                    log.error("Minimax API SSE error: ", error);
                    sseMessageHelper.handleSseError(error, messageProtobufQuery, messageProtobufReply, emitter);
                    success[0] = false;
                },
                () -> {
                    log.info("Minimax API SSE complete");
                    // 发送流结束消息，包含token使用情况
            sseMessageHelper.sendStreamEndMessage(messageProtobufQuery, messageProtobufReply, emitter, 
                tokenUsage[0].getPromptTokens(), tokenUsage[0].getCompletionTokens(), tokenUsage[0].getTotalTokens(), prompt, LlmProviderConstants.DEEPSEEK, (llm != null && StringUtils.hasText(llm.getTextModel())) ? llm.getTextModel() : LlmProviderConstants.DEEPSEEK);
                    // 记录token使用情况
                    long responseTime = System.currentTimeMillis() - startTime;
                    String modelType = (llm != null && StringUtils.hasText(llm.getTextModel())) ? llm.getTextModel() : LlmProviderConstants.DEEPSEEK;
            tokenUsageHelper.recordAiTokenUsage(robot, LlmProviderConstants.DEEPSEEK, modelType, 
                            tokenUsage[0].getPromptTokens(), tokenUsage[0].getCompletionTokens(), success[0], responseTime);
                });
    }

    /**
     * 专门为Minimax API提取token使用情况
     * 由于Minimax API返回的usage字段是EmptyUsage对象，需要特殊处理
     * 
     * @param response ChatResponse对象
     * @return TokenUsage对象
     */
    private ChatTokenUsage extractDeepSeekTokenUsage(ChatResponse response) {
        try {
            if (response == null) {
                log.warn("Minimax API response is null");
                return new ChatTokenUsage(0, 0, 0);
            }

            var metadata = response.getMetadata();
            if (metadata == null) {
                log.warn("Minimax API response metadata is null");
                return new ChatTokenUsage(0, 0, 0);
            }

            log.info("Minimax API token extraction - metadata: {}", metadata);

            // 直接通过getUsage()方法获取token使用情况，无需反射
            try {
                var usage = metadata.getUsage();
                if (usage != null) {
                    long promptTokens = usage.getPromptTokens();
                    long completionTokens = usage.getCompletionTokens();
                    long totalTokens = usage.getTotalTokens();
                    
                    log.info("Minimax API direct usage extraction - prompt: {}, completion: {}, total: {}", 
                            promptTokens, completionTokens, totalTokens);
                    
                    if (totalTokens > 0) {
                        return new ChatTokenUsage(promptTokens, completionTokens, totalTokens);
                    }
                }
            } catch (Exception e) {
                log.debug("Could not get usage via getUsage() method: {}", e.getMessage());
            }

            return new ChatTokenUsage(0, 0, 0);
            
        } catch (Exception e) {
            log.error("Error in Minimax token extraction", e);
            return new ChatTokenUsage(0, 0, 0);
        }
    }
    
    public Boolean isServiceHealthy() {
        if (minimaxChatModel == null) {
            return false;
        }

        try {
            String response = processPromptSync("test", null);
            return !response.contains("不可用") && !response.equals("Minimax service is not available");
        } catch (Exception e) {
            log.error("Error checking Minimax service health", e);
            return false;
        }
    }

}
