/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-28 11:44:03
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-16 10:45:17
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.providers.baidu;

import java.util.List;

import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import com.bytedesk.ai.robot.RobotLlm;
import com.bytedesk.ai.robot.RobotProtobuf;
import com.bytedesk.ai.springai.service.BaseSpringAIService;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.llm.LlmProviderConstants;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.MessageTypeEnum;
import com.bytedesk.ai.springai.service.ChatTokenUsage;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@ConditionalOnProperty(prefix = "spring.ai.baidu.chat", name = "enabled", havingValue = "true", matchIfMissing = false)
public class SpringAIBaiduChatService extends BaseSpringAIService {

    @Autowired(required = false)
    private OpenAiChatModel baiduChatModel;

    public SpringAIBaiduChatService() {
        super(); // 调用基类的无参构造函数
    }

    /**
     * 根据机器人配置创建动态的OpenAiChatOptions
     * 
     * @param llm 机器人LLM配置
     * @return 根据机器人配置创建的选项
     */
    private OpenAiChatOptions createDynamicOptions(RobotLlm llm) {
        return super.createDynamicOptions(llm, robotLlm -> OpenAiChatOptions.builder()
                .model(robotLlm.getTextModel())
                .temperature(robotLlm.getTemperature())
                .topP(robotLlm.getTopP())
                .build());
    }

    @Override
    protected void processPromptWebsocket(Prompt prompt, RobotProtobuf robot, MessageProtobuf messageProtobufQuery,
            MessageProtobuf messageProtobufReply, String fullPromptContent) {
        log.info("SpringAIBaiduService processPromptWebsocket with full prompt content: {}", fullPromptContent);
        // 从robot中获取llm配置
        RobotLlm llm = robot.getLlm();
        
        if (baiduChatModel == null) {
            sendMessageWebsocket(MessageTypeEnum.ERROR, I18Consts.I18N_SERVICE_TEMPORARILY_UNAVAILABLE, messageProtobufReply);
            return;
        }

        // 如果有自定义选项，创建新的Prompt
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

        baiduChatModel.stream(requestPrompt).subscribe(
                response -> {
                    if (response != null) {
                        log.info("Baidu API response metadata: {}", response.getMetadata());
                        List<Generation> generations = response.getResults();
                        for (Generation generation : generations) {
                            AssistantMessage assistantMessage = generation.getOutput();
                            String textContent = assistantMessage.getText();
                            
                            // 累积响应文本
                            if (textContent != null) {
                                fullResponseText[0].append(textContent);
                            }
                            
                            sendMessageWebsocket(MessageTypeEnum.STREAM, textContent, messageProtobufReply);
                        }
                        // 提取token使用情况 - 使用百度专用的提取方法
                        tokenUsage[0] = extractBaiduTokenUsage(response);
                        success[0] = true;
                    }
                },
                error -> {
                    log.error("Baidu API error: ", error);
                    sendMessageWebsocket(MessageTypeEnum.ERROR, I18Consts.I18N_SERVICE_TEMPORARILY_UNAVAILABLE, messageProtobufReply);
                    success[0] = false;
                },
                () -> {
                    log.info("Chat stream completed");
                    
                    // 如果token提取失败，使用累积的完整响应文本来估算token
                    if (tokenUsage[0].getTotalTokens() == 0 && fullResponseText[0].length() > 0) {
                        log.info("Baidu API using accumulated response text for token estimation: {}", fullResponseText[0].toString());
                        ChatTokenUsage estimatedUsage = estimateBaiduTokenUsageFromText(fullResponseText[0].toString());
                        tokenUsage[0] = estimatedUsage;
                        log.info("Baidu API final estimated token usage: {}", estimatedUsage);
                    }
                    
                    // 记录token使用情况
                    long responseTime = System.currentTimeMillis() - startTime;
                    String modelType = (llm != null && StringUtils.hasText(llm.getTextModel())) ? llm.getTextModel()
                            : "ernie-bot";
                    recordAiTokenUsage(robot, LlmProviderConstants.BAIDU, modelType,
                            tokenUsage[0].getPromptTokens(), tokenUsage[0].getCompletionTokens(), success[0],
                            responseTime);
                });
    }

