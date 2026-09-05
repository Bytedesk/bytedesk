/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-28 11:44:03
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-25 09:23:33
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.provider.dashscope;

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
import com.bytedesk.ai.provider.dashscope.chat.DashScopeChatModel;
import com.bytedesk.ai.provider.dashscope.chat.DashScopeChatOptions;
import com.bytedesk.ai.robot.RobotLlm;
import com.bytedesk.ai.robot.RobotProtobuf;
import com.bytedesk.ai.service.BaseSpringAIService;
import com.bytedesk.ai.service.ChatTokenUsage;
import com.bytedesk.ai.service.TokenUsageHelper;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.llm.LlmDefaults;
import com.bytedesk.core.llm.LlmProviderConstants;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.content.RobotContent;

import io.micrometer.observation.ObservationRegistry;
import lombok.extern.slf4j.Slf4j;

/**
 * DashScope 文本对话服务（Spring AI ChatClient 链路）。
 *
 * <p>2026-09-04（规划 docs/plans/2026-09-04-dashscope-springai-chatmodel-chatmemory-plan.md Phase 3）：
 * 回接 Spring AI ChatClient 路径，对齐 SpringAIDeepseekService —— 动态创建
 * {@link DashScopeChatModel}（provider apiKey/baseUrl + robot options），经
 * {@code createChatClient(chatModel, prompt, robot)} 注入 Advisor 链，从而获得：
 * <ul>
 *   <li>多轮记忆：MessageChatMemoryAdvisor → SPRING_AI_CHAT_MEMORY 表</li>
 *   <li>工具调用：ToolCallingChatOptions + 框架自动注册的 ToolCallingAdvisor</li>
 *   <li>SafeGuard / ReReading / Logging 等横切 Advisor</li>
 * </ul>
 * 2026-08-12 的原生 SDK 直调路径（Generation.call/streamCall）由此退役；
 * baseUrl 归一化与 apiKey 回退语义（2026-08-12 回归三连修复）保留在
 * resolveBaseUrl/resolveApiKey 中，由动态建 model 复用。</p>
 */
@Slf4j
@Service
public class DashscopeService extends BaseSpringAIService {

    public DashscopeService(
            LlmProviderRestService llmProviderRestService,
            TokenUsageHelper tokenUsageHelper,
            @Qualifier("bytedeskDashscopeChatModel") ObjectProvider<ChatModel> defaultChatModelProvider,
            ObjectProvider<ObservationRegistry> observationRegistryProvider,
            @Value("${spring.ai.dashscope.api-key:}") String globalApiKey,
            @Value("${spring.ai.dashscope.base-url:}") String globalBaseUrl) {
        this.llmProviderRestService = llmProviderRestService;
        this.tokenUsageHelper = tokenUsageHelper;
        this.defaultChatModel = defaultChatModelProvider.getIfAvailable();
        this.observationRegistryProvider = observationRegistryProvider;
        this.globalApiKey = globalApiKey;
        this.globalBaseUrl = globalBaseUrl;
    }

    private final LlmProviderRestService llmProviderRestService;

    private final TokenUsageHelper tokenUsageHelper;

    /** 全局回退 ChatModel Bean（DashscopeChatConfig，全局 spring.ai.dashscope.* 配置），provider 不可用时回退。 */
    private final ChatModel defaultChatModel;

    /** 动态建 model 时传入的 ObservationRegistry（可选，产生 gen_ai_* 指标）。 */
    private final ObjectProvider<ObservationRegistry> observationRegistryProvider;

    /** 全局回退 apiKey（来自 spring.ai.dashscope.api-key），与旧 bytedeskDashscopeChatModel Bean 一致。 */
    private final String globalApiKey;

    /** 全局回退 baseUrl（来自 spring.ai.dashscope.base-url）。 */
    private final String globalBaseUrl;

    /**
     * 解析 apiKey：优先 provider 自身配置，为空时回退到全局 spring.ai.dashscope.api-key。
     * 两者都不可用时返回 null（由 createDashscopeChatModel 回退 defaultChatModel Bean）。
     */
    private String resolveApiKey(LlmProviderEntity provider) {
        String key = provider.getApiKey();
        if (StringUtils.hasText(key)) {
            return key;
        }
        if (StringUtils.hasText(globalApiKey) && !"sk-xxx".equalsIgnoreCase(globalApiKey)) {
            log.info("Provider {} apiKey empty, fallback to global spring.ai.dashscope.api-key", provider.getUid());
            return globalApiKey;
        }
        return null;
    }

    /**
     * 解析 baseUrl：优先 provider 自身配置，为空时回退到全局 spring.ai.dashscope.base-url。
     * 统一通过 {@link DashScopeBaseUrlSupport#normalize} 归一化，与旧 DashScopeChatModel 行为一致
     * （默认值 https://dashscope.aliyuncs.com/api/v1，含 /api/v1 后缀，避免 SSE/同步请求 404）。
     */
    private String resolveBaseUrl(LlmProviderEntity provider) {
        String url = provider.getBaseUrl();
        if (!StringUtils.hasText(url)) {
            url = globalBaseUrl;
        }
        return DashScopeBaseUrlSupport.normalize(url);
    }

