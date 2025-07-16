/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-28 11:44:03
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-16 11:38:40
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.providers.openrouter;

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
import com.bytedesk.core.constant.LlmConsts;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.MessageTypeEnum;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@ConditionalOnProperty(prefix = "spring.ai.openrouter.chat", name = "enabled", havingValue = "true", matchIfMissing = false)
public class SpringAIOpenrouterService extends BaseSpringAIService {

    @Autowired(required = false)
    private OpenAiChatModel openrouterChatModel;

    public SpringAIOpenrouterService() {
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
                .model(robotLlm.getModel())
                .temperature(robotLlm.getTemperature())
                .topP(robotLlm.getTopP())
                .build()
        );
    }

    @Override
    protected void processPromptWebsocket(Prompt prompt, RobotProtobuf robot, MessageProtobuf messageProtobufQuery,
            MessageProtobuf messageProtobufReply, String fullPromptContent) {
        log.info("SpringAIOpenrouterService processPromptWebsocket with full prompt content: {}", fullPromptContent);
        processPromptWebsocket(prompt, robot, messageProtobufQuery, messageProtobufReply);
    }

    @Override
    protected String processPromptSync(String message, RobotProtobuf robot, String fullPromptContent) {
        log.info("SpringAIOpenrouterService processPromptSync with full prompt content: {}", fullPromptContent);
        return processPromptSync(message, robot);
    }

    @Override
    protected void processPromptSse(Prompt prompt, RobotProtobuf robot, MessageProtobuf messageProtobufQuery,
            MessageProtobuf messageProtobufReply, SseEmitter emitter, String fullPromptContent) {
        log.info("SpringAIOpenrouterService processPromptSse with full prompt content: {}", fullPromptContent);
        processPromptSse(prompt, robot, messageProtobufQuery, messageProtobufReply, emitter);
    }

    @Override
    protected void processPromptWebsocket(Prompt prompt, RobotProtobuf robot, MessageProtobuf messageProtobufQuery, MessageProtobuf messageProtobufReply) {
        // 从robot中获取llm配置
        RobotLlm llm = robot.getLlm();
        
        if (openrouterChatModel == null) {
            sendMessageWebsocket(MessageTypeEnum.ERROR, "OpenAI服务不可用", messageProtobufReply);
            return;
        }
        
        // 如果有自定义选项，创建新的Prompt
        Prompt requestPrompt = prompt;
        OpenAiChatOptions customOptions = createDynamicOptions(llm);
        if (customOptions != null) {
            requestPrompt = new Prompt(prompt.getInstructions(), customOptions);
        }
        
        long startTime = System.currentTimeMillis();
        final boolean[] success = {false};
        final TokenUsage[] tokenUsage = {new TokenUsage(0, 0, 0)};
        
        // 使用同一个ChatModel实例，但传入不同的选项
        openrouterChatModel.stream(requestPrompt).subscribe(
                response -> {
                    if (response != null) {
                        log.info("Openrouter API response metadata: {}", response.getMetadata());
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
                    log.error("Openrouter API error: ", error);
                    sendMessageWebsocket(MessageTypeEnum.ERROR, "服务暂时不可用，请稍后重试", messageProtobufReply);
                    success[0] = false;
                },
                () -> {
                    log.info("Chat stream completed");
                    // 记录token使用情况
                    long responseTime = System.currentTimeMillis() - startTime;
                    String modelType = (llm != null && StringUtils.hasText(llm.getModel())) ? llm.getModel() : "openrouter-chat";
                    recordAiTokenUsage(robot, LlmConsts.OPENROUTER, modelType, 
                            tokenUsage[0].getPromptTokens(), tokenUsage[0].getCompletionTokens(), success[0], responseTime);
                });
    }

    // @Override
    // protected String generateFaqPairs(String prompt) {
    //     return openrouterChatModel != null ? openrouterChatModel.call(prompt) : "";
    // }

    @Override
    protected String processPromptSync(String message, RobotProtobuf robot) {
        long startTime = System.currentTimeMillis();
        boolean success = false;
        TokenUsage tokenUsage = new TokenUsage(0, 0, 0);
        
        try {
            if (openrouterChatModel == null) {
                return "Openrouter service is not available";
            }
            
            // 如果有robot参数，尝试创建自定义选项
            if (robot != null && robot.getLlm() != null) {
                // 创建自定义选项
                OpenAiChatOptions customOptions = createDynamicOptions(robot.getLlm());
                if (customOptions != null) {
                    // 使用自定义选项创建Prompt
                    Prompt prompt = new Prompt(message, customOptions);
                    var response = openrouterChatModel.call(prompt);
                    tokenUsage = extractTokenUsage(response);
                    success = true;
                    return extractTextFromResponse(response);
                }
            }
            
            var response = openrouterChatModel.call(message);
            tokenUsage = extractTokenUsage(response);
            success = true;
            return extractTextFromResponse(response);
        } catch (Exception e) {
            log.error("Openrouter API sync error: ", e);
            success = false;
            return "服务暂时不可用，请稍后重试";
        } finally {
            // 记录token使用情况
            long responseTime = System.currentTimeMillis() - startTime;
            String modelType = (robot != null && robot.getLlm() != null && StringUtils.hasText(robot.getLlm().getModel())) 
                    ? robot.getLlm().getModel() : "openrouter-chat";
            recordAiTokenUsage(robot, LlmConsts.OPENROUTER, modelType, 
                    tokenUsage.getPromptTokens(), tokenUsage.getCompletionTokens(), success, responseTime);
        }
    }

    @Override
    protected void processPromptSse(Prompt prompt, RobotProtobuf robot, MessageProtobuf messageProtobufQuery, MessageProtobuf messageProtobufReply, SseEmitter emitter) {
        RobotLlm llm = robot.getLlm();

        if (openrouterChatModel == null) {
            handleSseError(new RuntimeException("OpenAI service not available"), messageProtobufQuery, messageProtobufReply, emitter);
            return;
        }

        // 发送起始消息
        sendStreamStartMessage(messageProtobufReply, emitter, "正在思考中...");

        Prompt requestPrompt = prompt;
        OpenAiChatOptions customOptions = createDynamicOptions(llm);
        if (customOptions != null) {
            requestPrompt = new Prompt(prompt.getInstructions(), customOptions);
        }

        long startTime = System.currentTimeMillis();
        final boolean[] success = {false};
        final TokenUsage[] tokenUsage = {new TokenUsage(0, 0, 0)};

        openrouterChatModel.stream(requestPrompt).subscribe(
                response -> {
                    try {
                        if (response != null) {
                            List<Generation> generations = response.getResults();
                            for (Generation generation : generations) {
                                AssistantMessage assistantMessage = generation.getOutput();
                                String textContent = assistantMessage.getText();
                                log.info("Openrouter API response metadata: {}, text {}",
                                        response.getMetadata(), textContent);
                                
                                sendStreamMessage(messageProtobufQuery, messageProtobufReply, emitter, textContent);
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
                    log.error("Openrouter API SSE error: ", error);
                    handleSseError(error, messageProtobufQuery, messageProtobufReply, emitter);
                    success[0] = false;
                },
                () -> {
                    log.info("OpenRouter API SSE complete");
                    // 发送流结束消息，包含token使用情况
                    sendStreamEndMessage(messageProtobufQuery, messageProtobufReply, emitter, 
                            tokenUsage[0].getPromptTokens(), tokenUsage[0].getCompletionTokens(), tokenUsage[0].getTotalTokens());
                    // 记录token使用情况
                    long responseTime = System.currentTimeMillis() - startTime;
                    String modelType = (llm != null && StringUtils.hasText(llm.getModel())) ? llm.getModel() : "openrouter-chat";
                    recordAiTokenUsage(robot, LlmConsts.OPENROUTER, modelType, 
                            tokenUsage[0].getPromptTokens(), tokenUsage[0].getCompletionTokens(), success[0], responseTime);
                });
    }

    public OpenAiChatModel getChatModel() {
        return openrouterChatModel;
    }
    
    public Boolean isServiceHealthy() {
        if (openrouterChatModel == null) {
            return false;
        }

        try {
            String response = processPromptSync("test", null);
            return !response.contains("不可用") && !response.equals("Openrouter service is not available");
        } catch (Exception e) {
            log.error("Error checking OpenAI service health", e);
            return false;
        }
    }
}
