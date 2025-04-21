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

import com.bytedesk.ai.springai.base.BaseSpringAIService;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.MessageTypeEnum;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@ConditionalOnProperty(name = "spring.ai.ollama.chat.enabled", havingValue = "true")
public class SpringAIOllamaService extends BaseSpringAIService {

    @Autowired
    @Qualifier("bytedeskOllamaChatModel")
    private Optional<OllamaChatModel> bytedeskOllamaChatModel;

    public SpringAIOllamaService() {
        super(); // 调用基类的无参构造函数
    }

    @Override
    protected void processPrompt(Prompt prompt, MessageProtobuf messageProtobuf) {
        bytedeskOllamaChatModel.ifPresent(model -> model.stream(prompt).subscribe(
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
                    // 发送流结束标记
                    // messageProtobuf.setType(MessageTypeEnum.STREAM_END);
                    // messageProtobuf.setContent(""); // 或者可以是任何结束标记
                    // messageSendService.sendProtobufMessage(messageProtobuf);
                }));
    }

    @Override
    protected String generateFaqPairs(String prompt) {
        return bytedeskOllamaChatModel.map(model -> model.call(prompt)).orElse("");
    }

    @Override
    protected String processPromptSync(String message) {
        try {
            return bytedeskOllamaChatModel.map(model -> model.call(message))
                    .orElse("Ollama service is not available");
        } catch (Exception e) {
            log.error("Ollama API sync error: ", e);
            return "服务暂时不可用，请稍后重试";
        }
    }

    @Override
    protected void processPromptSSE(Prompt prompt, MessageProtobuf messageProtobufQuery, MessageProtobuf messageProtobufReply, SseEmitter emitter) {
        //
        bytedeskOllamaChatModel.ifPresentOrElse(
                model -> {
                    model.stream(prompt).subscribe(
                            response -> {
                                try {
                                    if (response != null) {
                                        List<Generation> generations = response.getResults();
                                        for (Generation generation : generations) {
                                            AssistantMessage assistantMessage = generation.getOutput();
                                            String textContent = assistantMessage.getText();
                                            // log.info("Ollama API response metadata: {}, text {}",
                                            //         response.getMetadata(), textContent);
                                            // StringUtils.hasLength() 检查字符串非 null 且长度大于 0，允许包含空格
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
                                    log.error("Ollama Error sending SSE event 1", e);
                                    messageProtobufReply.setType(MessageTypeEnum.ERROR);
                                    messageProtobufReply.setContent("服务暂时不可用，请稍后重试");
                                    // 保存消息到数据库
                                    persistMessage(messageProtobufQuery, messageProtobufReply);
                                    String messageJson = messageProtobufReply.toJson();
                                    //
                                    try {
                                        emitter.send(SseEmitter.event()
                                                .data(messageJson)
                                                .id(messageProtobufReply.getUid())
                                                .name("error"));
                                        emitter.complete();
                                    } catch (Exception ex) {
                                        emitter.completeWithError(ex);
                                    }
                                }
                            },
                            error -> {
                                log.error("Ollama API SSE error: ", error);
                                try {
                                    messageProtobufReply.setType(MessageTypeEnum.ERROR);
                                    messageProtobufReply.setContent("服务暂时不可用，请稍后重试");
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
                                    emitter.completeWithError(e);
                                }
                            },
                            () -> {
                                try {
                                    // 发送流结束标记
                                    messageProtobufReply.setType(MessageTypeEnum.STREAM_END);
                                    messageProtobufReply.setContent(""); // 或者可以是任何结束标记
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
                },
                () -> {
                    log.info("Ollama API SSE complete");
                    try {
                        // 发送流结束标记
                        messageProtobufReply.setType(MessageTypeEnum.STREAM_END);
                        messageProtobufReply.setContent("Ollama service is not available"); // 或者可以是任何结束标记
                        // 保存消息到数据库
                        persistMessage(messageProtobufQuery, messageProtobufReply);
                                                String messageJson = messageProtobufReply.toJson();
                        // 发送SSE事件
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

    /**
     * 检查Ollama服务是否正常运行
     * 
     * @return 如果服务正常运行返回true，否则返回false
     */
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

    /**
     * 获取Ollama所有可用的模型列表
     * 
     * @return 包含所有可用模型信息的对象
     */
    // public Object getAvailableModels() {
    // if (!bytedeskOllamaChatModel.isPresent()) {
    // throw new RuntimeException("Ollama service is not available");
    // }

    // try {
    // // 这里假设OllamaChatModel有一个方法来获取模型列表
    // // 如果没有现成的方法，我们可以通过发送特定的API请求来获取
    // OllamaChatModel model = bytedeskOllamaChatModel.get();

    // // 使用反射获取模型的client对象，这样可以访问底层的API
    // // 注意：这种方法依赖于Spring AI的内部实现，如果库更新可能需要调整
    // Object ollamaApi = getOllamaApiClient(model);
    // if (ollamaApi != null) {
    // // 通过反射调用模型列表API
    // return invokeListModelsMethod(ollamaApi);
    // }

    // // 如果无法通过反射获取，则使用通用方法
    // // 请求Ollama的模型列表API
    // return getModelsViaPrompt();
    // } catch (Exception e) {
    // log.error("Error retrieving Ollama models", e);
    // throw new RuntimeException("Failed to retrieve Ollama models: " +
    // e.getMessage(), e);
    // }
    // }

    // /**
    // * 通过反射获取OllamaChatModel的API客户端
    // */
    // private Object getOllamaApiClient(OllamaChatModel model) {
    // try {
    // java.lang.reflect.Field clientField =
    // model.getClass().getDeclaredField("client");
    // clientField.setAccessible(true);
    // return clientField.get(model);
    // } catch (Exception e) {
    // log.warn("Could not access Ollama API client through reflection", e);
    // return null;
    // }
    // }

    // /**
    // * 通过反射调用API客户端的listModels方法
    // */
    // private Object invokeListModelsMethod(Object ollamaApi) {
    // try {
    // java.lang.reflect.Method listModelsMethod =
    // ollamaApi.getClass().getMethod("listModels");
    // return listModelsMethod.invoke(ollamaApi);
    // } catch (Exception e) {
    // log.warn("Could not invoke listModels method through reflection", e);
    // return null;
    // }
    // }

    // /**
    // * 如果无法通过反射获取，则通过发送特定提示来获取模型列表
    // */
    // private Object getModelsViaPrompt() {
    // // 向Ollama发送特定命令以获取模型列表
    // String response = processPromptSync("Please list all available models in a
    // JSON format");

    // try {
    // // 尝试解析响应为JSON
    // return JSON.parse(response);
    // } catch (Exception e) {
    // // 如果无法解析为JSON，则返回原始字符串
    // return response;
    // }
    // }

    public Optional<OllamaChatModel> getOllamaChatModel() {
        return bytedeskOllamaChatModel;
    }

}
