/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-28 11:44:03
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-25 09:24:56
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.providers.openai;

import java.util.List;

import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import com.bytedesk.ai.robot.RobotLlm;
import com.bytedesk.ai.robot.RobotProtobuf;
import com.bytedesk.ai.service.BaseSpringAIService;
import com.bytedesk.ai.service.ChatTokenUsage;
import com.bytedesk.ai.service.TokenUsageHelper;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.llm.LlmProviderConstants;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.content.RobotContent;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@ConditionalOnProperty(prefix = "spring.ai.openai.chat", name = "enabled", havingValue = "true", matchIfMissing = false)
public class SpringAIOpenaiChatService extends BaseSpringAIService {

    public SpringAIOpenaiChatService(
            @Qualifier("openaiChatModel") ObjectProvider<OpenAiChatModel> openaiChatModelProvider,
            TokenUsageHelper tokenUsageHelper) {
        this.openaiChatModel = openaiChatModelProvider.getIfAvailable();
        this.tokenUsageHelper = tokenUsageHelper;
    }


    private final OpenAiChatModel openaiChatModel;

    private final TokenUsageHelper tokenUsageHelper;

    
    /**
     * 根据机器人配置创建动态的OpenAiChatOptions
     * 
     * @param llm 机器人LLM配置
     * @return 根据机器人配置创建的选项
     */
    private OpenAiChatOptions createDynamicOptions(RobotLlm llm) {
        if (llm == null || !StringUtils.hasText(llm.getTextModel())) {
            return null;
        }
        try {
                return applyRobotToolCallbacks(OpenAiChatOptions.builder()
                .model(llm.getTextModel())
                .temperature(llm.getTemperature())
                .topP(llm.getTopP())
                    .build(), llm);
        } catch (Exception e) {
            log.error("Error creating dynamic options for model {}", llm.getTextModel(), e);
            return null;
        }
    }

    // @Override
    // protected void processPromptWebsocket(Prompt prompt, RobotProtobuf robot, MessageProtobuf messageProtobufQuery, MessageProtobuf messageProtobufReply) {
    //     // 从robot中获取llm配置
    // RobotLlm llm = robot.getLlm();
    //     log.info("OpenAI API websocket ");
        
    //     if (openaiChatModel == null) {
    //         sseMessageHelper.sendMessageWebsocket(MessageTypeEnum.ERROR, I18Consts.I18N_SERVICE_TEMPORARILY_UNAVAILABLE, messageProtobufReply);
    //         return;
    //     }
        
    //     // 如果有自定义选项，创建新的Prompt
    //     Prompt requestPrompt = prompt;
    //     OpenAiChatOptions customOptions = createDynamicOptions(llm);
    //     if (customOptions != null) {
    //         requestPrompt = processPromptWithOptions(prompt, customOptions);
    //     }
        
    //     long startTime = System.currentTimeMillis();
    //     final boolean[] success = {false};
    //     final ChatTokenUsage[] tokenUsage = {new ChatTokenUsage(0, 0, 0)};
        
    //     // 使用同一个ChatModel实例，但传入不同的选项
    //     invokePromptStream(openaiChatModel, requestPrompt).subscribe(
    //             response -> {
    //                 if (response != null) {
    //                     log.info("Openai API response metadata: {}", response.getMetadata());
    //                     List<Generation> generations = response.getResults();
    //                     for (Generation generation : generations) {
    //                         AssistantMessage assistantMessage = generation.getOutput();
    //                         String textContent = assistantMessage.getText();

    //                         sseMessageHelper.sendMessageWebsocket(MessageTypeEnum.ROBOT_STREAM, textContent, messageProtobufReply);
    //                     }
    //                     // 提取token使用情况
    //                     tokenUsage[0] = tokenUsageHelper.extractTokenUsage(response);
    //                     success[0] = true;
    //                 }
    //             },
    //             error -> {
    //                 log.error("Openai API error: ", error);
    //                 sseMessageHelper.sendMessageWebsocket(MessageTypeEnum.ERROR, I18Consts.I18N_SERVICE_TEMPORARILY_UNAVAILABLE, messageProtobufReply);
    //                 success[0] = false;
    //             },
    //             () -> {
    //                 log.info("Chat stream completed");
    //                 // 记录token使用情况
    //                 long responseTime = System.currentTimeMillis() - startTime;
    //                 String modelType = (llm != null && StringUtils.hasText(llm.getTextModel())) ? llm.getTextModel() : "gpt-3.5-turbo";
    //         tokenUsageHelper.recordAiTokenUsage(robot, LlmProviderConstants.OPENAI, modelType, 
    //                         tokenUsage[0].getPromptTokens(), tokenUsage[0].getCompletionTokens(), success[0], responseTime);
    //             });
    // }

    @Override
    protected String processPromptSync(String message, RobotProtobuf robot) {
        OpenAiChatOptions customOptions = robot != null && robot.getLlm() != null ? createDynamicOptions(robot.getLlm()) : null;
        return processPromptSync(buildUserOnlyPrompt(message, customOptions), robot);
    }

