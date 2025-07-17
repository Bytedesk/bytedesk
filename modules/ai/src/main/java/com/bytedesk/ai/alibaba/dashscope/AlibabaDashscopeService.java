/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-07-18 07:05:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-18 07:12:08
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.alibaba.dashscope;

import java.util.List;

import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import com.bytedesk.ai.robot.RobotLlm;
import com.bytedesk.ai.robot.RobotProtobuf;
import com.bytedesk.ai.springai.service.BaseSpringAIService;
import com.bytedesk.core.constant.LlmConsts;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.MessageTypeEnum;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@ConditionalOnProperty(prefix = "spring.ai.dashscope.chat", name = "enabled", havingValue = "true", matchIfMissing = false)
public class AlibabaDashscopeService extends BaseSpringAIService {

    @Autowired(required = false)
    @Qualifier("alibabaDashscopeChatModel")
    private ChatModel alibabaDashscopeChatModel;

    public AlibabaDashscopeService() {
        super(); // 调用基类的无参构造函数
    }

    /**
     * 根据机器人配置创建动态的DashScopeChatOptions
     * 
     * @param llm 机器人LLM配置
     * @return 根据机器人配置创建的选项
     */
    private DashScopeChatOptions createDynamicOptions(RobotLlm llm) {
        if (llm == null || !StringUtils.hasText(llm.getTextModel())) {
            return null;
        }
        
        try {
            return DashScopeChatOptions.builder()
                .withModel(llm.getTextModel())
                .withTemperature(llm.getTemperature())
                .withTopP(llm.getTopP())
                .build();
        } catch (Exception e) {
            log.error("Error creating dynamic DashScope options for model {}", llm.getTextModel(), e);
            return null;
        }
    }

    @Override
    protected void processPromptWebsocket(Prompt prompt, RobotProtobuf robot, MessageProtobuf messageProtobufQuery, MessageProtobuf messageProtobufReply, String fullPromptContent) {
        // 从robot中获取llm配置
        RobotLlm llm = robot.getLlm();
        log.info("Alibaba Dashscope API websocket fullPromptContent: {}", fullPromptContent);
        
        if (alibabaDashscopeChatModel == null) {
            sendMessageWebsocket(MessageTypeEnum.ERROR, "Alibaba Dashscope服务不可用", messageProtobufReply);
            return;
        }
        
        // 如果有自定义选项，创建新的Prompt
        Prompt requestPrompt = prompt;
        DashScopeChatOptions customOptions = createDynamicOptions(llm);
        if (customOptions != null) {
            requestPrompt = new Prompt(prompt.getInstructions(), customOptions);
        }
        
        long startTime = System.currentTimeMillis();
        final boolean[] success = {false};
        final TokenUsage[] tokenUsage = {new TokenUsage(0, 0, 0)};
        final StringBuilder[] fullResponseText = {new StringBuilder()};
        
        // 使用官方的DashScopeChatModel
        alibabaDashscopeChatModel.stream(requestPrompt).subscribe(
                response -> {
                    if (response != null) {
                        log.info("Alibaba Dashscope API response metadata: {}", response.getMetadata());
                        List<Generation> generations = response.getResults();
                        for (Generation generation : generations) {
                            AssistantMessage assistantMessage = generation.getOutput();
                            String textContent = assistantMessage.getText();

                            // 累积完整的响应文本用于token估算
                            if (textContent != null) {
                                fullResponseText[0].append(textContent);
                            }

                            sendMessageWebsocket(MessageTypeEnum.STREAM, textContent, messageProtobufReply);
                        }
                        // 提取token使用情况
                        tokenUsage[0] = extractAlibabaDashscopeTokenUsage(response);
                        success[0] = true;
                    }
                },
                error -> {
                    log.error("Alibaba Dashscope API error: ", error);
                    sendMessageWebsocket(MessageTypeEnum.ERROR, "服务暂时不可用，请稍后重试", messageProtobufReply);
                    success[0] = false;
                },
                () -> {
                    log.info("Alibaba Dashscope Chat stream completed");
                    
                    // 如果token提取失败，使用累积的完整响应文本来估算token
                    if (tokenUsage[0].getTotalTokens() == 0 && fullResponseText[0].length() > 0) {
                        log.info("Alibaba Dashscope API using accumulated response text for token estimation: {}", fullResponseText[0].toString());
                        TokenUsage estimatedUsage = estimateAlibabaDashscopeTokenUsageFromText(fullResponseText[0].toString(), fullPromptContent);
                        tokenUsage[0] = estimatedUsage;
                        log.info("Alibaba Dashscope API final estimated token usage: {}", estimatedUsage);
                    }
                    
                    // 记录token使用情况
                    long responseTime = System.currentTimeMillis() - startTime;
                    String modelType = (llm != null && StringUtils.hasText(llm.getTextModel())) ? llm.getTextModel() : "qwen-turbo";
                    recordAiTokenUsage(robot, LlmConsts.DASHSCOPE, modelType, 
                            tokenUsage[0].getPromptTokens(), tokenUsage[0].getCompletionTokens(), success[0], responseTime);
                });
    }

