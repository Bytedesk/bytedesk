/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-26 16:59:14
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-16 14:18:12
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

import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import com.bytedesk.ai.robot.RobotLlm;
import com.bytedesk.ai.robot.RobotProtobuf;
import com.bytedesk.ai.springai.service.BaseSpringAIService;
import com.bytedesk.core.constant.LlmConsts;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.MessageTypeEnum;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@ConditionalOnProperty(prefix = "spring.ai.ollama.chat", name = "enabled", havingValue = "true", matchIfMissing = false)
public class SpringAIOllamaService extends BaseSpringAIService {

    @Autowired(required = false)
    @Qualifier("bytedeskOllamaChatModel")
    private OllamaChatModel bytedeskOllamaChatModel;
    
    @Autowired
    @Qualifier("bytedeskOllamaApi")
    private OllamaApi ollamaApi;

    public SpringAIOllamaService() {
        super(); // 调用基类的无参构造函数
    }

    /**
     * 根据机器人配置创建动态的OllamaOptions
     * 
     * @param llm 机器人LLM配置
     * @return 根据机器人配置创建的选项
     */
    private OllamaOptions createDynamicOptions(RobotLlm llm) {
        return super.createDynamicOptions(llm, robotLlm -> 
            OllamaOptions.builder()
                .model(robotLlm.getTextModel())
                .temperature(robotLlm.getTemperature())
                .topP(robotLlm.getTopP())
                .topK(robotLlm.getTopK())
                .build()
        );
    }

    /**
     * 根据机器人配置创建动态的OllamaChatModel
     * 
     * @param llm 机器人LLM配置
     * @return 配置了特定模型的OllamaChatModel
     */
    private OllamaChatModel createDynamicChatModel(RobotLlm llm) {
        if (llm == null || !StringUtils.hasText(llm.getTextModel())) {
            // 如果没有指定模型或设置，使用默认配置
            return bytedeskOllamaChatModel;
        }

        try {
            OllamaOptions options = createDynamicOptions(llm);
            if (options == null) {
                return bytedeskOllamaChatModel;
            }

            return OllamaChatModel.builder()
                    .ollamaApi(ollamaApi)
                    .defaultOptions(options)
                    .build();
        } catch (Exception e) {
            log.error("Error creating dynamic chat model for model {}", llm.getTextModel(), e);
            return bytedeskOllamaChatModel;
        }
    }

