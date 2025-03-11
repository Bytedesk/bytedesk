/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-28 17:56:26
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-11 18:36:39
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.dashscope;

import java.util.Optional;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.bytedesk.ai.robot.RobotEntity;
import com.bytedesk.ai.robot.RobotRestService;
import com.bytedesk.ai.springai.base.BaseSpringAIService;
import com.bytedesk.ai.springai.spring.SpringAIVectorService;
import com.bytedesk.core.message.IMessageSendService;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.MessageTypeEnum;
import com.bytedesk.core.thread.ThreadProtobuf;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.core.uid.UidUtils;

import lombok.extern.slf4j.Slf4j;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

@Slf4j
@Service
@ConditionalOnProperty(name = "spring.ai.dashscope.chat.enabled", havingValue = "true", matchIfMissing = false)
public class SpringAIDashscopeService extends BaseSpringAIService {

    // private final Optional<DashScopeChatModel> bytedeskDashScopeChatModel;
    // private final ChatClient bytedeskDashScopeChatClient;
    @Qualifier("bytedeskDashScopeChatClient")
    private final ChatClient bytedeskDashScopeChatClient;

    private final Counter aiRequestCounter;
    private final Timer aiResponseTimer;

    public SpringAIDashscopeService(
            @Qualifier("bytedeskDashScopeChatClient") ChatClient bytedeskDashScopeChatClient,
            Optional<SpringAIVectorService> springAIVectorService,
            IMessageSendService messageSendService,
            MeterRegistry registry,
            UidUtils uidUtils,
            RobotRestService robotRestService,
            ThreadRestService threadRestService) {
        super(springAIVectorService, messageSendService, uidUtils, robotRestService, threadRestService);

        // this.bytedeskDashScopeChatModel = bytedeskDashScopeChatModel;
        this.bytedeskDashScopeChatClient = bytedeskDashScopeChatClient;

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
    protected void processPromptSSE(RobotEntity robot, Prompt prompt, ThreadProtobuf threadProtobuf,
            MessageProtobuf messageProtobuf, SseEmitter emitter) {

        // MessageProtobuf messageProtobuf = JSON.parseObject(messageJson,
        // MessageProtobuf.class);

        try {
            bytedeskDashScopeChatClient.prompt(messageProtobuf.getContent())
                    .stream()
                    .content()
                    .subscribe(
                            content -> {
                                try {
                                    emitter.send(SseEmitter.event()
                                            .data(content)
                                            .id(String.valueOf(System.currentTimeMillis()))
                                            .name("message"));
                                } catch (Exception e) {
                                    log.error("Error sending SSE event", e);
                                    emitter.completeWithError(e);
                                }
                            },
                            error -> {
                                log.error("DashScope API SSE error: ", error);
                                try {
                                    emitter.send(SseEmitter.event()
                                            .data("服务暂时不可用，请稍后重试")
                                            .name("error"));
                                    emitter.complete();
                                } catch (Exception e) {
                                    emitter.completeWithError(e);
                                }
                            },
                            () -> {
                                try {
                                    emitter.complete();
                                } catch (Exception e) {
                                    log.error("Error completing SSE", e);
                                }
                            });
        } catch (Exception e) {
            log.error("Error starting SSE stream", e);
            emitter.completeWithError(e);
        }
    }

}