    @Override
    protected String processPromptSync(String message, RobotProtobuf robot, String fullPromptContent) {
        long startTime = System.currentTimeMillis();
        boolean success = false;
        TokenUsage tokenUsage = new TokenUsage(0, 0, 0);
        
        log.info("Alibaba Dashscope API sync fullPromptContent: {}", fullPromptContent);
        
        try {
            if (alibabaDashscopeChatModel == null) {
                return "Alibaba Dashscope service is not available";
            }

            try {
                // 如果有robot参数，尝试创建自定义选项
                if (robot != null && robot.getLlm() != null) {
                    // 创建自定义选项
                    DashScopeChatOptions customOptions = createDynamicOptions(robot.getLlm());
                    if (customOptions != null) {
                        // 使用自定义选项创建Prompt
                        Prompt prompt = new Prompt(message, customOptions);
                        ChatResponse response = alibabaDashscopeChatModel.call(prompt);
                        tokenUsage = extractAlibabaDashscopeTokenUsage(response);
                        success = true;
                        return extractTextFromResponse(response);
                    }
                }
                
                ChatResponse response = alibabaDashscopeChatModel.call(new Prompt(message));
                tokenUsage = extractAlibabaDashscopeTokenUsage(response);
                success = true;
                return extractTextFromResponse(response);
            } catch (Exception e) {
                log.error("Alibaba Dashscope API call error: ", e);
                success = false;
                return "服务暂时不可用，请稍后重试";
            }
        } catch (Exception e) {
            log.error("Alibaba Dashscope API sync error: ", e);
            success = false;
            return "服务暂时不可用，请稍后重试";
        } finally {
            // 记录token使用情况
            long responseTime = System.currentTimeMillis() - startTime;
            String modelType = (robot != null && robot.getLlm() != null && StringUtils.hasText(robot.getLlm().getTextModel())) 
                    ? robot.getLlm().getTextModel() : "qwen-turbo";
            recordAiTokenUsage(robot, LlmConsts.DASHSCOPE, modelType, 
                    tokenUsage.getPromptTokens(), tokenUsage.getCompletionTokens(), success, responseTime);
        }
    }

