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
import java.util.Optional;

import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.bytedesk.ai.llm_provider.LlmProviderEntity;
import com.bytedesk.ai.llm_provider.LlmProviderRestService;
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
import io.micrometer.observation.ObservationRegistry;
import com.volcengine.ark.runtime.service.ArkApi;

import lombok.extern.slf4j.Slf4j;

/**
 * 火山引擎方舟服务。
 *
 * <p>2026-11 迁移至方舟官方 SDK（com.volcengine:ark-runtime），动态创建
 * {@link ArkChatModel}（provider apiKey/baseUrl + robot options），经
 * {@code createChatClient} 注入 Advisor 链（多轮记忆/工具调用/SafeGuard），
 * 模式对齐 ZhipuaiService / DashscopeService。</p>
 */
@Slf4j
@Service
public class SpringAIVolcengineService extends BaseSpringAIService {

    public SpringAIVolcengineService(
            LlmProviderRestService llmProviderRestService,
            @Qualifier("volcengineChatModel") ObjectProvider<ChatModel> defaultChatModelProvider,
            TokenUsageHelper tokenUsageHelper,
            ObjectProvider<ObservationRegistry> observationRegistryProvider,
            @Value("${spring.ai.volcengine.api-key:}") String globalApiKey,
            @Value("${spring.ai.volcengine.base-url:https://ark.cn-beijing.volces.com/api/v3}") String globalBaseUrl) {
        this.llmProviderRestService = llmProviderRestService;
        this.defaultChatModel = defaultChatModelProvider.getIfAvailable();
        this.tokenUsageHelper = tokenUsageHelper;
        this.observationRegistryProvider = observationRegistryProvider;
        this.globalApiKey = globalApiKey;
        this.globalBaseUrl = globalBaseUrl;
    }

    private final LlmProviderRestService llmProviderRestService;

    /** 全局回退 ChatModel Bean（volcengineChatModel，全局 spring.ai.volcengine.* 配置）。 */
    private final ChatModel defaultChatModel;

    private final TokenUsageHelper tokenUsageHelper;

    /** 动态建 model 时传入的 ObservationRegistry（可选，产生 gen_ai_* 指标）。 */
    private final ObjectProvider<ObservationRegistry> observationRegistryProvider;

    /** 全局回退 apiKey（来自 spring.ai.volcengine.api-key）。 */
    private final String globalApiKey;

    /** 全局回退 baseUrl（来自 spring.ai.volcengine.base-url）。 */
    private final String globalBaseUrl;

    /**
     * 根据机器人配置创建动态的ArkChatOptions（含工具回调与思考模式）。
     * enableThinking 仅在 llm.thinking 显式为 true 时开启；否则不设置
     * （思考模式由模型名决定，非思考模型设置 thinking 会导致 400）。
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
            log.error("Error creating dynamic Volcengine options for model {}", llm.getTextModel(), e);
            return null;
        }
    }

    /**
     * 根据机器人配置动态创建 ArkChatModel。
     * provider 缺失 / apiKey 缺失 / options 构建失败时回退 volcengineChatModel Bean（全局配置）。
     */
    private ChatModel createVolcengineChatModel(RobotLlm llm) {
        if (llm == null || !StringUtils.hasText(llm.getTextModel())) {
            return defaultChatModel;
        }
        try {
            ArkChatOptions options = createDynamicOptions(llm);
            if (options == null) {
                log.warn("Failed to create Volcengine options, using default chat model");
                return defaultChatModel;
            }
            // provider 凭据优先；缺失时回退全局 spring.ai.volcengine.* 配置
            String apiKey = null;
            String baseUrl = null;
            if (StringUtils.hasText(llm.getTextProviderUid())) {
                Optional<LlmProviderEntity> llmProviderOptional =
                        llmProviderRestService.findByUid(llm.getTextProviderUid());
                if (llmProviderOptional.isPresent()) {
                    LlmProviderEntity provider = llmProviderOptional.get();
                    apiKey = StringUtils.hasText(provider.getApiKey()) ? provider.getApiKey() : null;
                    baseUrl = StringUtils.hasText(provider.getBaseUrl())
                            ? provider.getBaseUrl() : null;
                } else {
                    log.warn("LlmProvider with uid {} not found", llm.getTextProviderUid());
                }
            }
            if (apiKey == null && StringUtils.hasText(globalApiKey) && !"sk-xxx".equalsIgnoreCase(globalApiKey)) {
                apiKey = globalApiKey;
            }
            if (apiKey == null) {
                log.warn("No apiKey available for Volcengine provider, using default chat model");
                return defaultChatModel;
            }
            if (baseUrl == null) {
                baseUrl = globalBaseUrl;
            }
            ArkApi service = ArkServiceFactory.createApi(apiKey,
                    SpringAIVolcengineChatConfig.normalizeBaseUrl(baseUrl));
            ObservationRegistry observationRegistry = observationRegistryProvider.getIfAvailable();
            if (observationRegistry != null) {
                return new ArkChatModel(service, options, observationRegistry);
            }
            return new ArkChatModel(service, options);
        } catch (Exception e) {
            log.error("Failed to create dynamic Volcengine chat model, using default chat model", e);
            return defaultChatModel;
        }
    }

