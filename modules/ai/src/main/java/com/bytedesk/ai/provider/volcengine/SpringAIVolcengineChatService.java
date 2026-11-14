/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-28 11:44:03
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2026-11-10
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.provider.volcengine;

import java.util.List;

import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
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
import com.bytedesk.ai.service.SseMessageHelper;
import com.bytedesk.ai.service.TokenUsageHelper;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.llm.LlmProviderConstants;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.content.RobotContent;

import lombok.extern.slf4j.Slf4j;

/**
 * 火山引擎方舟 Chat 服务（2026-11 迁移至 ark-runtime 官方 SDK，
 * 原 OpenAiChatModel 静态注入改为 ArkChatModel 全局 Bean）。
 */
@Slf4j
@Service
@ConditionalOnProperty(prefix = "spring.ai.volcengine.chat", name = "enabled", havingValue = "true", matchIfMissing = false)
public class SpringAIVolcengineChatService extends BaseSpringAIService {

    public SpringAIVolcengineChatService(
            @Qualifier("volcengineChatModel") ObjectProvider<ChatModel> volcengineChatModelProvider,
            TokenUsageHelper tokenUsageHelper) {
        this.volcengineChatModel = volcengineChatModelProvider.getIfAvailable();
        this.tokenUsageHelper = tokenUsageHelper;
    }

    private final ChatModel volcengineChatModel;

    private final TokenUsageHelper tokenUsageHelper;

    /**
     * 根据机器人配置创建动态的ArkChatOptions
     * 
     * @param llm 机器人LLM配置
     * @return 根据机器人配置创建的选项
     */
    private ArkChatOptions createDynamicOptions(RobotLlm llm) {
        if (llm == null || !StringUtils.hasText(llm.getTextModel())) {
            return null;
        }
        try {
            ArkChatOptions.Builder builder = ArkChatOptions.builder()
                    .model(llm.getTextModel())
                    .temperature(llm.getTemperature())
                    .maxTokens(llm.getMaxTokens())
                    .topP(llm.getTopP());
            if (Boolean.TRUE.equals(llm.getThinking())) {
                builder.enableThinking(true);
            }
            return applyRobotToolCallbacks(builder.build(), llm);
        } catch (Exception e) {
            log.error("Error creating dynamic options for model {}", llm.getTextModel(), e);
            return null;
        }
    }

    @Override
    protected String processPromptSync(String message, RobotProtobuf robot) {
        ArkChatOptions customOptions = robot != null && robot.getLlm() != null ? createDynamicOptions(robot.getLlm())
                : null;
        return processPromptSync(buildUserOnlyPrompt(message, customOptions), robot);
    }

    @Override
    protected String processPromptSync(Prompt prompt, RobotProtobuf robot) {
        long startTime = System.currentTimeMillis();
        boolean success = false;
        ChatTokenUsage tokenUsage = new ChatTokenUsage(0, 0, 0);
        RobotLlm llm = robot != null ? robot.getLlm() : null;

        if (volcengineChatModel == null) {
            return I18Consts.I18N_SERVICE_TEMPORARILY_UNAVAILABLE;
        }

        try {
            Prompt requestPrompt = prompt;
            ArkChatOptions customOptions = createDynamicOptions(llm);
            if (customOptions != null) {
                requestPrompt = processPromptWithOptions(prompt, customOptions);
            }
            var chatClient = createChatClient(volcengineChatModel, requestPrompt, robot);
            var response = invokePromptSync(chatClient, requestPrompt);
            tokenUsage = tokenUsageHelper.extractTokenUsage(response);
            success = true;
            return promptHelper.extractTextFromResponse(response);
        } catch (Exception e) {
            log.error("Volcengine API call error: ", e);
            success = false;
            return SseMessageHelper.resolveErrorText(e);
        } finally {
            long responseTime = System.currentTimeMillis() - startTime;
            String modelType = (llm != null && StringUtils.hasText(llm.getTextModel())) ? llm.getTextModel()
                    : "volcengine-chat";
            tokenUsageHelper.recordAiTokenUsage(robot, LlmProviderConstants.VOLCENGINE, modelType,
                    tokenUsage.getPromptTokens(), tokenUsage.getCompletionTokens(), success, responseTime);
        }
    }

