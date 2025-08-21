/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-28 11:44:03
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-21 12:46:49
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.providers.openai;

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
import com.bytedesk.ai.springai.service.ChatTokenUsage;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@ConditionalOnProperty(prefix = "spring.ai.openai.chat", name = "enabled", havingValue = "true", matchIfMissing = false)
public class SpringAIOpenaiChatService extends BaseSpringAIService {

    @Autowired(required = false)
    private OpenAiChatModel openaiChatModel;

    public SpringAIOpenaiChatService() {
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

    @Override
    protected void processPromptWebsocket(Prompt prompt, RobotProtobuf robot, MessageProtobuf messageProtobufQuery, MessageProtobuf messageProtobufReply, String fullPromptContent) {
        // 从robot中获取llm配置
        RobotLlm llm = robot.getLlm();
        log.info("OpenAI API websocket fullPromptContent: {}", fullPromptContent);
        
        if (openaiChatModel == null) {
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
        final ChatTokenUsage[] tokenUsage = {new ChatTokenUsage(0, 0, 0)};
        
        // 使用同一个ChatModel实例，但传入不同的选项
        openaiChatModel.stream(requestPrompt).subscribe(
                response -> {
                    if (response != null) {
                        log.info("Openai API response metadata: {}", response.getMetadata());
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
                    log.error("Openai API error: ", error);
                    sendMessageWebsocket(MessageTypeEnum.ERROR, "服务暂时不可用，请稍后重试", messageProtobufReply);
                    success[0] = false;
                },
                () -> {
                    log.info("Chat stream completed");
                    // 记录token使用情况
                    long responseTime = System.currentTimeMillis() - startTime;
                    String modelType = (llm != null && StringUtils.hasText(llm.getTextModel())) ? llm.getTextModel() : "gpt-3.5-turbo";
                    recordAiTokenUsage(robot, LlmConsts.OPENAI, modelType, 
                            tokenUsage[0].getPromptTokens(), tokenUsage[0].getCompletionTokens(), success[0], responseTime);
                });
    }

    @Override
    protected String processPromptSync(String message, RobotProtobuf robot, String fullPromptContent) {
        long startTime = System.currentTimeMillis();
        boolean success = false;
        ChatTokenUsage tokenUsage = new ChatTokenUsage(0, 0, 0);
        
        log.info("OpenAI API sync fullPromptContent: {}", fullPromptContent);
        
        try {
            if (openaiChatModel == null) {
                return "Openai service is not available";
            }

            try {
                // 如果有robot参数，尝试创建自定义选项
                if (robot != null && robot.getLlm() != null) {
                    // 创建自定义选项
                    OpenAiChatOptions customOptions = createDynamicOptions(robot.getLlm());
                    if (customOptions != null) {
                        // 使用自定义选项创建Prompt
                        Prompt prompt = new Prompt(message, customOptions);
                        var response = openaiChatModel.call(prompt);
                        tokenUsage = extractTokenUsage(response);
                        success = true;
                        return extractTextFromResponse(response);
                    }
                }
                
                var response = openaiChatModel.call(message);
                tokenUsage = extractTokenUsage(response);
                success = true;
                return extractTextFromResponse(response);
            } catch (Exception e) {
                log.error("Openai API call error: ", e);
                success = false;
                return "服务暂时不可用，请稍后重试";
            }
        } catch (Exception e) {
            log.error("Openai API sync error: ", e);
            success = false;
            return "服务暂时不可用，请稍后重试";
        } finally {
            // 记录token使用情况
            long responseTime = System.currentTimeMillis() - startTime;
            String modelType = (robot != null && robot.getLlm() != null && StringUtils.hasText(robot.getLlm().getTextModel())) 
                    ? robot.getLlm().getTextModel() : "gpt-3.5-turbo";
            recordAiTokenUsage(robot, LlmConsts.OPENAI, modelType, 
                    tokenUsage.getPromptTokens(), tokenUsage.getCompletionTokens(), success, responseTime);
        }
    }

    @Override
    protected void processPromptSse(Prompt prompt, RobotProtobuf robot, MessageProtobuf messageProtobufQuery, MessageProtobuf messageProtobufReply, SseEmitter emitter, String fullPromptContent) {
        RobotLlm llm = robot.getLlm();
        log.info("OpenAI API SSE fullPromptContent: {}", fullPromptContent);

        if (openaiChatModel == null) {
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
        final ChatTokenUsage[] tokenUsage = {new ChatTokenUsage(0, 0, 0)};

        openaiChatModel.stream(requestPrompt).subscribe(
                response -> {
                    try {
                        if (response != null) {
                            List<Generation> generations = response.getResults();
                            for (Generation generation : generations) {
                                AssistantMessage assistantMessage = generation.getOutput();
                                String textContent = assistantMessage.getText();
                                log.info("Openai API response metadata: {}, text {}",
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
                    log.error("Openai API SSE error: ", error);
                    handleSseError(error, messageProtobufQuery, messageProtobufReply, emitter);
                    success[0] = false;
                },
                () -> {
                    log.info("Openai API SSE complete");
                    // 发送流结束消息，包含token使用情况和prompt内容
                    sendStreamEndMessage(messageProtobufQuery, messageProtobufReply, emitter, 
                            tokenUsage[0].getPromptTokens(), tokenUsage[0].getCompletionTokens(), tokenUsage[0].getTotalTokens(), fullPromptContent, LlmConsts.OPENAI, (llm != null && StringUtils.hasText(llm.getTextModel())) ? llm.getTextModel() : "gpt-3.5-turbo");
                    // 记录token使用情况
                    long responseTime = System.currentTimeMillis() - startTime;
                    String modelType = (llm != null && StringUtils.hasText(llm.getTextModel())) ? llm.getTextModel() : "gpt-3.5-turbo";
                    recordAiTokenUsage(robot, LlmConsts.OPENAI, modelType, 
                            tokenUsage[0].getPromptTokens(), tokenUsage[0].getCompletionTokens(), success[0], responseTime);
                });
    }

    public OpenAiChatModel getChatModel() {
        return openaiChatModel;
    }
    
    public Boolean isServiceHealthy() {
        if (openaiChatModel == null) {
            return false;
        }

        try {
            String response = processPromptSync("test", null, "");
            return !response.contains("不可用") && !response.equals("Openai service is not available");
        } catch (Exception e) {
            log.error("Error checking OpenAI service health", e);
            return false;
        }
    }
}
