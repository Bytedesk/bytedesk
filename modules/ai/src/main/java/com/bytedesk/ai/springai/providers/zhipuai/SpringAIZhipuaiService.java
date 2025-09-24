/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-26 16:58:56
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-25 07:47:55
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
import java.util.Optional;

import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.zhipuai.ZhiPuAiChatModel;
import org.springframework.ai.zhipuai.ZhiPuAiChatOptions;
import org.springframework.ai.zhipuai.api.ZhiPuAiApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.bytedesk.ai.provider.LlmProviderEntity;
import com.bytedesk.ai.provider.LlmProviderRestService;
import com.bytedesk.ai.robot.RobotLlm;
import com.bytedesk.ai.robot.RobotProtobuf;
import com.bytedesk.ai.springai.service.BaseSpringAIService;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.llm.LlmProviderConstants;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.MessageTypeEnum;
import com.bytedesk.core.message.content.StreamContent;
import com.bytedesk.ai.springai.service.ChatTokenUsage;

import lombok.extern.slf4j.Slf4j;

/**
 * Spring AI ZhiPuAI服务
 */
@Slf4j
@Service
public class SpringAIZhipuaiService extends BaseSpringAIService {

    @Autowired
    private LlmProviderRestService llmProviderRestService;

    public SpringAIZhipuaiService() {
        super(); // 调用基类的无参构造函数
    }

    /**
     * 根据机器人配置创建动态的ZhiPuAiChatOptions
     * 
     * @param llm 机器人LLM配置
     * @return 根据机器人配置创建的选项
     */
    private ZhiPuAiChatOptions createZhipuaiOptions(RobotLlm llm) {
        return super.createDynamicOptions(llm, robotLlm -> 
            ZhiPuAiChatOptions.builder()
                .model(robotLlm.getTextModel())
                .temperature(robotLlm.getTemperature())
                .topP(robotLlm.getTopP())
                .build()
        );
    }

    public ZhiPuAiApi createZhipuaiApi(String apiKey) {
        return new ZhiPuAiApi(apiKey);
    }

    /**
     * 根据机器人配置创建动态的ZhiPuAiChatModel
     * 
     * @param llm 机器人LLM配置
     * @return 配置了特定模型的ZhiPuAiChatModel
     */
    private ZhiPuAiChatModel createZhipuaiChatModel(RobotLlm llm) {

        Optional<LlmProviderEntity> llmProviderOptional = llmProviderRestService.findByUid(llm.getTextProviderUid());
        if (llmProviderOptional.isEmpty()) {
            log.warn("LlmProvider with uid {} not found", llm.getTextProviderUid());
            return null;
        }
        // 使用动态的ZhiPuAiApi实例
        LlmProviderEntity provider = llmProviderOptional.get();
        ZhiPuAiApi zhipuaiApi = createZhipuaiApi(provider.getApiKey());
        ZhiPuAiChatOptions options = createZhipuaiOptions(llm);
        if (options == null) {
            return null;
        }
        return new ZhiPuAiChatModel(zhipuaiApi, options);
    }