    @Override
    protected void processPromptSse(Prompt prompt, RobotProtobuf robot, MessageProtobuf messageProtobufQuery, MessageProtobuf messageProtobufReply, SseEmitter emitter, String fullPromptContent) {
        // 从robot中获取llm配置
        RobotLlm llm = robot.getLlm();
        log.info("Alibaba Dashscope API SSE fullPromptContent: {}", fullPromptContent);

        if (alibabaDashscopeChatModel == null) {
            handleSseError(new RuntimeException("Alibaba Dashscope service not available"), messageProtobufQuery, messageProtobufReply, emitter);
            return;
        }

        // 发送起始消息
        sendStreamStartMessage(messageProtobufReply, emitter, "正在思考中...");

        // 如果有自定义选项，创建新的Prompt
        Prompt requestPrompt = prompt;
        DashScopeChatOptions customOptions = createDynamicOptions(llm);
        if (customOptions != null) {
            requestPrompt = new Prompt(prompt.getInstructions(), customOptions);
        }

        long startTime = System.currentTimeMillis();
        final boolean[] success = {false};
        final TokenUsage[] tokenUsage = {new TokenUsage(0, 0, 0)};
        final StringBuilder[] fullResponseText = {new StringBuilder()};

        alibabaDashscopeChatModel.stream(requestPrompt).subscribe(
                response -> {
                    try {
                        if (response != null) {
                            List<Generation> generations = response.getResults();
                            for (Generation generation : generations) {
                                AssistantMessage assistantMessage = generation.getOutput();
                                String textContent = assistantMessage.getText();
                                log.info("Alibaba Dashscope API response metadata: {}, text {}",
                                        response.getMetadata(), textContent);
                                
                                // 累积完整的响应文本用于token估算
                                if (textContent != null) {
                                    fullResponseText[0].append(textContent);
                                }
                                
                                sendStreamMessage(messageProtobufQuery, messageProtobufReply, emitter, textContent);
                            }
                            // 提取token使用情况
                            tokenUsage[0] = extractAlibabaDashscopeTokenUsage(response);
                            success[0] = true;
                        }
                    } catch (Exception e) {
                        log.error("Error sending SSE event", e);
                        handleSseError(e, messageProtobufQuery, messageProtobufReply, emitter);
                        success[0] = false;
                    }
                },
                error -> {
                    log.error("Alibaba Dashscope API SSE error: ", error);
                    handleSseError(error, messageProtobufQuery, messageProtobufReply, emitter);
                    success[0] = false;
                },
                () -> {
                    log.info("Alibaba Dashscope API SSE complete");
                    
                    // 如果token提取失败，使用累积的完整响应文本来估算token
                    if (tokenUsage[0].getTotalTokens() == 0 && fullResponseText[0].length() > 0) {
                        log.info("Alibaba Dashscope API using accumulated response text for token estimation: {}", fullResponseText[0].toString());
                        TokenUsage estimatedUsage = estimateAlibabaDashscopeTokenUsageFromText(fullResponseText[0].toString(), fullPromptContent);
                        tokenUsage[0] = estimatedUsage;
                        log.info("Alibaba Dashscope API final estimated token usage: {}", estimatedUsage);
                    }
                    
                    // 发送流结束消息，包含token使用情况和prompt内容
                    sendStreamEndMessage(messageProtobufQuery, messageProtobufReply, emitter, 
                            tokenUsage[0].getPromptTokens(), tokenUsage[0].getCompletionTokens(), tokenUsage[0].getTotalTokens(), fullPromptContent, LlmConsts.DASHSCOPE, (llm != null && StringUtils.hasText(llm.getTextModel())) ? llm.getTextModel() : "qwen-turbo");
                    // 记录token使用情况
                    long responseTime = System.currentTimeMillis() - startTime;
                    String modelType = (llm != null && StringUtils.hasText(llm.getTextModel())) ? llm.getTextModel() : "qwen-turbo";
                    recordAiTokenUsage(robot, LlmConsts.DASHSCOPE, modelType, 
                            tokenUsage[0].getPromptTokens(), tokenUsage[0].getCompletionTokens(), success[0], responseTime);
                });
    }

    /**
     * 从文本估算token使用量（包含prompt和completion）
     * 
     * @param responseText 响应文本
     * @param promptContent prompt内容
     * @return TokenUsage对象
     */
    private TokenUsage estimateAlibabaDashscopeTokenUsageFromText(String responseText, String promptContent) {
        try {
            if (responseText == null || responseText.isEmpty()) {
                return new TokenUsage(0, 0, 0);
            }

            log.info("Alibaba Dashscope API estimating token usage from text - response length: {}, prompt length: {}", 
                    responseText.length(), promptContent != null ? promptContent.length() : 0);

            // 估算completion tokens
            long completionTokens = estimateTokensFromText(responseText);
            
            // 估算prompt tokens
            long promptTokens = 0;
            if (promptContent != null && !promptContent.isEmpty()) {
                promptTokens = estimateTokensFromText(promptContent);
            }
            
            // 如果没有prompt内容，使用默认比例
            if (promptTokens == 0) {
                promptTokens = (long) (completionTokens * 0.3);
            }
            
            long totalTokens = promptTokens + completionTokens;

            log.info("Alibaba Dashscope API estimated token usage from text - prompt: {}, completion: {}, total: {}", 
                    promptTokens, completionTokens, totalTokens);

            return new TokenUsage(promptTokens, completionTokens, totalTokens);

        } catch (Exception e) {
            log.error("Error estimating Alibaba Dashscope token usage from text", e);
            return new TokenUsage(0, 0, 0);
        }
    }

