/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-28 11:44:03
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-14 09:39:12
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.deepseek;

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

import com.alibaba.fastjson2.JSON;
import com.aliyun.oss.common.utils.StringUtils;
import com.bytedesk.ai.springai.base.BaseSpringAIService;
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

    @Override
    protected void processPrompt(Prompt prompt, MessageProtobuf messageProtobuf) {
        deepseekChatModel.ifPresent(model -> model.stream(prompt).subscribe(
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
                    // 发送流结束标记
                    // messageProtobuf.setType(MessageTypeEnum.STREAM_END);
                    // messageProtobuf.setContent(""); // 或者可以是任何结束标记
                    // messageSendService.sendProtobufMessage(messageProtobuf);
                }));
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
    protected void processPromptSSE(Prompt prompt, MessageProtobuf messageProtobuf, SseEmitter emitter) {
        deepseekChatModel.ifPresentOrElse(
                model -> {
                    model.stream(prompt).subscribe(
                            response -> {
                                try {
                                    if (response != null) {
                                        List<Generation> generations = response.getResults();
                                        for (Generation generation : generations) {
                                            AssistantMessage assistantMessage = generation.getOutput();
                                            String textContent = assistantMessage.getText();
                                            log.info("Deepseek API response metadata: {}, text {}",
                                                    response.getMetadata(), textContent);
                                            //
                                            if (StringUtils.hasValue(textContent)) {
                                                messageProtobuf.setContent(textContent);
                                                messageProtobuf.setType(MessageTypeEnum.STREAM);
                                                // 保存消息到数据库
                                                String messageJson = JSON.toJSONString(messageProtobuf);
                                                persistMessage(messageJson);
                                                // 发送SSE事件
                                                emitter.send(SseEmitter.event()
                                                        .data(messageJson)
                                                        .id(messageProtobuf.getUid())
                                                        .name("message"));
                                            }
                                        }
                                    }
                                } catch (Exception e) {
                                    log.error("Error sending SSE event", e);
                                    messageProtobuf.setType(MessageTypeEnum.ERROR);
                                    messageProtobuf.setContent("服务暂时不可用，请稍后重试");
                                    // 保存消息到数据库
                                    String messageJson = JSON.toJSONString(messageProtobuf);
                                    persistMessage(messageJson);
                                    //
                                    try {
                                        emitter.send(SseEmitter.event()
                                                .data(messageJson)
                                                .id(messageProtobuf.getUid())
                                                .name("error"));
                                        emitter.complete();
                                    } catch (Exception ex) {
                                        emitter.completeWithError(ex);
                                    }
                                }
                            },
                            error -> {
                                log.error("Deepseek API SSE error: ", error);
                                //
                                try {
                                    messageProtobuf.setType(MessageTypeEnum.ERROR);
                                    messageProtobuf.setContent("服务暂时不可用，请稍后重试");
                                    // 保存消息到数据库
                                    String messageJson = JSON.toJSONString(messageProtobuf);
                                    persistMessage(messageJson);
                                    // 发送SSE事件
                                    emitter.send(SseEmitter.event()
                                            .data(messageJson)
                                            .id(messageProtobuf.getUid())
                                            .name("message"));
                                    emitter.complete();
                                } catch (Exception ex) {
                                    emitter.completeWithError(ex);
                                }
                            },
                            () -> {
                                log.info("Deepseek API SSE complete");
                                try {
                                    // 发送流结束标记
                                    messageProtobuf.setType(MessageTypeEnum.STREAM_END);
                                    messageProtobuf.setContent(""); // 或者可以是任何结束标记
                                    // 保存消息到数据库
                                    String messageJson = JSON.toJSONString(messageProtobuf);
                                    persistMessage(messageJson);
                                    // 发送SSE事件
                                    emitter.send(SseEmitter.event()
                                            .data(messageJson)
                                            .id(messageProtobuf.getUid())
                                            .name("message"));
                                    emitter.complete();
                                } catch (Exception e) {
                                    log.error("Error completing SSE", e);
                                }
                            });
                },
                () -> {
                    messageProtobuf.setType(MessageTypeEnum.ERROR);
                    messageProtobuf.setContent("服务暂时不可用，请稍后重试");
                    // 保存消息到数据库
                    String messageJson = JSON.toJSONString(messageProtobuf);
                    persistMessage(messageJson);
                    //
                    try {
                        emitter.send(SseEmitter.event()
                                .data(messageJson)
                                .id(messageProtobuf.getUid())
                                .name("message"));
                        emitter.complete();
                    } catch (Exception ex) {
                        emitter.completeWithError(ex);
                    }
                });
    }

    public Optional<OpenAiChatModel> getDeepseekChatModel() {
        return deepseekChatModel;
    }

}
