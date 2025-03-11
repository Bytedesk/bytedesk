/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-26 16:58:56
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-11 16:35:47
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
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.ai.springai.base.BaseSpringAIService;
import com.bytedesk.ai.springai.spring.SpringAIVectorService;
import com.bytedesk.core.message.IMessageSendService;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.MessageTypeEnum;
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
            IMessageSendService messageSendService) {
        super(springAIVectorService, messageSendService);
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
                    // 判断输出是否结束
                    // if (response.isComplete()) {
                    //     messageProtobuf.setType(MessageTypeEnum.STREAM_END);
                    //     messageProtobuf.setContent("输出结束");
                    //     messageSendService.sendProtobufMessage(messageProtobuf);
                    // }
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
                messageProtobuf.setType(MessageTypeEnum.STREAM_END);
                messageProtobuf.setContent(""); // 或者可以是任何结束标记
                messageSendService.sendProtobufMessage(messageProtobuf);
            }
        );
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
    public void processPromptSSE(String messageJson, SseEmitter emitter) {

        MessageProtobuf messageProtobuf = JSON.parseObject(messageJson, MessageProtobuf.class);
        // 
        // ThreadProtobuf thread = ThreadProtobuf
        //     .builder()
        //     .uid(uid)
        //     .build();
        // UserProtobuf user = UserProtobuf
        //     .builder()
        //     .uid(uid)
        //     .nickname("ollama")
        //     .avatar("")
        //     .build();
        // // 
        // MessageProtobuf messageProtobuf = MessageProtobuf
        //     .builder()
        //     .uid(uid)
        //     // .content(textContent)
        //     .type(MessageTypeEnum.STREAM)
        //     .status(MessageStatusEnum.SUCCESS)
        //     .client(ClientEnum.SYSTEM)
        //     .createdAt(LocalDateTime.now())
        //     .thread(thread)
        //     .user(user)
        //     .build();  
            // 
        Prompt prompt = new Prompt(messageProtobuf.getContent());
        Flux<ChatResponse> responseFlux = bytedeskZhipuaiChatModel.stream(prompt);

        responseFlux.subscribe(
            response -> {
                try {
                    if (response != null) {
                        List<Generation> generations = response.getResults();
                        for (Generation generation : generations) {
                            AssistantMessage assistantMessage = generation.getOutput();
                            String textContent = assistantMessage.getText();
                            messageProtobuf.setContent(textContent);
                            
                            // 发送SSE事件
                            emitter.send(SseEmitter.event()
                                .data(textContent)
                                .id(String.valueOf(System.currentTimeMillis()))
                                .name("message"));
                        }
                    }
                } catch (Exception e) {
                    log.error("Error sending SSE event", e);
                    emitter.completeWithError(e);
                }
            },
            error -> {
                log.error("Zhipuai API SSE error: ", error);
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
                    // 发送流结束标记
                    messageProtobuf.setType(MessageTypeEnum.STREAM_END);
                    messageProtobuf.setContent(""); // 或者可以是任何结束标记
                    messageSendService.sendProtobufMessage(messageProtobuf);
                    // 
                    emitter.complete();
                } catch (Exception e) {
                    log.error("Error completing SSE", e);
                }
            }
        );
    }

    @Override
    protected String generateFaqPairs(String prompt) {
        return bytedeskZhipuaiChatModel.call(prompt);
    }
}