    /**
     * 从文本估算token数量
     * 
     * @param text 输入文本
     * @return 估算的token数量
     */
    private long estimateTokensFromText(String text) {
        if (text == null || text.isEmpty()) {
            return 0;
        }

        long tokenCount = 0;
        
        // 简单的估算方法：
        // 1. 中文字符：每个字符约等于1个token
        // 2. 英文单词：每个单词约等于1.3个token
        // 3. 数字：每个数字约等于0.5个token
        // 4. 标点符号：每个约等于0.5个token
        
        // 计算中文字符数量
        long chineseChars = text.chars().filter(ch -> Character.UnicodeScript.of(ch) == Character.UnicodeScript.HAN).count();
        
        // 计算英文单词数量
        String[] words = text.split("\\s+");
        long englishWords = 0;
        for (String word : words) {
            if (word.matches("^[a-zA-Z]+$")) {
                englishWords++;
            }
        }
        
        // 计算数字数量
        long digits = text.chars().filter(Character::isDigit).count();
        
        // 计算标点符号数量
        long punctuation = text.chars().filter(ch -> !Character.isLetterOrDigit(ch) && !Character.isWhitespace(ch)).count();
        
        tokenCount = chineseChars + (long)(englishWords * 1.3) + (long)(digits * 0.5) + (long)(punctuation * 0.5);
        
        // 确保至少返回1个token
        return Math.max(1, tokenCount);
    }

    /**
     * 专门为Alibaba Dashscope API提取token使用情况
     * 使用官方DashScopeChatModel，token信息应该更准确
     * 
     * @param response ChatResponse对象
     * @return TokenUsage对象
     */
    private TokenUsage extractAlibabaDashscopeTokenUsage(ChatResponse response) {
        try {
            if (response == null) {
                log.warn("Alibaba Dashscope API response is null");
                return new TokenUsage(0, 0, 0);
            }

            var metadata = response.getMetadata();
            if (metadata == null) {
                log.warn("Alibaba Dashscope API response metadata is null");
                return new TokenUsage(0, 0, 0);
            }

            log.info("Alibaba Dashscope API token extraction - metadata: {}", metadata);

            // 尝试从metadata中直接获取usage对象
            Object usage = null;

            // 方法1: 尝试通过反射获取usage字段
            try {
                java.lang.reflect.Field usageField = metadata.getClass().getDeclaredField("usage");
                usageField.setAccessible(true);
                usage = usageField.get(metadata);
                log.info("Alibaba Dashscope API found usage field via reflection: {}", usage);
            } catch (Exception e) {
                log.debug("Could not get usage field via reflection: {}", e.getMessage());
            }

            // 方法2: 如果反射失败，尝试从metadata的toString()中解析
            if (usage == null || usage.toString().contains("EmptyUsage")) {
                log.info("Alibaba Dashscope API usage field is EmptyUsage, trying to parse from metadata string");
                String metadataStr = metadata.toString();
                
                // 查找usage信息，格式通常是: DefaultUsage{promptTokens=16, completionTokens=14, totalTokens=30}
                if (metadataStr.contains("DefaultUsage{") || metadataStr.contains("usage:")) {
                    // 提取promptTokens
                    int promptStart = metadataStr.indexOf("promptTokens=");
                    int promptEnd = metadataStr.indexOf(",", promptStart);
                    if (promptEnd == -1) promptEnd = metadataStr.indexOf("}", promptStart);
                    
                    // 提取completionTokens
                    int completionStart = metadataStr.indexOf("completionTokens=");
                    int completionEnd = metadataStr.indexOf(",", completionStart);
                    if (completionEnd == -1) completionEnd = metadataStr.indexOf("}", completionStart);
                    
                    // 提取totalTokens
                    int totalStart = metadataStr.indexOf("totalTokens=");
                    int totalEnd = metadataStr.indexOf("}", totalStart);
                    
                    long promptTokens = 0, completionTokens = 0, totalTokens = 0;
                    
                    if (promptStart > 0 && promptEnd > promptStart) {
                        try {
                            promptTokens = Long.parseLong(metadataStr.substring(promptStart + 13, promptEnd));
                        } catch (NumberFormatException e) {
                            log.warn("Could not parse promptTokens from: {}", metadataStr.substring(promptStart + 13, promptEnd));
                        }
                    }
                    
                    if (completionStart > 0 && completionEnd > completionStart) {
                        try {
                            completionTokens = Long.parseLong(metadataStr.substring(completionStart + 17, completionEnd));
                        } catch (NumberFormatException e) {
                            log.warn("Could not parse completionTokens from: {}", metadataStr.substring(completionStart + 17, completionEnd));
                        }
                    }
                    
                    if (totalStart > 0 && totalEnd > totalStart) {
                        try {
                            totalTokens = Long.parseLong(metadataStr.substring(totalStart + 12, totalEnd));
                        } catch (NumberFormatException e) {
                            log.warn("Could not parse totalTokens from: {}", metadataStr.substring(totalStart + 12, totalEnd));
                        }
                    }
                    
                    log.info("Alibaba Dashscope API parsed from string - prompt: {}, completion: {}, total: {}", 
                            promptTokens, completionTokens, totalTokens);
                    
                    if (totalTokens > 0) {
                        return new TokenUsage(promptTokens, completionTokens, totalTokens);
                    }
                }
            }

            // 如果usage字段不存在，尝试其他可能的字段
            log.info("Alibaba Dashscope API usage field not found, checking other fields");
            for (String key : metadata.keySet()) {
                Object value = metadata.get(key);
                log.info("Alibaba Dashscope API metadata [{}]: {} (class: {})",
                        key, value, value != null ? value.getClass().getName() : "null");
            }

            // 方法3: 如果手动提取失败，尝试使用原始的extractTokenUsage方法作为后备
            log.info("Alibaba Dashscope API manual extraction failed, trying original extractTokenUsage method");
            TokenUsage fallbackUsage = extractTokenUsage(response);
            if (fallbackUsage.getTotalTokens() > 0) {
                log.info("Alibaba Dashscope API fallback extraction successful: {}", fallbackUsage);
                return fallbackUsage;
            }

            // 方法4: 如果所有方法都失败，尝试估算token使用量
            log.info("Alibaba Dashscope API all extraction methods failed, attempting to estimate token usage");
            TokenUsage estimatedUsage = estimateAlibabaDashscopeTokenUsageFromResponse(response);
            log.info("Alibaba Dashscope API estimated token usage: {}", estimatedUsage);
            return estimatedUsage;

        } catch (Exception e) {
            log.error("Error in manual Alibaba Dashscope token extraction", e);
            return new TokenUsage(0, 0, 0);
        }
    }