    @Override
    protected String processPromptSync(String message, RobotProtobuf robot) {
        ArkChatOptions customOptions = robot != null && robot.getLlm() != null
                ? createDynamicOptions(robot.getLlm()) : null;
        return processPromptSync(buildUserOnlyPrompt(message, customOptions), robot);
    }

    @Override
    protected String processPromptSync(Prompt prompt, RobotProtobuf robot) {
        log.info("SpringAIVolcengineService processPromptSync with full prompt content");
        long startTime = System.currentTimeMillis();
        boolean success = false;
        ChatTokenUsage tokenUsage = new ChatTokenUsage(0, 0, 0);
        RobotLlm llm = robot != null ? robot.getLlm() : null;
        ChatModel chatModel = createVolcengineChatModel(llm);
        if (chatModel == null) {
            return I18Consts.I18N_SERVICE_TEMPORARILY_UNAVAILABLE;
        }

        try {
            Prompt requestPrompt = prompt;
            ArkChatOptions customOptions = createDynamicOptions(llm);
            if (customOptions != null) {
                requestPrompt = processPromptWithOptions(prompt, customOptions);
            }
            var chatClient = createChatClient(chatModel, requestPrompt, robot);
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
            String modelType = (llm != null && StringUtils.hasText(llm.getTextModel())) ? llm.getTextModel() : "volcengine-chat";
            tokenUsageHelper.recordAiTokenUsage(robot, LlmProviderConstants.VOLCENGINE,
                    modelType, tokenUsage.getPromptTokens(), tokenUsage.getCompletionTokens(), success, responseTime);
        }
    }

    @Override
    protected void processPromptSse(Prompt prompt, RobotProtobuf robot, MessageProtobuf messageProtobufQuery,
            MessageProtobuf messageProtobufReply, List<RobotContent.SourceReference> sourceReferences, SseEmitter emitter) {
        log.info("SpringAIVolcengineService processPromptSse with full prompt content");
        // 从robot中获取llm配置
        RobotLlm llm = robot.getLlm();

        // 创建动态chatModel
        ChatModel chatModel = createVolcengineChatModel(llm);
        if (chatModel == null) {
            sseMessageHelper.handleSseError(new RuntimeException(I18Consts.I18N_SERVICE_TEMPORARILY_UNAVAILABLE), messageProtobufQuery, messageProtobufReply, emitter);
            return;
        }

        // 发送起始消息
        sseMessageHelper.sendStreamStartMessage(messageProtobufQuery, messageProtobufReply, emitter, I18Consts.I18N_THINKING);

        // 如果有自定义选项，创建新的Prompt
        Prompt requestPrompt = prompt;
        ArkChatOptions customOptions = createDynamicOptions(llm);
        if (customOptions != null) {
            requestPrompt = processPromptWithOptions(prompt, customOptions);
        }

        long startTime = System.currentTimeMillis();
        final boolean[] success = {false};
        final ChatTokenUsage[] tokenUsage = {new ChatTokenUsage(0, 0, 0)};

        var chatClient = createChatClient(chatModel, requestPrompt, robot);
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
                    log.error("Volcengine API SSE error: ", error);
                    sseMessageHelper.handleSseError(error, messageProtobufQuery, messageProtobufReply, emitter);
                    success[0] = false;
                },
                () -> {
                    log.info("Volcengine API SSE complete");
                    // 发送流结束消息，包含token使用情况和prompt内容
                    sseMessageHelper.sendStreamEndMessage(messageProtobufQuery, messageProtobufReply, emitter,
                            tokenUsage[0].getPromptTokens(), tokenUsage[0].getCompletionTokens(), tokenUsage[0].getTotalTokens(), prompt, LlmProviderConstants.VOLCENGINE, (llm != null && StringUtils.hasText(llm.getTextModel())) ? llm.getTextModel() : "volcengine-chat");
                    // 记录token使用情况
                    long responseTime = System.currentTimeMillis() - startTime;
                    String modelType = (llm != null && StringUtils.hasText(llm.getTextModel())) ? llm.getTextModel() : "volcengine-chat";
                    tokenUsageHelper.recordAiTokenUsage(robot, LlmProviderConstants.VOLCENGINE, modelType,
                            tokenUsage[0].getPromptTokens(), tokenUsage[0].getCompletionTokens(), success[0], responseTime);
                });
    }

    public ChatModel getChatModel() {
        // 动态创建场景需 robot 配置；此处返回全局 Bean 供调试端点使用
        return defaultChatModel;
    }

    public Boolean isServiceHealthy() {
        try {
            // 由于现在使用动态创建，健康检查需要有效的robot配置
            // 这里简化处理，返回true表示服务可用
            return true;
        } catch (Exception e) {
            log.error("Error checking Volcengine service health", e);
            return false;
        }
    }
}
