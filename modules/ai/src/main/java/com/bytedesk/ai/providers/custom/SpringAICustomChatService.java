/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-28 11:44:03
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-25 07:51:35
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.providers.custom;

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
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.content.RobotContent;
import com.bytedesk.core.constant.I18Consts;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@ConditionalOnProperty(prefix = "spring.ai.custom.chat", name = "enabled", havingValue = "true", matchIfMissing = false)
public class SpringAICustomChatService extends BaseSpringAIService {

    public SpringAICustomChatService(
            @Qualifier("customChatModel") ObjectProvider<OpenAiChatModel> customChatModelProvider,
            TokenUsageHelper tokenUsageHelper) {
        this.customChatModel = customChatModelProvider.getIfAvailable();
        this.tokenUsageHelper = tokenUsageHelper;
    }


    private final OpenAiChatModel customChatModel;

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
            // 创建自定义的选项对象
                return applyRobotToolCallbacks(OpenAiChatOptions.builder()
                    .model(llm.getTextModel())
                    .temperature(llm.getTemperature())
                    .maxTokens(llm.getMaxTokens())
                    .topP(llm.getTopP())
                        .build(), llm);
        } catch (Exception e) {
            log.error("Error creating dynamic options for model {}", llm.getTextModel(), e);
            return null;
        }
    }

    // @Override
    // protected void processPromptWebsocket(Prompt prompt, RobotProtobuf robot, MessageProtobuf messageProtobufQuery,
    //         MessageProtobuf messageProtobufReply) {
    //     // 从robot中获取llm配置
    //     RobotLlm llm = robot.getLlm();
    //     log.info("Custom API websocket ");

    //     if (customChatModel == null) {
    //         sseMessageHelper.sendMessageWebsocket(MessageTypeEnum.ERROR, I18Consts.I18N_SERVICE_TEMPORARILY_UNAVAILABLE,
    //                 messageProtobufReply);
    //         return;
    //     }

    //     // 如果有自定义选项，创建新的Prompt
    //     Prompt requestPrompt = prompt;
    //     OpenAiChatOptions customOptions = createDynamicOptions(llm);
    //     if (customOptions != null) {
    //         requestPrompt = processPromptWithOptions(prompt, customOptions);
    //     }

    //     // 使用同一个ChatModel实例，但传入不同的选项
    //     invokePromptStream(customChatModel, requestPrompt).subscribe(
    //             response -> {
    //                 if (response != null) {
    //                     log.info("Custom API response metadata: {}", response.getMetadata());
    //                     List<Generation> generations = response.getResults();
    //                     for (Generation generation : generations) {
    //                         AssistantMessage assistantMessage = generation.getOutput();
    //                         String textContent = assistantMessage.getText();

    //                         sseMessageHelper.sendMessageWebsocket(MessageTypeEnum.ROBOT_STREAM, textContent,
    //                                 messageProtobufReply);
    //                     }
    //                 }
    //             },
    //             error -> {
    //                 log.error("Custom API error: ", error);
    //                 sseMessageHelper.sendMessageWebsocket(MessageTypeEnum.ERROR,
    //                         I18Consts.I18N_SERVICE_TEMPORARILY_UNAVAILABLE, messageProtobufReply);
    //             },
    //             () -> {
    //                 log.info("Chat stream completed");
    //             });
    // }

    @Override
    protected String processPromptSync(String message, RobotProtobuf robot) {
        OpenAiChatOptions customOptions = robot != null && robot.getLlm() != null ? createDynamicOptions(robot.getLlm()) : null;
        return processPromptSync(buildUserOnlyPrompt(message, customOptions), robot);
    }

    @Override
    protected String processPromptSync(Prompt prompt, RobotProtobuf robot) {
        log.info("Custom API sync ");
        long startTime = System.currentTimeMillis();
        boolean success = false;
        ChatTokenUsage tokenUsage = new ChatTokenUsage(0, 0, 0);
        RobotLlm llm = robot != null ? robot.getLlm() : null;

        if (customChatModel == null) {
            return I18Consts.I18N_SERVICE_TEMPORARILY_UNAVAILABLE;
        }

        try {
            Prompt requestPrompt = prompt;
            OpenAiChatOptions customOptions = createDynamicOptions(llm);
            if (customOptions != null) {
                requestPrompt = processPromptWithOptions(prompt, customOptions);
            }
            var chatClient = createChatClient(customChatModel, requestPrompt);
            var response = invokePromptSync(chatClient, requestPrompt);
            tokenUsage = tokenUsageHelper.extractTokenUsage(response);
            success = true;
            return promptHelper.extractTextFromResponse(response);
        } catch (Exception e) {
            log.error("Custom API call error: ", e);
            success = false;
            return I18Consts.I18N_SERVICE_TEMPORARILY_UNAVAILABLE;
        } finally {
            long responseTime = System.currentTimeMillis() - startTime;
            String modelType = (llm != null && StringUtils.hasText(llm.getTextModel())) ? llm.getTextModel()
                    : "custom-chat";
            tokenUsageHelper.recordAiTokenUsage(robot, "custom", modelType,
                    tokenUsage.getPromptTokens(), tokenUsage.getCompletionTokens(), success, responseTime);
        }
    }

    @Override
    protected void processPromptSse(Prompt prompt, RobotProtobuf robot, MessageProtobuf messageProtobufQuery,
            MessageProtobuf messageProtobufReply, List<RobotContent.SourceReference> sourceReferences,
            SseEmitter emitter) {
        // 从robot中获取llm配置
        RobotLlm llm = robot.getLlm();
        log.info("Custom API SSE ");

        if (customChatModel == null) {
            sseMessageHelper.handleSseError(new RuntimeException("Custom service not available"), messageProtobufQuery,
                    messageProtobufReply, emitter);
            return;
        }

        // 发送起始消息
        sseMessageHelper.sendStreamStartMessage(messageProtobufQuery, messageProtobufReply, emitter,
                I18Consts.I18N_THINKING);

        // 如果有自定义选项，创建新的Prompt
        Prompt requestPrompt = prompt;
        OpenAiChatOptions customOptions = createDynamicOptions(llm);
        if (customOptions != null) {
            requestPrompt = processPromptWithOptions(prompt, customOptions);
        }

        var chatClient = createChatClient(customChatModel, requestPrompt);
        invokePromptStream(chatClient, requestPrompt).subscribe(
                response -> {
                    try {
                        if (response != null) {
                            List<Generation> generations = response.getResults();
                            for (Generation generation : generations) {
                                AssistantMessage assistantMessage = generation.getOutput();
                                String textContent = assistantMessage.getText();
                                String reasonContent = extractReasoningContent(generation, assistantMessage);
                                log.info("Custom API response metadata: {}, text {}",
                                        response.getMetadata(), textContent);

                                sseMessageHelper.sendStreamMessage(messageProtobufQuery, messageProtobufReply, emitter,
                                    textContent, reasonContent, sourceReferences);
                            }
                        }
                    } catch (Exception e) {
                        log.error("Error sending SSE event", e);
                        sseMessageHelper.handleSseError(e, messageProtobufQuery, messageProtobufReply, emitter);
                    }
                },
                error -> {
                    log.error("Custom API SSE error: ", error);
                    sseMessageHelper.handleSseError(error, messageProtobufQuery, messageProtobufReply, emitter);
                },
                () -> {
                    log.info("Custom API SSE complete");
                    sseMessageHelper.sendStreamEndMessage(messageProtobufQuery, messageProtobufReply, emitter,
                            0, 0, 0, prompt, "custom",
                            (llm != null && StringUtils.hasText(llm.getTextModel())) ? llm.getTextModel()
                                    : "custom-chat");
                });
    }

    public OpenAiChatModel getChatModel() {
        return customChatModel;
    }

    public Boolean isServiceHealthy() {
        if (customChatModel == null) {
            return false;
        }

        try {
            String response = processPromptSync("test", null);
            return !response.contains("不可用") && !response.equals("Custom service is not available");
        } catch (Exception e) {
            log.error("Error checking Custom service health", e);
            return false;
        }
    }
}
