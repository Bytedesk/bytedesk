/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-26 16:59:14
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-22 10:41:23
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
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
@ConditionalOnProperty(name = "spring.ai.ollama.chat.enabled", havingValue = "true", matchIfMissing = false)
public class SpringAIOllamaService extends BaseSpringAIService {

    @Autowired
    @Qualifier("bytedeskOllamaChatModel")
    private Optional<OllamaChatModel> bytedeskOllamaChatModel;
    
    @Autowired
    @Qualifier("bytedeskOllamaApi")
    private OllamaApi ollamaApi;

    @Autowired
    private SpringAIOllamaConfig ollamaConfig;

    public SpringAIOllamaService() {
        super(); // 调用基类的无参构造函数
    }

    /**
     * 根据机器人配置创建动态的OllamaChatModel
     * 
     * @param llm 机器人LLM配置
     * @return 配置了特定模型的OllamaChatModel
     */
    private OllamaChatModel createDynamicChatModel(RobotLlm llm) {
        if (llm == null || !StringUtils.hasText(llm.getModel())) {
            // 如果没有指定模型或设置，使用默认配置
            return bytedeskOllamaChatModel.orElse(null);
        }

        try {
            OllamaOptions options = OllamaOptions.builder()
                    .model(llm.getModel())
                    .temperature(llm.getTemperature())  // 现在直接使用Double类型
                    .topP(llm.getTopP())  // 现在直接使用Double类型
                    .build();

            return OllamaChatModel.builder()
                    .ollamaApi(ollamaApi)
                    .defaultOptions(options)
                    .build();
        } catch (Exception e) {
            log.error("Error creating dynamic chat model for model {}", llm.getModel(), e);
            return bytedeskOllamaChatModel.orElse(null);
        }
    }

