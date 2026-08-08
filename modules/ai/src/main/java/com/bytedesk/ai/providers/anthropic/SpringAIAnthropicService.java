/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-08-02 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2026-08-02 10:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM –
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 *  contact: 270580156@qq.com
 *
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved.
 */
package com.bytedesk.ai.providers.anthropic;

import java.util.List;
import java.util.Optional;

import org.springframework.ai.anthropic.AnthropicChatModel;
import org.springframework.ai.anthropic.AnthropicChatOptions;
import org.springframework.ai.chat.messages.AssistantMessage;
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

/**
 * Anthropic AI 服务
 * https://docs.spring.io/spring-ai/reference/api/chat/anthropic-chat.html
 */
@Slf4j
@Service
public class SpringAIAnthropicService extends BaseSpringAIService {

    public SpringAIAnthropicService(
            LlmProviderRestService llmProviderRestService,
            @Qualifier("anthropicChatModel") ObjectProvider<AnthropicChatModel> defaultChatModelProvider,
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
     * 根据机器人配置创建 AnthropicChatOptions
     *
     * @param llm 机器人LLM配置
     * @return 根据机器人配置创建的选项
     */
    private AnthropicChatOptions createAnthropicOptions(RobotLlm llm) {
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
            log.error("Error creating Anthropic options for model {}", llm.getTextModel(), e);
            return null;
        }
    }

