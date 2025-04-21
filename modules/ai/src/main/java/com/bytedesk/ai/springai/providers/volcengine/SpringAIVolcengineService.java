/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-28 11:44:03
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-14 22:02:59
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
import org.springframework.beans.factory.annotation.Autowired;
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
@ConditionalOnProperty(name = "spring.ai.volcengine.chat.enabled", havingValue = "true", matchIfMissing = false)
public class SpringAIVolcengineService extends BaseSpringAIService {

    @Autowired(required = false)
    private Optional<OpenAiChatModel> volcengineChatModel;

    public SpringAIVolcengineService() {
        super(); // 调用基类的无参构造函数
    }

    @Override
    protected void processPrompt(Prompt prompt, MessageProtobuf messageProtobuf) {
        volcengineChatModel.ifPresent(model -> model.stream(prompt).subscribe(
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
                    // 发送流结束标记
                    // messageProtobuf.setType(MessageTypeEnum.STREAM_END);
                    // messageProtobuf.setContent(""); // 或者可以是任何结束标记
                    // messageSendService.sendProtobufMessage(messageProtobuf);
                }));
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
    protected void processPromptSSE(Prompt prompt, MessageProtobuf messageProtobufQuery, MessageProtobuf messageProtobufReply, SseEmitter emitter) {
        volcengineChatModel.ifPresentOrElse(
                model -> {
                    model.stream(prompt).subscribe(
                            response -> {
                                try {
                                    if (response != null) {
                                        List<Generation> generations = response.getResults();
                                        for (Generation generation : generations) {
                                            AssistantMessage assistantMessage = generation.getOutput();
                                            String textContent = assistantMessage.getText();
                                            log.info("Volcengine API response metadata: {}, text {}",
                                                    response.getMetadata(), textContent);
                                            //
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
                                    log.error("Error sending SSE event", e);
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
                                log.error("Volcengine API SSE error: ", error);
                                //
                                try {
                                    messageProtobufReply.setType(MessageTypeEnum.ERROR);
                                    messageProtobufReply.setContent("服务暂时不可用，请稍后重试");
                                    // 保存消息到数据库
                                    persistMessage(messageProtobufQuery, messageProtobufReply);
                                    String messageJson = messageProtobufReply.toJson();
                                    // 发送SSE事件
                                    emitter.send(SseEmitter.event()
                                            .data(messageJson)
                                            .id(messageProtobufReply.getUid())
                                            .name("message"));
                                    emitter.complete();
                                } catch (Exception ex) {
                                    emitter.completeWithError(ex);
                                }
                            },
                            () -> {
                                log.info("Volcengine API SSE complete");
                                try {
                                    // 发送流结束标记
                                    messageProtobufReply.setType(MessageTypeEnum.STREAM_END);
                                    messageProtobufReply.setContent(""); // 或者可以是任何结束标记
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
                                    log.error("Error completing SSE", e);
                                }
                            });
                },
                () -> {
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
                                .name("message"));
                        emitter.complete();
                    } catch (Exception ex) {
                        emitter.completeWithError(ex);
                    }
                });
    }

    public Optional<OpenAiChatModel> getVolcengineChatModel() {
        return volcengineChatModel;
    }

}
