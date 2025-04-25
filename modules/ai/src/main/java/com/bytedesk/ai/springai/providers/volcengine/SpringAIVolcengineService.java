/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-28 11:44:03
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-25 12:29:49
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.providers.volcengine;

import java.util.List;
import java.util.Optional;

import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.util.StringUtils;

import com.bytedesk.ai.robot.RobotLlm;
import com.bytedesk.ai.robot.RobotProtobuf;
import com.bytedesk.ai.springai.service.BaseSpringAIService;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.MessageTypeEnum;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@ConditionalOnProperty(name = "spring.ai.volcengine.chat.enabled", havingValue = "true", matchIfMissing = false)
public class SpringAIVolcengineService extends BaseSpringAIService {

    @Autowired(required = false)
    private Optional<OpenAiChatModel> volcengineChatModel;

    public SpringAIVolcengineService() {
        super(); // 调用基类的无参构造函数
    }
    
    /**
     * 根据机器人配置创建动态的OpenAiChatOptions
     * 
     * @param llm 机器人LLM配置
     * @return 根据机器人配置创建的选项
     */
    private OpenAiChatOptions createDynamicOptions(RobotLlm llm) {
        if (llm == null || !StringUtils.hasText(llm.getModel())) {
            return null;
        }
        
        try {
            // 创建自定义的选项对象
            return OpenAiChatOptions.builder()
                .model(llm.getModel())
                .temperature(llm.getTemperature())
                .topP(llm.getTopP())
                .build();
        } catch (Exception e) {
            log.error("Error creating dynamic options for model {}", llm.getModel(), e);
            return null;
        }
    }

    @Override
    protected void processPrompt(Prompt prompt, RobotProtobuf robot, MessageProtobuf messageProtobufQuery, MessageProtobuf messageProtobufReply) {
        // 从robot中获取llm配置
        RobotLlm llm = robot.getLlm();
        
        // 使用自定义选项处理请求
        processPromptStreamWithCustomOptions(prompt, messageProtobufReply, llm);
    }

    /**
     * 处理带有自定义选项的流式请求
     * 
     * @param prompt 提示
     * @param messageProtobuf 消息对象
     * @param llm 机器人LLM配置
     */
    private void processPromptStreamWithCustomOptions(Prompt prompt, MessageProtobuf messageProtobuf, RobotLlm llm) {
        if (!volcengineChatModel.isPresent()) {
            messageProtobuf.setType(MessageTypeEnum.ERROR);
            messageProtobuf.setContent("火山引擎服务不可用");
            messageSendService.sendProtobufMessage(messageProtobuf);
            return;
        }
        
        // 如果有自定义选项，创建新的Prompt
        Prompt requestPrompt = prompt;
        OpenAiChatOptions customOptions = createDynamicOptions(llm);
        if (customOptions != null) {
            requestPrompt = new Prompt(prompt.getInstructions(), customOptions);
        }
        
        // 使用同一个ChatModel实例，但传入不同的选项
        volcengineChatModel.get().stream(requestPrompt).subscribe(
                response -> {
                    if (response != null) {
                        log.info("Volcengine API response metadata: {}", response.getMetadata());
                        List<Generation> generations = response.getResults();
                        for (Generation generation : generations) {
                            AssistantMessage assistantMessage = generation.getOutput();
                            String textContent = assistantMessage.getText();

                            messageProtobuf.setType(MessageTypeEnum.STREAM);
                            messageProtobuf.setContent(textContent);
                            messageSendService.sendProtobufMessage(messageProtobuf);
                        }
                    }
                },
                error -> {
                    log.error("Volcengine API error: ", error);
                    messageProtobuf.setType(MessageTypeEnum.ERROR);
                    messageProtobuf.setContent("服务暂时不可用，请稍后重试");
                    messageSendService.sendProtobufMessage(messageProtobuf);
                },
                () -> {
                    log.info("Chat stream completed");
                });
    }

    @Override
    protected String generateFaqPairs(String prompt) {
        return volcengineChatModel.map(model -> model.call(prompt)).orElse("");
    }

    @Override
    protected String processPromptSync(String message) {
        try {
            return volcengineChatModel.map(model -> model.call(message))
                    .orElse("Volcengine service is not available");
        } catch (Exception e) {
            log.error("Volcengine API sync error: ", e);
            return "服务暂时不可用，请稍后重试";
        }
    }

    @Override
    protected void processPromptSSE(Prompt prompt, RobotProtobuf robot, MessageProtobuf messageProtobufQuery, MessageProtobuf messageProtobufReply, SseEmitter emitter) {
        // 从robot中获取llm配置
        RobotLlm llm = robot.getLlm();

        if (!volcengineChatModel.isPresent()) {
            handleSseError(new RuntimeException("Volcengine service not available"), messageProtobufQuery, messageProtobufReply, emitter);
            return;
        }

        // 发送起始消息
        sendStreamStartMessage(messageProtobufReply, emitter, "正在思考中...");

        // 如果有自定义选项，创建新的Prompt
        Prompt requestPrompt = prompt;
        OpenAiChatOptions customOptions = createDynamicOptions(llm);
        if (customOptions != null) {
            requestPrompt = new Prompt(prompt.getInstructions(), customOptions);
        }

        volcengineChatModel.get().stream(requestPrompt).subscribe(
                response -> {
                    try {
                        if (response != null) {
                            List<Generation> generations = response.getResults();
                            for (Generation generation : generations) {
                                AssistantMessage assistantMessage = generation.getOutput();
                                String textContent = assistantMessage.getText();
                                log.info("Volcengine API response metadata: {}, text {}",
                                        response.getMetadata(), textContent);
                                
                                sendStreamMessage(messageProtobufQuery, messageProtobufReply, emitter, textContent);
                            }
                        }
                    } catch (Exception e) {
                        log.error("Error sending SSE event", e);
                        handleSseError(e, messageProtobufQuery, messageProtobufReply, emitter);
                    }
                },
                error -> {
                    log.error("Volcengine API SSE error: ", error);
                    handleSseError(error, messageProtobufQuery, messageProtobufReply, emitter);
                },
                () -> {
                    log.info("Volcengine API SSE complete");
                    sendStreamEndMessage(messageProtobufQuery, messageProtobufReply, emitter);
                });
    }

    public Optional<OpenAiChatModel> getVolcengineChatModel() {
        return volcengineChatModel;
    }
    
    public boolean isServiceHealthy() {
        if (!volcengineChatModel.isPresent()) {
            return false;
        }

        try {
            String response = processPromptSync("test");
            return !response.contains("不可用") && !response.equals("Volcengine service is not available");
        } catch (Exception e) {
            log.error("Error checking Volcengine service health", e);
            return false;
        }
    }
}