    @Override
    protected void processPromptSse(Prompt prompt, RobotProtobuf robot, MessageProtobuf messageProtobufQuery,
            MessageProtobuf messageProtobufReply, List<RobotContent.SourceReference> sourceReferences,
            SseEmitter emitter) {
        // 直接实现SSE逻辑，而不是调用不支持fullPromptContent的版本
        // 从robot中获取llm配置
        RobotLlm llm = robot.getLlm();

        if (volcengineChatModel == null) {
            sseMessageHelper.handleSseError(new RuntimeException("Volcengine service not available"),
                    messageProtobufQuery, messageProtobufReply, emitter);
            return;
        }

        // 发送起始消息
        sseMessageHelper.sendStreamStartMessage(messageProtobufQuery, messageProtobufReply, emitter,
                I18Consts.I18N_THINKING);

        // 如果有自定义选项，创建新的Prompt
        Prompt requestPrompt = prompt;
        ArkChatOptions customOptions = createDynamicOptions(llm);
        if (customOptions != null) {
            requestPrompt = processPromptWithOptions(prompt, customOptions);
        }

        long startTime = System.currentTimeMillis();
        final boolean[] success = { false };
        final ChatTokenUsage[] tokenUsage = { new ChatTokenUsage(0, 0, 0) };

        var chatClient = createChatClient(volcengineChatModel, requestPrompt, robot);
        String conversationId = extractConversationId(messageProtobufQuery);
        invokePromptStream(chatClient, requestPrompt, conversationId).subscribe(
                response -> {
                    try {
                        if (response != null && !sseMessageHelper.isEmitterCompleted(emitter)) {
                            List<Generation> generations = response.getResults();
                            for (Generation generation : generations) {
                                AssistantMessage assistantMessage = generation.getOutput();
                                String textContent = assistantMessage.getText();
                                String reasonContent = extractReasoningContent(generation, assistantMessage);

                                sseMessageHelper.sendStreamMessage(messageProtobufQuery, messageProtobufReply, emitter,
                                        textContent, reasonContent, sourceReferences);
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
                    log.error("Volcengine API SSE error: ", error);
                    sseMessageHelper.handleSseError(error, messageProtobufQuery, messageProtobufReply, emitter);
                    success[0] = false;
                },
                () -> {
                    log.info("Volcengine API SSE complete");
                    // 发送流结束消息，包含token使用情况和prompt内容
                    sseMessageHelper.sendStreamEndMessage(messageProtobufQuery, messageProtobufReply, emitter,
                            tokenUsage[0].getPromptTokens(), tokenUsage[0].getCompletionTokens(),
                            tokenUsage[0].getTotalTokens(), prompt, LlmProviderConstants.VOLCENGINE,
                            (llm != null && StringUtils.hasText(llm.getTextModel())) ? llm.getTextModel()
                                    : "volcengine-chat");
                    // 记录token使用情况
                    long responseTime = System.currentTimeMillis() - startTime;
                    String modelType = (llm != null && StringUtils.hasText(llm.getTextModel())) ? llm.getTextModel()
                            : "volcengine-chat";
                    tokenUsageHelper.recordAiTokenUsage(robot, LlmProviderConstants.VOLCENGINE, modelType,
                            tokenUsage[0].getPromptTokens(), tokenUsage[0].getCompletionTokens(), success[0],
                            responseTime);
                });
    }

    public ChatModel getChatModel() {
        return volcengineChatModel;
    }

    public Boolean isServiceHealthy() {
        if (volcengineChatModel == null) {
            return false;
        }

        try {
            String response = processPromptSync("test", null);
            return !response.contains("不可用") && !response.equals("Volcengine service is not available");
        } catch (Exception e) {
            log.error("Error checking Volcengine service health", e);
            return false;
        }
    }
}
