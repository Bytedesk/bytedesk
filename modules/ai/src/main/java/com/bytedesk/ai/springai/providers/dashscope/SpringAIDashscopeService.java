/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-28 17:56:26
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-14 22:02:13
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.providers.dashscope;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
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
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

@Slf4j
@Service
@ConditionalOnProperty(name = "spring.ai.dashscope.chat.enabled", havingValue = "true", matchIfMissing = false)
public class SpringAIDashscopeService extends BaseSpringAIService {

    @Autowired
    @Qualifier("bytedeskDashScopeChatClient")
    private ChatClient bytedeskDashScopeChatClient;

    private final Counter aiRequestCounter;
    private final Timer aiResponseTimer;

    public SpringAIDashscopeService(MeterRegistry registry) {
        super(); // 调用基类的无参构造函数

        // 初始化监控指标
        this.aiRequestCounter = Counter.builder("bytedesk.ai.dashscope.requests")
                .description("Number of DashScope AI requests")
                .register(registry);

        this.aiResponseTimer = Timer.builder("bytedesk.ai.dashscope.response.time")
                .description("DashScope AI response time")
                .register(registry);
    }

    @Override
    protected void processPrompt(Prompt prompt, MessageProtobuf messageProtobuf) {
        aiRequestCounter.increment();

        Timer.Sample sample = Timer.start();
        try {
            bytedeskDashScopeChatClient.prompt(prompt.toString())
                    .stream()
                    .content()
                    .subscribe(
                            content -> {
                                log.info("DashScope API response  text {}", content);
                                messageProtobuf.setType(MessageTypeEnum.STREAM);
                                messageProtobuf.setContent(content);
                                messageSendService.sendProtobufMessage(messageProtobuf);
                            },
                            error -> {
                                log.error("DashScope API error: ", error);
                                messageProtobuf.setType(MessageTypeEnum.ERROR);
                                messageProtobuf.setContent("服务暂时不可用，请稍后重试");
                                messageSendService.sendProtobufMessage(messageProtobuf);
                            },
                            () -> {
                                sample.stop(aiResponseTimer);
                                log.info("Chat stream completed");
                            });
        } catch (Exception e) {
            sample.stop(aiResponseTimer);
            log.error("Error in processPrompt", e);
            messageProtobuf.setType(MessageTypeEnum.ERROR);
            messageProtobuf.setContent("服务暂时不可用，请稍后重试");
            messageSendService.sendProtobufMessage(messageProtobuf);
        }
    }

    @Override
    protected String generateFaqPairs(String prompt) {
        return bytedeskDashScopeChatClient.prompt(prompt).call().content();
    }

    @Override
    protected String processPromptSync(String message) {
        try {
            return bytedeskDashScopeChatClient.prompt(message).call().content();
        } catch (Exception e) {
            log.error("DashScope API sync error: ", e);
            return "服务暂时不可用，请稍后重试";
        }
    }

    @Override
    protected void processPromptSSE(Prompt prompt, MessageProtobuf messageProtobufQuery,
            MessageProtobuf messageProtobufReply, SseEmitter emitter) {

        try {
            bytedeskDashScopeChatClient.prompt(messageProtobufReply.getContent())
                    .stream()
                    .content()
                    .subscribe(
                            textContent -> {
                                try {
                                    //
                                    log.info("DashScope API response  text {}", textContent);
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
                                log.error("DashScope API SSE error: ", error);
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
                            },
                            () -> {
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
        } catch (Exception e) {
            log.error("DashScope API SSE error: ", e);
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
        }
    }

}
