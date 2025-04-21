/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-26 16:59:14
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-14 22:02:34
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
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.util.StringUtils;

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

    public SpringAIOllamaService() {
        super(); // 调用基类的无参构造函数
    }

    @Override
    protected void processPrompt(Prompt prompt, MessageProtobuf messageProtobuf) {
        bytedeskOllamaChatModel.ifPresent(model -> {
            try {
                model.stream(prompt).subscribe(
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
        });
    }

    @Override
    protected String generateFaqPairs(String prompt) {
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
        bytedeskOllamaChatModel.ifPresentOrElse(
                model -> {
                    try {
                        model.stream(prompt).subscribe(
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
                },
                () -> {
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
                });
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
