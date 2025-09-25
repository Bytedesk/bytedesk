/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-08-21 15:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-25 09:23:25
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.providers.custom;

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
import com.bytedesk.ai.springai.service.BaseSpringAIService;
import com.bytedesk.ai.springai.service.ChatTokenUsage;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.llm.LlmProviderConstants;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.MessageTypeEnum;
import com.bytedesk.core.message.content.StreamContent;

import lombok.extern.slf4j.Slf4j;

/**
 * 统一的OpenAI兼容服务
 * 支持所有基于OpenAI API兼容的LLM提供商，包括：
 * - OpenAI
 * - DeepSeek
 * - GiteeAI
 * - Tencent HunYuan
 * - Baidu Qianfan
 * - Volcengine
 * - OpenRouter
 * - SiliconFlow
 * - Custom providers
 * 等
 */
@Slf4j
@Service
public class SpringAICustomService extends BaseSpringAIService {

    @Autowired
    private LlmProviderRestService llmProviderRestService;

    @Autowired(required = false)
    @Qualifier("customChatModel")
    private OpenAiChatModel defaultChatModel;

    public SpringAICustomService() {
        super(); // 调用基类的无参构造函数
    }

    /**
     * 根据机器人配置创建动态的OpenAiChatOptions
     * 
     * @param llm 机器人LLM配置
     * @return 根据机器人配置创建的选项
     */
    private OpenAiChatOptions createDynamicOptions(RobotLlm llm) {
        return super.createDynamicOptions(llm, robotLlm -> 
            OpenAiChatOptions.builder()
                .model(robotLlm.getTextModel())
                .temperature(robotLlm.getTemperature())
                .topP(robotLlm.getTopP())
                .build()
        );
    }

