/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-26 16:58:56
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-19 10:28:44
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.zhipuai;

import java.util.List;
import java.util.Optional;

import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.zhipuai.ZhiPuAiChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
// import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

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
import reactor.core.publisher.Flux;

@Slf4j
@Service
@ConditionalOnProperty(name = "spring.ai.zhipuai.chat.enabled", havingValue = "true")
public class SpringAIZhipuaiService extends BaseSpringAIService {

    private final ZhiPuAiChatModel bytedeskZhipuaiChatModel;

    public SpringAIZhipuaiService(
            @Qualifier("bytedeskZhipuaiChatModel") ZhiPuAiChatModel bytedeskZhipuaiChatModel,
            Optional<SpringAIVectorService> springAIVectorService,
            IMessageSendService messageSendService,
            UidUtils uidUtils,
            RobotRestService robotRestService,
            ThreadRestService threadRestService, 
            MessagePersistCache messagePersistCache) {
        super(springAIVectorService, messageSendService, uidUtils, robotRestService, threadRestService, messagePersistCache);
        this.bytedeskZhipuaiChatModel = bytedeskZhipuaiChatModel;
    }

    /**
     * 方式1：异步流式调用
     */
    @Override
    protected void processPrompt(Prompt prompt, MessageProtobuf messageProtobuf) {
        bytedeskZhipuaiChatModel.stream(prompt).subscribe(
                response -> {
                    if (response != null) {
                        log.info("Zhipuai API response metadata: {}", response.getMetadata());
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
                    log.error("Zhipuai API error: ", error);
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
                });
    }

    /**
     * 方式2：同步调用
     */
    @Override
    public String processPromptSync(String message) {
        try {
            return bytedeskZhipuaiChatModel.call(message);
        } catch (Exception e) {
            log.error("Zhipuai API sync error: ", e);
            return "服务暂时不可用，请稍后重试";
        }
    }

    /**
     * 方式3：SSE方式调用
     */
    @Override
    public void processPromptSSE(Prompt prompt, 
            MessageProtobuf messageProtobuf, SseEmitter emitter) {

        // 检查权限
        // if (!SecurityContextHolder.getContext().getAuthentication().isAuthenticated()) {
        //     log.warn("User not authenticated for SSE stream");
        //     return;
        // }

        Flux<ChatResponse> responseFlux = bytedeskZhipuaiChatModel.stream(prompt);

        responseFlux.subscribe(
                response -> {
                    try {
                        if (response != null) {
                            List<Generation> generations = response.getResults();
                            for (Generation generation : generations) {
                                AssistantMessage assistantMessage = generation.getOutput();
                                String textContent = assistantMessage.getText();
                                log.info("Zhipuai API response metadata: {}, text {}", response.getMetadata(),
                                        textContent);
                                // 判断textContent是否为null
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
                        log.error("Zhipuai API Error sending SSE event 1：", e);
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
                            log.error("Zhipuai API SSE complete Error 1", ex);
                            emitter.completeWithError(ex);
                        }
                    }
                },
                error -> {
                    log.error("Zhipuai API SSE error 2:", error);
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
                        log.error("Zhipuai API SSE complete Error 2", ex);
                        emitter.completeWithError(ex);
                    }
                },
                () -> {
                    log.info("Zhipuai API SSE complete");
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
                        log.error("Zhipuai API SSE complete Error completing SSE", e);
                    }
                });
    }

    @Override
    protected String generateFaqPairs(String prompt) {
        return bytedeskZhipuaiChatModel.call(prompt);
    }
}
