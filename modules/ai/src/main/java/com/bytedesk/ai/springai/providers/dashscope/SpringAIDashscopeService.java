/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-28 11:44:03
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-21 12:46:21
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.providers.dashscope;

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
import com.bytedesk.ai.springai.service.ChatTokenUsage;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@ConditionalOnProperty(prefix = "spring.ai.dashscope.chat", name = "enabled", havingValue = "true", matchIfMissing = false)
public class SpringAIDashscopeService extends BaseSpringAIService {

    @Autowired(required = false)
    @Qualifier("bytedeskDashscopeChatModel")
    private ChatModel bytedeskDashscopeChatModel;

    public SpringAIDashscopeService() {
        super(); // 调用基类的无参构造函数
    }

    /**
     * 根据机器人配置创建动态的DashScopeChatOptions
     * 
     * @param llm 机器人LLM配置
     * @return 根据机器人配置创建的选项
     */
    private DashScopeChatOptions createDynamicOptions(RobotLlm llm) {
        return super.createDynamicOptions(llm, robotLlm -> 
            DashScopeChatOptions.builder()
                .withModel(robotLlm.getTextModel())
                .withTemperature(robotLlm.getTemperature())
                .withTopP(robotLlm.getTopP())
                // .withEnableSearch(robotLlm.getEnableSearch())
                // .withEnableThinking(robotLlm.getEnableThinking())
                .build()
        );
    }