    /**
     * 根据机器人配置创建动态的OpenAiApi
     * 
     * @param apiUrl API URL
     * @param apiKey API Key
     * @return 配置的OpenAiApi实例
     */
    public OpenAiApi createOpenAiApi(String apiUrl, String apiKey) {
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
    private OpenAiChatModel createChatModel(RobotLlm llm) {
        if (llm == null || !StringUtils.hasText(llm.getTextProviderUid())) {
            log.warn("RobotLlm is null or textProviderUid is empty");
            return defaultChatModel;
        }

        Optional<LlmProviderEntity> llmProviderOptional = llmProviderRestService.findByUid(llm.getTextProviderUid());
        if (llmProviderOptional.isEmpty()) {
            log.warn("LlmProvider with uid {} not found", llm.getTextProviderUid());
            return defaultChatModel;
        }

        LlmProviderEntity provider = llmProviderOptional.get();

        try {
            // 创建 OpenAiApi 实例
            OpenAiApi openAiApi = createOpenAiApi(provider.getBaseUrl(), provider.getApiKey());

            // 创建选项
            OpenAiChatOptions options = createDynamicOptions(llm);
            if (options == null) {
                log.warn("Failed to create dynamic options for provider {}, using default chat model", provider.getType());
                return defaultChatModel;
            }

            return OpenAiChatModel.builder()
                    .openAiApi(openAiApi)
                    .defaultOptions(options)
                    .build();
        } catch (Exception e) {
            log.error("Failed to create dynamic chat model for provider {}, using default chat model", provider.getUid(), e);
            return defaultChatModel;
        }
    }

    @Override
    protected void processPromptWebsocket(Prompt prompt, RobotProtobuf robot, MessageProtobuf messageProtobufQuery,
            MessageProtobuf messageProtobufReply) {
        // 从robot中获取llm配置和提供商信息
        RobotLlm llm = robot.getLlm();
        String providerName = getProviderType(llm);
        log.info("{} API websocket", providerName);
        
        if (llm == null) {
            sendMessageWebsocket(MessageTypeEnum.ERROR, providerName + I18Consts.I18N_SERVICE_TEMPORARILY_UNAVAILABLE, messageProtobufReply);
            return;
        }

        // 获取适当的模型实例
        OpenAiChatModel chatModel = createChatModel(llm);
        if (chatModel == null) {
            sendMessageWebsocket(MessageTypeEnum.ERROR, providerName + I18Consts.I18N_SERVICE_TEMPORARILY_UNAVAILABLE, messageProtobufReply);
            return;
        }
        
        long startTime = System.currentTimeMillis();
        final boolean[] success = {false};
        final ChatTokenUsage[] tokenUsage = {new ChatTokenUsage(0, 0, 0)};
        final StringBuilder[] fullResponseText = {new StringBuilder()};
        
        try {
            // 如果有自定义选项，创建新的Prompt
            Prompt requestPrompt = prompt;
            OpenAiChatOptions customOptions = createDynamicOptions(llm);
            if (customOptions != null) {
                requestPrompt = new Prompt(prompt.getInstructions(), customOptions);
            }

            chatModel.stream(requestPrompt).subscribe(
                    response -> {
                        if (response != null) {
                            log.debug("{} API response metadata: {}", providerName, response.getMetadata());
                            List<Generation> generations = response.getResults();
                            for (Generation generation : generations) {
                                AssistantMessage assistantMessage = generation.getOutput();
                                String textContent = assistantMessage.getText();
                                
                                // 累积响应文本
                                if (textContent != null) {
                                    fullResponseText[0].append(textContent);
                                }
                                
                                sendMessageWebsocket(MessageTypeEnum.ROBOT_STREAM, textContent, messageProtobufReply);
                            }
                            // 提取token使用情况
                            tokenUsage[0] = extractTokenUsage(response);
                            success[0] = true;
                        }
                    },
                    error -> {
                        log.error("{} API error: ", providerName, error);
                        sendMessageWebsocket(MessageTypeEnum.ERROR, I18Consts.I18N_SERVICE_TEMPORARILY_UNAVAILABLE, messageProtobufReply);
                        success[0] = false;
                    },
                    () -> {
                        log.info("{} Chat stream completed", providerName);
                        
                        // 如果token提取失败，使用累积的完整响应文本来估算token
                        if (tokenUsage[0].getTotalTokens() == 0 && fullResponseText[0].length() > 0) {
                            log.info("{} API using accumulated response text for token estimation: {}", providerName, fullResponseText[0].toString());
                            ChatTokenUsage estimatedUsage = estimateTokenUsageFromText(fullResponseText[0].toString());
                            tokenUsage[0] = estimatedUsage;
                            log.info("{} API final estimated token usage: {}", providerName, estimatedUsage);
                        }
                        
                        // 记录token使用情况
                        long responseTime = System.currentTimeMillis() - startTime;
                        String modelType = (llm != null && StringUtils.hasText(llm.getTextModel())) ? llm.getTextModel()
                                : "default-model";
                        recordAiTokenUsage(robot, getProviderConstant(llm), modelType,
                                tokenUsage[0].getPromptTokens(), tokenUsage[0].getCompletionTokens(), success[0],
                                responseTime);
                    });
        } catch (Exception e) {
            log.error("{} API websocket error: ", providerName, e);
            sendMessageWebsocket(MessageTypeEnum.ERROR, I18Consts.I18N_SERVICE_TEMPORARILY_UNAVAILABLE, messageProtobufReply);
            // 记录失败的token使用情况
            long responseTime = System.currentTimeMillis() - startTime;
            String modelType = (llm != null && StringUtils.hasText(llm.getTextModel())) ? llm.getTextModel()
                    : "default-model";
            recordAiTokenUsage(robot, getProviderConstant(llm), modelType, 0, 0, false, responseTime);
        }
    }

    @Override
    protected String processPromptSync(String message, RobotProtobuf robot) {
        long startTime = System.currentTimeMillis();
        boolean success = false;
        ChatTokenUsage tokenUsage = new ChatTokenUsage(0, 0, 0);
        
        // 从robot中获取llm配置和提供商信息
        RobotLlm llm = robot.getLlm();
        String providerName = getProviderType(llm);
        log.info("{} API sync: {}", providerName);

        if (llm == null) {
            return providerName + " service is not available";
        }

        // 获取适当的模型实例
        OpenAiChatModel chatModel = createChatModel(llm);

        try {
            if (chatModel == null) {
                return providerName + " service is not available";
            }

            // 如果有robot参数，尝试创建自定义选项
            if (robot != null && robot.getLlm() != null) {
                // 创建自定义选项
                OpenAiChatOptions customOptions = createDynamicOptions(robot.getLlm());
                if (customOptions != null) {
                    // 使用自定义选项创建Prompt
                    Prompt prompt = new Prompt(message, customOptions);
                    var response = chatModel.call(prompt);
                    tokenUsage = extractTokenUsage(response);
                    success = true;
                    return extractTextFromResponse(response);
                }
            }

            // 如果没有robot参数或自定义选项，使用默认方式
            Prompt prompt = new Prompt(message);
            var response = chatModel.call(prompt);
            tokenUsage = extractTokenUsage(response);
            success = true;
            return extractTextFromResponse(response);
        } catch (Exception e) {
            log.error("{} API sync error: ", providerName, e);
            success = false;
            return I18Consts.I18N_SERVICE_TEMPORARILY_UNAVAILABLE;
        } finally {
            // 记录token使用情况
            long responseTime = System.currentTimeMillis() - startTime;
            String modelType = (robot != null && robot.getLlm() != null
                    && StringUtils.hasText(robot.getLlm().getTextModel()))
                            ? robot.getLlm().getTextModel()
                            : "default-model";
            recordAiTokenUsage(robot, getProviderConstant(llm), modelType,
                    tokenUsage.getPromptTokens(), tokenUsage.getCompletionTokens(), success, responseTime);
        }
    }

    @Override
    protected void processPromptSse(Prompt prompt, RobotProtobuf robot, MessageProtobuf messageProtobufQuery,
            MessageProtobuf messageProtobufReply, List<StreamContent.SourceReference> sourceReferences, SseEmitter emitter) {
        // 从robot中获取llm配置和提供商信息
        RobotLlm llm = robot.getLlm();
        String providerName = getProviderType(llm);
        log.info("{} API SSE: {}", providerName);

        if (llm == null) {
            handleSseError(new RuntimeException(providerName + " service not available"), messageProtobufQuery,
                    messageProtobufReply, emitter);
            return;
        }

        // 获取适当的模型实例
        OpenAiChatModel chatModel = createChatModel(llm);
        if (chatModel == null) {
            handleSseError(new RuntimeException(providerName + " service not available"), messageProtobufQuery,
                    messageProtobufReply, emitter);
            return;
        }

        // 发送起始消息
        sendStreamStartMessage(messageProtobufReply, emitter, I18Consts.I18N_THINKING);

        Prompt requestPrompt = prompt;
        OpenAiChatOptions customOptions = createDynamicOptions(llm);
        if (customOptions != null) {
            requestPrompt = new Prompt(prompt.getInstructions(), customOptions);
        }

        // 记录开始时间和初始化token使用统计
        long startTime = System.currentTimeMillis();
        final boolean[] success = { false };
        final ChatTokenUsage[] tokenUsage = { new ChatTokenUsage(0, 0, 0) };
        // 用于累积所有响应文本
        final StringBuilder[] fullResponseText = { new StringBuilder() };

        chatModel.stream(requestPrompt).subscribe(
                response -> {
                    try {
                        if (response != null) {
                            List<Generation> generations = response.getResults();
                            for (Generation generation : generations) {
                                AssistantMessage assistantMessage = generation.getOutput();
                                String textContent = assistantMessage.getText();
                                log.debug("{} API response metadata: {}, text {}", providerName, response.getMetadata(),
                                        textContent);
                                
                                // 累积响应文本
                                if (textContent != null) {
                                    fullResponseText[0].append(textContent);
                                }
                                
                                sendStreamMessage(messageProtobufQuery, messageProtobufReply, emitter, textContent, null, sourceReferences);
                            }
                            // 提取token使用情况
                            tokenUsage[0] = extractTokenUsage(response);
                            success[0] = true;
                        }
                    } catch (Exception e) {
                        log.error("Error sending SSE event", e);
                        handleSseError(e, messageProtobufQuery, messageProtobufReply, emitter);
                        success[0] = false;
                    }
                },
                error -> {
                    log.error("{} API SSE error: ", providerName, error);
                    handleSseError(error, messageProtobufQuery, messageProtobufReply, emitter);
                    success[0] = false;
                },
                () -> {
                    log.info("{} API SSE complete", providerName);
                    
                    // 如果token提取失败，使用累积的完整响应文本来估算token
                    if (tokenUsage[0].getTotalTokens() == 0 && fullResponseText[0].length() > 0) {
                        log.info("{} API using accumulated response text for token estimation: {}", providerName, fullResponseText[0].toString());
                        ChatTokenUsage estimatedUsage = estimateTokenUsageFromText(fullResponseText[0].toString());
                        tokenUsage[0] = estimatedUsage;
                        log.info("{} API final estimated token usage: {}", providerName, estimatedUsage);
                    }
                    
                    // 发送流结束消息，包含token使用情况和prompt内容
                    sendStreamEndMessage(messageProtobufQuery, messageProtobufReply, emitter,
                            tokenUsage[0].getPromptTokens(), tokenUsage[0].getCompletionTokens(),
                            tokenUsage[0].getTotalTokens(), prompt, getProviderConstant(llm),
                            (llm != null && StringUtils.hasText(llm.getTextModel())) ? llm.getTextModel()
                                    : "default-model");
                    // 记录token使用情况
                    long responseTime = System.currentTimeMillis() - startTime;
                    String modelType = (llm != null && StringUtils.hasText(llm.getTextModel())) ? llm.getTextModel()
                            : "default-model";
                    recordAiTokenUsage(robot, getProviderConstant(llm), modelType,
                            tokenUsage[0].getPromptTokens(), tokenUsage[0].getCompletionTokens(), success[0],
                            responseTime);
                });
    }

    /**
     * 从完整响应文本估算token使用量
     * 
     * @param outputText 完整的输出文本
     * @return 估算的TokenUsage对象
     */
    private ChatTokenUsage estimateTokenUsageFromText(String outputText) {
        try {
            if (outputText == null || outputText.isEmpty()) {
                return new ChatTokenUsage(0, 0, 0);
            }
            
            // 估算token使用量
            // 中文大约1个字符=1个token，英文大约4个字符=1个token
            long completionTokens = estimateTokens(outputText);
            
            // 假设输入文本长度约为输出文本的30%（这是一个常见的比例）
            long promptTokens = (long) (completionTokens * 0.3);
            long totalTokens = promptTokens + completionTokens;
            
            log.info(
                    "Estimated tokens - output: {} chars -> {} tokens, estimated prompt: {} tokens, total: {} tokens",
                    outputText.length(), completionTokens, promptTokens, totalTokens);
            
            return new ChatTokenUsage(promptTokens, completionTokens, totalTokens);
            
        } catch (Exception e) {
            log.error("Error estimating token usage from text", e);
            return new ChatTokenUsage(0, 0, 0);
        }
    }

    /**
     * 估算文本的token数量
     * 
     * @param text 输入文本
     * @return 估算的token数量
     */
    private long estimateTokens(String text) {
        if (text == null || text.isEmpty()) {
            return 0;
        }

        // 简单的token估算：中文约1字符=1token，英文约4字符=1token
        int chineseChars = 0;
        int englishChars = 0;

        for (char c : text.toCharArray()) {
            if (Character.UnicodeScript.of(c) == Character.UnicodeScript.HAN) {
                chineseChars++;
            } else if (Character.isLetterOrDigit(c)) {
                englishChars++;
            }
        }

        // 估算token数量
        long tokens = chineseChars + (englishChars / 4);

        // 确保至少有1个token
        return Math.max(1, tokens);
    }

    /**
     * 根据LLM配置获取提供商名称（用于日志）
     */
    private String getProviderType(RobotLlm llm) {
        if (llm == null) {
            return "Unknown";
        }
        
        Optional<LlmProviderEntity> llmProviderOptional = llmProviderRestService.findByUid(llm.getTextProviderUid());
        if (llmProviderOptional.isPresent()) {
            return llmProviderOptional.get().getType();
        }
        
        return "Unknown Provider";
    }

    /**
     * 根据LLM配置获取提供商常量（用于统计）
     */
    private String getProviderConstant(RobotLlm llm) {
        if (llm == null) {
            return LlmProviderConstants.CUSTOM;
        }
        
        Optional<LlmProviderEntity> llmProviderOptional = llmProviderRestService.findByUid(llm.getTextProviderUid());
        if (llmProviderOptional.isPresent()) {
            String providerType = llmProviderOptional.get().getType();
            if (StringUtils.hasText(providerType)) {
                // 将提供商名称转换为对应的常量
                return mapProviderNameToConstant(providerType.toLowerCase());
            }
        }
        
        return LlmProviderConstants.CUSTOM;
    }

    /**
     * 将提供商名称映射到对应的常量
     */
    private String mapProviderNameToConstant(String providerType) {
        if (providerType == null) {
            return LlmProviderConstants.CUSTOM;
        }
        
        // 移除空格并转为小写
        providerType = providerType.replaceAll("\\s+", "").toLowerCase();
        
        // 根据名称匹配对应的常量
        if (providerType.contains("deepseek")) {
            return LlmProviderConstants.DEEPSEEK;
        } else if (providerType.contains("openai")) {
            return LlmProviderConstants.OPENAI;
        } else if (providerType.contains("gitee")) {
            return LlmProviderConstants.GITEE;
        } else if (providerType.contains("tencent") || providerType.contains("hunyuan")) {
            return LlmProviderConstants.TENCENT;
        } else if (providerType.contains("baidu") || providerType.contains("qianfan")) {
            return LlmProviderConstants.BAIDU;
        } else if (providerType.contains("volcengine") || providerType.contains("doubao")) {
            return LlmProviderConstants.VOLCENGINE;
        } else if (providerType.contains("siliconflow") || providerType.contains("silicon")) {
            return LlmProviderConstants.SILICONFLOW;
        } else if (providerType.contains("openrouter")) {
            return LlmProviderConstants.OPENROUTER;
        } else if (providerType.contains("groq")) {
            return LlmProviderConstants.GROQ;
        } else if (providerType.contains("anthropic") || providerType.contains("claude")) {
            return LlmProviderConstants.ANTHROPIC;
        } else if (providerType.contains("moonshot") || providerType.contains("kimi")) {
            return LlmProviderConstants.MOONSHOT;
        } else if (providerType.contains("stepfun")) {
            return LlmProviderConstants.STEPFUN;
        } else if (providerType.contains("yi") || providerType.contains("01.ai")) {
            return LlmProviderConstants.YI;
        } else if (providerType.contains("baichuan")) {
            return LlmProviderConstants.BAICHUAN;
        } else {
            return LlmProviderConstants.CUSTOM;
        }
    }
}
