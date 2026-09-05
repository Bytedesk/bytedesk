/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-09-23 13:34:37
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-25 07:53:25
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.provider.zhipuai;

import java.util.ArrayList;
import java.util.List;

import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.bytedesk.ai.llm_provider.LlmProviderEntity;
import com.bytedesk.ai.llm_provider.LlmProviderRestService;
import com.bytedesk.ai.provider.zhipuai.chat.ZhipuaiChatModel;
import com.bytedesk.ai.provider.zhipuai.chat.ZhipuaiChatOptions;
import com.bytedesk.ai.provider.zhipuai.chat.ZhipuaiContentParser;
import com.bytedesk.ai.robot.RobotLlm;
import com.bytedesk.ai.robot.RobotProtobuf;
import com.bytedesk.ai.service.BaseSpringAIService;
import com.bytedesk.ai.service.ChatTokenUsage;
import com.bytedesk.ai.service.TokenUsageHelper;
import com.bytedesk.core.llm.LlmProviderConstants;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.message.MessageProtobuf;

import ai.z.openapi.ZhipuAiClient;
import ai.z.openapi.service.model.ChatCompletionCreateParams;
import ai.z.openapi.service.model.ChatCompletionResponse;
import ai.z.openapi.service.model.ChatMessage;
import ai.z.openapi.service.model.ChatMessageRole;
import ai.z.openapi.service.model.ChatThinking;
import ai.z.openapi.service.model.MessageContent;

import io.micrometer.observation.ObservationRegistry;
import lombok.extern.slf4j.Slf4j;
import com.bytedesk.core.message.content.RobotContent;
import com.bytedesk.core.message.enums.MessageTypeEnum;

@Slf4j
@Service
public class ZhipuaiService extends BaseSpringAIService {

    public ZhipuaiService(
            @Qualifier("zhipuAiClient") ObjectProvider<ZhipuAiClient> defaultClientProvider,
            @Qualifier("bytedeskZhipuaiChatModel") ObjectProvider<ZhipuaiChatModel> defaultChatModelProvider,
            ObjectProvider<ObservationRegistry> observationRegistryProvider,
            LlmProviderRestService llmProviderRestService,
            TokenUsageHelper tokenUsageHelper) {
        this.llmProviderRestService = llmProviderRestService;
        this.tokenUsageHelper = tokenUsageHelper;
        this.defaultClient = defaultClientProvider.getIfAvailable();
        this.defaultChatModel = defaultChatModelProvider.getIfAvailable();
        this.observationRegistryProvider = observationRegistryProvider;
    }

    private final LlmProviderRestService llmProviderRestService;

    private final ZhipuAiClient defaultClient;

    private final TokenUsageHelper tokenUsageHelper;

    /** 全局回退 ChatModel Bean（ZhipuaiConfig#bytedeskZhipuaiChatModel），动态创建失败时回退。 */
    private final ZhipuaiChatModel defaultChatModel;

    /** 动态建 model 时传入的 ObservationRegistry（可选，产生 gen_ai_* 指标）。 */
    private final ObjectProvider<ObservationRegistry> observationRegistryProvider;

    private static final String DEFAULT_MULTI_MODEL = "glm-4.1v-thinking-flash";
    // zai-sdk 思维模式常量（多模态同步直调路径使用）
    private static final String ZAI_THINKING_ENABLED = "enabled";
    private static final String ZAI_THINKING_DISABLED = "disabled";

    private String getModel(RobotProtobuf robot) {
        try {
            if (robot != null && robot.getLlm() != null && robot.getLlm().getTextModel() != null
                    && !robot.getLlm().getTextModel().isEmpty()) {
                return robot.getLlm().getTextModel();
            }
        } catch (Exception ignore) {
        }
        return DEFAULT_MULTI_MODEL;
    }