    @Override
    protected void processPromptWebsocket(Prompt prompt, RobotProtobuf robot, MessageProtobuf messageProtobufQuery,
            MessageProtobuf messageProtobufReply) {
        // 从robot中获取llm配置
        RobotLlm llm = robot.getLlm();
        if (llm == null) {
            log.info("Zhipuai API not available");
            sendMessageWebsocket(MessageTypeEnum.ERROR, I18Consts.I18N_SERVICE_TEMPORARILY_UNAVAILABLE, messageProtobufReply);
            return;
        }

        // 获取适当的模型实例
        ZhiPuAiChatModel chatModel = createZhipuaiChatModel(llm);
        if (chatModel == null) {
            log.info("Zhipuai API not available");
            sendMessageWebsocket(MessageTypeEnum.ERROR, I18Consts.I18N_SERVICE_TEMPORARILY_UNAVAILABLE, messageProtobufReply);
            return;
        }
        
        long startTime = System.currentTimeMillis();
        final boolean[] success = {false};
        final ChatTokenUsage[] tokenUsage = {new ChatTokenUsage(0, 0, 0)};
        
        try {
            chatModel.stream(prompt).subscribe(
                    response -> {
                        if (response != null) {
                            log.info("Zhipuai API response metadata: {}", response.getMetadata());
                            List<Generation> generations = response.getResults();
                            for (Generation generation : generations) {
                                AssistantMessage assistantMessage = generation.getOutput();
                                String textContent = assistantMessage.getText();
                                log.info("Zhipuai API Websocket response text: {}", textContent);

                                sendMessageWebsocket(MessageTypeEnum.ROBOT_STREAM, textContent, messageProtobufReply);
                            }
                            // 提取token使用情况
                            tokenUsage[0] = extractTokenUsage(response);
                            success[0] = true;
                        }
                    },
                    error -> {
                        log.error("Zhipuai API error: ", error);
                        sendMessageWebsocket(MessageTypeEnum.ERROR, I18Consts.I18N_SERVICE_TEMPORARILY_UNAVAILABLE, messageProtobufReply);
                        success[0] = false;
                    },
                    () -> {
                        log.info("Chat stream completed");
                        // 记录token使用情况
                        long responseTime = System.currentTimeMillis() - startTime;
                        String modelType = (llm != null && StringUtils.hasText(llm.getTextModel())) ? llm.getTextModel() : "glm-4";
                        recordAiTokenUsage(robot, LlmProviderConstants.ZHIPUAI, modelType, 
                                tokenUsage[0].getPromptTokens(), tokenUsage[0].getCompletionTokens(), success[0], responseTime);
                    });
        } catch (Exception e) {
            log.error("Error processing Zhipuai prompt", e);
            sendMessageWebsocket(MessageTypeEnum.ERROR, I18Consts.I18N_SERVICE_TEMPORARILY_UNAVAILABLE, messageProtobufReply);
            success[0] = false;
            // 记录token使用情况
            long responseTime = System.currentTimeMillis() - startTime;
            String modelType = (llm != null && StringUtils.hasText(llm.getTextModel())) ? llm.getTextModel() : "glm-4";
            recordAiTokenUsage(robot, LlmProviderConstants.ZHIPUAI, modelType, 
                    tokenUsage[0].getPromptTokens(), tokenUsage[0].getCompletionTokens(), success[0], responseTime);
        }
    }

    @Override
    protected String processPromptSync(String message, RobotProtobuf robot) {
        long startTime = System.currentTimeMillis();
        boolean success = false;
        ChatTokenUsage tokenUsage = new ChatTokenUsage(0, 0, 0);
        
        
        // 从robot中获取llm配置
        RobotLlm llm = robot.getLlm();

        if (llm == null) {
            log.info("Zhipuai API not available");
            return "Zhipuai service is not available";
        }

        // 获取适当的模型实例
        ZhiPuAiChatModel chatModel = createZhipuaiChatModel(llm);

        try {
            try {
                // 如果有robot参数，尝试创建自定义选项
                if (robot != null && robot.getLlm() != null) {
                    // 创建自定义选项
                    ZhiPuAiChatOptions customOptions = createZhipuaiOptions(robot.getLlm());
                    if (customOptions != null) {
                        // 使用自定义选项创建Prompt
                        Prompt prompt = new Prompt(message, customOptions);
                        var response = chatModel.call(prompt);
                        log.info("Zhipuai API Sync response metadata: {}", response.getMetadata());
                        tokenUsage = extractTokenUsage(response);
                        success = true;
                        return extractTextFromResponse(response);
                    }
                }
                var response = chatModel.call(message);
                tokenUsage = extractTokenUsage(response);
                success = true;
                return extractTextFromResponse(response);
            } catch (Exception e) {
                log.error("Zhipuai API sync error", e);
                success = false;
                return I18Consts.I18N_SERVICE_TEMPORARILY_UNAVAILABLE;
            }

        } catch (Exception e) {
            log.error("Zhipuai API sync error", e);
            success = false;
            return I18Consts.I18N_SERVICE_TEMPORARILY_UNAVAILABLE;
        } finally {
            // 记录token使用情况
            long responseTime = System.currentTimeMillis() - startTime;
            String modelType = (robot != null && robot.getLlm() != null && StringUtils.hasText(robot.getLlm().getTextModel())) 
                    ? robot.getLlm().getTextModel() : "glm-4";
            recordAiTokenUsage(robot, LlmProviderConstants.ZHIPUAI, modelType, 
                    tokenUsage.getPromptTokens(), tokenUsage.getCompletionTokens(), success, responseTime);
        }
    }