    @Override
    protected void processPromptWebsocket(Prompt prompt, RobotProtobuf robot, MessageProtobuf messageProtobufQuery, MessageProtobuf messageProtobufReply, String fullPromptContent) {
        // 从robot中获取llm配置
        RobotLlm llm = robot.getLlm();
        log.info("Ollama API websocket fullPromptContent: {}", fullPromptContent);

        // 获取适当的模型实例
        OllamaChatModel chatModel = (llm != null) ? createDynamicChatModel(llm) : bytedeskOllamaChatModel;
        
        if (chatModel == null) {
            log.info("Ollama API not available");
            sendMessageWebsocket(MessageTypeEnum.ERROR, "Ollama service is not available", messageProtobufReply);
            return;
        }

        long startTime = System.currentTimeMillis();
        final boolean[] success = {false};
        final TokenUsage[] tokenUsage = {new TokenUsage(0, 0, 0)};

        try {
            chatModel.stream(prompt).subscribe(
                response -> {
                    if (response != null) {
                        log.info("Ollama API response metadata: {}", response.getMetadata());
                        List<Generation> generations = response.getResults();
                        for (Generation generation : generations) {
                            AssistantMessage assistantMessage = generation.getOutput();
                            String textContent = assistantMessage.getText();

                            sendMessageWebsocket(MessageTypeEnum.STREAM, textContent, messageProtobufReply);
                        }
                        // 提取token使用情况
                        tokenUsage[0] = extractTokenUsage(response);
                        success[0] = true;
                    }
                },
                error -> {
                    log.error("Ollama API error: ", error);
                    sendMessageWebsocket(MessageTypeEnum.ERROR, "服务暂时不可用，请稍后重试", messageProtobufReply);
                    success[0] = false;
                },
                () -> {
                    log.info("Chat stream completed");
                    // 记录token使用情况
                    long responseTime = System.currentTimeMillis() - startTime;
                    String modelType = (llm != null && StringUtils.hasText(llm.getTextModel())) ? llm.getTextModel() : "llama2";
                    recordAiTokenUsage(robot, LlmConsts.OLLAMA, modelType, 
                            tokenUsage[0].getPromptTokens(), tokenUsage[0].getCompletionTokens(), success[0], responseTime);
                });
        } catch (Exception e) {
            log.error("Error processing Ollama prompt", e);
            sendMessageWebsocket(MessageTypeEnum.ERROR, "服务暂时不可用，请稍后重试", messageProtobufReply);
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
        TokenUsage tokenUsage = new TokenUsage(0, 0, 0);
        
        log.info("Ollama API sync fullPromptContent: {}", fullPromptContent);
        
        try {
            if (bytedeskOllamaChatModel != null) {
                try {
                    // 如果有robot参数，尝试创建自定义选项
                    if (robot != null && robot.getLlm() != null) {
                        // 创建自定义选项
                        OllamaOptions customOptions = createDynamicOptions(robot.getLlm());
                        if (customOptions != null) {
                            // 使用自定义选项创建Prompt
                            Prompt prompt = new Prompt(message, customOptions);
                            var response = bytedeskOllamaChatModel.call(prompt);
                            tokenUsage = extractTokenUsage(response);
                            success = true;
                            return extractTextFromResponse(response);
                        }
                    }
                    var response = bytedeskOllamaChatModel.call(message);
                    tokenUsage = extractTokenUsage(response);
                    success = true;
                    return extractTextFromResponse(response);
                } catch (Exception e) {
                    log.error("Ollama API sync error", e);
                    success = false;
                    return "服务暂时不可用，请稍后重试";
                }
            } else {
                return "Ollama service is not available";
            }
        } catch (Exception e) {
            log.error("Ollama API sync error", e);
            success = false;
            return "服务暂时不可用，请稍后重试";
        } finally {
            // 记录token使用情况
            long responseTime = System.currentTimeMillis() - startTime;
            String modelType = (robot != null && robot.getLlm() != null && StringUtils.hasText(robot.getLlm().getTextModel())) 
                    ? robot.getLlm().getTextModel() : "llama2";
            recordAiTokenUsage(robot, LlmConsts.OLLAMA, modelType, 
                    tokenUsage.getPromptTokens(), tokenUsage.getCompletionTokens(), success, responseTime);
        }
    }

    @Override
    protected void processPromptSse(Prompt prompt, RobotProtobuf robot, MessageProtobuf messageProtobufQuery, MessageProtobuf messageProtobufReply, SseEmitter emitter, String fullPromptContent) {
        Assert.notNull(emitter, "SseEmitter must not be null");
        // 从robot中获取llm配置
        RobotLlm llm = robot.getLlm();
        log.info("Ollama API SSE fullPromptContent: {}", fullPromptContent);

        // 获取适当的模型实例并配置较长的超时时间
        OllamaChatModel chatModel = bytedeskOllamaChatModel;
        if (chatModel != null && llm != null) {
            chatModel = createDynamicChatModel(llm);
            // 配置更长的超时时间 - 5分钟
            chatModel = configureModelWithTimeout(chatModel, 300000);
        }
        
        if (chatModel == null) {
            log.info("Ollama API not available");
            // 使用sendStreamEndMessage方法替代重复的代码
            sendStreamEndMessage(messageProtobufQuery, messageProtobufReply, emitter, 0, 0, 0, fullPromptContent, LlmConsts.OLLAMA, (llm != null && StringUtils.hasText(llm.getTextModel())) ? llm.getTextModel() : "llama2");
            return;
        }

        long startTime = System.currentTimeMillis();
        final boolean[] success = {false};
        final TokenUsage[] tokenUsage = {new TokenUsage(0, 0, 0)};

        try {
            // 发送初始消息，告知用户请求已收到，正在处理
            sendStreamStartMessage(messageProtobufReply, emitter, "正在思考中...");
            
            chatModel.stream(prompt).subscribe(
                    response -> {
                        try {
                            if (response != null && !isEmitterCompleted(emitter)) {
                                List<Generation> generations = response.getResults();
                                for (Generation generation : generations) {
                                    AssistantMessage assistantMessage = generation.getOutput();
                                    String textContent = assistantMessage.getText();
                                    
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
                                tokenUsage[0].getPromptTokens(), tokenUsage[0].getCompletionTokens(), tokenUsage[0].getTotalTokens(), fullPromptContent, LlmConsts.OLLAMA, (llm != null && StringUtils.hasText(llm.getTextModel())) ? llm.getTextModel() : "llama2");
                        // 记录token使用情况
                        long responseTime = System.currentTimeMillis() - startTime;
                        String modelType = (llm != null && StringUtils.hasText(llm.getTextModel())) ? llm.getTextModel() : "llama2";
                        recordAiTokenUsage(robot, LlmConsts.OLLAMA, modelType, 
                                tokenUsage[0].getPromptTokens(), tokenUsage[0].getCompletionTokens(), success[0], responseTime);
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

    // 修改响应超时配置方法
    private OllamaChatModel configureModelWithTimeout(OllamaChatModel model, long timeoutMillis) {
        try {
            // 因为OllamaOptions.Builder没有from方法，所以需要使用getDefaultOptions()获取当前选项
            // 然后手动复制所有必要的配置
            OllamaOptions defaultOptions = (OllamaOptions)model.getDefaultOptions();
            
            // 创建新的选项，设置超时时间
            OllamaOptions options = OllamaOptions.builder()
                    .model(defaultOptions.getModel())
                    .temperature(defaultOptions.getTemperature())
                    .topP(defaultOptions.getTopP())
                    .topK(defaultOptions.getTopK())
                    .numPredict(defaultOptions.getNumPredict())
                    .numCtx(defaultOptions.getNumCtx())
                    // 设置更长的保活时间
                    .keepAlive(String.format("%ds", timeoutMillis / 1000)) // 将毫秒转换为秒，如"300s"
                    .build();
            
            // 创建一个新的模型实例，具有更长的超时时间
            return OllamaChatModel.builder()
                    .ollamaApi(ollamaApi)
                    .defaultOptions(options)
                    .build();
        } catch (Exception e) {
            log.warn("无法配置模型超时，使用默认模型: {}", e.getMessage());
            return model;
        }
    }

    public Boolean isServiceHealthy() {
        if (bytedeskOllamaChatModel == null) {
            return false;
        }

        try {
            // 发送一个简单的测试请求来检测服务是否响应
            String response = processPromptSync("test", null, "");
            return !response.contains("不可用") && !response.equals("Ollama service is not available");
        } catch (Exception e) {
            log.error("Error checking Ollama service health", e);
            return false;
        }
    }

    public OllamaChatModel getChatModel() {
        return bytedeskOllamaChatModel;
    }
}