    @Override
    protected String processPromptSync(String message, RobotProtobuf robot, String fullPromptContent) {
        log.info("SpringAIBaiduService processPromptSync with full prompt content: {}", fullPromptContent);
        long startTime = System.currentTimeMillis();
        boolean success = false;
        ChatTokenUsage tokenUsage = new ChatTokenUsage(0, 0, 0);

        try {
            if (baiduChatModel == null) {
                return "Baidu service is not available";
            }

            try {
                // 如果有robot参数，尝试创建自定义选项
                if (robot != null && robot.getLlm() != null) {
                    // 创建自定义选项
                    OpenAiChatOptions customOptions = createDynamicOptions(robot.getLlm());
                    if (customOptions != null) {
                        // 使用自定义选项创建Prompt
                        Prompt prompt = new Prompt(message, customOptions);
                        var response = baiduChatModel.call(prompt);
                        tokenUsage = extractBaiduTokenUsage(response);
                        success = true;
                        return extractTextFromResponse(response);
                    }
                }

                // 如果没有robot参数或自定义选项，使用默认方式
                Prompt prompt = new Prompt(message);
                var response = baiduChatModel.call(prompt);
                tokenUsage = extractBaiduTokenUsage(response);
                success = true;
                return extractTextFromResponse(response);
            } catch (Exception e) {
                log.error("Baidu API call error: ", e);
                success = false;
                return I18Consts.I18N_SERVICE_TEMPORARILY_UNAVAILABLE;
            }
        } catch (Exception e) {
            log.error("Baidu API sync error: ", e);
            success = false;
            return I18Consts.I18N_SERVICE_TEMPORARILY_UNAVAILABLE;
        } finally {
            // 记录token使用情况
            long responseTime = System.currentTimeMillis() - startTime;
            String modelType = (robot != null && robot.getLlm() != null
                    && StringUtils.hasText(robot.getLlm().getTextModel()))
                            ? robot.getLlm().getTextModel()
                            : "ernie-bot";
            recordAiTokenUsage(robot, LlmProviderConstants.BAIDU, modelType,
                    tokenUsage.getPromptTokens(), tokenUsage.getCompletionTokens(), success, responseTime);
        }
    }

