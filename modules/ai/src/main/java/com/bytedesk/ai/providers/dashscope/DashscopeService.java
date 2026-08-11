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

import java.util.List;
import java.util.Optional;

import org.springframework.ai.chat.messages.AssistantMessage;
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
import com.bytedesk.ai.providers.dashscope.chat.DashScopeChatModel;
import com.bytedesk.ai.providers.dashscope.chat.DashScopeChatOptions;
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

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DashscopeService extends BaseSpringAIService {

    public DashscopeService(
            LlmProviderRestService llmProviderRestService,
            @Qualifier("bytedeskDashscopeChatModel") ObjectProvider<ChatModel> defaultChatModelProvider,
            TokenUsageHelper tokenUsageHelper) {
        this.llmProviderRestService = llmProviderRestService;
        this.defaultChatModel = defaultChatModelProvider.getIfAvailable();
        this.tokenUsageHelper = tokenUsageHelper;
    }

    private final LlmProviderRestService llmProviderRestService;

    private final ChatModel defaultChatModel;

    private final TokenUsageHelper tokenUsageHelper;

    /**
     * 根据机器人配置创建动态的BytedeskDashScopeChatOptions
     * 
     * @param llm 机器人LLM配置
     * @return 根据机器人配置创建的选项
     */
    private DashScopeChatOptions createDashscopeOptions(RobotLlm llm) {
        if (llm == null || !StringUtils.hasText(llm.getTextModel())) {
            return null;
        }
        try {
            return DashScopeChatOptions.builder()
                    .model(llm.getTextModel())
                    .temperature(llm.getTemperature())
                    .maxTokens(llm.getMaxTokens())
                    .topP(llm.getTopP())
                    .build();
        } catch (Exception e) {
            log.error("Error creating dynamic options for model {}", llm.getTextModel(), e);
            return null;
        }
    }

    /**
     * 根据机器人配置创建动态的BytedeskDashScopeChatModel
     * 
     * @param llm 机器人LLM配置
     * @return 配置了特定模型的ChatModel
     */
    private ChatModel createDashscopeChatModel(RobotLlm llm) {
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
            log.debug("Creating dynamic Dashscope chat model with provider: {} ({})", provider.getType(),
                    provider.getUid());
            DashScopeChatOptions options = createDashscopeOptions(llm);
            if (options == null) {
                log.warn("Failed to create Dashscope options, using default chat model");
                return defaultChatModel;
            }
            return new DashScopeChatModel(provider.getBaseUrl(), provider.getApiKey(), options);
        } catch (Exception e) {
            log.error("Failed to create dynamic Dashscope chat model for provider {}, using default chat model",
                    provider.getUid(), e);
            return defaultChatModel;
        }
    }

    @Override
    protected String processPromptSync(String message, RobotProtobuf robot) {
        String toolServiceResponse = tryProcessPromptSyncWithProviderToolService(message, robot);
        if (StringUtils.hasText(toolServiceResponse)) {
            return toolServiceResponse;
        }
        DashScopeChatOptions customOptions = robot != null && robot.getLlm() != null ? createDashscopeOptions(robot.getLlm()) : null;
        return processPromptSync(buildUserOnlyPrompt(message, customOptions), robot);
    }

    @Override
    protected String processPromptSync(Prompt prompt, RobotProtobuf robot) {
        long startTime = System.currentTimeMillis();
        boolean success = false;
        ChatTokenUsage tokenUsage = new ChatTokenUsage(0, 0, 0);

        // 从robot中获取llm配置
        RobotLlm llm = robot.getLlm();

        if (llm == null) {
            log.info("Dashscope API not available");
            return "Dashscope service is not available";
        }

        // 获取适当的模型实例
        ChatModel chatModel = createDashscopeChatModel(llm);
        if (chatModel == null) {
            return I18Consts.I18N_SERVICE_TEMPORARILY_UNAVAILABLE;
        }

        try {
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
            success = false;
            return I18Consts.I18N_SERVICE_TEMPORARILY_UNAVAILABLE;
        } finally {
            long responseTime = System.currentTimeMillis() - startTime;
            String modelType = (llm != null && StringUtils.hasText(llm.getTextModel())) ? llm.getTextModel()
                    : LlmDefaults.DEFAULT_CHAT_MODEL;
            tokenUsageHelper.recordAiTokenUsage(robot, LlmProviderConstants.DASHSCOPE, modelType,
                    tokenUsage.getPromptTokens(), tokenUsage.getCompletionTokens(), success, responseTime);
        }
    }

    @Override
    protected void processPromptSse(Prompt prompt, RobotProtobuf robot, MessageProtobuf messageProtobufQuery,
            MessageProtobuf messageProtobufReply, List<RobotContent.SourceReference> sourceReferences,
            SseEmitter emitter) {
        if (tryProcessPromptSseWithProviderToolService(prompt, robot, messageProtobufQuery, messageProtobufReply,
                sourceReferences, emitter)) {
            return;
        }

        // 从robot中获取llm配置
        RobotLlm llm = robot.getLlm();

        if (llm == null) {
            log.info("Dashscope API not available");
            sseMessageHelper.sendStreamEndMessage(messageProtobufQuery, messageProtobufReply, emitter, 0, 0, 0, prompt,
                    LlmProviderConstants.DASHSCOPE,
                    (llm != null && StringUtils.hasText(llm.getTextModel())) ? llm.getTextModel() : LlmDefaults.DEFAULT_CHAT_MODEL);
            return;
        }

        // 获取适当的模型实例
        ChatModel chatModel = createDashscopeChatModel(llm);

        if (chatModel == null) {
            log.error("Failed to create Dashscope chat model and no default chat model available");
            // 使用sendStreamEndMessage方法替代重复的代码
            sseMessageHelper.sendStreamEndMessage(messageProtobufQuery, messageProtobufReply, emitter, 0, 0, 0, prompt,
                    LlmProviderConstants.DASHSCOPE,
                    (llm != null && StringUtils.hasText(llm.getTextModel())) ? llm.getTextModel() : LlmDefaults.DEFAULT_CHAT_MODEL);
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

                                    sseMessageHelper.sendStreamMessage(messageProtobufQuery, messageProtobufReply,
                                            emitter, textContent, reasonContent, sourceReferences);
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
                        // 发送流结束消息，包含token使用情况和prompt内容
                        sseMessageHelper.sendStreamEndMessage(messageProtobufQuery, messageProtobufReply, emitter,
                                tokenUsage[0].getPromptTokens(), tokenUsage[0].getCompletionTokens(),
                                tokenUsage[0].getTotalTokens(), prompt, LlmProviderConstants.DASHSCOPE,
                                (llm != null && StringUtils.hasText(llm.getTextModel())) ? llm.getTextModel()
                                        : LlmDefaults.DEFAULT_CHAT_MODEL);
                        // 记录token使用情况
                        long responseTime = System.currentTimeMillis() - startTime;
                        String modelType = (llm != null && StringUtils.hasText(llm.getTextModel())) ? llm.getTextModel()
                                : LlmDefaults.DEFAULT_CHAT_MODEL;
                        tokenUsageHelper.recordAiTokenUsage(robot, LlmProviderConstants.DASHSCOPE, modelType,
                                tokenUsage[0].getPromptTokens(), tokenUsage[0].getCompletionTokens(), success[0],
                                responseTime);
                    });
        } catch (Exception e) {
            log.error("Error starting Dashscope stream 4", e);
            sseMessageHelper.handleSseError(e, messageProtobufQuery, messageProtobufReply, emitter);
            success[0] = false;
            // 记录token使用情况
            long responseTime = System.currentTimeMillis() - startTime;
            String modelType = (llm != null && StringUtils.hasText(llm.getTextModel())) ? llm.getTextModel()
                    : LlmDefaults.DEFAULT_CHAT_MODEL;
            tokenUsageHelper.recordAiTokenUsage(robot, LlmProviderConstants.DASHSCOPE, modelType,
                    tokenUsage[0].getPromptTokens(), tokenUsage[0].getCompletionTokens(), success[0], responseTime);
        }
    }

}
