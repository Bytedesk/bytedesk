/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-26 16:59:14
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-24 17:44:15
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
import org.springframework.ai.ollama.api.OllamaOptions;
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
import com.bytedesk.ai.robot.RobotLlm;
import com.bytedesk.ai.robot.RobotProtobuf;
import com.bytedesk.ai.springai.service.BaseSpringAIService;
import com.bytedesk.ai.springai.service.ChatTokenUsage;
import com.bytedesk.core.constant.LlmConsts;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.MessageTypeEnum;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SpringAIOllamaService extends BaseSpringAIService {

    @Autowired
    private LlmProviderRestService llmProviderRestService;

    @Autowired(required = false)
    @Qualifier("bytedeskOllamaChatModel")
    private OllamaChatModel defaultChatModel;

    public SpringAIOllamaService() {
        super(); // 调用基类的无参构造函数
    }

    /**
     * 根据机器人配置创建动态的OllamaOptions
     * 
     * @param llm 机器人LLM配置
     * @return 根据机器人配置创建的选项
     */
    private OllamaOptions createOllamaOptions(RobotLlm llm) {
        return super.createDynamicOptions(llm, robotLlm -> OllamaOptions.builder()
                .model(robotLlm.getTextModel())
                .temperature(robotLlm.getTemperature())
                .topP(robotLlm.getTopP())
                .topK(robotLlm.getTopK())
                .build());
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
    private OllamaChatModel createOllamaChatModel(RobotLlm llm) {

        Optional<LlmProviderEntity> llmProviderOptional = llmProviderRestService.findByUid(llm.getTextProviderUid());
        if (llmProviderOptional.isEmpty()) {
            log.warn("LlmProvider with uid {} not found", llm.getTextProviderUid());
            return defaultChatModel;
        }
        
        try {
            // 使用默认的OllamaApi实例
            OllamaApi ollamaApi = createOllamaApi(llmProviderOptional.get().getApiUrl());
            OllamaOptions options = createOllamaOptions(llm);
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
            MessageProtobuf messageProtobufReply, String fullPromptContent) {
        // 从robot中获取llm配置
        RobotLlm llm = robot.getLlm();
        log.info("Ollama API websocket fullPromptContent: {}", fullPromptContent);
        if (llm == null) {
            log.info("Ollama API not available");
            sendMessageWebsocket(MessageTypeEnum.ERROR, "Ollama service is not available", messageProtobufReply);
            return;
        }

        // 获取适当的模型实例
        OllamaChatModel chatModel = createOllamaChatModel(llm);
        if (chatModel == null) {
            log.info("Ollama API not available");
            sendMessageWebsocket(MessageTypeEnum.ERROR, "Ollama service is not available", messageProtobufReply);
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

                                sendMessageWebsocket(MessageTypeEnum.STREAM, textContent, messageProtobufReply);
                            }
                            // 提取token使用情况
                            tokenUsage[0] = extractTokenUsage(response);
                            success[0] = true;
                        }
                    },
                    error -> {
                        log.error("Ollama API error: ", error);
                        sendMessageWebsocket(MessageTypeEnum.ERROR, I18Consts.I18N_SERVICE_TEMPORARILY_UNAVAILABLE, messageProtobufReply);
                        success[0] = false;
                    },
                    () -> {
                        log.info("Chat stream completed");
                        // 记录token使用情况
                        long responseTime = System.currentTimeMillis() - startTime;
                        String modelType = (llm != null && StringUtils.hasText(llm.getTextModel())) ? llm.getTextModel()
                                : "llama2";
                        recordAiTokenUsage(robot, LlmConsts.OLLAMA, modelType,
                                tokenUsage[0].getPromptTokens(), tokenUsage[0].getCompletionTokens(), success[0],
                                responseTime);
                    });
        } catch (Exception e) {
            log.error("Error processing Ollama prompt", e);
            sendMessageWebsocket(MessageTypeEnum.ERROR, I18Consts.I18N_SERVICE_TEMPORARILY_UNAVAILABLE, messageProtobufReply);
            success[0] = false;
            // 记录token使用情况
            long responseTime = System.currentTimeMillis() - startTime;
            String modelType = (llm != null && StringUtils.hasText(llm.getTextModel())) ? llm.getTextModel() : "llama2";
            recordAiTokenUsage(robot, LlmConsts.OLLAMA, modelType,
                    tokenUsage[0].getPromptTokens(), tokenUsage[0].getCompletionTokens(), success[0], responseTime);
        }
    }

    @Override
    protected String processPromptSync(String message, RobotProtobuf robot, String fullPromptContent) {
        long startTime = System.currentTimeMillis();
        boolean success = false;
        ChatTokenUsage tokenUsage = new ChatTokenUsage(0, 0, 0);

        log.info("Ollama API sync fullPromptContent: {}", fullPromptContent);

        // 从robot中获取llm配置
        RobotLlm llm = robot.getLlm();
        log.info("Ollama API websocket fullPromptContent: {}", fullPromptContent);

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
                    OllamaOptions customOptions = createOllamaOptions(robot.getLlm());
                    if (customOptions != null) {
                        // 使用自定义选项创建Prompt
                        Prompt prompt = new Prompt(message, customOptions);
                        var response = chatModel.call(prompt);
                        log.info("Ollama API Sync response metadata: {}", response.getMetadata());
                        tokenUsage = extractTokenUsage(response);
                        success = true;
                        return extractTextFromResponse(response);
                    }
                }
                var response = chatModel.call(message);
                tokenUsage = extractTokenUsage(response);
                success = true;
                return extractTextFromResponse(response);
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
            recordAiTokenUsage(robot, LlmConsts.OLLAMA, modelType,
                    tokenUsage.getPromptTokens(), tokenUsage.getCompletionTokens(), success, responseTime);
        }
    }

    @Override
    protected void processPromptSse(Prompt prompt, RobotProtobuf robot, MessageProtobuf messageProtobufQuery,
            MessageProtobuf messageProtobufReply, SseEmitter emitter, String fullPromptContent) {
        Assert.notNull(emitter, "SseEmitter must not be null");
        // 从robot中获取llm配置
        RobotLlm llm = robot.getLlm();
        log.info("Ollama API SSE fullPromptContent: {}", fullPromptContent);

        if (llm == null) {
            log.info("Ollama API not available");
            sendStreamEndMessage(messageProtobufQuery, messageProtobufReply, emitter, 0, 0, 0, fullPromptContent,
                    LlmConsts.OLLAMA,
                    (llm != null && StringUtils.hasText(llm.getTextModel())) ? llm.getTextModel() : "llama2");
            return;
        }

        // 获取适当的模型实例
        OllamaChatModel chatModel = createOllamaChatModel(llm);

        if (chatModel == null) {
            log.info("Ollama API not available");
            // 使用sendStreamEndMessage方法替代重复的代码
            sendStreamEndMessage(messageProtobufQuery, messageProtobufReply, emitter, 0, 0, 0, fullPromptContent,
                    LlmConsts.OLLAMA,
                    (llm != null && StringUtils.hasText(llm.getTextModel())) ? llm.getTextModel() : "llama2");
            return;
        }

        long startTime = System.currentTimeMillis();
        final boolean[] success = { false };
        final ChatTokenUsage[] tokenUsage = { new ChatTokenUsage(0, 0, 0) };

        try {
            // 发送初始消息，告知用户请求已收到，正在处理
            sendStreamStartMessage(messageProtobufReply, emitter, I18Consts.I18N_THINKING);

            chatModel.stream(prompt).subscribe(
                    response -> {
                        try {
                            if (response != null && !isEmitterCompleted(emitter)) {
                                List<Generation> generations = response.getResults();
                                for (Generation generation : generations) {
                                    AssistantMessage assistantMessage = generation.getOutput();
                                    String textContent = assistantMessage.getText();
                                    log.info("Ollama API SSE response text: {}", textContent);

                                    sendStreamMessage(messageProtobufQuery, messageProtobufReply, emitter, textContent);
                                }
                                // 提取token使用情况
                                tokenUsage[0] = extractTokenUsage(response);
                                success[0] = true;
                            }
                        } catch (Exception e) {
                            log.error("Ollama API SSE error 1: ", e);
                            handleSseError(e, messageProtobufQuery, messageProtobufReply, emitter);
                            success[0] = false;
                        }
                    },
                    error -> {
                        log.error("Ollama API SSE error 2: ", error);
                        handleSseError(error, messageProtobufQuery, messageProtobufReply, emitter);
                        success[0] = false;
                    },
                    () -> {
                        log.info("Ollama API SSE complete");
                        // 发送流结束消息，包含token使用情况和prompt内容
                        sendStreamEndMessage(messageProtobufQuery, messageProtobufReply, emitter,
                                tokenUsage[0].getPromptTokens(), tokenUsage[0].getCompletionTokens(),
                                tokenUsage[0].getTotalTokens(), fullPromptContent, LlmConsts.OLLAMA,
                                (llm != null && StringUtils.hasText(llm.getTextModel())) ? llm.getTextModel()
                                        : "llama2");
                        // 记录token使用情况
                        long responseTime = System.currentTimeMillis() - startTime;
                        String modelType = (llm != null && StringUtils.hasText(llm.getTextModel())) ? llm.getTextModel()
                                : "llama2";
                        recordAiTokenUsage(robot, LlmConsts.OLLAMA, modelType,
                                tokenUsage[0].getPromptTokens(), tokenUsage[0].getCompletionTokens(), success[0],
                                responseTime);
                    });
        } catch (Exception e) {
            log.error("Error starting Ollama stream 4", e);
            handleSseError(e, messageProtobufQuery, messageProtobufReply, emitter);
            success[0] = false;
            // 记录token使用情况
            long responseTime = System.currentTimeMillis() - startTime;
            String modelType = (llm != null && StringUtils.hasText(llm.getTextModel())) ? llm.getTextModel() : "llama2";
            recordAiTokenUsage(robot, LlmConsts.OLLAMA, modelType,
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
        OllamaApi ollamaApi = createOllamaApi(request.getApiUrl());
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
