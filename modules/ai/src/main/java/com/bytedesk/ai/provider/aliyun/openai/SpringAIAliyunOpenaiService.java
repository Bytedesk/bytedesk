/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-10-20 21:20:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2026-10-21 09:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM –
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 *  contact: 270580156@qq.com
 *  联系：270580156@qq.com
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved.
 */
package com.bytedesk.ai.provider.aliyun.openai;

import java.util.List;

import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.bytedesk.ai.llm_provider.LlmProviderEntity;
import com.bytedesk.ai.provider.openai.OpenAiCompatibleModelFactory;
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

/**
 * 阿里云百炼 OpenAI 兼容模式文本对话服务。
 *
 * <p>复用 Spring AI 的 {@link OpenAiChatModel} 接入
 * https://dashscope.aliyuncs.com/compatible-mode/v1，使 qwen3.8-max / qwen3.8-flash
 * 等仅支持 OpenAI 兼容协议的最新模型可用。</p>
 *
 * <p>与 SiliconFlow 等兼容服务的差异（规划 4.2/4.3/4.4）：</p>
 * <ul>
 * <li>所有入口（同步/SSE/结构化）先经 {@link AliyunOpenaiProviderConnection}
 * 校验开关、类型、启用状态与凭据，失败即明确报错，不静默回退默认模型；</li>
 * <li>结构化调用不再走 ChatClientInfoService 的 primary 回退，而是用机器人
 * 选定的 provider 连接与模型（规划 B5）。</li>
 * </ul>
 */
@Slf4j
@Service
public class SpringAIAliyunOpenaiService extends BaseSpringAIService {

    private final TokenUsageHelper tokenUsageHelper;

    private final AliyunOpenaiProviderConnection providerConnection;

    public SpringAIAliyunOpenaiService(
            TokenUsageHelper tokenUsageHelper,
            AliyunOpenaiProviderConnection providerConnection) {
        this.tokenUsageHelper = tokenUsageHelper;
        this.providerConnection = providerConnection;
    }
    /**
     * 根据机器人配置创建动态的OpenAiChatOptions
     */
    private OpenAiChatOptions createDynamicOptions(RobotLlm llm) {
        if (llm == null || !StringUtils.hasText(llm.getTextModel())) {
            return null;
        }
        return applyRobotToolCallbacks(OpenAiChatOptions.builder()
                .model(llm.getTextModel())
                .temperature(llm.getTemperature())
                .maxTokens(llm.getMaxTokens())
                .topP(llm.getTopP())
                .build(), llm);
    }

    /**
     * 根据机器人配置创建动态的OpenAiChatModel。
     *
     * <p>解析失败（关闭/缺配置/缺 Key）抛 {@link AliyunOpenaiProviderException}，
     * 不回退默认模型（规划 B1/B4）。</p>
     */
    private OpenAiChatModel createAliyunOpenaiChatModel(RobotLlm llm) {
        LlmProviderEntity provider = providerConnection.requireProvider(llm);

        OpenAiChatOptions options = createDynamicOptions(llm);
        if (options == null) {
            throw new AliyunOpenaiProviderException("RobotLlm textModel is empty");
        }

        String apiKey = providerConnection.resolveApiKey(provider);
        if (apiKey == null) {
            throw new AliyunOpenaiProviderException(
                    "aliyun_openai provider apiKey not configured: " + provider.getUid());
        }
        String baseUrl = providerConnection.resolveBaseUrl(provider);

        OpenAiChatOptions resolvedOptions = OpenAiCompatibleModelFactory.withConnection(options, baseUrl, apiKey);
        log.info("Creating dynamic AliyunOpenai chat model with provider: {} ({})", provider.getType(),
                provider.getUid());
        return OpenAiCompatibleModelFactory.chatModel(resolvedOptions);
    }

    @Override
    protected String processPromptSync(String message, RobotProtobuf robot) {
        OpenAiChatOptions customOptions = robot != null && robot.getLlm() != null ? createDynamicOptions(robot.getLlm())
                : null;
        return processPromptSync(buildUserOnlyPrompt(message, customOptions), robot);
    }

