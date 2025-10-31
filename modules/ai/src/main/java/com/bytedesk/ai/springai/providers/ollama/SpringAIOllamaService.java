/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-26 16:59:14
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-25 07:45:42
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.providers.ollama;

import java.util.List;
import java.util.Optional;

import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaChatOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.bytedesk.ai.provider.LlmProviderEntity;
import com.bytedesk.ai.provider.LlmProviderRestService;
import com.bytedesk.ai.robot_settings.RobotLlmResponse;
import com.bytedesk.ai.robot.RobotProtobuf;
import com.bytedesk.ai.springai.service.BaseSpringAIService;
import com.bytedesk.ai.springai.service.ChatTokenUsage;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.llm.LlmProviderConstants;
import com.bytedesk.ai.springai.service.TokenUsageHelper;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.MessageTypeEnum;
import com.bytedesk.core.message.content.RobotContent;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SpringAIOllamaService extends BaseSpringAIService {

    @Autowired
    private LlmProviderRestService llmProviderRestService;

    @Autowired(required = false)
    @Qualifier("bytedeskOllamaChatModel")
    private OllamaChatModel defaultChatModel;

    @Autowired
    private TokenUsageHelper tokenUsageHelper;
    
    public SpringAIOllamaService() {
        super(); // 调用基类的无参构造函数
    }

    /**
     * 根据机器人配置创建动态的OllamaChatOptions
     * 
     * @param llm 机器人LLM配置
     * @return 根据机器人配置创建的选项
     */
    private OllamaChatOptions createOllamaChatOptions(RobotLlmResponse llm) {
        if (llm == null || !StringUtils.hasText(llm.getTextModel())) {
            return null;
        }
        try {
            return OllamaChatOptions.builder()
                    .model(llm.getTextModel())
                    .temperature(llm.getTemperature())
                    .topP(llm.getTopP())
                    .topK(llm.getTopK())
                    .build();
        } catch (Exception e) {
            log.error("Error creating Ollama options for model {}", llm.getTextModel(), e);
            return null;
        }
    }

    public OllamaApi createOllamaApi(String apiUrl) {
        return OllamaApi.builder()
                .baseUrl(apiUrl)
                .build();
    }

    /**
     * 根据机器人配置创建动态的OllamaChatModel
     * 
     * @param llm 机器人LLM配置
     * @return 配置了特定模型的OllamaChatModel
     */
    private OllamaChatModel createOllamaChatModel(RobotLlmResponse llm) {

        Optional<LlmProviderEntity> llmProviderOptional = llmProviderRestService.findByUid(llm.getTextProviderUid());
        if (llmProviderOptional.isEmpty()) {
            log.warn("LlmProvider with uid {} not found", llm.getTextProviderUid());
            return defaultChatModel;
        }
        
        try {
            // 使用默认的OllamaApi实例
            OllamaApi ollamaApi = createOllamaApi(llmProviderOptional.get().getBaseUrl());
            OllamaChatOptions options = createOllamaChatOptions(llm);
            if (options == null) {
                log.warn("Failed to create Ollama options, using default chat model");
                return defaultChatModel;
            }
            return OllamaChatModel.builder()
                    .ollamaApi(ollamaApi)
                    .defaultOptions(options)
                    .build();
        } catch (Exception e) {
            log.error("Failed to create dynamic Ollama chat model for provider {}, using default chat model", llmProviderOptional.get().getUid(), e);
            return defaultChatModel;
        }
    }

    @Override
    protected void processPromptWebsocket(Prompt prompt, RobotProtobuf robot, MessageProtobuf messageProtobufQuery,
            MessageProtobuf messageProtobufReply) {
        // 从robot中获取llm配置
    RobotLlmResponse llm = robot.getLlm();
        log.info("Ollama API websocket ");
        if (llm == null) {
            log.info("Ollama API not available");
            sseMessageHelper.sendMessageWebsocket(MessageTypeEnum.ERROR, "Ollama service is not available", messageProtobufReply);
            return;
        }

        // 获取适当的模型实例
        OllamaChatModel chatModel = createOllamaChatModel(llm);
        if (chatModel == null) {
            log.info("Ollama API not available");
            sseMessageHelper.sendMessageWebsocket(MessageTypeEnum.ERROR, "Ollama service is not available", messageProtobufReply);
            return;
        }

        long startTime = System.currentTimeMillis();
        final boolean[] success = { false };
        final ChatTokenUsage[] tokenUsage = { new ChatTokenUsage(0, 0, 0) };

        try {
            chatModel.stream(prompt).subscribe(
                    response -> {
                        if (response != null) {
                            log.info("Ollama API response metadata: {}", response.getMetadata());
                            List<Generation> generations = response.getResults();
                            for (Generation generation : generations) {
                                AssistantMessage assistantMessage = generation.getOutput();
                                String textContent = assistantMessage.getText();
                                log.info("Ollama API Websocket response text: {}", textContent);

                                sseMessageHelper.sendMessageWebsocket(MessageTypeEnum.ROBOT_STREAM, textContent, messageProtobufReply);
                            }
                            // 提取token使用情况
                            tokenUsage[0] = tokenUsageHelper.extractTokenUsage(response);
                            success[0] = true;
                        }
                    },
                    error -> {
                        log.error("Ollama API error: ", error);
                        sseMessageHelper.sendMessageWebsocket(MessageTypeEnum.ERROR, I18Consts.I18N_SERVICE_TEMPORARILY_UNAVAILABLE, messageProtobufReply);
                        success[0] = false;
                    },
                    () -> {
                        log.info("Chat stream completed");
                        // 记录token使用情况
                        long responseTime = System.currentTimeMillis() - startTime;
                        String modelType = (llm != null && StringUtils.hasText(llm.getTextModel())) ? llm.getTextModel()
                                : "llama2";
            tokenUsageHelper.recordAiTokenUsage(robot, LlmProviderConstants.OLLAMA, modelType,
                                tokenUsage[0].getPromptTokens(), tokenUsage[0].getCompletionTokens(), success[0],
                                responseTime);
                    });
        } catch (Exception e) {
            log.error("Error processing Ollama prompt", e);
            sseMessageHelper.sendMessageWebsocket(MessageTypeEnum.ERROR, I18Consts.I18N_SERVICE_TEMPORARILY_UNAVAILABLE, messageProtobufReply);
            success[0] = false;
            // 记录token使用情况
            long responseTime = System.currentTimeMillis() - startTime;
            String modelType = (llm != null && StringUtils.hasText(llm.getTextModel())) ? llm.getTextModel() : "llama2";
        tokenUsageHelper.recordAiTokenUsage(robot, LlmProviderConstants.OLLAMA, modelType,
                    tokenUsage[0].getPromptTokens(), tokenUsage[0].getCompletionTokens(), success[0], responseTime);
        }
    }

    @Override
    protected String processPromptSync(String message, RobotProtobuf robot) {
        long startTime = System.currentTimeMillis();
        boolean success = false;
        ChatTokenUsage tokenUsage = new ChatTokenUsage(0, 0, 0);

        log.info("Ollama API sync ");

        // 从robot中获取llm配置
    RobotLlmResponse llm = robot.getLlm();
        log.info("Ollama API websocket ");

        if (llm == null) {
            log.info("Ollama API not available");
            return "Ollama service is not available";
        }

        // 获取适当的模型实例
        OllamaChatModel chatModel = createOllamaChatModel(llm);

        try {
            try {
                // 如果有robot参数，尝试创建自定义选项
                if (robot != null && robot.getLlm() != null) {
                    // 创建自定义选项
                    OllamaChatOptions customOptions = createOllamaChatOptions(robot.getLlm());
                    if (customOptions != null) {
                        // 使用自定义选项创建Prompt
                        Prompt prompt = new Prompt(message, customOptions);
                        var response = chatModel.call(prompt);
                        log.info("Ollama API Sync response metadata: {}", response.getMetadata());
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
                log.error("Ollama API sync error", e);
                success = false;
                return I18Consts.I18N_SERVICE_TEMPORARILY_UNAVAILABLE;
            }

        } catch (Exception e) {
            log.error("Ollama API sync error", e);
            success = false;
            return I18Consts.I18N_SERVICE_TEMPORARILY_UNAVAILABLE;
        } finally {
            // 记录token使用情况
            long responseTime = System.currentTimeMillis() - startTime;
            String modelType = (robot != null && robot.getLlm() != null
                    && StringUtils.hasText(robot.getLlm().getTextModel()))
                            ? robot.getLlm().getTextModel()
                            : "llama2";
        tokenUsageHelper.recordAiTokenUsage(robot, LlmProviderConstants.OLLAMA, modelType,
                    tokenUsage.getPromptTokens(), tokenUsage.getCompletionTokens(), success, responseTime);
        }
    }

    @Override
    protected void processPromptSse(Prompt prompt, RobotProtobuf robot, MessageProtobuf messageProtobufQuery,
            MessageProtobuf messageProtobufReply, List<RobotContent.SourceReference> sourceReferences, SseEmitter emitter) {
        Assert.notNull(emitter, "SseEmitter must not be null");
        // 从robot中获取llm配置
    RobotLlmResponse llm = robot.getLlm();
        log.info("Ollama API SSE ");

        if (llm == null) {
            log.info("Ollama API not available");
        sseMessageHelper.sendStreamEndMessage(messageProtobufQuery, messageProtobufReply, emitter, 0, 0, 0, prompt,
            LlmProviderConstants.OLLAMA,
            (llm != null && StringUtils.hasText(llm.getTextModel())) ? llm.getTextModel() : "llama2");
            return;
        }

        // 获取适当的模型实例
        OllamaChatModel chatModel = createOllamaChatModel(llm);

        if (chatModel == null) {
            log.info("Ollama API not available");
            // 使用sendStreamEndMessage方法替代重复的代码
        sseMessageHelper.sendStreamEndMessage(messageProtobufQuery, messageProtobufReply, emitter, 0, 0, 0, prompt,
            LlmProviderConstants.OLLAMA,
            (llm != null && StringUtils.hasText(llm.getTextModel())) ? llm.getTextModel() : "llama2");
            return;
        }

        long startTime = System.currentTimeMillis();
        final boolean[] success = { false };
        final ChatTokenUsage[] tokenUsage = { new ChatTokenUsage(0, 0, 0) };

        try {
            // 发送初始消息，告知用户请求已收到，正在处理
            sseMessageHelper.sendStreamStartMessage(messageProtobufQuery, messageProtobufReply, emitter, I18Consts.I18N_THINKING);

            chatModel.stream(prompt).subscribe(
                    response -> {
                        try {
                            if (response != null && !sseMessageHelper.isEmitterCompleted(emitter)) {
                                List<Generation> generations = response.getResults();
                                for (Generation generation : generations) {
                                    AssistantMessage assistantMessage = generation.getOutput();
                                    String textContent = assistantMessage.getText();
                                    log.info("Ollama API SSE response text: {}", textContent);

                                    sseMessageHelper.sendStreamMessage(messageProtobufQuery, messageProtobufReply, emitter, textContent, null, sourceReferences);
                                }
                                // 提取token使用情况
                                tokenUsage[0] = tokenUsageHelper.extractTokenUsage(response);
                                success[0] = true;
                            }
                        } catch (Exception e) {
                            log.error("Ollama API SSE error 1: ", e);
                            sseMessageHelper.handleSseError(e, messageProtobufQuery, messageProtobufReply, emitter);
                            success[0] = false;
                        }
                    },
                    error -> {
                        log.error("Ollama API SSE error 2: ", error);
                        sseMessageHelper.handleSseError(error, messageProtobufQuery, messageProtobufReply, emitter);
                        success[0] = false;
                    },
                    () -> {
                        log.info("Ollama API SSE complete");
                        // 发送流结束消息，包含token使用情况和prompt内容
            sseMessageHelper.sendStreamEndMessage(messageProtobufQuery, messageProtobufReply, emitter,
                                tokenUsage[0].getPromptTokens(), tokenUsage[0].getCompletionTokens(),
                                tokenUsage[0].getTotalTokens(), prompt, LlmProviderConstants.OLLAMA,
                                (llm != null && StringUtils.hasText(llm.getTextModel())) ? llm.getTextModel()
                                        : "llama2");
                        // 记录token使用情况
                        long responseTime = System.currentTimeMillis() - startTime;
                        String modelType = (llm != null && StringUtils.hasText(llm.getTextModel())) ? llm.getTextModel()
                                : "llama2";
            tokenUsageHelper.recordAiTokenUsage(robot, LlmProviderConstants.OLLAMA, modelType,
                                tokenUsage[0].getPromptTokens(), tokenUsage[0].getCompletionTokens(), success[0],
                                responseTime);
                    });
        } catch (Exception e) {
            log.error("Error starting Ollama stream 4", e);
            sseMessageHelper.handleSseError(e, messageProtobufQuery, messageProtobufReply, emitter);
            success[0] = false;
            // 记录token使用情况
            long responseTime = System.currentTimeMillis() - startTime;
            String modelType = (llm != null && StringUtils.hasText(llm.getTextModel())) ? llm.getTextModel() : "llama2";
        tokenUsageHelper.recordAiTokenUsage(robot, LlmProviderConstants.OLLAMA, modelType,
                    tokenUsage[0].getPromptTokens(), tokenUsage[0].getCompletionTokens(), success[0], responseTime);
        }
    }

    /**
     * 检查模型是否存在
     * 
     * @param model 模型名称
     * @return 如果模型存在返回true，否则返回false
     */
    public Boolean isModelExists(OllamaRequest request) {
        OllamaApi ollamaApi = createOllamaApi(request.getBaseUrl());
        String modelName = request.getModel();
        Assert.hasText(modelName, "Model name must not be null or empty");
        try {
            ollamaApi.showModel(new OllamaApi.ShowModelRequest(modelName));
            return true;
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                return false;
            }
            log.error("检查模型是否存在时发生错误: {}, 状态码: {}", modelName, e.getStatusCode());
        } catch (Exception e) {
            log.error("检查模型是否存在时发生未知错误: {}, 错误: {}", modelName, e.getMessage());
        }
        return false;
    }

}
