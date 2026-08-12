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
package com.bytedesk.ai.providers.dashscope;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationOutput;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.aigc.generation.GenerationUsage;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.protocol.Protocol;

import com.bytedesk.ai.llm_provider.LlmProviderEntity;
import com.bytedesk.ai.llm_provider.LlmProviderRestService;
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

import io.reactivex.Flowable;
import lombok.extern.slf4j.Slf4j;

/**
 * DashScope 文本对话服务（原生 SDK 实现）。
 *
 * <p>2026-08-12：将 Spring AI ChatModel/ChatClient 调用路径完全替换为 dashscope-sdk-java 原生
 * {@link Generation} 客户端。本服务不再注入 {@code bytedeskDashscopeChatModel} Bean，也不再使用
 * {@code DashScopeChatOptions}。该 Bean 仍由 {@code DashscopeChatConfig} 保留，供
 * {@code modules/ai} 内部其它引用（ChatModelPrimaryConfig / ChatModelInfoService /
 * DashscopeChatController / DashscopeChatService）使用——参见迁移规划 §0.3。
 */
@Slf4j
@Service
public class DashscopeService extends BaseSpringAIService {

    public DashscopeService(
            LlmProviderRestService llmProviderRestService,
            TokenUsageHelper tokenUsageHelper,
            @Value("${spring.ai.dashscope.api-key:}") String globalApiKey,
            @Value("${spring.ai.dashscope.base-url:}") String globalBaseUrl) {
        this.llmProviderRestService = llmProviderRestService;
        this.tokenUsageHelper = tokenUsageHelper;
        this.globalApiKey = globalApiKey;
        this.globalBaseUrl = globalBaseUrl;
    }

    private final LlmProviderRestService llmProviderRestService;

    private final TokenUsageHelper tokenUsageHelper;

    /** 全局回退 apiKey（来自 spring.ai.dashscope.api-key），与旧 bytedeskDashscopeChatModel Bean 一致。 */
    private final String globalApiKey;

    /** 全局回退 baseUrl（来自 spring.ai.dashscope.base-url）。 */
    private final String globalBaseUrl;

    /**
     * 解析 provider。仅当 textProviderUid 为空或 provider 不存在时抛异常；
     * apiKey 为空时不抛异常（由 {@link #resolveApiKey} 回退到全局配置）。
     */
    private LlmProviderEntity resolveProvider(RobotLlm llm) {
        if (llm == null || !StringUtils.hasText(llm.getTextProviderUid())) {
            throw new IllegalStateException("RobotLlm or textProviderUid is null");
        }
        Optional<LlmProviderEntity> opt = llmProviderRestService.findByUid(llm.getTextProviderUid());
        if (opt.isEmpty()) {
            throw new IllegalStateException("LlmProvider not found: " + llm.getTextProviderUid());
        }
        return opt.get();
    }

    /**
     * 解析 apiKey：优先 provider 自身配置，为空时回退到全局 spring.ai.dashscope.api-key。
     * 两者都为空才抛异常（与旧代码回退到 defaultChatModel Bean 后 apiKey 仍无效的行为一致）。
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
        throw new IllegalStateException(
                "API key not configured for provider " + provider.getUid() + " (and no global fallback)");
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
     * 动态创建 {@link Generation} 客户端。Generation 无状态，按方法内创建。
     * 当解析到自定义 baseUrl 时，使用 {@code new Generation(protocol, baseUrl)}；
     * 否则使用无参默认端点。
     */
    private Generation createGeneration(String baseUrl) {
        if (StringUtils.hasText(baseUrl)) {
            return new Generation(Protocol.HTTP.getValue(), baseUrl);
        }
        return new Generation();
    }

