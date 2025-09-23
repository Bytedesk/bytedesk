/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-28 11:44:03
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-23 16:23:28
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.providers.dashscope;

import java.util.List;
import java.util.Optional;

import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.bytedesk.ai.provider.LlmProviderEntity;
import com.bytedesk.ai.provider.LlmProviderRestService;
import com.bytedesk.ai.robot.RobotLlm;
import com.bytedesk.ai.robot.RobotProtobuf;
import com.bytedesk.ai.springai.service.BaseSpringAIService;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.llm.LlmProviderConstants;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.MessageTypeEnum;
import com.bytedesk.ai.springai.service.ChatTokenUsage;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SpringAIDashscopeService extends BaseSpringAIService {

    @Autowired
    private LlmProviderRestService llmProviderRestService;

    @Autowired(required = false)
    @Qualifier("bytedeskDashscopeChatModel")
    private DashScopeChatModel defaultChatModel;

    public SpringAIDashscopeService() {
        super(); // 调用基类的无参构造函数
    }

    /**
     * 根据机器人配置创建动态的DashScopeChatOptions
     * 
     * @param llm 机器人LLM配置
     * @return 根据机器人配置创建的选项
     */
    private DashScopeChatOptions createDashscopeOptions(RobotLlm llm) {
        return super.createDynamicOptions(llm, robotLlm -> 
            DashScopeChatOptions.builder()
                .withModel(robotLlm.getTextModel())
                .withTemperature(robotLlm.getTemperature())
                .withTopP(robotLlm.getTopP())
                .build()
        );
    }

    public DashScopeApi createDashscopeApi(String apiUrl, String apiKey) {
        return DashScopeApi.builder()
                .baseUrl(apiUrl)
                .apiKey(apiKey)
                .build();
    }

    /**
     * 根据机器人配置创建动态的DashScopeChatModel
     * 
     * @param llm 机器人LLM配置
     * @return 配置了特定模型的DashScopeChatModel
     */
    private DashScopeChatModel createDashscopeChatModel(RobotLlm llm) {
        if (llm == null || llm.getTextProviderUid() == null) {
            log.warn("RobotLlm or textProviderUid is null, using default chat model");
            return defaultChatModel;
        }

        Optional<LlmProviderEntity> llmProviderOptional = llmProviderRestService.findByUid(llm.getTextProviderUid());
        if (llmProviderOptional.isEmpty()) {
            log.warn("LlmProvider with uid {} not found, using default chat model", llm.getTextProviderUid());
            return defaultChatModel;
        }

        LlmProviderEntity provider = llmProviderOptional.get();
        if (provider.getApiKey() == null || provider.getApiKey().trim().isEmpty()) {
            log.warn("API key is not configured for provider {}, using default chat model", provider.getUid());
            return defaultChatModel;
        }

        try {
            log.info("Creating dynamic Dashscope chat model with provider: {} ({})", provider.getType(), provider.getUid());
            // 使用动态的DashScopeApi实例
            DashScopeApi dashscopeApi = createDashscopeApi(provider.getBaseUrl(), provider.getApiKey());
            DashScopeChatOptions options = createDashscopeOptions(llm);
            if (options == null) {
                log.warn("Failed to create Dashscope options, using default chat model");
                return defaultChatModel;
            }
            return DashScopeChatModel.builder()
                    .dashScopeApi(dashscopeApi)
                    .defaultOptions(options)
                    .build();
        } catch (Exception e) {
            log.error("Failed to create dynamic Dashscope chat model for provider {}, using default chat model", provider.getUid(), e);
            return defaultChatModel;
        }
    }

    @Override
    protected void processPromptWebsocket(Prompt prompt, RobotProtobuf robot, MessageProtobuf messageProtobufQuery,
            MessageProtobuf messageProtobufReply, String fullPromptContent) {
        // 从robot中获取llm配置
        RobotLlm llm = robot.getLlm();
        log.info("Dashscope API websocket fullPromptContent: {}", fullPromptContent);
        if (llm == null) {
            log.info("Dashscope API not available");
            sendMessageWebsocket(MessageTypeEnum.ERROR, I18Consts.I18N_SERVICE_TEMPORARILY_UNAVAILABLE, messageProtobufReply);
            return;
        }

        // 获取适当的模型实例
        DashScopeChatModel chatModel = createDashscopeChatModel(llm);
        if (chatModel == null) {
            log.error("Failed to create Dashscope chat model and no default chat model available");
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
                            log.info("Dashscope API response metadata: {}", response.getMetadata());
                            List<Generation> generations = response.getResults();
                            for (Generation generation : generations) {
                                AssistantMessage assistantMessage = generation.getOutput();
                                String textContent = assistantMessage.getText();
                                log.info("Dashscope API Websocket response text: {}", textContent);

                                sendMessageWebsocket(MessageTypeEnum.ROBOT_STREAM, textContent, messageProtobufReply);
                            }
                            // 提取token使用情况
                            tokenUsage[0] = extractTokenUsage(response);
                            success[0] = true;
                        }
                    },
                    error -> {
                        log.error("Dashscope API error: ", error);
                        sendMessageWebsocket(MessageTypeEnum.ERROR, I18Consts.I18N_SERVICE_TEMPORARILY_UNAVAILABLE, messageProtobufReply);
                        success[0] = false;
                    },
                    () -> {
                        log.info("Chat stream completed");
                        // 记录token使用情况
                        long responseTime = System.currentTimeMillis() - startTime;
                        String modelType = (llm != null && StringUtils.hasText(llm.getTextModel())) ? llm.getTextModel() : "qwen-turbo";
                        recordAiTokenUsage(robot, LlmProviderConstants.DASHSCOPE, modelType, 
                                tokenUsage[0].getPromptTokens(), tokenUsage[0].getCompletionTokens(), success[0], responseTime);
                    });
        } catch (Exception e) {
            log.error("Error processing Dashscope prompt", e);
            sendMessageWebsocket(MessageTypeEnum.ERROR, I18Consts.I18N_SERVICE_TEMPORARILY_UNAVAILABLE, messageProtobufReply);
            success[0] = false;
            // 记录token使用情况
            long responseTime = System.currentTimeMillis() - startTime;
            String modelType = (llm != null && StringUtils.hasText(llm.getTextModel())) ? llm.getTextModel() : "qwen-turbo";
            recordAiTokenUsage(robot, LlmProviderConstants.DASHSCOPE, modelType, 
                    tokenUsage[0].getPromptTokens(), tokenUsage[0].getCompletionTokens(), success[0], responseTime);
        }
    }

    @Override
    protected String processPromptSync(String message, RobotProtobuf robot, String fullPromptContent) {
        long startTime = System.currentTimeMillis();
        boolean success = false;
        ChatTokenUsage tokenUsage = new ChatTokenUsage(0, 0, 0);
        
        log.info("Dashscope API sync fullPromptContent: {}", fullPromptContent);
        
        // 从robot中获取llm配置
        RobotLlm llm = robot.getLlm();
        log.info("Dashscope API websocket fullPromptContent: {}", fullPromptContent);

        if (llm == null) {
            log.info("Dashscope API not available");
            return "Dashscope service is not available";
        }

        // 获取适当的模型实例
        DashScopeChatModel chatModel = createDashscopeChatModel(llm);

        try {
            try {
                // 如果有robot参数，尝试创建自定义选项
                if (robot != null && robot.getLlm() != null) {
                    // 创建自定义选项
                    DashScopeChatOptions customOptions = createDashscopeOptions(robot.getLlm());
                    if (customOptions != null) {
                        // 使用自定义选项创建Prompt
                        Prompt prompt = new Prompt(message, customOptions);
                        var response = chatModel.call(prompt);
                        log.info("Dashscope API Sync response metadata: {}", response.getMetadata());
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
                log.error("Dashscope API sync error", e);
                success = false;
                return I18Consts.I18N_SERVICE_TEMPORARILY_UNAVAILABLE;
            }

        } catch (Exception e) {
            log.error("Dashscope API sync error", e);
            success = false;
            return I18Consts.I18N_SERVICE_TEMPORARILY_UNAVAILABLE;
        } finally {
            // 记录token使用情况
            long responseTime = System.currentTimeMillis() - startTime;
            String modelType = (robot != null && robot.getLlm() != null && StringUtils.hasText(robot.getLlm().getTextModel())) 
                    ? robot.getLlm().getTextModel() : "qwen-turbo";
            recordAiTokenUsage(robot, LlmProviderConstants.DASHSCOPE, modelType, 
                    tokenUsage.getPromptTokens(), tokenUsage.getCompletionTokens(), success, responseTime);
        }
    }

    @Override
    protected void processPromptSse(Prompt prompt, RobotProtobuf robot, MessageProtobuf messageProtobufQuery,
            MessageProtobuf messageProtobufReply, SseEmitter emitter, String fullPromptContent) {
        // 从robot中获取llm配置
        RobotLlm llm = robot.getLlm();
        log.info("Dashscope API SSE fullPromptContent: {}", fullPromptContent);

        if (llm == null) {
            log.info("Dashscope API not available");
            sendStreamEndMessage(messageProtobufQuery, messageProtobufReply, emitter, 0, 0, 0, fullPromptContent,
                    LlmProviderConstants.DASHSCOPE,
                    (llm != null && StringUtils.hasText(llm.getTextModel())) ? llm.getTextModel() : "qwen-turbo");
            return;
        }

        // 获取适当的模型实例
        DashScopeChatModel chatModel = createDashscopeChatModel(llm);

        if (chatModel == null) {
            log.error("Failed to create Dashscope chat model and no default chat model available");
            // 使用sendStreamEndMessage方法替代重复的代码
            sendStreamEndMessage(messageProtobufQuery, messageProtobufReply, emitter, 0, 0, 0, fullPromptContent,
                    LlmProviderConstants.DASHSCOPE,
                    (llm != null && StringUtils.hasText(llm.getTextModel())) ? llm.getTextModel() : "qwen-turbo");
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
                                    log.info("Dashscope API SSE response text: {}", textContent);

                                    sendStreamMessage(messageProtobufQuery, messageProtobufReply, emitter, textContent);
                                }
                                // 提取token使用情况
                                tokenUsage[0] = extractTokenUsage(response);
                                success[0] = true;
                            }
                        } catch (Exception e) {
                            log.error("Dashscope API SSE error 1: ", e);
                            handleSseError(e, messageProtobufQuery, messageProtobufReply, emitter);
                            success[0] = false;
                        }
                    },
                    error -> {
                        log.error("Dashscope API SSE error 2: ", error);
                        handleSseError(error, messageProtobufQuery, messageProtobufReply, emitter);
                        success[0] = false;
                    },
                    () -> {
                        log.info("Dashscope API SSE complete");
                        // 发送流结束消息，包含token使用情况和prompt内容
                        sendStreamEndMessage(messageProtobufQuery, messageProtobufReply, emitter,
                                tokenUsage[0].getPromptTokens(), tokenUsage[0].getCompletionTokens(),
                                tokenUsage[0].getTotalTokens(), fullPromptContent, LlmProviderConstants.DASHSCOPE,
                                (llm != null && StringUtils.hasText(llm.getTextModel())) ? llm.getTextModel()
                                        : "qwen-turbo");
                        // 记录token使用情况
                        long responseTime = System.currentTimeMillis() - startTime;
                        String modelType = (llm != null && StringUtils.hasText(llm.getTextModel())) ? llm.getTextModel()
                                : "qwen-turbo";
                        recordAiTokenUsage(robot, LlmProviderConstants.DASHSCOPE, modelType,
                                tokenUsage[0].getPromptTokens(), tokenUsage[0].getCompletionTokens(), success[0],
                                responseTime);
                    });
        } catch (Exception e) {
            log.error("Error starting Dashscope stream 4", e);
            handleSseError(e, messageProtobufQuery, messageProtobufReply, emitter);
            success[0] = false;
            // 记录token使用情况
            long responseTime = System.currentTimeMillis() - startTime;
            String modelType = (llm != null && StringUtils.hasText(llm.getTextModel())) ? llm.getTextModel() : "qwen-turbo";
            recordAiTokenUsage(robot, LlmProviderConstants.DASHSCOPE, modelType,
                    tokenUsage[0].getPromptTokens(), tokenUsage[0].getCompletionTokens(), success[0], responseTime);
        }
    }

}