    /**
     * 根据机器人配置动态创建 ZhipuAiClient，优先使用 provider apiKey，失败则回退默认 Bean。
     */
    private ZhipuAiClient createDynamicClient(RobotLlm llm) {
        try {
            if (llm == null || llm.getTextProviderUid() == null) {
                log.warn("RobotLlm or textProviderUid is null, using default ZhipuAiClient");
                return defaultClient;
            }
            var opt = llmProviderRestService.findByUid(llm.getTextProviderUid());
            if (opt.isEmpty()) {
                log.warn("LlmProvider with uid {} not found, using default ZhipuAiClient", llm.getTextProviderUid());
                return defaultClient;
            }
            LlmProviderEntity provider = opt.get();
            String apiKey = provider.getApiKey();
            if (apiKey == null || apiKey.trim().isEmpty()) {
                log.warn("API key is not configured for provider {}, using default ZhipuAiClient", provider.getUid());
                return defaultClient;
            }
            log.info("Creating dynamic ZhipuAiClient with provider: {} ({})", provider.getType(), provider.getUid());
            return ZhipuAiClient.builder().apiKey(apiKey).build();
        } catch (Exception e) {
            log.error("Failed to create dynamic ZhipuAiClient, using default", e);
            return defaultClient;
        } catch (LinkageError e) {
            log.error("Failed to create dynamic ZhipuAiClient due to SDK/runtime incompatibility, using default", e);
            return defaultClient;
        }
    }

    /**
     * 根据机器人配置创建 Zhipuai chat options（含工具回调与思考模式）。
     * 对齐 DashscopeService.createDashscopeOptions 模式；enableThinking 恒非 null
     * （按 llm.thinking 显式 enabled/disabled，与旧直调路径行为一致）。
     */
    private ZhipuaiChatOptions createZhipuaiOptions(RobotLlm llm) {
        if (llm == null || !StringUtils.hasText(llm.getTextModel())) {
            return null;
        }
        try {
            return applyRobotToolCallbacks(ZhipuaiChatOptions.builder()
                    .model(llm.getTextModel())
                    .temperature(llm.getTemperature())
                    .maxTokens(llm.getMaxTokens())
                    .topP(llm.getTopP())
                    .enableThinking(Boolean.TRUE.equals(llm.getThinking()))
                    .build(), llm);
        } catch (Exception e) {
            log.error("Error creating Zhipuai options for model {}", llm.getTextModel(), e);
            return null;
        }
    }

    /**
     * 根据机器人配置动态创建 ZhipuaiChatModel（对齐 DashscopeService.createDashscopeChatModel 模式）。
     * provider 缺失 / apiKey 缺失 / options 构建失败时回退 bytedeskZhipuaiChatModel Bean（全局配置）。
     */
    private ChatModel createZhipuaiChatModel(RobotLlm llm) {
        if (llm == null || !StringUtils.hasText(llm.getTextProviderUid())) {
            log.warn("RobotLlm or textProviderUid is null, using default chat model");
            return defaultChatModel;
        }
        var opt = llmProviderRestService.findByUid(llm.getTextProviderUid());
        if (opt.isEmpty()) {
            log.warn("LlmProvider with uid {} not found, using default chat model", llm.getTextProviderUid());
            return defaultChatModel;
        }
        LlmProviderEntity provider = opt.get();
        String apiKey = provider.getApiKey();
        if (apiKey == null || apiKey.trim().isEmpty()) {
            log.warn("API key is not configured for provider {}, using default chat model", provider.getUid());
            return defaultChatModel;
        }
        try {
            ZhipuaiChatOptions options = createZhipuaiOptions(llm);
            if (options == null) {
                log.warn("Failed to create Zhipuai options, using default chat model");
                return defaultChatModel;
            }
            log.info("Creating dynamic ZhipuaiChatModel with provider: {} ({})", provider.getType(), provider.getUid());
            ZhipuAiClient client = ZhipuAiClient.builder().apiKey(apiKey).build();
            ObservationRegistry observationRegistry = observationRegistryProvider.getIfAvailable();
            if (observationRegistry != null) {
                return new ZhipuaiChatModel(client, options, observationRegistry);
            }
            return new ZhipuaiChatModel(client, options);
        } catch (Exception e) {
            log.error("Failed to create dynamic ZhipuaiChatModel for provider {}, using default", provider.getUid(), e);
            return defaultChatModel;
        }
    }

