/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-26 16:58:56
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-22 12:05:01
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
import org.springframework.ai.zhipuai.ZhiPuAiChatOptions;
import org.springframework.ai.zhipuai.api.ZhiPuAiApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.bytedesk.ai.robot.RobotLlm;
import com.bytedesk.ai.robot.RobotProtobuf;
import com.bytedesk.ai.springai.service.BaseSpringAIService;
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

    @Autowired
    @Qualifier("bytedeskZhipuaiApi")
    private ZhiPuAiApi zhipuaiApi;

    public SpringAIZhipuaiService() {
        super(); // 调用基类的无参构造函数
    }

    /**
     * 根据机器人配置创建动态的ZhiPuAiChatModel
     * 
     * @param llm 机器人LLM配置
     * @return 配置了特定模型的ZhiPuAiChatModel
     */
    private ZhiPuAiChatModel createDynamicChatModel(RobotLlm llm) {
        if (llm == null || !StringUtils.hasText(llm.getModel())) {
            // 如果没有指定模型或设置，使用默认配置
            return bytedeskZhipuaiChatModel;
        }

        try {
            ZhiPuAiChatOptions options = ZhiPuAiChatOptions.builder()
                    .model(llm.getModel())
                    .temperature(llm.getTemperature()) // 使用Double类型
                    .topP(llm.getTopP()) // 使用Double类型
                    .build();

            return new ZhiPuAiChatModel(zhipuaiApi, options);
        } catch (Exception e) {
            log.error("Error creating dynamic chat model for model {}", llm.getModel(), e);
            return bytedeskZhipuaiChatModel;
        }
    }

    /**
     * 方式1：异步流式调用
     */
    @Override
    protected void processPrompt(Prompt prompt, RobotProtobuf robot, MessageProtobuf messageProtobufQuery,
            MessageProtobuf messageProtobufReply) {
        // 从robot中获取llm配置
        RobotLlm llm = robot.getLlm();

        // 获取适当的模型实例
        ZhiPuAiChatModel chatModel = (llm != null) ? createDynamicChatModel(llm) : bytedeskZhipuaiChatModel;

        chatModel.stream(prompt).subscribe(
                response -> {
                    if (response != null) {
                        log.info("Zhipuai API response metadata: {}", response.getMetadata());
                        List<Generation> generations = response.getResults();
                        for (Generation generation : generations) {
                            AssistantMessage assistantMessage = generation.getOutput();
                            String textContent = assistantMessage.getText();

                            messageProtobufReply.setType(MessageTypeEnum.STREAM);
                            messageProtobufReply.setContent(textContent);
                            messageSendService.sendProtobufMessage(messageProtobufReply);
                        }
                    }
                },
                error -> {
                    log.error("Zhipuai API error: ", error);
                    messageProtobufReply.setType(MessageTypeEnum.ERROR);
                    messageProtobufReply.setContent("服务暂时不可用，请稍后重试");
                    messageSendService.sendProtobufMessage(messageProtobufReply);
                },
                () -> {
                    log.info("Chat stream completed");
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
    protected void processPromptSSE(Prompt prompt, RobotProtobuf robot, MessageProtobuf messageProtobufQuery,
            MessageProtobuf messageProtobufReply, SseEmitter emitter) {
        // 从robot中获取llm配置
        RobotLlm llm = robot.getLlm();

        // 获取适当的模型实例
        ZhiPuAiChatModel chatModel = (llm != null) ? createDynamicChatModel(llm) : bytedeskZhipuaiChatModel;

        Flux<ChatResponse> responseFlux = chatModel.stream(prompt);

        responseFlux.subscribe(
                response -> {
                    try {
                        if (response != null) {
                            List<Generation> generations = response.getResults();
                            for (Generation generation : generations) {
                                AssistantMessage assistantMessage = generation.getOutput();
                                String textContent = assistantMessage.getText();
                                // log.info("Zhipuai API response metadata: {}, text {}",response.getMetadata(),
                                // textContent);
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
                        handleSseError(e, messageProtobufQuery, messageProtobufReply, emitter);
                    }
                },
                error -> {
                    log.error("Zhipuai API SSE error 2:", error);
                    handleSseError(error, messageProtobufQuery, messageProtobufReply, emitter);
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

    @Override
    protected String generateFaqPairs(String prompt) {
        return bytedeskZhipuaiChatModel.call(prompt);
    }

    public boolean isServiceHealthy() {
        try {
            // 发送一个简单的测试请求来检测服务是否响应
            String response = processPromptSync("test");
            return !response.contains("不可用");
        } catch (Exception e) {
            log.error("Error checking Zhipuai service health", e);
            return false;
        }
    }
}