    @Override
    protected void processPrompt(Prompt prompt, MessageProtobuf messageProtobuf) {
        // 从messageProtobuf的extra字段中获取llm配置
        RobotLlm llm = null;
        try {
            // 这里假设extra中有robotLlm字段，实际中可能需要调整
            if (messageProtobuf.getExtra() != null) {
                // 根据实际情况从消息中获取LLM配置
                // 如果无法获取，将使用默认配置
            }
        } catch (Exception e) {
            log.warn("Failed to extract LLM config from message, using default model", e);
        }

        // 获取适当的模型实例
        OllamaChatModel chatModel = (llm != null) ? createDynamicChatModel(llm) : bytedeskOllamaChatModel.orElse(null);
        
        if (chatModel == null) {
            log.info("Ollama API not available");
            messageProtobuf.setType(MessageTypeEnum.ERROR);
            messageProtobuf.setContent("Ollama service is not available");
            messageSendService.sendProtobufMessage(messageProtobuf);
            return;
        }

        try {
            chatModel.stream(prompt).subscribe(
                response -> {
                    if (response != null) {
                        log.info("Ollama API response metadata: {}", response.getMetadata());
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
                    log.error("Ollama API error: ", error);
                    messageProtobuf.setType(MessageTypeEnum.ERROR);
                    messageProtobuf.setContent("服务暂时不可用，请稍后重试");
                    messageSendService.sendProtobufMessage(messageProtobuf);
                },
                () -> {
                    log.info("Chat stream completed");
                });
        } catch (Exception e) {
            log.error("Error processing Ollama prompt", e);
            messageProtobuf.setType(MessageTypeEnum.ERROR);
            messageProtobuf.setContent("服务暂时不可用，请稍后重试");
            messageSendService.sendProtobufMessage(messageProtobuf);
        }
    }

    @Override
    protected String generateFaqPairs(String prompt) {
        // 使用默认模型生成FAQ对
        return bytedeskOllamaChatModel.map(model -> {
            try {
                return model.call(prompt);
            } catch (Exception e) {
                log.error("Error generating FAQ pairs", e);
                return "生成FAQ对失败，请稍后重试";
            }
        }).orElse("");
    }

    @Override
    protected String processPromptSync(String message) {
        try {
            return bytedeskOllamaChatModel.map(model -> {
                try {
                    return model.call(message);
                } catch (Exception e) {
                    log.error("Ollama API sync error", e);
                    return "服务暂时不可用，请稍后重试";
                }
            }).orElse("Ollama service is not available");
        } catch (Exception e) {
            log.error("Ollama API sync error", e);
            return "服务暂时不可用，请稍后重试";
        }
    }

    @Override
    protected void processPromptSSE(Prompt prompt, MessageProtobuf messageProtobufQuery, MessageProtobuf messageProtobufReply, SseEmitter emitter) {
        // 从messageProtobuf中提取RobotProtobuf信息
        RobotLlm llm = null;
        try {
            // 此处根据实际应用逻辑从消息中获取机器人配置
            // 例如可以从messageProtobufQuery的extra字段中获取机器人配置
            if (messageProtobufQuery.getExtra() != null) {
                // 从extra中解析RobotLlm配置
                // 此处实现需根据实际应用逻辑调整
            }
        } catch (Exception e) {
            log.warn("Failed to extract robot info, using default model", e);
        }

        // 获取适当的模型实例
        OllamaChatModel chatModel = (llm != null) ? createDynamicChatModel(llm) : bytedeskOllamaChatModel.orElse(null);
        
        if (chatModel == null) {
            log.info("Ollama API not available");
            try {
                messageProtobufReply.setType(MessageTypeEnum.STREAM_END);
                messageProtobufReply.setContent("Ollama service is not available"); 
                persistMessage(messageProtobufQuery, messageProtobufReply);
                String messageJson = messageProtobufReply.toJson();
                
                emitter.send(SseEmitter.event()
                        .data(messageJson)
                        .id(messageProtobufReply.getUid())
                        .name("message"));
                emitter.complete();
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
            return;
        }

        try {
            chatModel.stream(prompt).subscribe(
                    response -> {
                        try {
                            if (response != null) {
                                List<Generation> generations = response.getResults();
                                for (Generation generation : generations) {
                                    AssistantMessage assistantMessage = generation.getOutput();
                                    String textContent = assistantMessage.getText();
                                    
                                    if (StringUtils.hasLength(textContent)) {
                                        messageProtobufReply.setContent(textContent);
                                        messageProtobufReply.setType(MessageTypeEnum.STREAM);
                                        // 保存消息到数据库
                                        persistMessage(messageProtobufQuery, messageProtobufReply);
                                        String messageJson = messageProtobufReply.toJson();
                                        // 发送SSE事件
                                        emitter.send(SseEmitter.event()
                                                .data(messageJson)
                                                .id(messageProtobufReply.getUid())
                                                .name("message"));
                                    }
                                }
                            }
                        } catch (Exception e) {
                            handleSseError(e, messageProtobufQuery, messageProtobufReply, emitter);
                        }
                    },
                    error -> {
                        log.error("Ollama API SSE error: ", error);
                        handleSseError(error, messageProtobufQuery, messageProtobufReply, emitter);
                    },
                    () -> {
                        try {
                            // 发送流结束标记
                            messageProtobufReply.setType(MessageTypeEnum.STREAM_END);
                            messageProtobufReply.setContent(""); 
                            // 保存消息到数据库
                            persistMessage(messageProtobufQuery, messageProtobufReply);
                            String messageJson = messageProtobufReply.toJson();
                            //
                            emitter.send(SseEmitter.event()
                                    .data(messageJson)
                                    .id(messageProtobufReply.getUid())
                                    .name("message"));
                            emitter.complete();
                        } catch (Exception e) {
                            log.error("Ollama Error completing SSE", e);
                        }
                    });
        } catch (Exception e) {
            log.error("Error starting Ollama stream", e);
            handleSseError(e, messageProtobufQuery, messageProtobufReply, emitter);
        }
    }
    
    // 添加新的辅助方法处理SSE错误
    private void handleSseError(Throwable error, MessageProtobuf messageProtobufQuery, 
                               MessageProtobuf messageProtobufReply, SseEmitter emitter) {
        try {
            messageProtobufReply.setType(MessageTypeEnum.ERROR);
            messageProtobufReply.setContent("服务暂时不可用，请稍后重试");
            // 保存消息到数据库
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

    public boolean isServiceHealthy() {
        if (!bytedeskOllamaChatModel.isPresent()) {
            return false;
        }

        try {
            // 发送一个简单的测试请求来检测服务是否响应
            String response = processPromptSync("test");
            return !response.contains("不可用") && !response.equals("Ollama service is not available");
        } catch (Exception e) {
            log.error("Error checking Ollama service health", e);
            return false;
        }
    }

    public Optional<OllamaChatModel> getOllamaChatModel() {
        return bytedeskOllamaChatModel;
    }
}