    /**
     * 估算Alibaba Dashscope API的token使用量（当无法从API获取实际token信息时使用）
     * 
     * @param response ChatResponse对象
     * @return 估算的TokenUsage对象
     */
    private TokenUsage estimateAlibabaDashscopeTokenUsageFromResponse(ChatResponse response) {
        try {
            if (response == null || response.getResults() == null || response.getResults().isEmpty()) {
                return new TokenUsage(0, 0, 0);
            }

            // 计算响应文本的总长度
            StringBuilder fullResponseText = new StringBuilder();
            for (Generation generation : response.getResults()) {
                if (generation.getOutput() != null && generation.getOutput().getText() != null) {
                    fullResponseText.append(generation.getOutput().getText());
                }
            }

            String responseText = fullResponseText.toString();
            log.info("Alibaba Dashscope API estimating token usage from response text length: {}", responseText.length());

            // 更准确的token估算：
            // 1. 中文：每个字符约等于1个token
            // 2. 英文：每个单词约等于1.3个token
            // 3. 标点符号：每个约等于0.5个token
            
            long completionTokens = estimateTokensFromText(responseText);
            
            // 假设prompt占30%，completion占70%
            long totalTokens = (long) (completionTokens / 0.7);
            long promptTokens = totalTokens - completionTokens;

            log.info("Alibaba Dashscope API estimated token usage - prompt: {}, completion: {}, total: {}", 
                    promptTokens, completionTokens, totalTokens);

            return new TokenUsage(promptTokens, completionTokens, totalTokens);

        } catch (Exception e) {
            log.error("Error estimating Alibaba Dashscope token usage", e);
            return new TokenUsage(0, 0, 0);
        }
    }

    public ChatModel getChatModel() {
        return alibabaDashscopeChatModel;
    }
    
    public Boolean isServiceHealthy() {
        if (alibabaDashscopeChatModel == null) {
            return false;
        }

        try {
            String response = processPromptSync("test", null, "");
            return !response.contains("不可用") && !response.equals("Alibaba Dashscope service is not available");
        } catch (Exception e) {
            log.error("Error checking Alibaba Dashscope service health", e);
            return false;
        }
    }
} 