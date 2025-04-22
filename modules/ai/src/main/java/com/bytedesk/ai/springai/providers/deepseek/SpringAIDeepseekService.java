/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-28 11:44:03
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-14 22:01:04
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.providers.deepseek;

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
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.bytedesk.ai.robot.RobotLlm;
import com.bytedesk.ai.springai.service.BaseSpringAIService;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.MessageTypeEnum;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@ConditionalOnProperty(name = "spring.ai.deepseek.chat.enabled", havingValue = "true", matchIfMissing = false)
public class SpringAIDeepseekService extends BaseSpringAIService {

    @Autowired(required = false)
    private Optional<OpenAiChatModel> deepseekChatModel;

    public SpringAIDeepseekService() {
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
    protected void processPrompt(Prompt prompt, MessageProtobuf messageProtobuf) {
        // 从messageProtobuf的extra字段中获取llm配置
        RobotLlm llm = null;
        try {
            // 从消息中提取LLM配置
            if (messageProtobuf.getExtra() != null) {
                // 根据实际情况从消息中获取LLM配置
                // 此处实现需根据实际应用逻辑调整
            }
        } catch (Exception e) {
            log.warn("Failed to extract LLM config from message, using default model", e);
        }
        
        // 使用自定义选项处理请求
        processPromptStreamWithCustomOptions(prompt, messageProtobuf, llm);
    }

    /**
     * 处理带有自定义选项的流式请求
     * 
     * @param prompt 提示
     * @param messageProtobuf 消息对象
     * @param llm 机器人LLM配置
     */
    private void processPromptStreamWithCustomOptions(Prompt prompt, MessageProtobuf messageProtobuf, RobotLlm llm) {
        if (!deepseekChatModel.isPresent()) {
            messageProtobuf.setType(MessageTypeEnum.ERROR);
            messageProtobuf.setContent("Deepseek服务不可用");
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
        deepseekChatModel.get().stream(requestPrompt).subscribe(
                response -> {
                    if (response != null) {
                        log.info("Deepseek API response metadata: {}", response.getMetadata());
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
                    log.error("Deepseek API error: ", error);
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
        return deepseekChatModel.map(model -> model.call(prompt)).orElse("");
    }

    @Override
    protected String processPromptSync(String message) {
        try {
            return deepseekChatModel.map(model -> model.call(message))
                    .orElse("Deepseek service is not available");
        } catch (Exception e) {
            log.error("Deepseek API sync error: ", e);
            return "服务暂时不可用，请稍后重试";
        }
    }

    @Override
    protected void processPromptSSE(Prompt prompt, MessageProtobuf messageProtobufQuery, MessageProtobuf messageProtobufReply, SseEmitter emitter) {
        RobotLlm llm = null;
        try {
            if (messageProtobufQuery.getExtra() != null) {
                // 从extra中解析RobotLlm配置
            }
        } catch (Exception e) {
            log.warn("Failed to extract robot info, using default model", e);
        }

        if (!deepseekChatModel.isPresent()) {
            handleSseError(new RuntimeException("Deepseek service not available"), messageProtobufQuery, messageProtobufReply, emitter);
            return;
        }

        Prompt requestPrompt = prompt;
        OpenAiChatOptions customOptions = createDynamicOptions(llm);
        if (customOptions != null) {
            requestPrompt = new Prompt(prompt.getInstructions(), customOptions);
        }

        deepseekChatModel.get().stream(requestPrompt).subscribe(
                response -> {
                    try {
                        if (response != null) {
                            List<Generation> generations = response.getResults();
                            for (Generation generation : generations) {
                                AssistantMessage assistantMessage = generation.getOutput();
                                String textContent = assistantMessage.getText();
                                log.info("Deepseek API response metadata: {}, text {}",
                                        response.getMetadata(), textContent);
                                if (StringUtils.hasLength(textContent)) {
                                    messageProtobufReply.setContent(textContent);
                                    messageProtobufReply.setType(MessageTypeEnum.STREAM);
                                    persistMessage(messageProtobufQuery, messageProtobufReply);
                                    String messageJson = messageProtobufReply.toJson();
                                    emitter.send(SseEmitter.event()
                                            .data(messageJson)
                                            .id(messageProtobufReply.getUid())
                                            .name("message"));
                                }
                            }
                        }
                    } catch (Exception e) {
                        log.error("Error sending SSE event", e);
                        handleSseError(e, messageProtobufQuery, messageProtobufReply, emitter);
                    }
                },
                error -> {
                    log.error("Deepseek API SSE error: ", error);
                    handleSseError(error, messageProtobufQuery, messageProtobufReply, emitter);
                },
                () -> {
                    log.info("Deepseek API SSE complete");
                    try {
                        messageProtobufReply.setType(MessageTypeEnum.STREAM_END);
                        messageProtobufReply.setContent("");
                        persistMessage(messageProtobufQuery, messageProtobufReply);
                        String messageJson = messageProtobufReply.toJson();
                        emitter.send(SseEmitter.event()
                                .data(messageJson)
                                .id(messageProtobufReply.getUid())
                                .name("message"));
                        emitter.complete();
                    } catch (Exception e) {
                        log.error("Error completing SSE", e);
                    }
                });
    }

    private void handleSseError(Throwable error, MessageProtobuf messageProtobufQuery, MessageProtobuf messageProtobufReply, SseEmitter emitter) {
        try {
            messageProtobufReply.setType(MessageTypeEnum.ERROR);
            messageProtobufReply.setContent("服务暂时不可用，请稍后重试");
            persistMessage(messageProtobufQuery, messageProtobufReply);
            String messageJson = messageProtobufReply.toJson();
            emitter.send(SseEmitter.event()
                    .data(messageJson)
                    .id(messageProtobufReply.getUid())
                    .name("message"));
            emitter.complete();
        } catch (Exception e) {
            log.error("Error handling SSE error", e);
            try {
                emitter.completeWithError(e);
            } catch (Exception ex) {
                log.error("Failed to complete emitter with error", ex);
            }
        }
    }

    public Optional<OpenAiChatModel> getDeepseekChatModel() {
        return deepseekChatModel;
    }
    
    public boolean isServiceHealthy() {
        if (!deepseekChatModel.isPresent()) {
            return false;
        }

        try {
            String response = processPromptSync("test");
            return !response.contains("不可用") && !response.equals("Deepseek service is not available");
        } catch (Exception e) {
            log.error("Error checking Deepseek service health", e);
            return false;
        }
    }
}