    @Override
    protected void processPromptSse(Prompt prompt, RobotProtobuf robot, MessageProtobuf messageProtobufQuery,
            MessageProtobuf messageProtobufReply, List<StreamContent.SourceReference> sourceReferences, SseEmitter emitter) {
        // 从robot中获取llm配置
        RobotLlm llm = robot.getLlm();

        if (llm == null) {
            log.info("Zhipuai API not available");
            sendStreamEndMessage(messageProtobufQuery, messageProtobufReply, emitter, 0, 0, 0, prompt,
                    LlmProviderConstants.ZHIPUAI,
                    (llm != null && StringUtils.hasText(llm.getTextModel())) ? llm.getTextModel() : "glm-4");
            return;
        }

        // 获取适当的模型实例
        ZhiPuAiChatModel chatModel = createZhipuaiChatModel(llm);

        if (chatModel == null) {
            log.info("Zhipuai API not available");
            // 使用sendStreamEndMessage方法替代重复的代码
            sendStreamEndMessage(messageProtobufQuery, messageProtobufReply, emitter, 0, 0, 0, prompt,
                    LlmProviderConstants.ZHIPUAI,
                    (llm != null && StringUtils.hasText(llm.getTextModel())) ? llm.getTextModel() : "glm-4");
            return;
        }

        long startTime = System.currentTimeMillis();
        final boolean[] success = {false};
        final ChatTokenUsage[] tokenUsage = {new ChatTokenUsage(0, 0, 0)};

        try {
            // 发送初始消息，告知用户请求已收到，正在处理
            sendStreamStartMessage(messageProtobufReply, emitter, I18Consts.I18N_THINKING);

            chatModel.stream(prompt).subscribe(
                    response -> {
                        try {
                            if (response != null && !isEmitterCompleted(emitter)) {
                                List<Generation> generations = response.getResults();
                                for (Generation generation : generations) {
                                    AssistantMessage assistantMessage = generation.getOutput();
                                    String textContent = assistantMessage.getText();
                                    log.info("Zhipuai API SSE response text: {}", textContent);

                                    sendStreamMessage(messageProtobufQuery, messageProtobufReply, emitter, textContent);
                                }
                                // 提取token使用情况
                                tokenUsage[0] = extractTokenUsage(response);
                                success[0] = true;
                            }
                        } catch (Exception e) {
                            log.error("Zhipuai API SSE error 1: ", e);
                            handleSseError(e, messageProtobufQuery, messageProtobufReply, emitter);
                            success[0] = false;
                        }
                    },
                    error -> {
                        log.error("Zhipuai API SSE error 2: ", error);
                        handleSseError(error, messageProtobufQuery, messageProtobufReply, emitter);
                        success[0] = false;
                    },
                    () -> {
                        log.info("Zhipuai API SSE complete");
                        // 发送流结束消息，包含token使用情况和prompt内容
                        sendStreamEndMessage(messageProtobufQuery, messageProtobufReply, emitter,
                                tokenUsage[0].getPromptTokens(), tokenUsage[0].getCompletionTokens(),
                                tokenUsage[0].getTotalTokens(), prompt, LlmProviderConstants.ZHIPUAI,
                                (llm != null && StringUtils.hasText(llm.getTextModel())) ? llm.getTextModel()
                                        : "glm-4");
                        // 记录token使用情况
                        long responseTime = System.currentTimeMillis() - startTime;
                        String modelType = (llm != null && StringUtils.hasText(llm.getTextModel())) ? llm.getTextModel()
                                : "glm-4";
                        recordAiTokenUsage(robot, LlmProviderConstants.ZHIPUAI, modelType,
                                tokenUsage[0].getPromptTokens(), tokenUsage[0].getCompletionTokens(), success[0],
                                responseTime);
                    });
        } catch (Exception e) {
            log.error("Error starting Zhipuai stream 4", e);
            handleSseError(e, messageProtobufQuery, messageProtobufReply, emitter);
            success[0] = false;
            // 记录token使用情况
            long responseTime = System.currentTimeMillis() - startTime;
            String modelType = (llm != null && StringUtils.hasText(llm.getTextModel())) ? llm.getTextModel() : "glm-4";
            recordAiTokenUsage(robot, LlmProviderConstants.ZHIPUAI, modelType,
                    tokenUsage[0].getPromptTokens(), tokenUsage[0].getCompletionTokens(), success[0], responseTime);
        }
    }

}