    /**
     * 根据机器人配置创建动态的AnthropicChatModel
     *
     * @param llm 机器人LLM配置
     * @return 配置了特定模型的AnthropicChatModel
     */
    private AnthropicChatModel createAnthropicChatModel(RobotLlm llm) {
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
            log.info("Creating dynamic Anthropic chat model with provider: {} ({})", provider.getType(),
                    provider.getUid());
            AnthropicChatOptions options = createAnthropicOptions(llm);
            if (options == null) {
                log.warn("Failed to create Anthropic options, using default chat model");
                return defaultChatModel;
            }
            // 通过 mutate 注入连接信息，AnthropicChatModel 内部会据此构建 client
            AnthropicChatOptions resolvedOptions = AnthropicChatOptions.builder()
                    .apiKey(provider.getApiKey())
                    .baseUrl(provider.getBaseUrl())
                    .model(llm.getTextModel())
                    .temperature(llm.getTemperature())
                    .maxTokens(llm.getMaxTokens())
                    .topP(llm.getTopP())
                    .build();
            return AnthropicChatModel.builder()
                    .options(resolvedOptions)
                    .build();
        } catch (Exception e) {
            log.error("Failed to create dynamic Anthropic chat model for provider {}, using default chat model",
                    provider.getUid(), e);
            return defaultChatModel;
        }
    }

    @Override
    protected String processPromptSync(String message, RobotProtobuf robot) {
        AnthropicChatOptions customOptions = robot != null && robot.getLlm() != null
                ? createAnthropicOptions(robot.getLlm())
                : null;
        return processPromptSync(buildUserOnlyPrompt(message, customOptions), robot);
    }

    @Override
    protected String processPromptSync(Prompt prompt, RobotProtobuf robot) {
        long startTime = System.currentTimeMillis();
        boolean success = false;
        ChatTokenUsage tokenUsage = new ChatTokenUsage(0, 0, 0);

        log.info("Anthropic API sync ");

        RobotLlm llm = robot.getLlm();
        if (llm == null) {
            log.info("Anthropic API not available");
            return "Anthropic service is not available";
        }

        AnthropicChatModel chatModel = createAnthropicChatModel(llm);

        try {
            Prompt requestPrompt = prompt;
            AnthropicChatOptions customOptions = robot != null && robot.getLlm() != null
                    ? createAnthropicOptions(robot.getLlm())
                    : null;
            if (customOptions != null) {
                requestPrompt = processPromptWithOptions(prompt, customOptions);
            }

            var chatClient = createChatClient(chatModel, requestPrompt);
            var response = invokePromptSync(chatClient, requestPrompt);
            tokenUsage = tokenUsageHelper.extractTokenUsage(response);
            success = true;
            return promptHelper.extractTextFromResponse(response);
        } catch (Exception e) {
            log.error("Anthropic API sync error", e);
            success = false;
            return I18Consts.I18N_SERVICE_TEMPORARILY_UNAVAILABLE;
        } finally {
            long responseTime = System.currentTimeMillis() - startTime;
            String modelType = (robot != null && robot.getLlm() != null
                    && StringUtils.hasText(robot.getLlm().getTextModel()))
                            ? robot.getLlm().getTextModel()
                            : LlmProviderConstants.ANTHROPIC_DEFAULT_MODEL;
            tokenUsageHelper.recordAiTokenUsage(robot, LlmProviderConstants.ANTHROPIC, modelType,
                    tokenUsage.getPromptTokens(), tokenUsage.getCompletionTokens(), success, responseTime);
        }
    }

    @Override
    protected void processPromptSse(Prompt prompt, RobotProtobuf robot, MessageProtobuf messageProtobufQuery,
            MessageProtobuf messageProtobufReply, List<RobotContent.SourceReference> sourceReferences,
            SseEmitter emitter) {
        RobotLlm llm = robot.getLlm();
        log.info("Anthropic API SSE ");

        if (llm == null) {
            log.info("Anthropic API not available");
            sseMessageHelper.sendStreamEndMessage(messageProtobufQuery, messageProtobufReply, emitter, 0, 0, 0, prompt,
                    LlmProviderConstants.ANTHROPIC,
                    LlmProviderConstants.ANTHROPIC_DEFAULT_MODEL);
            return;
        }

        // 获取适当的模型实例
        AnthropicChatModel chatModel = createAnthropicChatModel(llm);

        if (chatModel == null) {
            log.error("Failed to create Anthropic chat model and no default chat model available");
            sseMessageHelper.sendStreamEndMessage(messageProtobufQuery, messageProtobufReply, emitter, 0, 0, 0, prompt,
                    LlmProviderConstants.ANTHROPIC,
                    (llm != null && StringUtils.hasText(llm.getTextModel())) ? llm.getTextModel()
                            : LlmProviderConstants.ANTHROPIC_DEFAULT_MODEL);
            return;
        }

        long startTime = System.currentTimeMillis();
        final boolean[] success = { false };
        final ChatTokenUsage[] tokenUsage = { new ChatTokenUsage(0, 0, 0) };

        try {
            // 发送初始消息，告知用户请求已收到，正在处理
            sseMessageHelper.sendStreamStartMessage(messageProtobufQuery, messageProtobufReply, emitter,
                    I18Consts.I18N_THINKING);

            var chatClient = createChatClient(chatModel, prompt);
            invokePromptStream(chatClient, prompt).subscribe(
                    response -> {
                        try {
                            if (response != null && !sseMessageHelper.isEmitterCompleted(emitter)) {
                                List<Generation> generations = response.getResults();
                                for (Generation generation : generations) {
                                    AssistantMessage assistantMessage = generation.getOutput();
                                    String textContent = assistantMessage.getText();
                                    String reasonContent = extractReasoningContent(generation, assistantMessage);
                                    log.info("Anthropic API SSE response text: {}", textContent);

                                    sseMessageHelper.sendStreamMessage(messageProtobufQuery, messageProtobufReply,
                                            emitter, textContent, reasonContent, sourceReferences);
                                }
                                // 提取token使用情况
                                tokenUsage[0] = tokenUsageHelper.extractTokenUsage(response);
                                success[0] = true;
                            }
                        } catch (Exception e) {
                            log.error("Anthropic API SSE error 1: ", e);
                            sseMessageHelper.handleSseError(e, messageProtobufQuery, messageProtobufReply, emitter);
                            success[0] = false;
                        }
                    },
                    error -> {
                        log.error("Anthropic API SSE error 2: ", error);
                        sseMessageHelper.handleSseError(error, messageProtobufQuery, messageProtobufReply, emitter);
                        success[0] = false;
                    },
                    () -> {
                        log.info("Anthropic API SSE complete");
                        // 发送流结束消息，包含token使用情况和prompt内容
                        sseMessageHelper.sendStreamEndMessage(messageProtobufQuery, messageProtobufReply, emitter,
                                tokenUsage[0].getPromptTokens(), tokenUsage[0].getCompletionTokens(),
                                tokenUsage[0].getTotalTokens(), prompt, LlmProviderConstants.ANTHROPIC,
                                (llm != null && StringUtils.hasText(llm.getTextModel())) ? llm.getTextModel()
                                        : LlmProviderConstants.ANTHROPIC_DEFAULT_MODEL);
                        // 记录token使用情况
                        long responseTime = System.currentTimeMillis() - startTime;
                        String modelType = (llm != null && StringUtils.hasText(llm.getTextModel())) ? llm.getTextModel()
                                : LlmProviderConstants.ANTHROPIC_DEFAULT_MODEL;
                        tokenUsageHelper.recordAiTokenUsage(robot, LlmProviderConstants.ANTHROPIC, modelType,
                                tokenUsage[0].getPromptTokens(), tokenUsage[0].getCompletionTokens(), success[0],
                                responseTime);
                    });
        } catch (Exception e) {
            log.error("Error starting Anthropic stream 4", e);
            sseMessageHelper.handleSseError(e, messageProtobufQuery, messageProtobufReply, emitter);
            success[0] = false;
            // 记录token使用情况
            long responseTime = System.currentTimeMillis() - startTime;
            String modelType = (llm != null && StringUtils.hasText(llm.getTextModel())) ? llm.getTextModel()
                    : LlmProviderConstants.ANTHROPIC_DEFAULT_MODEL;
            tokenUsageHelper.recordAiTokenUsage(robot, LlmProviderConstants.ANTHROPIC, modelType,
                    tokenUsage[0].getPromptTokens(), tokenUsage[0].getCompletionTokens(), success[0], responseTime);
        }
    }

}
