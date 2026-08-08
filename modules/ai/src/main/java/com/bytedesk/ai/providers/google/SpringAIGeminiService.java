/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-28 11:44:03
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-25 09:24:51
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.providers.google;

import java.util.List;
import java.util.Optional;

import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.bytedesk.ai.llm_provider.LlmProviderEntity;
import com.bytedesk.ai.llm_provider.LlmProviderRestService;
import com.bytedesk.ai.providers.openai.OpenAiCompatibleModelFactory;
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

@Slf4j
@Service
public class SpringAIGeminiService extends BaseSpringAIService {

    public SpringAIGeminiService(
            LlmProviderRestService llmProviderRestService,
            @Qualifier("geminiChatModel") ObjectProvider<OpenAiChatModel> defaultChatModelProvider,
            TokenUsageHelper tokenUsageHelper) {
        this.llmProviderRestService = llmProviderRestService;
        this.defaultChatModel = defaultChatModelProvider.getIfAvailable();
        this.tokenUsageHelper = tokenUsageHelper;
    }


    private final LlmProviderRestService llmProviderRestService;

    private final OpenAiChatModel defaultChatModel;

    private final TokenUsageHelper tokenUsageHelper;


    /**
     * 根据机器人配置创建动态的OpenAiChatOptions
     * 
     * @param llm 机器人LLM配置
     * @return 根据机器人配置创建的选项
     */
    private OpenAiChatOptions createOpenaiOptions(RobotLlm llm) {
        if (llm == null || !StringUtils.hasText(llm.getTextModel())) {
            return null;
        }
        try {
                return applyRobotToolCallbacks(OpenAiChatOptions.builder()
                    .model(llm.getTextModel())
                    .temperature(llm.getTemperature())
                    .maxTokens(llm.getMaxTokens())
                    .topP(llm.getTopP())
                        .build(), llm);
        } catch (Exception e) {
            log.error("Error creating dynamic options for model {}", llm.getTextModel(), e);
            return null;
        }
    }