    /**
     * 多模态 enrich：媒体类型消息（IMAGE/VIDEO/FILE/AUDIO）时，将 Prompt 中最后一条用户消息文本
     * 替换为原始内容 JSON，由 ZhipuaiChatModel.toMessages → ZhipuaiContentParser 解析多模态内容
     * （等价旧 SSE 直调路径的「覆盖最新用户消息」逻辑）。非媒体消息原样返回。
     */
    private Prompt enrichPromptMedia(Prompt prompt, MessageProtobuf messageProtobufQuery) {
        try {
            if (prompt == null || messageProtobufQuery == null
                    || !StringUtils.hasText(messageProtobufQuery.getContent())) {
                return prompt;
            }
            MessageTypeEnum type = messageProtobufQuery.getType();
            if (type != MessageTypeEnum.IMAGE && type != MessageTypeEnum.VIDEO
                    && type != MessageTypeEnum.FILE && type != MessageTypeEnum.AUDIO) {
                return prompt;
            }
            List<org.springframework.ai.chat.messages.Message> instructions = new ArrayList<>(
                    prompt.getInstructions());
            for (int i = instructions.size() - 1; i >= 0; i--) {
                if (instructions.get(i) instanceof UserMessage) {
                    instructions.set(i, new UserMessage(messageProtobufQuery.getContent()));
                    return new Prompt(instructions, prompt.getOptions());
                }
            }
        } catch (Exception e) {
            log.debug("enrichPromptMedia failed, fallback to original prompt: {}", e.getMessage());
        }
        return prompt;
    }

    // 直接根据原始 MessageProtobuf（而不是通过 BD_MEDIA 标记）构建用户多模态内容
    // 2026-09-04 Phase 5：委托 ZhipuaiContentParser（仅多模态同步直调路径使用）
    private List<MessageContent> buildUserContentsFromMessage(MessageProtobuf messageProtobufQuery) {
        return ZhipuaiContentParser.contentsFromMessage(messageProtobufQuery);
    }

        // 保留：如需自定义解析辅助，可在此处添加
    // 2026-09-04 Phase 5：文本提取委托 ZhipuaiContentParser（仅多模态同步直调路径使用）
    private String extractFinalTextFromResponse(ChatCompletionResponse response) {
        try {
            if (response == null || response.getData() == null || response.getData().getChoices() == null
                    || response.getData().getChoices().isEmpty()) {
                return null;
            }
            Object msgObj = response.getData().getChoices().get(0).getMessage();
            if (msgObj instanceof ChatMessage cm) {
                return stripThinkTags(ZhipuaiContentParser.extractTextFromContent(cm.getContent()));
            }
            return String.valueOf(msgObj);
        } catch (Exception e) {
            log.warn("extractFinalTextFromResponse failed: {}", e.getMessage());
            return null;
        }
    }

    // 统一移除 <think>...</think>（委托 ZhipuaiContentParser，多模态同步路径与估算兑底使用）
    private String stripThinkTags(String text) {
        if (text == null)
            return null;
        if (text.contains("<think>")) {
            return text.replaceAll("(?s)<think>.*?</think>", "");
        }
        return text;
    }

    private long estimateTokens(String text) {
        if (text == null || text.isEmpty())
            return 1;
        int zh = 0, en = 0;
        for (char c : text.toCharArray()) {
            if (Character.UnicodeScript.of(c) == Character.UnicodeScript.HAN)
                zh++;
            else
                en++;
        }
        long est = (long) (zh / 1.5 + en / 4.0);
        return Math.max(1, est);
    }

    @Override
    protected String processPromptSync(String message, RobotProtobuf robot) {
        // provider tool service 优先（意图识别 / 外部工具），命中则直接返回
        String toolServiceResponse = tryProcessPromptSyncWithProviderToolService(message, robot);
        if (StringUtils.hasText(toolServiceResponse)) {
            return toolServiceResponse;
        }
        // 2026-09-04 Phase 5：回接 ChatClient 链路（多轮记忆/工具/Advisor）。
        // 注意：旧实现此处硬编码 responseFormat=json_object，为意图识别残留，普通对话会被强制 JSON 输出，
        // 与 DashScope 路径对齐后不再设置（意图识别 JSON 依赖提示词约束，与 DashScope 行为一致）。
        ZhipuaiChatOptions customOptions = robot != null && robot.getLlm() != null
                ? createZhipuaiOptions(robot.getLlm())
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
            log.info("Zhipuai API not available: robot.llm is null");
            return "Zhipuai service is not available";
        }
        String modelType = getModel(robot);

