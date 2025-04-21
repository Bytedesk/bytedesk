/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-26 16:58:56
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-14 22:03:10
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.providers.zhipuai;

import java.util.List;

import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.zhipuai.ZhiPuAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.bytedesk.ai.springai.base.BaseSpringAIService;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.MessageTypeEnum;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Slf4j
@Service
@ConditionalOnProperty(name = "spring.ai.zhipuai.chat.enabled", havingValue = "true")
public class SpringAIZhipuaiService extends BaseSpringAIService {

    @Autowired
    @Qualifier("bytedeskZhipuaiChatModel")
    private ZhiPuAiChatModel bytedeskZhipuaiChatModel;

    public SpringAIZhipuaiService() {
        super(); // 调用基类的无参构造函数
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
    public void processPromptSSE(Prompt prompt, MessageProtobuf messageProtobufQuery,
            MessageProtobuf messageProtobufReply, SseEmitter emitter) {

        Flux<ChatResponse> responseFlux = bytedeskZhipuaiChatModel.stream(prompt);

        responseFlux.subscribe(
                response -> {
                    try {
                        if (response != null) {
                            List<Generation> generations = response.getResults();
                            for (Generation generation : generations) {
                                AssistantMessage assistantMessage = generation.getOutput();
                                String textContent = assistantMessage.getText();
                                // log.info("Zhipuai API response metadata: {}, text {}",response.getMetadata(), textContent);
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
                        log.error("Zhipuai API Error sending SSE event 1：", e);
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
                            log.error("Zhipuai API SSE complete Error 1", ex);
                            emitter.completeWithError(ex);
                        }
                    }
                },
                error -> {
                    log.error("Zhipuai API SSE error 2:", error);
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
                        log.error("Zhipuai API SSE complete Error 2", ex);
                        emitter.completeWithError(ex);
                    }
                },
                () -> {
                    log.info("Zhipuai API SSE complete");
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
                        log.error("Zhipuai API SSE complete Error completing SSE", e);
                    }
                });
    }

    @Override
    protected String generateFaqPairs(String prompt) {
        return bytedeskZhipuaiChatModel.call(prompt);
    }
}
