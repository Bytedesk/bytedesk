/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-28 11:44:03
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-25 07:52:39
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.providers.deepseek;

import java.util.List;
import java.util.Optional;

import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.bytedesk.ai.provider.LlmProviderEntity;
import com.bytedesk.ai.provider.LlmProviderRestService;
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
public class SpringAIDeepseekService extends BaseSpringAIService {

    @Autowired
    private LlmProviderRestService llmProviderRestService;

    @Autowired(required = false)
    @Qualifier("deepseekChatModel")
    private OpenAiChatModel defaultChatModel;

    @Autowired
    private TokenUsageHelper tokenUsageHelper;

    public SpringAIDeepseekService() {
        super(); // 调用基类的无参构造函数
    }

    /**
     * 根据机器人配置创建动态的OpenAiChatOptions
     * 
     * @param llm 机器人LLM配置
     * @return 根据机器人配置创建的选项
     */
    private OpenAiChatOptions createDeepseekOptions(RobotLlm llm) {
        if (llm == null || !StringUtils.hasText(llm.getTextModel())) {
            return null;
        }
        try {
            return OpenAiChatOptions.builder()
                    .model(llm.getTextModel())
                    .temperature(llm.getTemperature())
                    .topP(llm.getTopP())
                    .build();
        } catch (Exception e) {
            log.error("Error creating Deepseek options for model {}", llm.getTextModel(), e);
            return null;
        }
    }

    public OpenAiApi createDeepseekApi(String apiUrl, String apiKey) {
        return OpenAiApi.builder()
                .baseUrl(apiUrl)
                .apiKey(apiKey)
                .build();
    }