        try {
            ChatModel chatModel = createZhipuaiChatModel(llm);
            if (chatModel == null) {
                log.error("Failed to create Zhipuai chat model and no default chat model available");
                return I18Consts.I18N_SERVICE_TEMPORARILY_UNAVAILABLE;
            }
            Prompt requestPrompt = prompt;
            ZhipuaiChatOptions customOptions = createZhipuaiOptions(llm);
            if (customOptions != null) {
                requestPrompt = processPromptWithOptions(prompt, customOptions);
            }
            var chatClient = createChatClient(chatModel, requestPrompt, robot);
            var response = invokePromptSync(chatClient, requestPrompt);
            tokenUsage = tokenUsageHelper.extractTokenUsage(response);
            success = true;
            return stripThinkTags(promptHelper.extractTextFromResponse(response));
        } catch (Exception e) {
            log.error("Zhipuai API sync error", e);
            return I18Consts.I18N_SERVICE_TEMPORARILY_UNAVAILABLE;
        } finally {
            long responseTime = System.currentTimeMillis() - startTime;
            // API 未返回 usage 时退回估算（保持旧路径行为）
            long promptTokens = tokenUsage.getPromptTokens() > 0 ? tokenUsage.getPromptTokens()
                    : estimateTokens(prompt != null ? prompt.getContents() : null);
            long completionTokens = tokenUsage.getCompletionTokens();
            tokenUsageHelper.recordAiTokenUsage(robot, LlmProviderConstants.ZHIPUAI, modelType,
                    promptTokens, completionTokens, success, responseTime);
        }
    }

    /**
     * 多模态同步请求处理，支持图片等媒体类型
     */
    public String processMultiModalSyncRequest(MessageProtobuf messageProtobuf, RobotProtobuf robot,
            boolean searchKnowledgeBase) {
        try {
            String model = getModel(robot);

            // 构建多模态内容
            List<MessageContent> userContents = buildUserContentsFromMessage(messageProtobuf);
            if (userContents.isEmpty()) {
                // 如果没有有效的多模态内容，回退到纯文本处理
                return processPromptSync(messageProtobuf.getContent(), robot);
            }

            // 构建消息列表
            List<ChatMessage> msgs = new ArrayList<>();

            // 如果需要搜索知识库，添加系统消息（这里暂时跳过，因为OCR通常不需要知识库）
            if (searchKnowledgeBase && robot != null) {
                // 可以在这里添加知识库相关的系统提示
            }

            // 添加用户消息
            msgs.add(ChatMessage.builder()
                    .role(ChatMessageRole.USER.value())
                    .content(userContents)
                    .build());

            // 获取客户端
            ZhipuAiClient client = createDynamicClient(robot != null ? robot.getLlm() : null);
            if (client == null) {
                log.error("No available ZhipuAiClient for multi-modal sync");
                return I18Consts.I18N_SERVICE_TEMPORARILY_UNAVAILABLE;
            }

            // 启用思维模式（如果配置了）
            boolean enableThinking = robot != null && robot.getLlm() != null
                    && Boolean.TRUE.equals(robot.getLlm().getThinking());

            // 构建请求
            ChatCompletionCreateParams req = ChatCompletionCreateParams.builder()
                    .model(model)
                    .messages(msgs)
                    .thinking(ChatThinking.builder().type(enableThinking ? ZAI_THINKING_ENABLED : ZAI_THINKING_DISABLED)
                            .build())
                    .build();

            long start = System.currentTimeMillis();
            ChatCompletionResponse resp = client.chat().createChatCompletion(req);
            boolean success = resp != null && resp.isSuccess();
            String text = success ? extractFinalTextFromResponse(resp) : null;
            if (text == null)
                text = "";
            text = stripThinkTags(text);

            // 记录用量事件（粗略估算）
            long promptTokens = estimateTokens("multi-modal-input");
            long completionTokens = estimateTokens(text);
            tokenUsageHelper.recordAiTokenUsage(robot, LlmProviderConstants.ZHIPUAI, model, promptTokens,
                    completionTokens, success,
                    System.currentTimeMillis() - start);

            return text;
        } catch (Exception e) {
            log.error("processMultiModalSyncRequest failed", e);
            return I18Consts.I18N_SERVICE_TEMPORARILY_UNAVAILABLE;
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
        if (llm == null) {
            sseMessageHelper.handleSseError(new IllegalArgumentException("robot or llm is null"), messageProtobufQuery,
                    messageProtobufReply, emitter);
            return;
        }
        String model = getModel(robot);

        ChatModel chatModel = createZhipuaiChatModel(llm);
        if (chatModel == null) {
            sseMessageHelper.handleSseError(new IllegalStateException("No available Zhipuai chat model"),
                    messageProtobufQuery, messageProtobufReply, emitter);
            return;
        }

        // 起始提示
        sseMessageHelper.sendStreamStartMessage(messageProtobufQuery, messageProtobufReply, emitter,
                I18Consts.I18N_THINKING);
        long start = System.currentTimeMillis();
        final StringBuilder finalAnswer = new StringBuilder();
        final boolean[] success = { false };
        final ChatTokenUsage[] tokenUsage = { new ChatTokenUsage(0, 0, 0) };

        try {
            // 多模态 enrich：媒体消息时替换最后一条用户消息为原始内容 JSON，
            // 由 ZhipuaiChatModel.toMessages → ZhipuaiContentParser 解析（等价旧「覆盖用户消息」逻辑）
            Prompt effectivePrompt = enrichPromptMedia(prompt, messageProtobufQuery);

            var chatClient = createChatClient(chatModel, effectivePrompt, robot);
            // SSE 异步跨线程，conversationId 显式传递（不走 ThreadLocal）
            String conversationId = extractConversationId(messageProtobufQuery);
            invokePromptStream(chatClient, effectivePrompt, conversationId).subscribe(
                    response -> {
                        try {
                            if (response != null && !sseMessageHelper.isEmitterCompleted(emitter)) {
                                List<Generation> generations = response.getResults();
                                for (Generation generation : generations) {
                                    AssistantMessage assistantMessage = generation.getOutput();
                                    String textContent = assistantMessage.getText();
                                    // "null" 字面量守卫已下沉到 ChatModel.toGeneration（filterLiteralNull）
                                    String reasonContent = extractReasoningContent(generation, assistantMessage);
                                    if (StringUtils.hasText(textContent) || StringUtils.hasText(reasonContent)) {
                                        finalAnswer.append(textContent != null ? textContent : "");
                                        sseMessageHelper.sendStreamMessage(messageProtobufQuery, messageProtobufReply,
                                                emitter, textContent != null ? textContent : "", reasonContent,
                                                sourceReferences);
                                    }
                                }
                                tokenUsage[0] = tokenUsageHelper.extractTokenUsage(response);
                                success[0] = true;
                            }
                        } catch (Exception ex) {
                            log.error("Zhipuai API SSE error 1: ", ex);
                            sseMessageHelper.handleSseError(ex, messageProtobufQuery, messageProtobufReply, emitter);
                            success[0] = false;
                        }
                    },
                    err -> {
                        log.error("Zhipuai API SSE error 2: ", err);
                        sseMessageHelper.handleSseError(err, messageProtobufQuery, messageProtobufReply, emitter);
                        success[0] = false;
                    },
                    () -> {
                        // 流式 chunk 通常不带 usage：为 0 时按累计文本估算兜底（保持旧路径行为）
                        long promptTokens = tokenUsage[0].getPromptTokens() > 0 ? tokenUsage[0].getPromptTokens()
                                : estimateTokens(prompt != null ? prompt.getContents() : null);
                        long completionTokens = tokenUsage[0].getCompletionTokens() > 0
                                ? tokenUsage[0].getCompletionTokens()
                                : estimateTokens(stripThinkTags(finalAnswer.toString()));
                        sseMessageHelper.sendStreamEndMessage(messageProtobufQuery, messageProtobufReply, emitter,
                                promptTokens, completionTokens, promptTokens + completionTokens, prompt,
                                LlmProviderConstants.ZHIPUAI, model);
                        tokenUsageHelper.recordAiTokenUsage(robot, LlmProviderConstants.ZHIPUAI, model,
                                promptTokens, completionTokens, success[0], System.currentTimeMillis() - start);
                    });
        } catch (Exception e) {
            log.error("processPromptSse failed", e);
            sseMessageHelper.handleSseError(e, messageProtobufQuery, messageProtobufReply, emitter);
        }
    }

}
