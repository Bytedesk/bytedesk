/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-28 11:44:03
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-25 07:46:35
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM –
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 *  contact: 270580156@qq.com
 *
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved.
 */
package com.bytedesk.ai.springai.providers.siliconflow;

import com.bytedesk.ai.robot.RobotLlm;
import com.bytedesk.ai.robot.RobotProtobuf;
import com.bytedesk.ai.service.BaseSpringAIService;
import com.bytedesk.ai.service.ChatTokenUsage;
import com.bytedesk.ai.service.PromptHelper;
import com.bytedesk.ai.service.SseMessageHelper;
import com.bytedesk.ai.service.TokenUsageHelper;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.llm.LlmProviderConstants;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.content.RobotContent;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.util.List;
import java.util.Optional;

/**
 * @author: https://github.com/fzj111
 *          date: 2025-03-19
 */
@Slf4j
@Service
@ConditionalOnProperty(prefix = "spring.ai.siliconflow.chat", name = "enabled", havingValue = "true", matchIfMissing = false)
public class SpringAISiliconFlowChatService extends BaseSpringAIService {

    @Autowired(required = false)
    private Optional<OpenAiChatModel> siliconFlowChatModel;

    @Autowired
    private TokenUsageHelper tokenUsageHelper;

    @Autowired
    private SseMessageHelper sseMessageHelper;

    @Autowired
    private PromptHelper promptHelper;

    public SpringAISiliconFlowChatService() {
        super(); // 调用基类的无参构造函数
    }

    /**
     * 根据机器人配置创建动态的OpenAiChatOptions
     * 
     * @param llm 机器人LLM配置
     * @return 根据机器人配置创建的选项
     */
    private OpenAiChatOptions createDynamicOptions(RobotLlm llm) {
        if (llm == null || !StringUtils.hasText(llm.getTextModel())) {
            return null;
        }
    try {
            return OpenAiChatOptions.builder()
                    .model(llm.getTextModel())
                    .temperature(llm.getTemperature())
                    .topP(llm.getTopP())
                    .build();
        } catch (Exception e) {
            log.error("Error creating dynamic OpenAiChatOptions for model {}", llm.getTextModel(), e);
            return null;
        }
    }

    // @Override
    // protected void processPromptWebsocket(Prompt prompt, RobotProtobuf robot, MessageProtobuf messageProtobufQuery, MessageProtobuf messageProtobufReply) {
    //     // 从robot中获取llm配置
    // RobotLlm llm = robot.getLlm();
    //     log.info("SiliconFlow API websocket ");
        
    //     if (!siliconFlowChatModel.isPresent()) {
    //         sseMessageHelper.sendMessageWebsocket(MessageTypeEnum.ERROR, I18Consts.I18N_SERVICE_TEMPORARILY_UNAVAILABLE, messageProtobufReply);
    //         return;
    //     }
        
    //     // 如果有自定义选项，创建新的Prompt
    //     Prompt requestPrompt = prompt;
    //     OpenAiChatOptions customOptions = createDynamicOptions(llm);
    //     if (customOptions != null) {
    //         requestPrompt = new Prompt(prompt.getInstructions(), customOptions);
    //     }
        
    //     long startTime = System.currentTimeMillis();
    //     final boolean[] success = {false};
    //     final ChatTokenUsage[] tokenUsage = {new ChatTokenUsage(0, 0, 0)};
        
    //     // 使用同一个ChatModel实例，但传入不同的选项
    //     siliconFlowChatModel.get().stream(requestPrompt).subscribe(
    //             response -> {
    //                 if (response != null) {
    //                     log.info("siliconFlow API response metadata: {}", response.getMetadata());
    //                     List<Generation> generations = response.getResults();
    //                     for (Generation generation : generations) {
    //                         AssistantMessage assistantMessage = generation.getOutput();
    //                         String textContent = assistantMessage.getText();

    //                         sseMessageHelper.sendMessageWebsocket(MessageTypeEnum.ROBOT_STREAM, textContent, messageProtobufReply);
    //                     }
    //                     // 提取token使用情况
    //                     tokenUsage[0] = tokenUsageHelper.extractTokenUsage(response);
    //                     success[0] = true;
    //                 }
    //             },
    //             error -> {
    //                 log.error("siliconFlow API error: ", error);
    //                 sseMessageHelper.sendMessageWebsocket(MessageTypeEnum.ERROR, I18Consts.I18N_SERVICE_TEMPORARILY_UNAVAILABLE, messageProtobufReply);
    //                 success[0] = false;
    //             },
    //             () -> {
    //                 log.info("Chat stream completed");
    //                 // 记录token使用情况
    //                 long responseTime = System.currentTimeMillis() - startTime;
    //                 String modelType = (llm != null && StringUtils.hasText(llm.getTextModel())) ? llm.getTextModel() : LlmProviderConstants.SILICONFLOW;
    //         tokenUsageHelper.recordAiTokenUsage(robot, LlmProviderConstants.SILICONFLOW, modelType, 
    //             tokenUsage[0].getPromptTokens(), tokenUsage[0].getCompletionTokens(), success[0], responseTime);
    //             });
    // }    // @Override
    