    /**
     * 构建 DashScope 原生 {@link GenerationParam}。
     *
     * @param llm       机器人 LLM 配置（模型、温度、maxTokens、topP、thinking）
     * @param provider  解析出的 provider（提供 apiKey）
     * @param messages  已转换的 DashScope {@link com.alibaba.dashscope.common.Message} 列表
     * @param stream    是否流式（流式开启 incrementalOutput 增量输出）
     */
    private GenerationParam buildGenerationParam(RobotLlm llm, LlmProviderEntity provider,
            List<com.alibaba.dashscope.common.Message> messages, boolean stream) {
        String model = StringUtils.hasText(llm.getTextModel()) ? llm.getTextModel() : LlmDefaults.DEFAULT_CHAT_MODEL;
        // 与旧 DashScopeChatModel.createParam 对齐：不显式设置 resultFormat、不设置 enableThinking。
        // enable_thinking 仅对 Qwen3 等思考模型有效，对 qwen-max 等非思考模型设置会导致 400 Bad Request。
        // 思考模式应由模型名称（如 qwen3-xxx）决定，不应盲目按 llm.thinking 标志开启。
        GenerationParam.GenerationParamBuilder<?, ?> b = GenerationParam.builder()
                .apiKey(resolveApiKey(provider))
                .model(model)
                .messages(messages)
                .incrementalOutput(stream);
        if (llm.getTemperature() != null) {
            b.temperature(llm.getTemperature().floatValue());
        }
        if (llm.getMaxTokens() != null) {
            b.maxTokens(llm.getMaxTokens());
        }
        if (llm.getTopP() != null) {
            b.topP(llm.getTopP().doubleValue());
        }
        GenerationParam param = b.build();
        return param;
    }

    /**
     * 将 Spring AI {@link Prompt} 的 instructions 转换为 DashScope 原生 {@link com.alibaba.dashscope.common.Message} 列表。
     * 通过 {@link MessageType} 统一分发，不依赖 Spring AI 具体子类。
     */
    private List<com.alibaba.dashscope.common.Message> buildDashscopeMessagesFromPrompt(Prompt prompt) {
        List<com.alibaba.dashscope.common.Message> messages = new ArrayList<>();
        if (prompt == null || prompt.getInstructions() == null) {
            return messages;
        }
        for (Message m : prompt.getInstructions()) {
            MessageType type = m.getMessageType();
            String text = m.getText();
            String role;
            if (type == MessageType.SYSTEM) {
                role = Role.SYSTEM.getValue();
            } else if (type == MessageType.USER) {
                role = Role.USER.getValue();
            } else if (type == MessageType.ASSISTANT) {
                role = Role.ASSISTANT.getValue();
            } else {
                // TOOL 等其它类型兜底按 user 处理
                role = Role.USER.getValue();
            }
            messages.add(com.alibaba.dashscope.common.Message.builder().role(role).content(text).build());
        }
        return messages;
    }

    /**
     * 从 {@link GenerationResult} 同步响应提取文本内容。
     */
    private String extractTextFromGenerationResult(GenerationResult result) {
        if (result == null || result.getOutput() == null) {
            return "";
        }
        GenerationOutput output = result.getOutput();
        // message 格式：从 choices[0].message.content 提取
        if (output.getChoices() != null && !output.getChoices().isEmpty()) {
            com.alibaba.dashscope.common.Message msg = output.getChoices().get(0).getMessage();
            if (msg != null && msg.getContent() != null) {
                return msg.getContent();
            }
        }
        // 兜底：text 字段
        if (output.getText() != null) {
            return output.getText();
        }
        return "";
    }

    /**
     * 从 {@link GenerationResult} 提取 reasoning_content（思考模型）。
     */
    private String extractReasoningFromGenerationResult(GenerationResult result) {
        if (result == null || result.getOutput() == null
                || result.getOutput().getChoices() == null
                || result.getOutput().getChoices().isEmpty()) {
            return null;
        }
        com.alibaba.dashscope.common.Message msg = result.getOutput().getChoices().get(0).getMessage();
        if (msg != null && msg.getReasoningContent() != null && !msg.getReasoningContent().isEmpty()) {
            return msg.getReasoningContent();
        }
        return null;
    }

