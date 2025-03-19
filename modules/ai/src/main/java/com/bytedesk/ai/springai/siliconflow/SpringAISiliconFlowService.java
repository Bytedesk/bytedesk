/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-28 11:44:03
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-19 16:28:59
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM –
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 *  contact: 270580156@qq.com
 *
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved.
 */
package com.bytedesk.ai.springai.siliconflow;

import com.alibaba.fastjson2.JSON;
import com.aliyun.oss.common.utils.StringUtils;
import com.bytedesk.ai.robot.RobotRestService;
import com.bytedesk.ai.springai.base.BaseSpringAIService;
import com.bytedesk.ai.springai.spring.SpringAIVectorService;

import com.bytedesk.core.message.IMessageSendService;
import com.bytedesk.core.message.MessagePersistCache;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.MessageTypeEnum;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.core.uid.UidUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@ConditionalOnProperty(name = "spring.ai.siliconflow.chat.enabled", havingValue = "true", matchIfMissing = false)
public class SpringAISiliconFlowService extends BaseSpringAIService {

   private final Optional<OpenAiChatModel> siliconFlowChatModel;
 
    public SpringAISiliconFlowService(
            Optional<OpenAiChatModel> siliconFlowChatModel,
            Optional<SpringAIVectorService> springAIVectorService,
            IMessageSendService messageSendService,
            UidUtils uidUtils,
            RobotRestService robotRestService,
            ThreadRestService threadRestService,
            MessagePersistCache messagePersistCache) {
        super(springAIVectorService,messageSendService,uidUtils,robotRestService,threadRestService,messagePersistCache);
        this.siliconFlowChatModel = siliconFlowChatModel;
    }

    @Override
    protected void processPrompt(Prompt prompt, MessageProtobuf messageProtobuf) {
        siliconFlowChatModel.ifPresent(model -> model.stream(prompt).subscribe(
                response -> {
                    if (response != null) {
                        log.info("siliconFlow API response metadata: {}", response.getMetadata());
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
                    log.error("siliconFlow API error: ", error);
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
        return siliconFlowChatModel.map(model -> model.call(prompt)).orElse("");
    }

    @Override
    protected String processPromptSync(String message) {
        try {
            return siliconFlowChatModel.map(model -> model.call(message))
                    .orElse("siliconFlow service is not available");
        } catch (Exception e) {
            log.error("siliconFlow API sync error: ", e);
            return "服务暂时不可用，请稍后重试";
        }
    }

    @Override
    protected void processPromptSSE(Prompt prompt, MessageProtobuf messageProtobuf, SseEmitter emitter) {
        siliconFlowChatModel.ifPresentOrElse(
                model -> {
                    model.stream(prompt).subscribe(
                            response -> {
                                try {
                                    if (response != null) {
                                        List<Generation> generations = response.getResults();
                                        for (Generation generation : generations) {
                                            AssistantMessage assistantMessage = generation.getOutput();
                                            String textContent = assistantMessage.getText();
                                            log.info("siliconFlow API response metadata: {}, text {}",
                                                    response.getMetadata(), textContent);
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
                                log.error("siliconFlow API SSE error: ", error);
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
                                log.info("siliconFlow API SSE complete");
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


    public Optional<OpenAiChatModel> getSiliconFlowChatModel() {
        return siliconFlowChatModel;
    }
}