    @Override
    protected String processPromptSync(Prompt prompt, RobotProtobuf robot) {
        long startTime = System.currentTimeMillis();
        boolean success = false;
        ChatTokenUsage tokenUsage = new ChatTokenUsage(0, 0, 0);
        RobotLlm llm = robot != null ? robot.getLlm() : null;

        log.info("AliyunOpenai API sync");
        OpenAiChatModel chatModel;
        try {
            chatModel = createAliyunOpenaiChatModel(llm);
        } catch (AliyunOpenaiProviderException e) {
            log.warn("AliyunOpenai provider resolution failed: {}", e.getMessage());
            return I18Consts.I18N_SERVICE_TEMPORARILY_UNAVAILABLE;
        }

        try {
            Prompt requestPrompt = prompt;
            OpenAiChatOptions customOptions = createDynamicOptions(llm);
            if (customOptions != null) {
                requestPrompt = processPromptWithOptions(prompt, customOptions);
            }
            var chatClient = createChatClient(chatModel, requestPrompt, robot);
            ChatResponse response = invokePromptSync(chatClient, requestPrompt);
            tokenUsage = tokenUsageHelper.extractTokenUsage(response);
            success = true;
            return promptHelper.extractTextFromResponse(response);
        } catch (Exception e) {
            log.error("aliyunOpenai API call error: ", e);
            return I18Consts.I18N_SERVICE_TEMPORARILY_UNAVAILABLE;
        } finally {
            long responseTime = System.currentTimeMillis() - startTime;
            String modelType = (llm != null && StringUtils.hasText(llm.getTextModel())) ? llm.getTextModel()
                    : LlmProviderConstants.ALIYUN_OPENAI;
            tokenUsageHelper.recordAiTokenUsage(robot, LlmProviderConstants.ALIYUN_OPENAI, modelType,
                    tokenUsage.getPromptTokens(), tokenUsage.getCompletionTokens(), success, responseTime);
        }
    }

    /**
     * 结构化输出：使用机器人选定的 provider 连接与模型，不走 primary 回退（规划 B5）。
     */
    @Override
    public <T> T processSyncRequest(String query, RobotProtobuf robot, Class<T> outputClass) {
        if (robot == null || robot.getLlm() == null || !robot.getLlm().getEnabled()) {
            log.warn("LLM未启用，无法处理AliyunOpenai结构化请求");
            return null;
        }

        RobotLlm llm = robot.getLlm();
        String promptText = llm.getPrompt();
        StringBuilder aiPrompt = new StringBuilder();
        if (StringUtils.hasText(promptText)) {
            aiPrompt.append(promptText);
        } else {
            aiPrompt.append(I18Consts.I18N_CONTEXT_BASED_ANSWER);
            aiPrompt.append(query);
        }

        OpenAiChatModel chatModel = createAliyunOpenaiChatModel(llm);
        OpenAiChatOptions options = createDynamicOptions(llm);
        List<Message> messages = List.of(new SystemMessage(aiPrompt.toString()), new UserMessage(query));
        Prompt structuredPrompt = options != null ? new Prompt(messages, options) : new Prompt(messages);

        var chatClient = createChatClient(chatModel, structuredPrompt, robot);
        return chatClient.prompt(structuredPrompt).call().entity(outputClass);
    }

    @Override
    protected void processPromptSse(Prompt prompt, RobotProtobuf robot, MessageProtobuf messageProtobufQuery,
            MessageProtobuf messageProtobufReply, List<RobotContent.SourceReference> sourceReferences, SseEmitter emitter) {
        RobotLlm llm = robot.getLlm();
        log.info("AliyunOpenai API SSE");

        OpenAiChatModel chatModel;
        try {
            chatModel = createAliyunOpenaiChatModel(llm);
        } catch (AliyunOpenaiProviderException e) {
            log.warn("AliyunOpenai provider resolution failed: {}", e.getMessage());
            sseMessageHelper.handleSseError(e, messageProtobufQuery, messageProtobufReply, emitter);
            return;
        }

        sseMessageHelper.sendStreamStartMessage(messageProtobufQuery, messageProtobufReply, emitter,
                I18Consts.I18N_THINKING);

        Prompt requestPrompt = prompt;
        OpenAiChatOptions customOptions = createDynamicOptions(llm);
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
                        if (response != null) {
                            List<Generation> generations = response.getResults();
                            for (Generation generation : generations) {
                                AssistantMessage assistantMessage = generation.getOutput();
                                String textContent = assistantMessage.getText();
                                String reasonContent = extractReasoningContent(generation, assistantMessage);
                                sseMessageHelper.sendStreamMessage(messageProtobufQuery, messageProtobufReply, emitter,
                                        textContent, reasonContent, sourceReferences);
                            }
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
                    log.error("aliyunOpenai API SSE error: ", error);
                    sseMessageHelper.handleSseError(error, messageProtobufQuery, messageProtobufReply, emitter);
                    success[0] = false;
                },
                () -> {
                    log.info("AliyunOpenai API SSE complete");
                    sseMessageHelper.sendStreamEndMessage(messageProtobufQuery, messageProtobufReply, emitter,
                            tokenUsage[0].getPromptTokens(), tokenUsage[0].getCompletionTokens(),
                            tokenUsage[0].getTotalTokens(), prompt, LlmProviderConstants.ALIYUN_OPENAI,
                            (llm != null && StringUtils.hasText(llm.getTextModel())) ? llm.getTextModel()
                                    : "aliyun-openai-chat");
                    long responseTime = System.currentTimeMillis() - startTime;
                    String modelType = (llm != null && StringUtils.hasText(llm.getTextModel())) ? llm.getTextModel()
                            : LlmProviderConstants.ALIYUN_OPENAI;
                    tokenUsageHelper.recordAiTokenUsage(robot, LlmProviderConstants.ALIYUN_OPENAI, modelType,
                            tokenUsage[0].getPromptTokens(), tokenUsage[0].getCompletionTokens(), success[0],
                            responseTime);
                });
    }

}
