/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-28 11:44:03
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-11 18:36:14
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

@Slf4j
@Service
@ConditionalOnProperty(name = "spring.ai.deepseek.chat.enabled", havingValue = "true", matchIfMissing = false)
public class SpringAIDeepseekService extends BaseSpringAIService {

    private final Optional<OpenAiChatModel> deepSeekChatModel;

    public SpringAIDeepseekService(
            Optional<OpenAiChatModel> deepSeekChatModel,
            Optional<SpringAIVectorService> springAIVectorService,
            IMessageSendService messageSendService,
            UidUtils uidUtils,
            RobotRestService robotRestService,
            ThreadRestService threadRestService) {
        super(springAIVectorService, messageSendService, uidUtils, robotRestService, threadRestService);
        this.deepSeekChatModel = deepSeekChatModel;

    }

    @Override
    protected void processPrompt(Prompt prompt, MessageProtobuf messageProtobuf) {
        deepSeekChatModel.ifPresent(model -> model.stream(prompt).subscribe(
            response -> {
                if (response != null) {
                    log.info("DeepSeek API response metadata: {}", response.getMetadata());
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
                log.error("DeepSeek API error: ", error);
                messageProtobuf.setType(MessageTypeEnum.ERROR);
                messageProtobuf.setContent("服务暂时不可用，请稍后重试");
                messageSendService.sendProtobufMessage(messageProtobuf);
            },
            () -> log.info("Chat stream completed")
        ));
    }

    @Override
    protected String generateFaqPairs(String prompt) {
        return deepSeekChatModel.map(model -> model.call(prompt)).orElse("");
    }

    @Override
    protected String processPromptSync(String message) {
        try {
            return deepSeekChatModel.map(model -> model.call(message))
                .orElse("DeepSeek service is not available");
        } catch (Exception e) {
            log.error("DeepSeek API sync error: ", e);
            return "服务暂时不可用，请稍后重试";
        }
    }

    @Override
    protected void processPromptSSE(RobotEntity robot, Prompt prompt, ThreadProtobuf threadProtobuf,
            MessageProtobuf messageProtobuf, SseEmitter emitter) {
        deepSeekChatModel.ifPresentOrElse(
            model -> {
                model.stream(prompt).subscribe(
                    response -> {
                        try {
                            if (response != null) {
                                List<Generation> generations = response.getResults();
                                for (Generation generation : generations) {
                                    AssistantMessage assistantMessage = generation.getOutput();
                                    String textContent = assistantMessage.getText();
                                    log.info("DeepSeek API response metadata: {}, text {}", response.getMetadata(), textContent);
                                    
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
                        log.error("DeepSeek API SSE error: ", error);
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
                        .data("DeepSeek service is not available")
                        .name("error"));
                    emitter.complete();
                } catch (Exception e) {
                    emitter.completeWithError(e);
                }
            }
        );
    }

    public Optional<OpenAiChatModel> getDeepSeekChatModel() {
        return deepSeekChatModel;
    }
}