    /**
     * 根据机器人配置创建动态的OpenAiChatModel
     * 
     * @param llm 机器人LLM配置
     * @return 配置了特定模型的OpenAiChatModel
     */
    private OpenAiChatModel createDeepseekChatModel(RobotLlm llm) {
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
            log.info("Creating dynamic Deepseek chat model with provider: {} ({})", provider.getType(),
                    provider.getUid());
            // 使用动态的OpenAiApi实例
            OpenAiApi deepseekApi = createDeepseekApi(provider.getBaseUrl(), provider.getApiKey());
            OpenAiChatOptions options = createDeepseekOptions(llm);
            if (options == null) {
                log.warn("Failed to create Deepseek options, using default chat model");
                return defaultChatModel;
            }
            return OpenAiChatModel.builder()
                    .openAiApi(deepseekApi)
                    .defaultOptions(options)
                    .build();
        } catch (Exception e) {
            log.error("Failed to create dynamic Deepseek chat model for provider {}, using default chat model",
                    provider.getUid(), e);
            return defaultChatModel;
        }
    }

    // @Override
    // protected void processPromptWebsocket(Prompt prompt, RobotProtobuf robot, MessageProtobuf messageProtobufQuery,
    //         MessageProtobuf messageProtobufReply) {
    //     // 从robot中获取llm配置
    //     RobotLlm llm = robot.getLlm();
    //     log.info("Deepseek API websocket ");
    //     if (llm == null) {
    //         log.info("Deepseek API not available");
    //         sseMessageHelper.sendMessageWebsocket(MessageTypeEnum.ERROR, "Deepseek service is not available",
    //                 messageProtobufReply);
    //         return;
    //     }

    //     // 获取适当的模型实例
    //     OpenAiChatModel chatModel = createDeepseekChatModel(llm);
    //     if (chatModel == null) {
    //         log.error("Failed to create Deepseek chat model and no default chat model available");
    //         sseMessageHelper.sendMessageWebsocket(MessageTypeEnum.ERROR, "Deepseek service is not available",
    //                 messageProtobufReply);
    //         return;
    //     }

    //     long startTime = System.currentTimeMillis();
    //     final boolean[] success = { false };
    //     final ChatTokenUsage[] tokenUsage = { new ChatTokenUsage(0, 0, 0) };

    //     try {
    //         chatModel.stream(prompt).subscribe(
    //                 response -> {
    //                     if (response != null) {
    //                         log.info("Deepseek API response metadata: {}", response.getMetadata());
    //                         List<Generation> generations = response.getResults();
    //                         for (Generation generation : generations) {
    //                             AssistantMessage assistantMessage = generation.getOutput();
    //                             String textContent = assistantMessage.getText();
    //                             log.info("Deepseek API Websocket response text: {}", textContent);

    //                             sseMessageHelper.sendMessageWebsocket(MessageTypeEnum.ROBOT_STREAM, textContent,
    //                                     messageProtobufReply);
    //                         }
    //                         // 提取token使用情况
    //                         tokenUsage[0] = tokenUsageHelper.extractTokenUsage(response);
    //                         success[0] = true;
    //                     }
    //                 },
    //                 error -> {
    //                     log.error("Deepseek API error: ", error);
    //                     sseMessageHelper.sendMessageWebsocket(MessageTypeEnum.ERROR,
    //                             I18Consts.I18N_SERVICE_TEMPORARILY_UNAVAILABLE, messageProtobufReply);
    //                     success[0] = false;
    //                 },
    //                 () -> {
    //                     log.info("Chat stream completed");
    //                     // 记录token使用情况
    //                     long responseTime = System.currentTimeMillis() - startTime;
    //                     String modelType = (llm != null && StringUtils.hasText(llm.getTextModel())) ? llm.getTextModel()
    //                             : "deepseek-chat";
    //                     tokenUsageHelper.recordAiTokenUsage(robot, LlmProviderConstants.DEEPSEEK, modelType,
    //                             tokenUsage[0].getPromptTokens(), tokenUsage[0].getCompletionTokens(), success[0],
    //                             responseTime);
    //                 });
    //     } catch (Exception e) {
    //         log.error("Error processing Deepseek prompt", e);
    //         sseMessageHelper.sendMessageWebsocket(MessageTypeEnum.ERROR, I18Consts.I18N_SERVICE_TEMPORARILY_UNAVAILABLE,
    //                 messageProtobufReply);
    //         success[0] = false;
    //         // 记录token使用情况
    //         long responseTime = System.currentTimeMillis() - startTime;
    //         String modelType = (llm != null && StringUtils.hasText(llm.getTextModel())) ? llm.getTextModel()
    //                 : "deepseek-chat";
    //         tokenUsageHelper.recordAiTokenUsage(robot, LlmProviderConstants.DEEPSEEK, modelType,
    //                 tokenUsage[0].getPromptTokens(), tokenUsage[0].getCompletionTokens(), success[0], responseTime);
    //     }
    // }

    @Override
    protected String processPromptSync(String message, RobotProtobuf robot) {
        long startTime = System.currentTimeMillis();
        boolean success = false;
        ChatTokenUsage tokenUsage = new ChatTokenUsage(0, 0, 0);

        log.info("Deepseek API sync ");

        // 从robot中获取llm配置
        RobotLlm llm = robot.getLlm();
        log.info("Deepseek API websocket ");

        if (llm == null) {
            log.info("Deepseek API not available");
            return "Deepseek service is not available";
        }

        // 获取适当的模型实例
        OpenAiChatModel chatModel = createDeepseekChatModel(llm);

        try {
            try {
                // 如果有robot参数，尝试创建自定义选项
                if (robot != null && robot.getLlm() != null) {
                    // 创建自定义选项
                    OpenAiChatOptions customOptions = createDeepseekOptions(robot.getLlm());
                    if (customOptions != null) {
                        // 使用自定义选项创建Prompt
                        Prompt prompt = new Prompt(message, customOptions);
                        var response = chatModel.call(prompt);
                        log.info("Deepseek API Sync response metadata: {}", response.getMetadata());
                        tokenUsage = tokenUsageHelper.extractTokenUsage(response);
                        success = true;
                        return promptHelper.extractTextFromResponse(response);
                    }
                }
                var response = chatModel.call(message);
                tokenUsage = tokenUsageHelper.extractTokenUsage(response);
                success = true;
                return promptHelper.extractTextFromResponse(response);
            } catch (Exception e) {
                log.error("Deepseek API sync error", e);
                success = false;
                return I18Consts.I18N_SERVICE_TEMPORARILY_UNAVAILABLE;
            }

        } catch (Exception e) {
            log.error("Deepseek API sync error", e);
            success = false;
            return I18Consts.I18N_SERVICE_TEMPORARILY_UNAVAILABLE;
        } finally {
            // 记录token使用情况
            long responseTime = System.currentTimeMillis() - startTime;
            String modelType = (robot != null && robot.getLlm() != null
                    && StringUtils.hasText(robot.getLlm().getTextModel()))
                            ? robot.getLlm().getTextModel()
                            : "deepseek-chat";
            tokenUsageHelper.recordAiTokenUsage(robot, LlmProviderConstants.DEEPSEEK, modelType,
                    tokenUsage.getPromptTokens(), tokenUsage.getCompletionTokens(), success, responseTime);
        }
    }

    @Override
    protected void processPromptSse(Prompt prompt, RobotProtobuf robot, MessageProtobuf messageProtobufQuery,
            MessageProtobuf messageProtobufReply, List<RobotContent.SourceReference> sourceReferences,
            SseEmitter emitter) {
        // 从robot中获取llm配置
        RobotLlm llm = robot.getLlm();
        log.info("Deepseek API SSE ");

        if (llm == null) {
            log.info("Deepseek API not available");
            sseMessageHelper.sendStreamEndMessage(messageProtobufQuery, messageProtobufReply, emitter, 0, 0, 0, prompt,
                    LlmProviderConstants.DEEPSEEK,
                    (llm != null && StringUtils.hasText(llm.getTextModel())) ? llm.getTextModel() : "deepseek-chat");
            return;
        }

        // 获取适当的模型实例
        OpenAiChatModel chatModel = createDeepseekChatModel(llm);

        if (chatModel == null) {
            log.error("Failed to create Deepseek chat model and no default chat model available");
            // 使用sendStreamEndMessage方法替代重复的代码
            sseMessageHelper.sendStreamEndMessage(messageProtobufQuery, messageProtobufReply, emitter, 0, 0, 0, prompt,
                    LlmProviderConstants.DEEPSEEK,
                    (llm != null && StringUtils.hasText(llm.getTextModel())) ? llm.getTextModel() : "deepseek-chat");
            return;
        }

        long startTime = System.currentTimeMillis();
        final boolean[] success = { false };
        final ChatTokenUsage[] tokenUsage = { new ChatTokenUsage(0, 0, 0) };

        try {
            // 发送初始消息，告知用户请求已收到，正在处理
            sseMessageHelper.sendStreamStartMessage(messageProtobufQuery, messageProtobufReply, emitter,
                    I18Consts.I18N_THINKING);

            chatModel.stream(prompt).subscribe(
                    response -> {
                        try {
                            if (response != null && !sseMessageHelper.isEmitterCompleted(emitter)) {
                                List<Generation> generations = response.getResults();
                                for (Generation generation : generations) {
                                    AssistantMessage assistantMessage = generation.getOutput();
                                    String textContent = assistantMessage.getText();
                                    log.info("Deepseek API SSE response text: {}", textContent);

                                    sseMessageHelper.sendStreamMessage(messageProtobufQuery, messageProtobufReply,
                                            emitter, textContent, null, sourceReferences);
                                }
                                // 提取token使用情况
                                tokenUsage[0] = tokenUsageHelper.extractTokenUsage(response);
                                success[0] = true;
                            }
                        } catch (Exception e) {
                            log.error("Deepseek API SSE error 1: ", e);
                            sseMessageHelper.handleSseError(e, messageProtobufQuery, messageProtobufReply, emitter);
                            success[0] = false;
                        }
                    },
                    error -> {
                        log.error("Deepseek API SSE error 2: ", error);
                        sseMessageHelper.handleSseError(error, messageProtobufQuery, messageProtobufReply, emitter);
                        success[0] = false;
                    },
                    () -> {
                        log.info("Deepseek API SSE complete");
                        // 发送流结束消息，包含token使用情况和prompt内容
                        sseMessageHelper.sendStreamEndMessage(messageProtobufQuery, messageProtobufReply, emitter,
                                tokenUsage[0].getPromptTokens(), tokenUsage[0].getCompletionTokens(),
                                tokenUsage[0].getTotalTokens(), prompt, LlmProviderConstants.DEEPSEEK,
                                (llm != null && StringUtils.hasText(llm.getTextModel())) ? llm.getTextModel()
                                        : "deepseek-chat");
                        // 记录token使用情况
                        long responseTime = System.currentTimeMillis() - startTime;
                        String modelType = (llm != null && StringUtils.hasText(llm.getTextModel())) ? llm.getTextModel()
                                : "deepseek-chat";
                        tokenUsageHelper.recordAiTokenUsage(robot, LlmProviderConstants.DEEPSEEK, modelType,
                                tokenUsage[0].getPromptTokens(), tokenUsage[0].getCompletionTokens(), success[0],
                                responseTime);
                    });
        } catch (Exception e) {
            log.error("Error starting Deepseek stream 4", e);
            sseMessageHelper.handleSseError(e, messageProtobufQuery, messageProtobufReply, emitter);
            success[0] = false;
            // 记录token使用情况
            long responseTime = System.currentTimeMillis() - startTime;
            String modelType = (llm != null && StringUtils.hasText(llm.getTextModel())) ? llm.getTextModel()
                    : "deepseek-chat";
            tokenUsageHelper.recordAiTokenUsage(robot, LlmProviderConstants.DEEPSEEK, modelType,
                    tokenUsage[0].getPromptTokens(), tokenUsage[0].getCompletionTokens(), success[0], responseTime);
        }
    }

}