    /**
     * 根据机器人配置创建动态的OpenAiChatModel
     * 
     * @param llm 机器人LLM配置
     * @return 配置了特定模型的OpenAiChatModel
     */
    private OpenAiChatModel createOpenaiChatModel(RobotLlm llm) {
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
            log.info("Creating dynamic OpenAI chat model with provider: {} ({})", provider.getType(),
                    provider.getUid());
            OpenAiChatOptions options = createOpenaiOptions(llm);
            if (options == null) {
                log.warn("Failed to create OpenAI options, using default chat model");
                return defaultChatModel;
            }
            OpenAiChatOptions resolvedOptions = OpenAiCompatibleModelFactory.withConnection(options,
                provider.getBaseUrl(), provider.getApiKey());
            return OpenAiCompatibleModelFactory.chatModel(resolvedOptions);
        } catch (Exception e) {
            log.error("Failed to create dynamic OpenAI chat model for provider {}, using default chat model",
                    provider.getUid(), e);
            return defaultChatModel;
        }
    }

    // @Override
    // protected void processPromptWebsocket(Prompt prompt, RobotProtobuf robot, MessageProtobuf messageProtobufQuery,
    //         MessageProtobuf messageProtobufReply) {
    //     // 从robot中获取llm配置
    //     RobotLlm llm = robot.getLlm();
    //     log.info("OpenAI API websocket ");
    //     if (llm == null) {
    //         log.info("OpenAI API not available");
    //         sseMessageHelper.sendMessageWebsocket(MessageTypeEnum.ERROR, I18Consts.I18N_SERVICE_TEMPORARILY_UNAVAILABLE,
    //                 messageProtobufReply);
    //         return;
    //     }

    //     // 获取适当的模型实例
    //     OpenAiChatModel chatModel = createOpenaiChatModel(llm);
    //     if (chatModel == null) {
    //         log.error("Failed to create OpenAI chat model and no default chat model available");
    //         sseMessageHelper.sendMessageWebsocket(MessageTypeEnum.ERROR, I18Consts.I18N_SERVICE_TEMPORARILY_UNAVAILABLE,
    //                 messageProtobufReply);
    //         return;
    //     }

    //     long startTime = System.currentTimeMillis();
    //     final boolean[] success = { false };
    //     final ChatTokenUsage[] tokenUsage = { new ChatTokenUsage(0, 0, 0) };

    //     try {
    //         invokePromptStream(chatModel, prompt).subscribe(
    //                 response -> {
    //                     if (response != null) {
    //                         log.info("OpenAI API response metadata: {}", response.getMetadata());
    //                         List<Generation> generations = response.getResults();
    //                         for (Generation generation : generations) {
    //                             AssistantMessage assistantMessage = generation.getOutput();
    //                             String textContent = assistantMessage.getText();
    //                             log.info("OpenAI API Websocket response text: {}", textContent);

    //                             sseMessageHelper.sendMessageWebsocket(MessageTypeEnum.ROBOT_STREAM, textContent,
    //                                     messageProtobufReply);
    //                         }
    //                         // 提取token使用情况
    //                         tokenUsage[0] = tokenUsageHelper.extractTokenUsage(response);
    //                         success[0] = true;
    //                     }
    //                 },
    //                 error -> {
    //                     log.error("OpenAI API error: ", error);
    //                     sseMessageHelper.sendMessageWebsocket(MessageTypeEnum.ERROR,
    //                             I18Consts.I18N_SERVICE_TEMPORARILY_UNAVAILABLE, messageProtobufReply);
    //                     success[0] = false;
    //                 },
    //                 () -> {
    //                     log.info("Chat stream completed");
    //                     // 记录token使用情况
    //                     long responseTime = System.currentTimeMillis() - startTime;
    //                     String modelType = (llm != null && StringUtils.hasText(llm.getTextModel())) ? llm.getTextModel()
    //                             : "gpt-3.5-turbo";
    //                     tokenUsageHelper.recordAiTokenUsage(robot, LlmProviderConstants.OPENAI, modelType,
    //                             tokenUsage[0].getPromptTokens(), tokenUsage[0].getCompletionTokens(), success[0],
    //                             responseTime);
    //                 });
    //     } catch (Exception e) {
    //         log.error("Error processing OpenAI prompt", e);
    //         sseMessageHelper.sendMessageWebsocket(MessageTypeEnum.ERROR, I18Consts.I18N_SERVICE_TEMPORARILY_UNAVAILABLE,
    //                 messageProtobufReply);
    //         success[0] = false;
    //         // 记录token使用情况
    //         long responseTime = System.currentTimeMillis() - startTime;
    //         String modelType = (llm != null && StringUtils.hasText(llm.getTextModel())) ? llm.getTextModel()
    //                 : "gpt-3.5-turbo";
    //         tokenUsageHelper.recordAiTokenUsage(robot, LlmProviderConstants.OPENAI, modelType,
    //                 tokenUsage[0].getPromptTokens(), tokenUsage[0].getCompletionTokens(), success[0], responseTime);
    //     }
    // }

    @Override
    protected String processPromptSync(String message, RobotProtobuf robot) {
        OpenAiChatOptions customOptions = robot != null && robot.getLlm() != null ? createOpenaiOptions(robot.getLlm()) : null;
        return processPromptSync(buildUserOnlyPrompt(message, customOptions), robot);
    }

    @Override
    protected String processPromptSync(Prompt prompt, RobotProtobuf robot) {
        long startTime = System.currentTimeMillis();
        boolean success = false;
        ChatTokenUsage tokenUsage = new ChatTokenUsage(0, 0, 0);

        log.info("OpenAI API sync ");

        // 从robot中获取llm配置
        RobotLlm llm = robot.getLlm();
        log.info("OpenAI API websocket ");

        if (llm == null) {
            log.info("OpenAI API not available");
            return "OpenAI service is not available";
        }

        // 获取适当的模型实例
        OpenAiChatModel chatModel = createOpenaiChatModel(llm);
        try {
            Prompt requestPrompt = prompt;
            OpenAiChatOptions customOptions = createOpenaiOptions(llm);
            if (customOptions != null) {
                requestPrompt = processPromptWithOptions(prompt, customOptions);
            }
            var chatClient = createChatClient(chatModel, requestPrompt);
            var response = invokePromptSync(chatClient, requestPrompt);
            tokenUsage = tokenUsageHelper.extractTokenUsage(response);
            success = true;
            return promptHelper.extractTextFromResponse(response);
        } catch (Exception e) {
            log.error("OpenAI API sync error", e);
            success = false;
            return I18Consts.I18N_SERVICE_TEMPORARILY_UNAVAILABLE;
        } finally {
            long responseTime = System.currentTimeMillis() - startTime;
            String modelType = (llm != null && StringUtils.hasText(llm.getTextModel())) ? llm.getTextModel()
                    : "gpt-3.5-turbo";
            tokenUsageHelper.recordAiTokenUsage(robot, LlmProviderConstants.OPENAI, modelType,
                    tokenUsage.getPromptTokens(), tokenUsage.getCompletionTokens(), success, responseTime);
        }
    }

    @Override
    protected void processPromptSse(Prompt prompt, RobotProtobuf robot, MessageProtobuf messageProtobufQuery,
            MessageProtobuf messageProtobufReply, List<RobotContent.SourceReference> sourceReferences,
            SseEmitter emitter) {
        // 从robot中获取llm配置
        RobotLlm llm = robot.getLlm();
        log.info("OpenAI API SSE ");

        if (llm == null) {
            log.info("OpenAI API not available");
            sseMessageHelper.sendStreamEndMessage(messageProtobufQuery, messageProtobufReply, emitter, 0, 0, 0, prompt,
                    LlmProviderConstants.OPENAI,
                    (llm != null && StringUtils.hasText(llm.getTextModel())) ? llm.getTextModel() : "gpt-3.5-turbo");
            return;
        }

        // 获取适当的模型实例
        OpenAiChatModel chatModel = createOpenaiChatModel(llm);

        if (chatModel == null) {
            log.error("Failed to create OpenAI chat model and no default chat model available");
            // 使用sendStreamEndMessage方法替代重复的代码
            sseMessageHelper.sendStreamEndMessage(messageProtobufQuery, messageProtobufReply, emitter, 0, 0, 0, prompt,
                    LlmProviderConstants.OPENAI,
                    (llm != null && StringUtils.hasText(llm.getTextModel())) ? llm.getTextModel() : "gpt-3.5-turbo");
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
                                    log.info("OpenAI API SSE response text: {}", textContent);

                                    sseMessageHelper.sendStreamMessage(messageProtobufQuery, messageProtobufReply,
                                        emitter, textContent, reasonContent, sourceReferences);
                                }
                                // 提取token使用情况
                                tokenUsage[0] = tokenUsageHelper.extractTokenUsage(response);
                                success[0] = true;
                            }
                        } catch (Exception e) {
                            log.error("OpenAI API SSE error 1: ", e);
                            sseMessageHelper.handleSseError(e, messageProtobufQuery, messageProtobufReply, emitter);
                            success[0] = false;
                        }
                    },
                    error -> {
                        log.error("OpenAI API SSE error 2: ", error);
                        sseMessageHelper.handleSseError(error, messageProtobufQuery, messageProtobufReply, emitter);
                        success[0] = false;
                    },
                    () -> {
                        log.info("OpenAI API SSE complete");
                        // 发送流结束消息，包含token使用情况和prompt内容
                        sseMessageHelper.sendStreamEndMessage(messageProtobufQuery, messageProtobufReply, emitter,
                                tokenUsage[0].getPromptTokens(), tokenUsage[0].getCompletionTokens(),
                                tokenUsage[0].getTotalTokens(), prompt, LlmProviderConstants.OPENAI,
                                (llm != null && StringUtils.hasText(llm.getTextModel())) ? llm.getTextModel()
                                        : "gpt-3.5-turbo");
                        // 记录token使用情况
                        long responseTime = System.currentTimeMillis() - startTime;
                        String modelType = (llm != null && StringUtils.hasText(llm.getTextModel())) ? llm.getTextModel()
                                : "gpt-3.5-turbo";
                        tokenUsageHelper.recordAiTokenUsage(robot, LlmProviderConstants.OPENAI, modelType,
                                tokenUsage[0].getPromptTokens(), tokenUsage[0].getCompletionTokens(), success[0],
                                responseTime);
                    });
        } catch (Exception e) {
            log.error("Error starting OpenAI stream 4", e);
            sseMessageHelper.handleSseError(e, messageProtobufQuery, messageProtobufReply, emitter);
            success[0] = false;
            // 记录token使用情况
            long responseTime = System.currentTimeMillis() - startTime;
            String modelType = (llm != null && StringUtils.hasText(llm.getTextModel())) ? llm.getTextModel()
                    : "gpt-3.5-turbo";
            tokenUsageHelper.recordAiTokenUsage(robot, LlmProviderConstants.OPENAI, modelType,
                    tokenUsage[0].getPromptTokens(), tokenUsage[0].getCompletionTokens(), success[0], responseTime);
        }
    }

}
