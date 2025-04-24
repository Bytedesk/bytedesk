/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-26 16:59:14
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-24 12:41:14
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
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import com.bytedesk.ai.robot.RobotLlm;
import com.bytedesk.ai.robot.RobotProtobuf;
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
    protected void processPrompt(Prompt prompt, RobotProtobuf robot, MessageProtobuf messageProtobufQuery, MessageProtobuf messageProtobufReply) {
        // 从robot中获取llm配置
        RobotLlm llm = robot.getLlm();

        // 获取适当的模型实例
        OllamaChatModel chatModel = (llm != null) ? createDynamicChatModel(llm) : bytedeskOllamaChatModel.orElse(null);
        
        if (chatModel == null) {
            log.info("Ollama API not available");
            messageProtobufReply.setType(MessageTypeEnum.ERROR);
            messageProtobufReply.setContent("Ollama service is not available");
            messageSendService.sendProtobufMessage(messageProtobufReply);
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

                            messageProtobufReply.setType(MessageTypeEnum.STREAM);
                            messageProtobufReply.setContent(textContent);
                            messageSendService.sendProtobufMessage(messageProtobufReply);
                        }
                    }
                },
                error -> {
                    log.error("Ollama API error: ", error);
                    messageProtobufReply.setType(MessageTypeEnum.ERROR);
                    messageProtobufReply.setContent("服务暂时不可用，请稍后重试");
                    messageSendService.sendProtobufMessage(messageProtobufReply);
                },
                () -> {
                    log.info("Chat stream completed");
                });
        } catch (Exception e) {
            log.error("Error processing Ollama prompt", e);
            messageProtobufReply.setType(MessageTypeEnum.ERROR);
            messageProtobufReply.setContent("服务暂时不可用，请稍后重试");
            messageSendService.sendProtobufMessage(messageProtobufReply);
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
    protected void processPromptSSE(Prompt prompt, RobotProtobuf robot, MessageProtobuf messageProtobufQuery, MessageProtobuf messageProtobufReply, SseEmitter emitter) {
        Assert.notNull(emitter, "SseEmitter must not be null");
        // 从robot中获取llm配置
        RobotLlm llm = robot.getLlm();

        // 获取适当的模型实例并配置较长的超时时间
        OllamaChatModel chatModel = bytedeskOllamaChatModel.orElse(null);
        if (chatModel != null && llm != null) {
            chatModel = createDynamicChatModel(llm);
            // 配置更长的超时时间 - 5分钟
            chatModel = configureModelWithTimeout(chatModel, 300000);
        }
        
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
            // 发送初始消息，告知用户请求已收到，正在处理
            if (!isEmitterCompleted(emitter)) {
                messageProtobufReply.setType(MessageTypeEnum.STREAM_START);
                messageProtobufReply.setContent("正在思考中...");
                String startJson = messageProtobufReply.toJson();
                emitter.send(SseEmitter.event()
                        .data(startJson)
                        .id(messageProtobufReply.getUid())
                        .name("message"));
            }
            
            chatModel.stream(prompt).subscribe(
                    response -> {
                        try {
                            if (response != null && !isEmitterCompleted(emitter)) {
                                List<Generation> generations = response.getResults();
                                for (Generation generation : generations) {
                                    AssistantMessage assistantMessage = generation.getOutput();
                                    String textContent = assistantMessage.getText();
                                    log.info("Ollama API response metadata: {}, text {}",
                                            response.getMetadata(), textContent);
                                    
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
                            log.error("Ollama API SSE error 1: ", e);
                            handleSseError(e, messageProtobufQuery, messageProtobufReply, emitter);
                        }
                    },
                    error -> {
                        log.error("Ollama API SSE error 2: ", error);
                        handleSseError(error, messageProtobufQuery, messageProtobufReply, emitter);
                    },
                    () -> {
                        try {
                            if (!isEmitterCompleted(emitter)) {
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
                            }
                        } catch (Exception e) {
                            log.error("Ollama Error completing SSE 3", e);
                            handleSseError(e, messageProtobufQuery, messageProtobufReply, emitter);
                        }
                    });
        } catch (Exception e) {
            log.error("Error starting Ollama stream 4", e);
            handleSseError(e, messageProtobufQuery, messageProtobufReply, emitter);
        }
    }
    
    // 添加新的辅助方法处理SSE错误
    private void handleSseError(Throwable error, MessageProtobuf messageProtobufQuery, 
                               MessageProtobuf messageProtobufReply, SseEmitter emitter) {
        try {
            // 检查emitter是否已完成
            if (emitter != null && !isEmitterCompleted(emitter)) {
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
            } else {
                log.warn("SSE emitter already completed, skipping sending error message");
                // 仍然需要持久化消息
                messageProtobufReply.setType(MessageTypeEnum.ERROR);
                messageProtobufReply.setContent("服务暂时不可用，请稍后重试");
                persistMessage(messageProtobufQuery, messageProtobufReply);
            }
        } catch (Exception e) {
            log.error("Error handling SSE error", e);
            try {
                if (emitter != null && !isEmitterCompleted(emitter)) {
                    emitter.completeWithError(e);
                }
            } catch (Exception ex) {
                log.error("Failed to complete emitter with error", ex);
            }
        }
    }
    
    // 添加一个新方法来检查SseEmitter是否已完成
    private boolean isEmitterCompleted(SseEmitter emitter) {
        if (emitter == null) {
            return true;
        }
        
        try {
            // 尝试发送一个心跳消息，如果emitter已完成则会抛出异常
            // 使用一个空注释作为心跳，这不会影响客户端
            emitter.send(SseEmitter.event().comment("heartbeat"));
            return false; // 如果发送成功，则表示emitter未完成
        } catch (Exception e) {
            // 如果发送失败，说明emitter已经完成或关闭
            log.debug("Emitter appears to be completed: {}", e.getMessage());
            return true;
        }
    }

    // 修改响应超时配置方法
    private OllamaChatModel configureModelWithTimeout(OllamaChatModel model, long timeoutMillis) {
        try {
            // 因为OllamaOptions.Builder没有from方法，所以需要使用getDefaultOptions()获取当前选项
            // 然后手动复制所有必要的配置
            OllamaOptions defaultOptions = (OllamaOptions)model.getDefaultOptions();
            
            // 创建新的选项，设置超时时间
            OllamaOptions options = OllamaOptions.builder()
                    .model(defaultOptions.getModel())
                    .temperature(defaultOptions.getTemperature())
                    .topP(defaultOptions.getTopP())
                    .topK(defaultOptions.getTopK())
                    .numPredict(defaultOptions.getNumPredict())
                    .numCtx(defaultOptions.getNumCtx())
                    // 设置更长的保活时间
                    .keepAlive(String.format("%ds", timeoutMillis / 1000)) // 将毫秒转换为秒，如"300s"
                    .build();
            
            // 创建一个新的模型实例，具有更长的超时时间
            return OllamaChatModel.builder()
                    .ollamaApi(ollamaApi)
                    .defaultOptions(options)
                    .build();
        } catch (Exception e) {
            log.warn("无法配置模型超时，使用默认模型: {}", e.getMessage());
            return model;
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