    @Override
    protected void processPromptWebsocket(Prompt prompt, RobotProtobuf robot, MessageProtobuf messageProtobufQuery, MessageProtobuf messageProtobufReply, String fullPromptContent) {
        // 从robot中获取llm配置
        RobotLlm llm = robot.getLlm();
        log.info("Dashscope API websocket fullPromptContent: {}", fullPromptContent);
        
        if (bytedeskDashscopeChatModel == null) {
            sendMessageWebsocket(MessageTypeEnum.ERROR, "Dashscope服务不可用", messageProtobufReply);
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
        final ChatTokenUsage[] tokenUsage = {new ChatTokenUsage(0, 0, 0)};
        final StringBuilder[] fullResponseText = {new StringBuilder()};
        
        // 使用同一个ChatModel实例，但传入不同的选项
        bytedeskDashscopeChatModel.stream(requestPrompt).subscribe(
                response -> {
                    if (response != null) {
                        log.info("Dashscope API response metadata: {}", response.getMetadata());
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
                        tokenUsage[0] = extractDashscopeTokenUsage(response);
                        success[0] = true;
                    }
                },
                error -> {
                    log.error("Dashscope API error: ", error);
                    sendMessageWebsocket(MessageTypeEnum.ERROR, "服务暂时不可用，请稍后重试", messageProtobufReply);
                    success[0] = false;
                },
                () -> {
                    log.info("Chat stream completed");
                    
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
        ChatTokenUsage tokenUsage = new ChatTokenUsage(0, 0, 0);
        
        log.info("Dashscope API sync fullPromptContent: {}", fullPromptContent);
        
        try {
            if (bytedeskDashscopeChatModel == null) {
                return "Dashscope service is not available";
            }

            try {
                // 如果有robot参数，尝试创建自定义选项
                if (robot != null && robot.getLlm() != null) {
                    // 创建自定义选项
                    DashScopeChatOptions customOptions = createDynamicOptions(robot.getLlm());
                    if (customOptions != null) {
                        // 使用自定义选项创建Prompt
                        Prompt prompt = new Prompt(message, customOptions);
                        var response = bytedeskDashscopeChatModel.call(prompt);
                        tokenUsage = extractDashscopeTokenUsage(response);
                        success = true;
                        return extractTextFromResponse(response);
                    }
                }
                
                ChatResponse response = bytedeskDashscopeChatModel.call(new Prompt(message));
                tokenUsage = extractDashscopeTokenUsage(response);
                success = true;
                return extractTextFromResponse(response);
            } catch (Exception e) {
                log.error("Dashscope API call error: ", e);
                success = false;
                return "服务暂时不可用，请稍后重试";
            }
        } catch (Exception e) {
            log.error("Dashscope API sync error: ", e);
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
        log.info("Dashscope API SSE fullPromptContent: {}", fullPromptContent);

        if (bytedeskDashscopeChatModel == null) {
            handleSseError(new RuntimeException("Dashscope service not available"), messageProtobufQuery, messageProtobufReply, emitter);
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
        final ChatTokenUsage[] tokenUsage = {new ChatTokenUsage(0, 0, 0)};
        final StringBuilder[] fullResponseText = {new StringBuilder()};

        bytedeskDashscopeChatModel.stream(requestPrompt).subscribe(
                response -> {
                    try {
                        if (response != null) {
                            List<Generation> generations = response.getResults();
                            for (Generation generation : generations) {
                                AssistantMessage assistantMessage = generation.getOutput();
                                String textContent = assistantMessage.getText();
                                log.info("Dashscope API response metadata: {}, text {}",
                                        response.getMetadata(), textContent);
                                
                                // 累积完整的响应文本用于token估算
                                if (textContent != null) {
                                    fullResponseText[0].append(textContent);
                                }
                                
                                sendStreamMessage(messageProtobufQuery, messageProtobufReply, emitter, textContent);
                            }
                            // 提取token使用情况
                            tokenUsage[0] = extractDashscopeTokenUsage(response);
                            success[0] = true;
                        }
                    } catch (Exception e) {
                        log.error("Error sending SSE event", e);
                        handleSseError(e, messageProtobufQuery, messageProtobufReply, emitter);
                        success[0] = false;
                    }
                },
                error -> {
                    log.error("Dashscope API SSE error: ", error);
                    handleSseError(error, messageProtobufQuery, messageProtobufReply, emitter);
                    success[0] = false;
                },
                () -> {
                    log.info("Dashscope API SSE complete");
                    
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
     * 专门为Dashscope API提取token使用情况
     * 由于Dashscope API返回的usage字段是EmptyUsage对象，需要特殊处理
     * 
     * @param response ChatResponse对象
     * @return TokenUsage对象
     */
    private ChatTokenUsage extractDashscopeTokenUsage(ChatResponse response) {
        try {
            if (response == null) {
                log.warn("Dashscope API response is null");
                return new ChatTokenUsage(0, 0, 0);
            }

            var metadata = response.getMetadata();
            if (metadata == null) {
                log.warn("Dashscope API response metadata is null");
                return new ChatTokenUsage(0, 0, 0);
            }

            log.info("Dashscope API token extraction - metadata: {}", metadata);

            // 直接通过getUsage()方法获取token使用情况，无需反射
            try {
                var usage = metadata.getUsage();
                if (usage != null) {
                    long promptTokens = usage.getPromptTokens();
                    long completionTokens = usage.getCompletionTokens();
                    long totalTokens = usage.getTotalTokens();
                    
                    log.info("Dashscope API direct usage extraction - prompt: {}, completion: {}, total: {}", 
                            promptTokens, completionTokens, totalTokens);
                    
                    if (totalTokens > 0) {
                        return new ChatTokenUsage(promptTokens, completionTokens, totalTokens);
                    }
                }
            } catch (Exception e) {
                log.debug("Could not get usage via getUsage() method: {}", e.getMessage());
            }

            return new ChatTokenUsage(0, 0, 0);
            
        } catch (Exception e) {
            log.error("Error in Dashscope token extraction", e);
            return new ChatTokenUsage(0, 0, 0);
        }
    }
    
    public Boolean isServiceHealthy() {
        if (bytedeskDashscopeChatModel == null) {
            return false;
        }

        try {
            String response = processPromptSync("test", null, "");
            return !response.contains("不可用") && !response.equals("Dashscope service is not available");
        } catch (Exception e) {
            log.error("Error checking Dashscope service health", e);
            return false;
        }
    }
}