    @Override
    protected String processPromptSync(Prompt prompt, RobotProtobuf robot) {
        long startTime = System.currentTimeMillis();
        boolean success = false;
        ChatTokenUsage tokenUsage = new ChatTokenUsage(0, 0, 0);
        RobotLlm llm = robot != null ? robot.getLlm() : null;
        
        log.info("OpenAI API sync ");
        if (openaiChatModel == null) {
            return "OpenAI service is not available";
        }

        try {
            Prompt requestPrompt = prompt;
            OpenAiChatOptions customOptions = createDynamicOptions(llm);
            if (customOptions != null) {
                requestPrompt = processPromptWithOptions(prompt, customOptions);
            }
            var chatClient = createChatClient(openaiChatModel, requestPrompt, robot);
            var response = invokePromptSync(chatClient, requestPrompt);
            tokenUsage = tokenUsageHelper.extractTokenUsage(response);
            success = true;
            return promptHelper.extractTextFromResponse(response);
        } catch (Exception e) {
            log.error("Openai API call error: ", e);
            success = false;
            return I18Consts.I18N_SERVICE_TEMPORARILY_UNAVAILABLE;
        } finally {
            long responseTime = System.currentTimeMillis() - startTime;
            String modelType = (llm != null && StringUtils.hasText(llm.getTextModel())) ? llm.getTextModel() : "gpt-3.5-turbo";
            tokenUsageHelper.recordAiTokenUsage(robot, LlmProviderConstants.OPENAI,
                    modelType, tokenUsage.getPromptTokens(), tokenUsage.getCompletionTokens(), success, responseTime);
        }
    }

    @Override
    protected void processPromptSse(Prompt prompt, RobotProtobuf robot, MessageProtobuf messageProtobufQuery, MessageProtobuf messageProtobufReply, List<RobotContent.SourceReference> sourceReferences, SseEmitter emitter) {
    RobotLlm llm = robot.getLlm();
        log.info("OpenAI API SSE ");

        if (openaiChatModel == null) {
            sseMessageHelper.handleSseError(new RuntimeException("OpenAI service not available"), messageProtobufQuery, messageProtobufReply, emitter);
            return;
        }

    // 发送起始消息（带上原始提问）
    sseMessageHelper.sendStreamStartMessage(messageProtobufQuery, messageProtobufReply, emitter, I18Consts.I18N_THINKING);

        Prompt requestPrompt = prompt;
        OpenAiChatOptions customOptions = createDynamicOptions(llm);
        if (customOptions != null) {
            requestPrompt = processPromptWithOptions(prompt, customOptions);
        }

        long startTime = System.currentTimeMillis();
        final boolean[] success = {false};
        final ChatTokenUsage[] tokenUsage = {new ChatTokenUsage(0, 0, 0)};

        var chatClient = createChatClient(openaiChatModel, requestPrompt, robot);
        String conversationId = extractConversationId(messageProtobufQuery);
        invokePromptStream(chatClient, requestPrompt, conversationId).subscribe(
                response -> {
                    try {
                        if (response != null) {
                            List<Generation> generations = response.getResults();
                            for (Generation generation : generations) {
                                AssistantMessage assistantMessage = generation.getOutput();
                                String textContent = assistantMessage.getText();
                                String reasonContent = extractReasoningContent(generation, assistantMessage);
                                log.info("Openai API response metadata: {}, text {}",
                                        response.getMetadata(), textContent);
                                
                                sseMessageHelper.sendStreamMessage(messageProtobufQuery, messageProtobufReply, emitter, textContent, reasonContent, sourceReferences);
                            }
                            // 提取token使用情况
                            tokenUsage[0] = tokenUsageHelper.extractTokenUsage(response);
                            success[0] = true;
                        }
                    } catch (Exception e) {
                        log.error("Error sending SSE event", e);
                        sseMessageHelper.handleSseError(e, messageProtobufQuery, messageProtobufReply, emitter);
                        success[0] = false;
                    }
                },
                error -> {
                    log.error("Openai API SSE error: ", error);
                    sseMessageHelper.handleSseError(error, messageProtobufQuery, messageProtobufReply, emitter);
                    success[0] = false;
                },
                () -> {
                    log.info("Openai API SSE complete");
                    // 发送流结束消息，包含token使用情况和prompt内容
                    sseMessageHelper.sendStreamEndMessage(messageProtobufQuery, messageProtobufReply, emitter, 
                            tokenUsage[0].getPromptTokens(), tokenUsage[0].getCompletionTokens(), tokenUsage[0].getTotalTokens(), prompt, LlmProviderConstants.OPENAI, (llm != null && StringUtils.hasText(llm.getTextModel())) ? llm.getTextModel() : "gpt-3.5-turbo");
                    // 记录token使用情况
                    long responseTime = System.currentTimeMillis() - startTime;
                    String modelType = (llm != null && StringUtils.hasText(llm.getTextModel())) ? llm.getTextModel() : "gpt-3.5-turbo";
            tokenUsageHelper.recordAiTokenUsage(robot, LlmProviderConstants.OPENAI, modelType, 
                            tokenUsage[0].getPromptTokens(), tokenUsage[0].getCompletionTokens(), success[0], responseTime);
                });
    }

    public OpenAiChatModel getChatModel() {
        return openaiChatModel;
    }
    
    public Boolean isServiceHealthy() {
        if (openaiChatModel == null) {
            return false;
        }

        try {
            String response = processPromptSync("test", null);
            return !response.contains("不可用") && !response.equals("Openai service is not available");
        } catch (Exception e) {
            log.error("Error checking OpenAI service health", e);
            return false;
        }
    }
}
