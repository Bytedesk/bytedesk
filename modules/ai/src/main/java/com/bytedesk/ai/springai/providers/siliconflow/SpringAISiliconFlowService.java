/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-28 11:44:03
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-22 11:09:13
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
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import com.bytedesk.ai.robot.RobotLlm;

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
     * 根据机器人配置创建动态的OpenAiChatModel（适用于SiliconFlow）
     * 
     * @param llm 机器人LLM配置
     * @return 配置了特定模型的OpenAiChatModel
     */
    private OpenAiChatModel createDynamicChatModel(RobotLlm llm) {
        if (llm == null || !StringUtils.hasText(llm.getModel()) || !siliconFlowChatModel.isPresent()) {
            // 如果没有指定模型或设置，使用默认配置
            return siliconFlowChatModel.orElse(null);
        }

        try {
            // 获取默认模型以复用其API配置
            OpenAiChatModel defaultModel = siliconFlowChatModel.get();

            // 创建新的选项，应用自定义模型参数
            OpenAiChatOptions options = OpenAiChatOptions.builder()
                    .model(llm.getModel())
                    .temperature(llm.getTemperature())
                    .topP(llm.getTopP())
                    .build();

            // 使用相同的API客户端但应用新的选项
            return new OpenAiChatModel(defaultModel.getClient(), options);
        } catch (Exception e) {
            log.error("Error creating dynamic chat model for model {}", llm.getModel(), e);
            return siliconFlowChatModel.orElse(null);
        }
    }

    @Override
    protected void processPrompt(Prompt prompt, MessageProtobuf messageProtobuf) {
        // 从messageProtobuf的extra字段中获取llm配置
        RobotLlm llm = null;
        try {
            // 这里假设extra中有robotLlm字段，实际中可能需要调整
            if (messageProtobuf.getExtra() != null) {
                // 根据实际情况从消息中获取LLM配置
                // 如果无法获取，将使用默认配置
            }
        } catch (Exception e) {
            log.warn("Failed to extract LLM config from message, using default model", e);
        }

        // 获取适当的模型实例
        OpenAiChatModel chatModel = (llm != null) ? createDynamicChatModel(llm) : siliconFlowChatModel.orElse(null);

        if (chatModel == null) {
            messageProtobuf.setType(MessageTypeEnum.ERROR);
            messageProtobuf.setContent("SiliconFlow服务不可用");
            messageSendService.sendProtobufMessage(messageProtobuf);
            return;
        }

        chatModel.stream(prompt).subscribe(
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
    protected void processPromptSSE(Prompt prompt, MessageProtobuf messageProtobufQuery,
            MessageProtobuf messageProtobufReply, SseEmitter emitter) {
        // 从messageProtobuf中提取RobotLlm信息
        RobotLlm llm = null;
        try {
            // 此处根据实际应用逻辑从消息中获取机器人配置
            // 例如可以从messageProtobufQuery的extra字段中获取机器人配置
            if (messageProtobufQuery.getExtra() != null) {
                // 从extra中解析RobotLlm配置
                // 此处实现需根据实际应用逻辑调整
            }
        } catch (Exception e) {
            log.warn("Failed to extract robot info, using default model", e);
        }

        // 获取适当的模型实例
        OpenAiChatModel chatModel = (llm != null) ? createDynamicChatModel(llm) : siliconFlowChatModel.orElse(null);

        if (chatModel == null) {
            handleSseError(new RuntimeException("SiliconFlow service not available"), messageProtobufQuery,
                    messageProtobufReply, emitter);
            return;
        }

        chatModel.stream(prompt).subscribe(
                response -> {
                    try {
                        if (response != null) {
                            List<Generation> generations = response.getResults();
                            for (Generation generation : generations) {
                                AssistantMessage assistantMessage = generation.getOutput();
                                String textContent = assistantMessage.getText();
                                log.info("siliconFlow API response metadata: {}, text {}",
                                        response.getMetadata(), textContent);
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
    }

    // 添加新的辅助方法处理SSE错误
    private void handleSseError(Throwable error, MessageProtobuf messageProtobufQuery,
            MessageProtobuf messageProtobufReply, SseEmitter emitter) {
        try {
            messageProtobufReply.setType(MessageTypeEnum.ERROR);
            messageProtobufReply.setContent("服务暂时不可用，请稍后重试");
            // 保存消息到数据库
            persistMessage(messageProtobufQuery, messageProtobufReply);
            String messageJson = messageProtobufReply.toJson();

            emitter.send(SseEmitter.event()
                    .data(messageJson)
                    .id(messageProtobufReply.getUid())
                    .name("message"));
            emitter.complete();
        } catch (Exception e) {
            log.error("Error handling SSE error", e);
            try {
                emitter.completeWithError(e);
            } catch (Exception ex) {
                log.error("Failed to complete emitter with error", ex);
            }
        }
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
