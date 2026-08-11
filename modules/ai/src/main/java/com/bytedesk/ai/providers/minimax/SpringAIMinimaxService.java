/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-28 11:44:03
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-25 09:24:23
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.providers.minimax;

import java.util.List;
import java.util.Optional;

import org.springframework.ai.anthropic.AnthropicChatModel;
import org.springframework.ai.anthropic.AnthropicChatOptions;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.bytedesk.ai.llm_provider.LlmProviderEntity;
import com.bytedesk.ai.llm_provider.LlmProviderRestService;
import com.bytedesk.ai.robot.RobotLlm;
import com.bytedesk.ai.robot.RobotProtobuf;
import com.bytedesk.ai.service.BaseSpringAIService;
import com.bytedesk.ai.service.ChatTokenUsage;
import com.bytedesk.ai.service.PromptHelper;
import com.bytedesk.ai.service.SseMessageHelper;
import com.bytedesk.ai.service.TokenUsageHelper;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.llm.LlmProviderConstants;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.content.RobotContent;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SpringAIMinimaxService extends BaseSpringAIService {

    public SpringAIMinimaxService(
            LlmProviderRestService llmProviderRestService,
            @Qualifier("minimaxChatModel") ObjectProvider<AnthropicChatModel> defaultChatModelProvider,
            TokenUsageHelper tokenUsageHelper,
            SseMessageHelper sseMessageHelper,
            PromptHelper promptHelper) {
        this.llmProviderRestService = llmProviderRestService;
        this.defaultChatModel = defaultChatModelProvider.getIfAvailable();
        this.tokenUsageHelper = tokenUsageHelper;
        this.sseMessageHelper = sseMessageHelper;
        this.promptHelper = promptHelper;
    }


    private final LlmProviderRestService llmProviderRestService;

    private final AnthropicChatModel defaultChatModel;

    private final TokenUsageHelper tokenUsageHelper;

    private final SseMessageHelper sseMessageHelper;

    private final PromptHelper promptHelper;


    /**
     * 根据机器人配置创建动态的MiniMaxChatOptions
     * 
     * @param llm 机器人LLM配置
     * @return 根据机器人配置创建的选项
     */
    private AnthropicChatOptions createDynamicOptions(RobotLlm llm) {
        if (llm == null || !StringUtils.hasText(llm.getTextModel())) {
            return null;
        }
        try {
                return applyRobotToolCallbacks(AnthropicChatOptions.builder()
                    .model(llm.getTextModel())
                    .temperature(llm.getTemperature())
                    .maxTokens(llm.getMaxTokens())
                    .topP(llm.getTopP())
                        .build(), llm);
        } catch (Exception e) {
            log.error("Error creating Minimax options for model {}", llm.getTextModel(), e);
            return null;
        }
    }

    /**
     * 根据机器人配置创建动态的MiniMaxChatModel
     * 
     * @param llm 机器人LLM配置
     * @return 配置了特定模型的MiniMaxChatModel
     */
    private AnthropicChatModel createMinimaxChatModel(RobotLlm llm) {
        if (llm == null || llm.getTextProviderUid() == null) {
            log.warn("RobotLlm or textProviderUid is null, using default chat model");
            return defaultChatModel;
        }

        Optional<LlmProviderEntity> llmProviderOptional = llmProviderRestService.findByUid(llm.getTextProviderUid());
        if (llmProviderOptional.isEmpty()) {
            log.warn("LlmProvider with uid {} not found, using default chat model", llm.getTextProviderUid());
            return defaultChatModel;
        }

        LlmProviderEntity provider = llmProviderOptional.get();
        if (provider.getApiKey() == null || provider.getApiKey().trim().isEmpty()) {
            log.warn("API key is not configured for provider {}, using default chat model", provider.getUid());
            return defaultChatModel;
        }

        try {
            log.info("Creating dynamic Minimax chat model with provider: {} ({})", provider.getType(),
                    provider.getUid());
            AnthropicChatOptions options = createDynamicOptions(llm);
            if (options == null) {
                log.warn("Failed to create Minimax options, using default chat model");
                return defaultChatModel;
            }
            AnthropicChatOptions resolvedOptions = AnthropicChatOptions.builder()
                .apiKey(provider.getApiKey())
                .baseUrl(StringUtils.hasText(provider.getBaseUrl()) ? provider.getBaseUrl() : "https://api.minimax.io/anthropic")
                .model(options.getModel())
                .temperature(options.getTemperature())
                .maxTokens(options.getMaxTokens())
                .topP(options.getTopP())
                .build();
            return AnthropicChatModel.builder()
                .options(resolvedOptions)
                .build();
        } catch (Exception e) {
            log.error("Failed to create dynamic Minimax chat model for provider {}, using default chat model",
                    provider.getUid(), e);
            return defaultChatModel;
        }
    }

    @Override
    protected String processPromptSync(String message, RobotProtobuf robot) {
        AnthropicChatOptions customOptions = robot != null && robot.getLlm() != null ? createDynamicOptions(robot.getLlm()) : null;
        return processPromptSync(buildUserOnlyPrompt(message, customOptions), robot);
    }

    @Override
    protected String processPromptSync(Prompt prompt, RobotProtobuf robot) {
        log.info("SpringAIMinimaxService processPromptSync with full prompt content");
        long startTime = System.currentTimeMillis();
        boolean success = false;
        ChatTokenUsage tokenUsage = new ChatTokenUsage(0, 0, 0);
        RobotLlm llm = robot != null ? robot.getLlm() : null;
        AnthropicChatModel chatModel = createMinimaxChatModel(llm);
        if (chatModel == null) {
            return I18Consts.I18N_SERVICE_TEMPORARILY_UNAVAILABLE;
        }

        try {
            Prompt requestPrompt = prompt;
            AnthropicChatOptions customOptions = createDynamicOptions(llm);
            if (customOptions != null) {
                requestPrompt = processPromptWithOptions(prompt, customOptions);
            }
            var chatClient = createChatClient(chatModel, requestPrompt, robot);
            var response = invokePromptSync(chatClient, requestPrompt);
            tokenUsage = extractMinimaxTokenUsage(response);
            success = true;
            return promptHelper.extractTextFromResponse(response);
        } catch (Exception e) {
            log.error("Minimax API call error: ", e);
            success = false;
            return I18Consts.I18N_SERVICE_TEMPORARILY_UNAVAILABLE;
        } finally {
            long responseTime = System.currentTimeMillis() - startTime;
            String modelType = (llm != null && StringUtils.hasText(llm.getTextModel())) ? llm.getTextModel()
                    : "minimax-chat";
            tokenUsageHelper.recordAiTokenUsage(robot, LlmProviderConstants.MINIMAX, modelType,
                    tokenUsage.getPromptTokens(), tokenUsage.getCompletionTokens(), success, responseTime);
        }
    }

    @Override
    protected void processPromptSse(Prompt prompt, RobotProtobuf robot, MessageProtobuf messageProtobufQuery,
            MessageProtobuf messageProtobufReply, List<RobotContent.SourceReference> sourceReferences,
            SseEmitter emitter) {
        log.info("SpringAIMinimaxService processPromptSse with full prompt content");
        // 从robot中获取llm配置
        RobotLlm llm = robot.getLlm();

        // 创建动态chatModel
        AnthropicChatModel chatModel = createMinimaxChatModel(llm);
        if (chatModel == null) {
            sseMessageHelper.handleSseError(new RuntimeException("Minimax service not available"), messageProtobufQuery,
                    messageProtobufReply, emitter);
            return;
        }

        // 发送起始消息
        sseMessageHelper.sendStreamStartMessage(messageProtobufQuery, messageProtobufReply, emitter,
                I18Consts.I18N_THINKING);

        // 如果有自定义选项，创建新的Prompt
        Prompt requestPrompt = prompt;
        AnthropicChatOptions customOptions = createDynamicOptions(llm);
        if (customOptions != null) {
            requestPrompt = processPromptWithOptions(prompt, customOptions);
        }

        long startTime = System.currentTimeMillis();
        final boolean[] success = { false };
        final ChatTokenUsage[] tokenUsage = { new ChatTokenUsage(0, 0, 0) };

        var chatClient = createChatClient(chatModel, requestPrompt, robot);
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
                                log.info("Minimax API response metadata: {}, text {}",
                                        response.getMetadata(), textContent);

                                sseMessageHelper.sendStreamMessage(messageProtobufQuery, messageProtobufReply, emitter,
                                textContent, reasonContent, sourceReferences);
                            }
                            // 提取token使用情况
                            tokenUsage[0] = extractMinimaxTokenUsage(response);
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
                            tokenUsage[0].getPromptTokens(), tokenUsage[0].getCompletionTokens(),
                            tokenUsage[0].getTotalTokens(), prompt, LlmProviderConstants.MINIMAX,
                            (llm != null && StringUtils.hasText(llm.getTextModel())) ? llm.getTextModel()
                                    : "minimax-chat");
                    // 记录token使用情况
                    long responseTime = System.currentTimeMillis() - startTime;
                    String modelType = (llm != null && StringUtils.hasText(llm.getTextModel())) ? llm.getTextModel()
                            : "minimax-chat";
                    tokenUsageHelper.recordAiTokenUsage(robot, LlmProviderConstants.MINIMAX, modelType,
                            tokenUsage[0].getPromptTokens(), tokenUsage[0].getCompletionTokens(), success[0],
                            responseTime);
                });
    }

    /**
     * 专门为Minimax API提取token使用情况
     * 由于Minimax API返回的usage字段是EmptyUsage对象，需要特殊处理
     * 
     * @param response ChatResponse对象
     * @return TokenUsage对象
     */
    private ChatTokenUsage extractMinimaxTokenUsage(ChatResponse response) {
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

}