    @Override
    protected String processPromptSync(String message, RobotProtobuf robot) {
        long startTime = System.currentTimeMillis();
        boolean success = false;
        ChatTokenUsage tokenUsage = new ChatTokenUsage(0, 0, 0);
        
        log.info("SiliconFlow API sync ");
        
        try {
            if (!siliconFlowChatModel.isPresent()) {
                return "siliconFlow service is not available";
            }

            try {
                // 如果有robot参数，尝试创建自定义选项
                if (robot != null && robot.getLlm() != null) {
                    // 创建自定义选项
                    OpenAiChatOptions customOptions = createDynamicOptions(robot.getLlm());
                    if (customOptions != null) {
                        // 使用自定义选项创建Prompt
                        Prompt prompt = new Prompt(message, customOptions);
                        var response = siliconFlowChatModel.get().call(prompt);
                        tokenUsage = tokenUsageHelper.extractTokenUsage(response);
                        success = true;
                        return promptHelper.extractTextFromResponse(response);
                    }
                }
                
                var response = siliconFlowChatModel.get().call(message);
                tokenUsage = tokenUsageHelper.extractTokenUsage(response);
                success = true;
                return promptHelper.extractTextFromResponse(response);
            } catch (Exception e) {
                log.error("siliconFlow API call error: ", e);
                success = false;
                return I18Consts.I18N_SERVICE_TEMPORARILY_UNAVAILABLE;
            }
        } catch (Exception e) {
            log.error("siliconFlow API sync error: ", e);
            success = false;
            return I18Consts.I18N_SERVICE_TEMPORARILY_UNAVAILABLE;
        } finally {
            // 记录token使用情况
            long responseTime = System.currentTimeMillis() - startTime;
            String modelType = (robot != null && robot.getLlm() != null && StringUtils.hasText(robot.getLlm().getTextModel())) 
                    ? robot.getLlm().getTextModel() : LlmProviderConstants.SILICONFLOW;
        tokenUsageHelper.recordAiTokenUsage(robot, LlmProviderConstants.SILICONFLOW, modelType, 
            tokenUsage.getPromptTokens(), tokenUsage.getCompletionTokens(), success, responseTime);
        }
    }

    @Override
    protected void processPromptSse(Prompt prompt, RobotProtobuf robot, MessageProtobuf messageProtobufQuery,
            MessageProtobuf messageProtobufReply, List<RobotContent.SourceReference> sourceReferences, SseEmitter emitter) {
        // 从robot中获取llm配置
    RobotLlm llm = robot.getLlm();
        log.info("SiliconFlow API SSE ");

        if (!siliconFlowChatModel.isPresent()) {
        sseMessageHelper.handleSseError(new RuntimeException("SiliconFlow service not available"), messageProtobufQuery,
            messageProtobufReply, emitter);
            return;
        }

        // 发送起始消息
    sseMessageHelper.sendStreamStartMessage(messageProtobufQuery, messageProtobufReply, emitter, I18Consts.I18N_THINKING);

        Prompt requestPrompt = prompt;
        OpenAiChatOptions customOptions = createDynamicOptions(llm);
        if (customOptions != null) {
            requestPrompt = new Prompt(prompt.getInstructions(), customOptions);
        }

        long startTime = System.currentTimeMillis();
        final boolean[] success = {false};
        final ChatTokenUsage[] tokenUsage = {new ChatTokenUsage(0, 0, 0)};

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
                                
                                sseMessageHelper.sendStreamMessage(messageProtobufQuery, messageProtobufReply, emitter, textContent, null, sourceReferences);
                            }
                            // 提取token使用情况
                            tokenUsage[0] = tokenUsageHelper.extractTokenUsage(response);
                            success[0] = true;
                        }
                    } catch (Exception e) {
                        log.error("Error sending SSE event", e);
                        sseMessageHelper.handleSseError(e, messageProtobufQuery, messageProtobufReply, emitter);
                        success[0] = false;
                    }
                },
                error -> {
                    log.error("siliconFlow API SSE error: ", error);
                    sseMessageHelper.handleSseError(error, messageProtobufQuery, messageProtobufReply, emitter);
                    success[0] = false;
                },
                () -> {
                    log.info("SiliconFlow API SSE complete");
                    // 发送流结束消息，包含token使用情况和prompt内容
                    // String promptText = extractTextFromPrompt(prompt);
                    sseMessageHelper.sendStreamEndMessage(messageProtobufQuery, messageProtobufReply, emitter, 
                            tokenUsage[0].getPromptTokens(), tokenUsage[0].getCompletionTokens(), tokenUsage[0].getTotalTokens(), prompt, LlmProviderConstants.SILICONFLOW, (llm != null && StringUtils.hasText(llm.getTextModel())) ? llm.getTextModel() : "siliconflow-chat");
                    // 记录token使用情况
                    long responseTime = System.currentTimeMillis() - startTime;
                    String modelType = (llm != null && StringUtils.hasText(llm.getTextModel())) ? llm.getTextModel() : LlmProviderConstants.SILICONFLOW;
            tokenUsageHelper.recordAiTokenUsage(robot, LlmProviderConstants.SILICONFLOW, modelType, 
                tokenUsage[0].getPromptTokens(), tokenUsage[0].getCompletionTokens(), success[0], responseTime);
                });
    }

    public Optional<OpenAiChatModel> getChatModel() {
        return siliconFlowChatModel;
    }    public Boolean isServiceHealthy() {
        if (!siliconFlowChatModel.isPresent()) {
            return false;
        }

        try {
            // 发送一个简单的测试请求来检测服务是否响应
            String response = processPromptSync("test", null);
            return !response.contains("不可用") && !response.equals("siliconFlow service is not available");
        } catch (Exception e) {
            log.error("Error checking SiliconFlow service health", e);
            return false;
        }
    }
}
