/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-26 16:59:14
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-11 14:56:30
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.ollama;

import java.util.List;
import java.util.Optional;

import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;
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
import com.bytedesk.core.uid.UidUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@ConditionalOnProperty(name = "spring.ai.ollama.chat.enabled", havingValue = "true")
public class SpringAIOllamaService extends BaseSpringAIService {
    
    private final Optional<OllamaChatModel> bytedeskOllamaChatModel;

    private final UidUtils uidUtils;

    public SpringAIOllamaService(
            @Qualifier("bytedeskOllamaChatModel") Optional<OllamaChatModel> bytedeskOllamaChatModel,
            Optional<SpringAIVectorService> springAIVectorService,
            IMessageSendService messageSendService, 
            UidUtils uidUtils) {
        super(springAIVectorService, messageSendService);
        this.bytedeskOllamaChatModel = bytedeskOllamaChatModel;
        this.uidUtils = uidUtils;
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
            () -> log.info("Chat stream completed")
        ));
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
    protected void processPromptSSE(String question, SseEmitter emitter) {
        bytedeskOllamaChatModel.ifPresentOrElse(
            model -> {
                Prompt prompt = new Prompt(question);
                model.stream(prompt).subscribe(
                    response -> {
                        try {
                            if (response != null) {
                                List<Generation> generations = response.getResults();
                                for (Generation generation : generations) {
                                    AssistantMessage assistantMessage = generation.getOutput();
                                    String textContent = assistantMessage.getText();
                                    // 
                                    MessageProtobuf messageProtobuf = MessageProtobuf
                                        .builder()
                                        .uid(uidUtils.getUid())
                                        .content(textContent)
                                        .build();                        
                                    // 发送SSE事件
                                    emitter.send(SseEmitter.event()
                                        .data(JSON.toJSONString(messageProtobuf))
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
                        log.error("Ollama API SSE error: ", error);
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
                    }
                );
            },
            () -> {
                try {
                    emitter.send(SseEmitter.event()
                        .data("Ollama service is not available")
                        .name("error"));
                    emitter.complete();
                } catch (Exception e) {
                    emitter.completeWithError(e);
                }
            }
        );
    }

    public Optional<OllamaChatModel> getOllamaChatModel() {
        return bytedeskOllamaChatModel;
    }

}
