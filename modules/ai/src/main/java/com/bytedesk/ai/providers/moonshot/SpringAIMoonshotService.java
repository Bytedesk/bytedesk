/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-04-23 14:25:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2026-04-23 14:25:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.providers.moonshot;

import java.util.List;
import java.util.Optional;

import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.retry.RetryTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.bytedesk.ai.llm_provider.LlmProviderEntity;
import com.bytedesk.ai.llm_provider.LlmProviderRestService;
import com.bytedesk.ai.providers.moonshot.api.MoonshotApi;
import com.bytedesk.ai.providers.moonshot.api.MoonshotChatModel;
import com.bytedesk.ai.providers.moonshot.api.MoonshotChatOptions;
import com.bytedesk.ai.robot.RobotLlm;
import com.bytedesk.ai.robot.RobotProtobuf;
import com.bytedesk.ai.service.BaseSpringAIService;
import com.bytedesk.ai.service.ChatTokenUsage;
import com.bytedesk.ai.service.TokenUsageHelper;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.llm.LlmProviderConstants;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.content.RobotContent;

import io.micrometer.observation.ObservationRegistry;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SpringAIMoonshotService extends BaseSpringAIService {

    private final ObjectProvider<MoonshotChatModel> defaultChatModelProvider;

    private final ObjectProvider<ToolCallingManager> toolCallingManagerProvider;

    private final ObjectProvider<RetryTemplate> retryTemplateProvider;

    private final ObjectProvider<ObservationRegistry> observationRegistryProvider;

    public SpringAIMoonshotService(
            LlmProviderRestService llmProviderRestService,
            @Qualifier("moonshotChatModel") ObjectProvider<MoonshotChatModel> defaultChatModelProvider,
            TokenUsageHelper tokenUsageHelper,
            ObjectProvider<ToolCallingManager> toolCallingManagerProvider,
            ObjectProvider<RetryTemplate> retryTemplateProvider,
            ObjectProvider<ObservationRegistry> observationRegistryProvider) {
        this.llmProviderRestService = llmProviderRestService;
        this.defaultChatModelProvider = defaultChatModelProvider;
        this.tokenUsageHelper = tokenUsageHelper;
        this.toolCallingManagerProvider = toolCallingManagerProvider;
        this.retryTemplateProvider = retryTemplateProvider;
        this.observationRegistryProvider = observationRegistryProvider;
    }

    // public SpringAIMoonshotService() {}

    static final String DEFAULT_MOONSHOT_MODEL = "kimi-k2.6";

    private final LlmProviderRestService llmProviderRestService;

    private final TokenUsageHelper tokenUsageHelper;


    static boolean requiresFixedTemperature(String model) {
        return StringUtils.hasText(model) && model.startsWith("kimi-k2");
    }

    static Double normalizeTemperature(String model, Double temperature) {
        if (requiresFixedTemperature(model)) {
            return 1.0D;
        }
        if (temperature == null) {
            return null;
        }
        return Math.max(0.0D, Math.min(1.0D, temperature));
    }

    static boolean requiresFixedTopP(String model) {
        return StringUtils.hasText(model) && model.startsWith("kimi-k2");
    }

    static Double normalizeTopP(String model, Double topP) {
        if (requiresFixedTopP(model)) {
            return 0.95D;
        }
        if (topP == null) {
            return null;
        }
        if (topP <= 0.0D) {
            return null;
        }
        return Math.min(1.0D, topP);
    }

    private MoonshotChatOptions createDynamicOptions(RobotLlm llm) {
        if (llm == null || !StringUtils.hasText(llm.getTextModel())) {
            return null;
        }
        try {
            MoonshotChatOptions.Thinking thinking = createThinkingOptions(llm);
                return applyRobotToolCallbacks(MoonshotChatOptions.builder()
                    .model(llm.getTextModel())
                    .temperature(normalizeTemperature(llm.getTextModel(), llm.getTemperature()))
                    .maxCompletionTokens(llm.getMaxTokens())
                    .topP(normalizeTopP(llm.getTextModel(), llm.getTopP()))
                    .thinking(thinking)
                        .build(), llm);
        } catch (Exception e) {
            log.error("Error creating Moonshot options for model {}", llm.getTextModel(), e);
            return null;
        }
    }

    private MoonshotChatOptions.Thinking createThinkingOptions(RobotLlm llm) {
        if (llm == null || !StringUtils.hasText(llm.getTextModel()) || !llm.getTextModel().startsWith("kimi-k2")) {
            return null;
        }
        boolean enabled = !Boolean.FALSE.equals(llm.getThinking());
        return new MoonshotChatOptions.Thinking(
                enabled ? MoonshotChatOptions.Thinking.ENABLED : MoonshotChatOptions.Thinking.DISABLED);
    }

    private void logMoonshotRequestFailure(String mode, Exception exception) {
        if (exception instanceof WebClientResponseException responseException) {
            log.error("Moonshot API {} error: status={}, body={}", mode,
                    responseException.getStatusCode(), responseException.getResponseBodyAsString(), responseException);
            return;
        }
        log.error("Moonshot API {} error", mode, exception);
    }

    private MoonshotApi createMoonshotApi(String apiUrl, String apiKey) {
        return MoonshotApi.builder()
                .baseUrl(apiUrl)
                .apiKey(apiKey)
                .build();
    }

    private MoonshotChatModel getDefaultChatModel() {
        return defaultChatModelProvider.getIfAvailable();
    }

    private ToolCallingManager getToolCallingManager() {
        return toolCallingManagerProvider.getIfAvailable();
    }

    private RetryTemplate getRetryTemplate() {
        return retryTemplateProvider.getIfAvailable();
    }

    private ObservationRegistry getObservationRegistry() {
        return observationRegistryProvider.getIfAvailable();
    }

    private MoonshotChatModel createMoonshotChatModel(RobotLlm llm) {
        MoonshotChatModel defaultChatModel = getDefaultChatModel();
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
        if (!StringUtils.hasText(provider.getApiKey())) {
            log.warn("API key is not configured for provider {}, using default chat model", provider.getUid());
            return defaultChatModel;
        }

        try {
            MoonshotChatOptions options = createDynamicOptions(llm);
            if (options == null) {
                log.warn("Failed to create Moonshot options, using default chat model");
                return defaultChatModel;
            }

            MoonshotChatModel.Builder builder = MoonshotChatModel.builder()
                    .moonshotApi(createMoonshotApi(provider.getBaseUrl(), provider.getApiKey()))
                    .defaultOptions(options);

            ToolCallingManager toolCallingManager = getToolCallingManager();
            if (toolCallingManager != null) {
                builder.toolCallingManager(toolCallingManager);
            }
            RetryTemplate retryTemplate = getRetryTemplate();
            if (retryTemplate != null) {
                builder.retryTemplate(retryTemplate);
            }
            ObservationRegistry observationRegistry = getObservationRegistry();
            if (observationRegistry != null) {
                builder.observationRegistry(observationRegistry);
            }

            return builder.build();
        } catch (Exception e) {
            log.error("Failed to create dynamic Moonshot chat model for provider {}, using default chat model",
                    provider.getUid(), e);
            return defaultChatModel;
        }
    }

    @Override
    protected String processPromptSync(String message, RobotProtobuf robot) {
        MoonshotChatOptions customOptions = robot != null && robot.getLlm() != null ? createDynamicOptions(robot.getLlm()) : null;
        return processPromptSync(buildUserOnlyPrompt(message, customOptions), robot);
    }

    @Override
    protected String processPromptSync(Prompt prompt, RobotProtobuf robot) {
        long startTime = System.currentTimeMillis();
        boolean success = false;
        ChatTokenUsage tokenUsage = new ChatTokenUsage(0, 0, 0);
        RobotLlm llm = robot != null ? robot.getLlm() : null;
        MoonshotChatModel chatModel = createMoonshotChatModel(llm);
        if (chatModel == null) {
            return I18Consts.I18N_SERVICE_TEMPORARILY_UNAVAILABLE;
        }

        try {
            Prompt requestPrompt = prompt;
            MoonshotChatOptions customOptions = createDynamicOptions(llm);
            if (customOptions != null) {
                requestPrompt = processPromptWithOptions(prompt, customOptions);
            }
            var chatClient = createChatClient(chatModel, requestPrompt, robot);
            var response = invokePromptSync(chatClient, requestPrompt);
            tokenUsage = tokenUsageHelper.extractTokenUsage(response);
            success = true;
            return promptHelper.extractTextFromResponse(response);
        } catch (Exception e) {
            log.error("AI sync error", e);
            success = false;
            return I18Consts.I18N_SERVICE_TEMPORARILY_UNAVAILABLE;
        } finally {
            long responseTime = System.currentTimeMillis() - startTime;
            String modelType = (llm != null && StringUtils.hasText(llm.getTextModel())) ? llm.getTextModel()
                    : DEFAULT_MOONSHOT_MODEL;
            tokenUsageHelper.recordAiTokenUsage(robot, LlmProviderConstants.MOONSHOT, modelType,
                    tokenUsage.getPromptTokens(), tokenUsage.getCompletionTokens(), success, responseTime);
        }
    }

    @Override
    protected void processPromptSse(Prompt prompt, RobotProtobuf robot, MessageProtobuf messageProtobufQuery,
            MessageProtobuf messageProtobufReply, List<RobotContent.SourceReference> sourceReferences,
            SseEmitter emitter) {
        RobotLlm llm = robot.getLlm();
        MoonshotChatModel chatModel = createMoonshotChatModel(llm);
        if (chatModel == null) {
            sseMessageHelper.handleSseError(new RuntimeException("Moonshot service not available"),
                    messageProtobufQuery, messageProtobufReply, emitter);
            return;
        }

        sseMessageHelper.sendStreamStartMessage(messageProtobufQuery, messageProtobufReply, emitter,
                I18Consts.I18N_THINKING);

        Prompt requestPrompt = prompt;
        MoonshotChatOptions customOptions = createDynamicOptions(llm);
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
                                sseMessageHelper.sendStreamMessage(messageProtobufQuery, messageProtobufReply,
                                        emitter, textContent, reasonContent, sourceReferences);
                            }
                            tokenUsage[0] = tokenUsageHelper.extractTokenUsage(response);
                            success[0] = true;
                        }
                    } catch (Exception e) {
                        log.error("Error sending Moonshot SSE event", e);
                        sseMessageHelper.handleSseError(e, messageProtobufQuery, messageProtobufReply, emitter);
                        success[0] = false;
                    }
                },
                error -> {
                    logMoonshotRequestFailure("SSE", error instanceof Exception ? (Exception) error : new RuntimeException(error));
                    sseMessageHelper.handleSseError(error, messageProtobufQuery, messageProtobufReply, emitter);
                    success[0] = false;
                },
                () -> {
                    sseMessageHelper.sendStreamEndMessage(messageProtobufQuery, messageProtobufReply, emitter,
                            tokenUsage[0].getPromptTokens(), tokenUsage[0].getCompletionTokens(),
                            tokenUsage[0].getTotalTokens(), prompt, LlmProviderConstants.MOONSHOT,
                        (llm != null && StringUtils.hasText(llm.getTextModel())) ? llm.getTextModel() : DEFAULT_MOONSHOT_MODEL);
                    long responseTime = System.currentTimeMillis() - startTime;
                    String modelType = (llm != null && StringUtils.hasText(llm.getTextModel())) ? llm.getTextModel()
                        : DEFAULT_MOONSHOT_MODEL;
                    tokenUsageHelper.recordAiTokenUsage(robot, LlmProviderConstants.MOONSHOT, modelType,
                            tokenUsage[0].getPromptTokens(), tokenUsage[0].getCompletionTokens(), success[0],
                            responseTime);
                });
    }

    public MoonshotChatModel getChatModel() {
        return getDefaultChatModel();
    }

    public Boolean isServiceHealthy() {
        return getDefaultChatModel() != null;
    }
}