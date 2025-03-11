/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-26 16:59:14
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-11 17:16:08
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.ollama;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.SerializationUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.ai.robot.RobotEntity;
import com.bytedesk.ai.robot.RobotRestService;
import com.bytedesk.ai.robot_message.RobotMessageUtils;
import com.bytedesk.ai.springai.base.BaseSpringAIService;
import com.bytedesk.ai.springai.spring.SpringAIVectorService;
import com.bytedesk.core.message.IMessageSendService;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.MessageTypeEnum;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.rbac.user.UserTypeEnum;
import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.core.thread.ThreadProtobuf;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.core.uid.UidUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@ConditionalOnProperty(name = "spring.ai.ollama.chat.enabled", havingValue = "true")
public class SpringAIOllamaService extends BaseSpringAIService {

    private final Optional<OllamaChatModel> bytedeskOllamaChatModel;

    private final UidUtils uidUtils;

    private final ThreadRestService threadRestService;

    private final RobotRestService robotRestService;

    public SpringAIOllamaService(
            @Qualifier("bytedeskOllamaChatModel") Optional<OllamaChatModel> bytedeskOllamaChatModel,
            Optional<SpringAIVectorService> springAIVectorService,
            IMessageSendService messageSendService,
            UidUtils uidUtils,
            ThreadRestService threadRestService,
            RobotRestService robotRestService) {
        super(springAIVectorService, messageSendService);
        this.bytedeskOllamaChatModel = bytedeskOllamaChatModel;
        this.uidUtils = uidUtils;
        this.threadRestService = threadRestService;
        this.robotRestService = robotRestService;
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
                () -> {
                    log.info("Chat stream completed");
                    // 发送流结束标记
                    messageProtobuf.setType(MessageTypeEnum.STREAM_END);
                    messageProtobuf.setContent(""); // 或者可以是任何结束标记
                    messageSendService.sendProtobufMessage(messageProtobuf);
                }));
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
    protected void processPromptSSE(String messageJson, SseEmitter emitter) {
        //
        MessageProtobuf messageProtobuf = JSON.parseObject(messageJson, MessageProtobuf.class);
        MessageTypeEnum messageType = messageProtobuf.getType();
        if (messageType.equals(MessageTypeEnum.STREAM)) {
            return;
        }
        String query = messageProtobuf.getContent();
        log.info("robot processMessage {}", query);
        ThreadProtobuf threadProtobuf = messageProtobuf.getThread();
        if (threadProtobuf == null) {
            throw new RuntimeException("thread is null");
        }
        // 暂时仅支持文字消息类型，其他消息类型，大模型暂不处理。
        if (!messageType.equals(MessageTypeEnum.TEXT)) {
            return;
        }
        String threadTopic = threadProtobuf.getTopic();
        ThreadEntity thread = threadRestService.findFirstByTopic(threadTopic)
                .orElseThrow(() -> new RuntimeException("thread with topic " + threadTopic +
                        " not found"));
        UserProtobuf agent = JSON.parseObject(thread.getAgent(), UserProtobuf.class);
        if (agent.getType().equals(UserTypeEnum.ROBOT.name())) {
            log.info("robot thread reply");
            RobotEntity robot = robotRestService.findByUid(agent.getUid())
                    .orElseThrow(() -> new RuntimeException("robot " + agent.getUid() + " not found"));
            //
            MessageProtobuf message = RobotMessageUtils.createRobotMessage(thread, threadProtobuf, robot,
                    messageProtobuf);
            //
            MessageProtobuf clonedMessage = SerializationUtils.clone(message);
            clonedMessage.setUid(uidUtils.getUid());
            clonedMessage.setType(MessageTypeEnum.PROCESSING);
            messageSendService.sendProtobufMessage(clonedMessage);
            //
            bytedeskOllamaChatModel.ifPresentOrElse(
                    model -> {
                        String prompt = "";
                        if (StringUtils.hasText(robot.getKbUid()) && robot.isKbEnabled()) {
                            List<String> contentList = springAIVectorService.get().searchText(query, robot.getKbUid());
                            String context = String.join("\n", contentList);
                            prompt = buildKbPrompt(robot.getLlm().getPrompt(), query, context);
                        } else {
                            prompt = robot.getLlm().getPrompt();
                        }
                        // 
                        List<Message> messages = new ArrayList<>();
                        messages.add(new SystemMessage(prompt));
                        messages.add(new UserMessage(query));
                        // 
                        Prompt aiPrompt = new Prompt(messages);
                        model.stream(aiPrompt).subscribe(
                                response -> {
                                    try {
                                        if (response != null) {
                                            List<Generation> generations = response.getResults();
                                            for (Generation generation : generations) {
                                                AssistantMessage assistantMessage = generation.getOutput();
                                                String textContent = assistantMessage.getText();
                                                //
                                                message.setContent(textContent);
                                                message.setType(MessageTypeEnum.STREAM);
                                                // 发送SSE事件
                                                emitter.send(SseEmitter.event()
                                                        .data(JSON.toJSONString(message))
                                                        .id(message.getUid())
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
                                        message.setType(MessageTypeEnum.ERROR);
                                        message.setContent("服务暂时不可用，请稍后重试");
                                        // 
                                        emitter.send(SseEmitter.event()
                                                .data(JSON.toJSONString(message))   
                                                .id(message.getUid())
                                                .name("error"));
                                        emitter.complete();
                                    } catch (Exception e) {
                                        emitter.completeWithError(e);
                                    }
                                },
                                () -> {
                                    try {
                                        // 发送流结束标记
                                        message.setType(MessageTypeEnum.STREAM_END);
                                        message.setContent(""); // 或者可以是任何结束标记
                                        emitter.send(SseEmitter.event()
                                                .data(JSON.toJSONString(message))   
                                                .id(message.getUid())
                                                .name("end"));
                                        emitter.complete();
                                    } catch (Exception e) {
                                        log.error("Error completing SSE", e);
                                    }
                                });
                    },
                    () -> {
                        try {
                            // 发送流结束标记
                            message.setType(MessageTypeEnum.STREAM_END);
                            message.setContent("Ollama service is not available"); // 或者可以是任何结束标记
                            emitter.send(SseEmitter.event()
                                    .data(JSON.toJSONString(message))   
                                    .id(message.getUid())
                                    .name("ollama_error"));
                            emitter.complete();
                        } catch (Exception e) {
                            emitter.completeWithError(e);
                        }
                    });
        }
    }

    public Optional<OllamaChatModel> getOllamaChatModel() {
        return bytedeskOllamaChatModel;
    }

}
