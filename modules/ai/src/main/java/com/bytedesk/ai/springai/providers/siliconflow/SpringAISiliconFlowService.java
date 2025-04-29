/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-28 11:44:03
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-29 11:26:23
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM –
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 *  contact: 270580156@qq.com
 *
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved.
 */
package com.bytedesk.ai.springai.providers.siliconflow;

import com.bytedesk.ai.springai.service.BaseSpringAIService;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.MessageTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.util.List;
import java.util.Optional;
import com.bytedesk.ai.robot.RobotLlm;
import com.bytedesk.ai.robot.RobotProtobuf;

/**
 * @author: https://github.com/fzj111
 *          date: 2025-03-19
 */
@Slf4j
@Service
@ConditionalOnProperty(name = "spring.ai.siliconflow.chat.enabled", havingValue = "true", matchIfMissing = false)
public class SpringAISiliconFlowService extends BaseSpringAIService {

    @Autowired(required = false)
    private Optional<OpenAiChatModel> siliconFlowChatModel;

    public SpringAISiliconFlowService() {
        super(); // 调用基类的无参构造函数
    }

    /**
     * 根据机器人配置创建动态的OpenAiChatOptions
     * 
     * @param llm 机器人LLM配置
     * @return 根据机器人配置创建的选项
     */
    private OpenAiChatOptions createDynamicOptions(RobotLlm llm) {
        return super.createDynamicOptions(llm, robotLlm -> 
            OpenAiChatOptions.builder()
                .model(robotLlm.getModel())
                .temperature(robotLlm.getTemperature())
                .topP(robotLlm.getTopP())
                .build()
        );
    }

    @Override
    protected void processPrompt(Prompt prompt, RobotProtobuf robot, MessageProtobuf messageProtobufQuery, MessageProtobuf messageProtobufReply) {
        // 从robot中获取llm配置
        RobotLlm llm = robot.getLlm();
        
        if (!siliconFlowChatModel.isPresent()) {
            sendMessage(MessageTypeEnum.ERROR, "SiliconFlow服务不可用", messageProtobufReply);
            return;
        }
        
        // 如果有自定义选项，创建新的Prompt
        Prompt requestPrompt = prompt;
        OpenAiChatOptions customOptions = createDynamicOptions(llm);
        if (customOptions != null) {
            requestPrompt = new Prompt(prompt.getInstructions(), customOptions);
        }
        
        // 使用同一个ChatModel实例，但传入不同的选项
        siliconFlowChatModel.get().stream(requestPrompt).subscribe(
                response -> {
                    if (response != null) {
                        log.info("siliconFlow API response metadata: {}", response.getMetadata());
                        List<Generation> generations = response.getResults();
                        for (Generation generation : generations) {
                            AssistantMessage assistantMessage = generation.getOutput();
                            String textContent = assistantMessage.getText();

                            sendMessage(MessageTypeEnum.STREAM, textContent, messageProtobufReply);
                        }
                    }
                },
                error -> {
                    log.error("siliconFlow API error: ", error);
                    sendMessage(MessageTypeEnum.ERROR, "服务暂时不可用，请稍后重试", messageProtobufReply);
                },
                () -> {
                    log.info("Chat stream completed");
                });
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
    protected void processPromptSSE(Prompt prompt, RobotProtobuf robot, MessageProtobuf messageProtobufQuery,
            MessageProtobuf messageProtobufReply, SseEmitter emitter) {
        // 从robot中获取llm配置
        RobotLlm llm = robot.getLlm();

        if (!siliconFlowChatModel.isPresent()) {
            handleSseError(new RuntimeException("SiliconFlow service not available"), messageProtobufQuery,
                    messageProtobufReply, emitter);
            return;
        }

        // 发送起始消息
        sendStreamStartMessage(messageProtobufReply, emitter, "正在思考中...");

        Prompt requestPrompt = prompt;
        OpenAiChatOptions customOptions = createDynamicOptions(llm);
        if (customOptions != null) {
            requestPrompt = new Prompt(prompt.getInstructions(), customOptions);
        }

        siliconFlowChatModel.get().stream(requestPrompt).subscribe(
                response -> {
                    try {
                        if (response != null) {
                            List<Generation> generations = response.getResults();
                            for (Generation generation : generations) {
                                AssistantMessage assistantMessage = generation.getOutput();
                                String textContent = assistantMessage.getText();
                                log.info("siliconFlow API response metadata: {}, text {}",
                                        response.getMetadata(), textContent);
                                
                                sendStreamMessage(messageProtobufQuery, messageProtobufReply, emitter, textContent);
                            }
                        }
                    } catch (Exception e) {
                        log.error("Error sending SSE event", e);
                        handleSseError(e, messageProtobufQuery, messageProtobufReply, emitter);
                    }
                },
                error -> {
                    log.error("siliconFlow API SSE error: ", error);
                    handleSseError(error, messageProtobufQuery, messageProtobufReply, emitter);
                },
                () -> {
                    log.info("siliconFlow API SSE complete");
                    sendStreamEndMessage(messageProtobufQuery, messageProtobufReply, emitter);
                });
    }

    public Optional<OpenAiChatModel> getSiliconFlowChatModel() {
        return siliconFlowChatModel;
    }

    public boolean isServiceHealthy() {
        if (!siliconFlowChatModel.isPresent()) {
            return false;
        }

        try {
            // 发送一个简单的测试请求来检测服务是否响应
            String response = processPromptSync("test");
            return !response.contains("不可用") && !response.equals("siliconFlow service is not available");
        } catch (Exception e) {
            log.error("Error checking SiliconFlow service health", e);
            return false;
        }
    }
}