    /**
     * 根据机器人配置创建 DashScope chat options（含工具回调注入，applyRobotToolCallbacks）。
     * 对齐 SpringAIDeepseekService.createDeepseekOptions 模式。
     */
    private DashScopeChatOptions createDashscopeOptions(RobotLlm llm) {
        if (llm == null || !StringUtils.hasText(llm.getTextModel())) {
            return null;
        }
        try {
            return applyRobotToolCallbacks(DashScopeChatOptions.builder()
                    .model(llm.getTextModel())
                    .temperature(llm.getTemperature())
                    .maxTokens(llm.getMaxTokens())
                    .topP(llm.getTopP())
                    .build(), llm);
        } catch (Exception e) {
            log.error("Error creating Dashscope options for model {}", llm.getTextModel(), e);
            return null;
        }
    }

    /**
     * 根据机器人配置动态创建 DashScopeChatModel（对齐 SpringAIDeepseekService.createDeepseekChatModel 模式）。
     * provider 缺失 / apiKey 缺失 / options 构建失败时回退 defaultChatModel Bean（全局配置）。
     * DashScopeChatModel 轻量（仅持 baseUrl/apiKey/options），按请求新建成本可接受（与 DeepSeek 一致）。
     */
    private ChatModel createDashscopeChatModel(RobotLlm llm) {
        if (llm == null || !StringUtils.hasText(llm.getTextProviderUid())) {
            log.warn("RobotLlm or textProviderUid is null, using default chat model");
            return defaultChatModel;
        }

        Optional<LlmProviderEntity> llmProviderOptional = llmProviderRestService.findByUid(llm.getTextProviderUid());
        if (llmProviderOptional.isEmpty()) {
            log.warn("LlmProvider with uid {} not found, using default chat model", llm.getTextProviderUid());
            return defaultChatModel;
        }

        LlmProviderEntity provider = llmProviderOptional.get();
        String apiKey = resolveApiKey(provider);
        if (apiKey == null) {
            log.warn("API key is not configured for provider {}, using default chat model", provider.getUid());
            return defaultChatModel;
        }

        try {
            log.info("Creating dynamic Dashscope chat model with provider: {} ({})", provider.getType(),
                    provider.getUid());
            DashScopeChatOptions options = createDashscopeOptions(llm);
            if (options == null) {
                log.warn("Failed to create Dashscope options, using default chat model");
                return defaultChatModel;
            }
            String baseUrl = resolveBaseUrl(provider);
            ObservationRegistry observationRegistry = observationRegistryProvider.getIfAvailable();
            if (observationRegistry != null) {
                return new DashScopeChatModel(baseUrl, apiKey, options, observationRegistry);
            }
            return new DashScopeChatModel(baseUrl, apiKey, options);
        } catch (Exception e) {
            log.error("Failed to create dynamic Dashscope chat model for provider {}, using default chat model",
                    provider.getUid(), e);
            return defaultChatModel;
        }
    }

    @Override
    protected String processPromptSync(String message, RobotProtobuf robot) {
        // provider tool service 优先（意图识别 / 外部工具），命中则直接返回
        String toolServiceResponse = tryProcessPromptSyncWithProviderToolService(message, robot);
        if (StringUtils.hasText(toolServiceResponse)) {
            return toolServiceResponse;
        }
        DashScopeChatOptions customOptions = robot != null && robot.getLlm() != null
                ? createDashscopeOptions(robot.getLlm())
                : null;
        return processPromptSync(buildUserOnlyPrompt(message, customOptions), robot);
    }

    @Override
    protected String processPromptSync(Prompt prompt, RobotProtobuf robot) {
        long startTime = System.currentTimeMillis();
        boolean success = false;
        ChatTokenUsage tokenUsage = new ChatTokenUsage(0, 0, 0);

        RobotLlm llm = robot != null ? robot.getLlm() : null;
        if (llm == null) {
            log.info("Dashscope API not available: robot.llm is null");
            return "Dashscope service is not available";
        }

        try {
            ChatModel chatModel = createDashscopeChatModel(llm);
            if (chatModel == null) {
                log.error("Failed to create Dashscope chat model and no default chat model available");
                return I18Consts.I18N_SERVICE_TEMPORARILY_UNAVAILABLE;
            }
            Prompt requestPrompt = prompt;
            DashScopeChatOptions customOptions = createDashscopeOptions(llm);
            if (customOptions != null) {
                requestPrompt = processPromptWithOptions(prompt, customOptions);
            }
            var chatClient = createChatClient(chatModel, requestPrompt, robot);
            var response = invokePromptSync(chatClient, requestPrompt);
            tokenUsage = tokenUsageHelper.extractTokenUsage(response);
            success = true;
            return promptHelper.extractTextFromResponse(response);
        } catch (Exception e) {
            log.error("Dashscope API sync error", e);
            return I18Consts.I18N_SERVICE_TEMPORARILY_UNAVAILABLE;
        } finally {
            long responseTime = System.currentTimeMillis() - startTime;
            String modelType = StringUtils.hasText(llm.getTextModel()) ? llm.getTextModel()
                    : LlmDefaults.DEFAULT_CHAT_MODEL;
            tokenUsageHelper.recordAiTokenUsage(robot, LlmProviderConstants.DASHSCOPE, modelType,
                    tokenUsage.getPromptTokens(), tokenUsage.getCompletionTokens(), success, responseTime);
        }
    }