    /**
     * 从 {@link GenerationUsage} 构造 {@link ChatTokenUsage}（null 安全）。
     */
    private ChatTokenUsage toChatTokenUsage(GenerationUsage usage) {
        if (usage == null) {
            return new ChatTokenUsage(0, 0, 0);
        }
        return new ChatTokenUsage(
                usage.getInputTokens() != null ? usage.getInputTokens() : 0,
                usage.getOutputTokens() != null ? usage.getOutputTokens() : 0,
                usage.getTotalTokens() != null ? usage.getTotalTokens() : 0);
    }

    @Override
    protected String processPromptSync(String message, RobotProtobuf robot) {
        // provider tool service 优先（意图识别 / 外部工具），命中则直接返回
        String toolServiceResponse = tryProcessPromptSyncWithProviderToolService(message, robot);
        if (StringUtils.hasText(toolServiceResponse)) {
            return toolServiceResponse;
        }
        // 将单条文本包装为 user-only Prompt，复用 Prompt(Prompt) 链路
        return processPromptSync(buildUserOnlyPrompt(message, null), robot);
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
            LlmProviderEntity provider = resolveProvider(llm);
            List<com.alibaba.dashscope.common.Message> messages = buildDashscopeMessagesFromPrompt(prompt);
            GenerationParam param = buildGenerationParam(llm, provider, messages, false);

            Generation gen = createGeneration(resolveBaseUrl(provider));
            GenerationResult result = gen.call(param);

            tokenUsage = toChatTokenUsage(result.getUsage());
            success = true;
            return extractTextFromGenerationResult(result);
        } catch (ApiException | NoApiKeyException | InputRequiredException e) {
            log.error("Dashscope API sync error: {}", e.getMessage());
            return I18Consts.I18N_SERVICE_TEMPORARILY_UNAVAILABLE;
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

        long startTime = System.currentTimeMillis();
        final boolean[] success = { false };
        final ChatTokenUsage[] tokenUsage = { new ChatTokenUsage(0, 0, 0) };

        try {
            LlmProviderEntity provider = resolveProvider(llm);
            List<com.alibaba.dashscope.common.Message> messages = buildDashscopeMessagesFromPrompt(prompt);
            GenerationParam param = buildGenerationParam(llm, provider, messages, true);

            // 发送起始提示，保持前端流式体验
            sseMessageHelper.sendStreamStartMessage(messageProtobufQuery, messageProtobufReply, emitter,
                    I18Consts.I18N_THINKING);

            Generation gen = createGeneration(resolveBaseUrl(provider));
            Flowable<GenerationResult> flowable = gen.streamCall(param);

            flowable.subscribe(
                    chunk -> {
                        try {
                            if (chunk == null || sseMessageHelper.isEmitterCompleted(emitter)) {
                                return;
                            }
                            String textContent = extractTextFromGenerationResult(chunk);
                            String reasonContent = extractReasoningFromGenerationResult(chunk);

                            // 增量输出时 textContent 为本片增量；非增量时为累计全文（SDK 已请求 incrementalOutput=true，故为增量）
                            if (StringUtils.hasText(textContent)) {
                                sseMessageHelper.sendStreamMessage(messageProtobufQuery, messageProtobufReply,
                                        emitter, textContent, reasonContent, sourceReferences);
                            } else if (StringUtils.hasText(reasonContent)) {
                                // thinking 模型：仅推理内容时也下发 reasonContent，不下发空文本噪音
                                sseMessageHelper.sendStreamMessage(messageProtobufQuery, messageProtobufReply,
                                        emitter, "", reasonContent, sourceReferences);
                            }

                            // token 用量通常在最后一个 chunk 出现
                            if (chunk.getUsage() != null) {
                                tokenUsage[0] = toChatTokenUsage(chunk.getUsage());
                            }
                            success[0] = true;
                        } catch (Exception e) {
                            log.error("Dashscope SSE chunk error", e);
                            sseMessageHelper.handleSseError(e, messageProtobufQuery, messageProtobufReply, emitter);
                            success[0] = false;
                        }
                    },
                    error -> {
                        log.error("Dashscope SSE stream error", error);
                        sseMessageHelper.handleSseError(error, messageProtobufQuery, messageProtobufReply, emitter);
                        success[0] = false;
                    },
                    () -> {
                        // 流结束，发送 token/provider/model 并记录用量
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