    @Override
    protected void processPromptSse(Prompt prompt, RobotProtobuf robot, MessageProtobuf messageProtobufQuery,
            MessageProtobuf messageProtobufReply, SseEmitter emitter, String fullPromptContent) {
        log.info("SpringAIBaiduService processPromptSse with full prompt content: {}", fullPromptContent);
        // 从robot中获取llm配置
        RobotLlm llm = robot.getLlm();

        if (baiduChatModel == null) {
            handleSseError(new RuntimeException(I18Consts.I18N_SERVICE_TEMPORARILY_UNAVAILABLE), messageProtobufQuery,
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

        baiduChatModel.stream(requestPrompt).subscribe(
                response -> {
                    try {
                        if (response != null) {
                            List<Generation> generations = response.getResults();
                            for (Generation generation : generations) {
                                AssistantMessage assistantMessage = generation.getOutput();
                                String textContent = assistantMessage.getText();
                                log.info("Baidu API response metadata: {}, text {}", response.getMetadata(),
                                        textContent);
                                
                                // 累积响应文本
                                if (textContent != null) {
                                    fullResponseText[0].append(textContent);
                                }
                                
                                sendStreamMessage(messageProtobufQuery, messageProtobufReply, emitter, textContent);
                            }
                            // 提取token使用情况 - 使用百度专用的提取方法
                            tokenUsage[0] = extractBaiduTokenUsage(response);
                            success[0] = true;
                        }
                    } catch (Exception e) {
                        log.error("Error sending SSE event", e);
                        handleSseError(e, messageProtobufQuery, messageProtobufReply, emitter);
                        success[0] = false;
                    }
                },
                error -> {
                    log.error("Baidu API SSE error: ", error);
                    handleSseError(error, messageProtobufQuery, messageProtobufReply, emitter);
                    success[0] = false;
                },
                () -> {
                    log.info("Baidu API SSE complete");
                    
                    // 如果token提取失败，使用累积的完整响应文本来估算token
                    if (tokenUsage[0].getTotalTokens() == 0 && fullResponseText[0].length() > 0) {
                        log.info("Baidu API using accumulated response text for token estimation: {}", fullResponseText[0].toString());
                        ChatTokenUsage estimatedUsage = estimateBaiduTokenUsageFromText(fullResponseText[0].toString());
                        tokenUsage[0] = estimatedUsage;
                        log.info("Baidu API final estimated token usage: {}", estimatedUsage);
                    }
                    
                    // 发送流结束消息，包含token使用情况和prompt内容
                    sendStreamEndMessage(messageProtobufQuery, messageProtobufReply, emitter,
                            tokenUsage[0].getPromptTokens(), tokenUsage[0].getCompletionTokens(),
                            tokenUsage[0].getTotalTokens(), fullPromptContent, LlmProviderConstants.BAIDU,
                            (llm != null && StringUtils.hasText(llm.getTextModel())) ? llm.getTextModel()
                                    : "ernie-bot");
                    // 记录token使用情况
                    long responseTime = System.currentTimeMillis() - startTime;
                    String modelType = (llm != null && StringUtils.hasText(llm.getTextModel())) ? llm.getTextModel()
                            : "ernie-bot";
                    recordAiTokenUsage(robot, LlmProviderConstants.BAIDU, modelType,
                            tokenUsage[0].getPromptTokens(), tokenUsage[0].getCompletionTokens(), success[0],
                            responseTime);
                });
    }

    /**
     * 专门为百度API提取token使用情况
     * 由于百度API返回的usage字段是EmptyUsage对象，需要特殊处理
     * 
     * @param response ChatResponse对象
     * @return TokenUsage对象
     */
    private ChatTokenUsage extractBaiduTokenUsage(org.springframework.ai.chat.model.ChatResponse response) {
        try {
            if (response == null) {
                log.warn("Baidu API response is null");
                return new ChatTokenUsage(0, 0, 0);
            }

            var metadata = response.getMetadata();
            if (metadata == null) {
                log.warn("Baidu API response metadata is null");
                return new ChatTokenUsage(0, 0, 0);
            }

            log.info("Baidu API manual token extraction - metadata: {}", metadata);

            // 百度API的token使用情况通常在以下字段中：
            // 1. usage.prompt_tokens
            // 2. usage.completion_tokens
            // 3. usage.total_tokens

            // 尝试从metadata中直接获取usage对象
            Object usage = null;

            // 方法1: 尝试通过反射获取usage字段
            try {
                java.lang.reflect.Field usageField = metadata.getClass().getDeclaredField("usage");
                usageField.setAccessible(true);
                usage = usageField.get(metadata);
                log.info("Baidu API found usage field via reflection: {}", usage);
            } catch (Exception e) {
                log.debug("Could not get usage field via reflection: {}", e.getMessage());
            }

            // 方法2: 如果反射失败，尝试从metadata的toString()中解析
            if (usage == null) {
                String metadataStr = metadata.toString();
                log.info("Baidu API parsing metadata string: {}", metadataStr);

                // 查找usage信息，格式通常是: DefaultUsage{promptTokens=16, completionTokens=14,
                // totalTokens=30}
                if (metadataStr.contains("DefaultUsage{") || metadataStr.contains("usage:")) {
                    // 提取promptTokens
                    int promptStart = metadataStr.indexOf("promptTokens=");
                    int promptEnd = metadataStr.indexOf(",", promptStart);
                    if (promptEnd == -1)
                        promptEnd = metadataStr.indexOf("}", promptStart);

                    // 提取completionTokens
                    int completionStart = metadataStr.indexOf("completionTokens=");
                    int completionEnd = metadataStr.indexOf(",", completionStart);
                    if (completionEnd == -1)
                        completionEnd = metadataStr.indexOf("}", completionStart);

                    // 提取totalTokens
                    int totalStart = metadataStr.indexOf("totalTokens=");
                    int totalEnd = metadataStr.indexOf("}", totalStart);

                    long promptTokens = 0;
                    long completionTokens = 0;
                    long totalTokens = 0;

                    if (promptStart > 0 && promptEnd > promptStart) {
                        try {
                            promptTokens = Long.parseLong(metadataStr.substring(promptStart + 13, promptEnd));
                        } catch (NumberFormatException e) {
                            log.warn("Could not parse promptTokens from: {}",
                                    metadataStr.substring(promptStart + 13, promptEnd));
                        }
                    }

                    if (completionStart > 0 && completionEnd > completionStart) {
                        try {
                            completionTokens = Long
                                    .parseLong(metadataStr.substring(completionStart + 17, completionEnd));
                        } catch (NumberFormatException e) {
                            log.warn("Could not parse completionTokens from: {}",
                                    metadataStr.substring(completionStart + 17, completionEnd));
                        }
                    }

                    if (totalStart > 0 && totalEnd > totalStart) {
                        try {
                            totalTokens = Long.parseLong(metadataStr.substring(totalStart + 12, totalEnd));
                        } catch (NumberFormatException e) {
                            log.warn("Could not parse totalTokens from: {}",
                                    metadataStr.substring(totalStart + 12, totalEnd));
                        }
                    }

                    // 如果没有total_tokens，计算它
                    if (totalTokens == 0 && (promptTokens > 0 || completionTokens > 0)) {
                        totalTokens = promptTokens + completionTokens;
                    }

                    // 如果只有total_tokens，估算prompt和completion
                    if (totalTokens > 0 && (promptTokens == 0 || completionTokens == 0)) {
                        if (promptTokens == 0) {
                            promptTokens = (long) (totalTokens * 0.3);
                        }
                        if (completionTokens == 0) {
                            completionTokens = totalTokens - promptTokens;
                        }
                    }

                    log.info("Baidu API manual token extraction result - prompt: {}, completion: {}, total: {}",
                            promptTokens, completionTokens, totalTokens);

                    return new ChatTokenUsage(promptTokens, completionTokens, totalTokens);
                }
            }

            // 如果usage字段不存在，尝试其他可能的字段
            log.info("Baidu API usage field not found, checking other fields");
            for (String key : metadata.keySet()) {
                Object value = metadata.get(key);
                log.info("Baidu API metadata [{}]: {} (class: {})",
                        key, value, value != null ? value.getClass().getName() : "null");
            }

            // 方法3: 如果手动提取失败，尝试使用原始的extractTokenUsage方法作为后备
            log.info("Baidu API manual extraction failed, trying original extractTokenUsage method");
            ChatTokenUsage fallbackUsage = extractTokenUsage(response);
            if (fallbackUsage.getTotalTokens() > 0) {
                log.info("Baidu API fallback extraction successful: {}", fallbackUsage);
                return fallbackUsage;
            }

            // 方法4: 如果所有方法都失败，尝试估算token使用量
            log.info("Baidu API all extraction methods failed, attempting to estimate token usage");
            ChatTokenUsage estimatedUsage = estimateBaiduTokenUsageFromResponse(response);
            log.info("Baidu API estimated token usage: {}", estimatedUsage);
            return estimatedUsage;

        } catch (Exception e) {
            log.error("Error in manual Baidu token extraction", e);
            return new ChatTokenUsage(0, 0, 0);
        }
    }

    /**
     * 估算百度API的token使用量（当无法从API获取实际token信息时使用）
     * 
     * @param response ChatResponse对象
     * @return 估算的TokenUsage对象
     */
    private ChatTokenUsage estimateBaiduTokenUsageFromResponse(org.springframework.ai.chat.model.ChatResponse response) {
        try {
            if (response == null || response.getResults() == null || response.getResults().isEmpty()) {
                return new ChatTokenUsage(0, 0, 0);
            }
            
            // 获取输出文本
            String outputText = "";
            for (org.springframework.ai.chat.model.Generation generation : response.getResults()) {
                if (generation.getOutput() != null) {
                    outputText += generation.getOutput().getText();
                }
            }
            
            return estimateBaiduTokenUsageFromText(outputText);
            
        } catch (Exception e) {
            log.error("Error estimating Baidu token usage", e);
            return new ChatTokenUsage(0, 0, 0);
        }
    }

    /**
     * 从完整响应文本估算百度API的token使用量
     * 
     * @param outputText 完整的输出文本
     * @return 估算的TokenUsage对象
     */
    private ChatTokenUsage estimateBaiduTokenUsageFromText(String outputText) {
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
                    "Baidu API estimated tokens - output: {} chars -> {} tokens, estimated prompt: {} tokens, total: {} tokens",
                    outputText.length(), completionTokens, promptTokens, totalTokens);
            
            return new ChatTokenUsage(promptTokens, completionTokens, totalTokens);
            
        } catch (Exception e) {
            log.error("Error estimating Baidu token usage from text", e);
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

    public OpenAiChatModel getChatModel() {
        return baiduChatModel;
    }

    public Boolean isServiceHealthy() {
        if (baiduChatModel == null) {
            return false;
        }
        try {
            String response = processPromptSync("test", null, "");
            return !response.contains("不可用") && !response.equals("Baidu service is not available");
        } catch (Exception e) {
            log.error("Error checking Baidu service health", e);
            return false;
        }
    }
}