    @Override
    protected void processPromptSse(Prompt prompt, RobotProtobuf robot, MessageProtobuf messageProtobufQuery,
            MessageProtobuf messageProtobufReply, List<RobotContent.SourceReference> sourceReferences,
            SseEmitter emitter) {
        // provider tool service 优先
        if (tryProcessPromptSseWithProviderToolService(prompt, robot, messageProtobufQuery, messageProtobufReply,
                sourceReferences, emitter)) {
            return;
        }

        RobotLlm llm = robot != null ? robot.getLlm() : null;
        String modelType = (llm != null && StringUtils.hasText(llm.getTextModel())) ? llm.getTextModel()
                : LlmDefaults.DEFAULT_CHAT_MODEL;

        if (llm == null) {
            log.info("Dashscope API not available: robot.llm is null");
            sseMessageHelper.sendStreamEndMessage(messageProtobufQuery, messageProtobufReply, emitter, 0, 0, 0, prompt,
                    LlmProviderConstants.DASHSCOPE, modelType);
            return;
        }

        ChatModel chatModel = createDashscopeChatModel(llm);

        if (chatModel == null) {
            log.error("Failed to create Dashscope chat model and no default chat model available");
            sseMessageHelper.sendStreamEndMessage(messageProtobufQuery, messageProtobufReply, emitter, 0, 0, 0, prompt,
                    LlmProviderConstants.DASHSCOPE, modelType);
            return;
        }

        long startTime = System.currentTimeMillis();
        final boolean[] success = { false };
        final ChatTokenUsage[] tokenUsage = { new ChatTokenUsage(0, 0, 0) };

        try {
            // 发送初始消息，告知用户请求已收到，正在处理
            sseMessageHelper.sendStreamStartMessage(messageProtobufQuery, messageProtobufReply, emitter,
                    I18Consts.I18N_THINKING);

            var chatClient = createChatClient(chatModel, prompt, robot);
            // SSE 异步跨线程，conversationId 显式传递（不走 ThreadLocal）
            String conversationId = extractConversationId(messageProtobufQuery);
            invokePromptStream(chatClient, prompt, conversationId).subscribe(
                    response -> {
                        try {
                            if (response != null && !sseMessageHelper.isEmitterCompleted(emitter)) {
                                List<Generation> generations = response.getResults();
                                for (Generation generation : generations) {
                                    AssistantMessage assistantMessage = generation.getOutput();
                                    String textContent = assistantMessage.getText();
                                    String reasonContent = extractReasoningContent(generation, assistantMessage);
                                    // 仅在有文本或思考内容时下发，避免空片噪音（保持原生路径行为）
                                    if (StringUtils.hasText(textContent) || StringUtils.hasText(reasonContent)) {
                                        sseMessageHelper.sendStreamMessage(messageProtobufQuery, messageProtobufReply,
                                                emitter, textContent, reasonContent, sourceReferences);
                                    }
                                }
                                // 提取token使用情况
                                tokenUsage[0] = tokenUsageHelper.extractTokenUsage(response);
                                success[0] = true;
                            }
                        } catch (Exception e) {
                            log.error("Dashscope API SSE error 1: ", e);
                            sseMessageHelper.handleSseError(e, messageProtobufQuery, messageProtobufReply, emitter);
                            success[0] = false;
                        }
                    },
                    error -> {
                        log.error("Dashscope API SSE error 2: ", error);
                        sseMessageHelper.handleSseError(error, messageProtobufQuery, messageProtobufReply, emitter);
                        success[0] = false;
                    },
                    () -> {
                        // 发送流结束消息（含 token/provider/model），并记录用量
                        sseMessageHelper.sendStreamEndMessage(messageProtobufQuery, messageProtobufReply, emitter,
                                tokenUsage[0].getPromptTokens(), tokenUsage[0].getCompletionTokens(),
                                tokenUsage[0].getTotalTokens(), prompt, LlmProviderConstants.DASHSCOPE, modelType);
                        long responseTime = System.currentTimeMillis() - startTime;
                        tokenUsageHelper.recordAiTokenUsage(robot, LlmProviderConstants.DASHSCOPE, modelType,
                                tokenUsage[0].getPromptTokens(), tokenUsage[0].getCompletionTokens(), success[0],
                                responseTime);
                    });
        } catch (Exception e) {
            log.error("Error starting Dashscope stream", e);
            sseMessageHelper.handleSseError(e, messageProtobufQuery, messageProtobufReply, emitter);
            success[0] = false;
            long responseTime = System.currentTimeMillis() - startTime;
            tokenUsageHelper.recordAiTokenUsage(robot, LlmProviderConstants.DASHSCOPE, modelType,
                    tokenUsage[0].getPromptTokens(), tokenUsage[0].getCompletionTokens(), success[0], responseTime);
        }
    }

}
