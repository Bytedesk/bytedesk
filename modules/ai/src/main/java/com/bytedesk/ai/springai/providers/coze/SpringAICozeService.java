/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-08-25 00:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-26 17:13:45
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.providers.coze;

import java.util.Collections;
import java.util.Optional;

import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.bytedesk.ai.provider.LlmProviderEntity;
import com.bytedesk.ai.provider.LlmProviderRestService;
import com.bytedesk.ai.robot.RobotLlm;
import com.bytedesk.ai.robot.RobotProtobuf;
import com.bytedesk.ai.springai.service.BaseSpringAIService;
import com.bytedesk.ai.springai.service.ChatTokenUsage;
import com.bytedesk.core.constant.LlmConsts;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.MessageTypeEnum;
import com.coze.openapi.client.chat.CreateChatReq;
import com.coze.openapi.client.chat.model.ChatEvent;
import com.coze.openapi.client.chat.model.ChatEventType;
import com.coze.openapi.client.chat.model.ChatPoll;
import com.coze.openapi.client.connversations.message.model.Message;
import com.coze.openapi.client.connversations.message.model.MessageType;
import com.coze.openapi.service.auth.TokenAuth;
import com.coze.openapi.service.service.CozeAPI;

import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import lombok.extern.slf4j.Slf4j;

/**
 * Coze AI服务类
 * 使用 Coze OpenAPI SDK 实现聊天功能，继承自BaseSpringAIService
 * https://www.coze.cn/open/docs/developer_guides
 */
@Slf4j
@Service
public class SpringAICozeService extends BaseSpringAIService {

    @Autowired
    private LlmProviderRestService llmProviderRestService;

    public SpringAICozeService() {
        super();
    }

    /**
     * 根据机器人配置创建动态的CozeAPI实例
     * 
     * @param llm 机器人LLM配置
     * @return 配置了特定参数的CozeAPI
     */
    private CozeAPI createDynamicCozeApi(RobotLlm llm) {
        if (llm == null || llm.getTextProviderUid() == null) {
            log.warn("RobotLlm or textProviderUid is null");
            return null;
        }

        Optional<LlmProviderEntity> llmProviderOptional = llmProviderRestService.findByUid(llm.getTextProviderUid());
        if (llmProviderOptional.isEmpty()) {
            log.warn("LlmProvider with uid {} not found", llm.getTextProviderUid());
            return null;
        }

        LlmProviderEntity provider = llmProviderOptional.get();
        String apiKey = provider.getApiKey();
        String baseUrl = provider.getApiUrl();
        
        if (apiKey == null || apiKey.trim().isEmpty()) {
            log.warn("API key is not configured for provider {}", provider.getUid());
            return null;
        }

        try {
            log.info("Creating dynamic Coze API with provider: {} ({})", provider.getName(), provider.getUid());
            
            TokenAuth authCli = new TokenAuth(apiKey);
            return new CozeAPI.Builder()
                    .baseURL(StringUtils.hasText(baseUrl) ? baseUrl : "https://api.coze.cn")
                    .auth(authCli)
                    .readTimeout(10000)
                    .build();
        } catch (Exception e) {
            log.error("Failed to create dynamic Coze API for provider {}", provider.getUid(), e);
            return null;
        }
    }

    /**
     * 根据机器人配置创建聊天请求
     */
    private CreateChatReq createChatRequest(RobotLlm llm, String message, String userID) {
        if (llm == null || llm.getTextModel() == null) {
            log.warn("RobotLlm or textModel is null, using default botID");
            // 使用默认的botID或从配置中获取
            return CreateChatReq.builder()
                    .botID("default-bot")
                    .userID(userID)
                    .messages(Collections.singletonList(Message.buildUserQuestionText(message)))
                    .build();
        }

        return CreateChatReq.builder()
                .botID(llm.getTextModel()) // 在Coze中，textModel字段存储botID
                .userID(userID)
                .messages(Collections.singletonList(Message.buildUserQuestionText(message)))
                .build();
    }

    /**
     * 从Prompt中提取文本内容
     */
    private String extractTextFromPrompt(Prompt prompt) {
        if (prompt == null || prompt.getInstructions() == null || prompt.getInstructions().isEmpty()) {
            return "";
        }
        
        StringBuilder fullPrompt = new StringBuilder();
        for (org.springframework.ai.chat.messages.Message message : prompt.getInstructions()) {
            if (message != null && StringUtils.hasText(message.getText())) {
                fullPrompt.append(message.getText()).append("\n");
            }
        }
        
        return fullPrompt.toString().trim();
    }

    @Override
    protected void processPromptWebsocket(Prompt prompt, RobotProtobuf robot, MessageProtobuf messageProtobufQuery,
            MessageProtobuf messageProtobufReply, String fullPromptContent) {
        // 从robot中获取llm配置
        RobotLlm llm = robot.getLlm();
        log.info("Coze API websocket fullPromptContent: {}", fullPromptContent);
        
        if (llm == null) {
            log.info("Coze API not available");
            sendMessageWebsocket(MessageTypeEnum.ERROR, "Coze service is not available", messageProtobufReply);
            return;
        }

        // 获取适当的API实例
        CozeAPI cozeApi = createDynamicCozeApi(llm);
        if (cozeApi == null) {
            log.info("Coze API not available");
            sendMessageWebsocket(MessageTypeEnum.ERROR, "Coze service is not available", messageProtobufReply);
            return;
        }

        long startTime = System.currentTimeMillis();
        final boolean[] success = {false};
        final ChatTokenUsage[] tokenUsage = {new ChatTokenUsage(0, 0, 0)};

        try {
            String userID = messageProtobufQuery.getThread() != null ? 
                messageProtobufQuery.getThread().getUid() : "default-user-" + System.currentTimeMillis();
            String messageText = extractTextFromPrompt(prompt);
            
            CreateChatReq req = createChatRequest(llm, messageText, userID);
            
            // 创建流式聊天并处理响应
            Flowable<ChatEvent> resp = cozeApi.chat().stream(req);
            resp.subscribeOn(Schedulers.io())
                    .subscribe(
                            event -> {
                                try {
                                    if (ChatEventType.CONVERSATION_MESSAGE_DELTA.equals(event.getEvent())) {
                                        if (event.getMessage() != null && event.getMessage().getContent() != null) {
                                            String textContent = event.getMessage().getContent();
                                            log.info("Coze API Websocket response text: {}", textContent);
                                            sendMessageWebsocket(MessageTypeEnum.STREAM, textContent, messageProtobufReply);
                                        }
                                    }
                                    if (ChatEventType.CONVERSATION_CHAT_COMPLETED.equals(event.getEvent())) {
                                        // 提取token使用情况
                                        if (event.getChat() != null && event.getChat().getUsage() != null) {
                                            long totalTokens = event.getChat().getUsage().getTokenCount();
                                            // 估算prompt和completion token
                                            long promptTokens = (long) (totalTokens * 0.3);
                                            long completionTokens = totalTokens - promptTokens;
                                            tokenUsage[0] = new ChatTokenUsage(promptTokens, completionTokens, totalTokens);
                                        }
                                        success[0] = true;
                                    }
                                } catch (Exception e) {
                                    log.error("Error processing Coze websocket event", e);
                                    success[0] = false;
                                }
                            },
                            error -> {
                                log.error("Coze API error: ", error);
                                sendMessageWebsocket(MessageTypeEnum.ERROR, I18Consts.I18N_SERVICE_TEMPORARILY_UNAVAILABLE, messageProtobufReply);
                                success[0] = false;
                            },
                            () -> {
                                log.info("Coze chat stream completed");
                                // 记录token使用情况
                                long responseTime = System.currentTimeMillis() - startTime;
                                String modelType = (llm != null && StringUtils.hasText(llm.getTextModel())) ? llm.getTextModel() : "coze-bot";
                                recordAiTokenUsage(robot, LlmConsts.COZE, modelType, 
                                        tokenUsage[0].getPromptTokens(), tokenUsage[0].getCompletionTokens(), success[0], responseTime);
                                // 关闭API连接
                                cozeApi.shutdownExecutor();
                            });
        } catch (Exception e) {
            log.error("Error processing Coze prompt", e);
            sendMessageWebsocket(MessageTypeEnum.ERROR, I18Consts.I18N_SERVICE_TEMPORARILY_UNAVAILABLE, messageProtobufReply);
            success[0] = false;
            // 记录token使用情况
            long responseTime = System.currentTimeMillis() - startTime;
            String modelType = (llm != null && StringUtils.hasText(llm.getTextModel())) ? llm.getTextModel() : "coze-bot";
            recordAiTokenUsage(robot, LlmConsts.COZE, modelType, 
                    tokenUsage[0].getPromptTokens(), tokenUsage[0].getCompletionTokens(), success[0], responseTime);
        }
    }

    @Override
    protected String processPromptSync(String message, RobotProtobuf robot, String fullPromptContent) {
        long startTime = System.currentTimeMillis();
        boolean success = false;
        ChatTokenUsage tokenUsage = new ChatTokenUsage(0, 0, 0);
        
        log.info("Coze API sync fullPromptContent: {}", fullPromptContent);
        
        // 从robot中获取llm配置
        RobotLlm llm = robot.getLlm();

        if (llm == null) {
            log.info("Coze API not available");
            return "Coze service is not available";
        }

        // 获取适当的API实例
        CozeAPI cozeApi = createDynamicCozeApi(llm);
        if (cozeApi == null) {
            return "Coze service is not available";
        }

        try {
            String userID = "sync-user-" + System.currentTimeMillis();
            CreateChatReq req = createChatRequest(llm, message, userID);
            
            // 使用轮询方式获取同步响应
            ChatPoll chatPoll = cozeApi.chat().createAndPoll(req);
            
            if (chatPoll != null && chatPoll.getChat() != null) {
                // 提取token使用情况
                if (chatPoll.getChat().getUsage() != null) {
                    long totalTokens = chatPoll.getChat().getUsage().getTokenCount();
                    long promptTokens = (long) (totalTokens * 0.3);
                    long completionTokens = totalTokens - promptTokens;
                    tokenUsage = new ChatTokenUsage(promptTokens, completionTokens, totalTokens);
                }
                
                // 获取最后的回复消息
                if (chatPoll.getMessages() != null && !chatPoll.getMessages().isEmpty()) {
                    // 查找最后一条助手消息
                    for (int i = chatPoll.getMessages().size() - 1; i >= 0; i--) {
                        var msg = chatPoll.getMessages().get(i);
                        if (MessageType.ANSWER.equals(msg.getType())) {
                            success = true;
                            return msg.getContent();
                        }
                    }
                }
                
                success = true;
                return "Coze响应成功，但未获取到具体内容";
            } else {
                success = false;
                return I18Consts.I18N_SERVICE_TEMPORARILY_UNAVAILABLE;
            }
        } catch (Exception e) {
            log.error("Coze API sync error", e);
            success = false;
            return I18Consts.I18N_SERVICE_TEMPORARILY_UNAVAILABLE;
        } finally {
            // 关闭API连接
            if (cozeApi != null) {
                cozeApi.shutdownExecutor();
            }
            // 记录token使用情况
            long responseTime = System.currentTimeMillis() - startTime;
            String modelType = (robot != null && robot.getLlm() != null && StringUtils.hasText(robot.getLlm().getTextModel())) 
                    ? robot.getLlm().getTextModel() : "coze-bot";
            recordAiTokenUsage(robot, LlmConsts.COZE, modelType, 
                    tokenUsage.getPromptTokens(), tokenUsage.getCompletionTokens(), success, responseTime);
        }
    }

    @Override
    protected void processPromptSse(Prompt prompt, RobotProtobuf robot, MessageProtobuf messageProtobufQuery,
            MessageProtobuf messageProtobufReply, SseEmitter emitter, String fullPromptContent) {
        // 从robot中获取llm配置
        RobotLlm llm = robot.getLlm();
        log.info("Coze API SSE fullPromptContent: {}", fullPromptContent);

        if (llm == null) {
            log.info("Coze API not available");
            sendStreamEndMessage(messageProtobufQuery, messageProtobufReply, emitter, 0, 0, 0, fullPromptContent,
                    LlmConsts.COZE, "coze-bot");
            return;
        }

        // 获取适当的API实例
        CozeAPI cozeApi = createDynamicCozeApi(llm);
        if (cozeApi == null) {
            log.info("Coze API not available");
            sendStreamEndMessage(messageProtobufQuery, messageProtobufReply, emitter, 0, 0, 0, fullPromptContent,
                    LlmConsts.COZE, (llm != null && StringUtils.hasText(llm.getTextModel())) ? llm.getTextModel() : "coze-bot");
            return;
        }

        long startTime = System.currentTimeMillis();
        final boolean[] success = {false};
        final ChatTokenUsage[] tokenUsage = {new ChatTokenUsage(0, 0, 0)};
        final boolean[] isCompleted = {false};

        try {
            // 发送初始消息，告知用户请求已收到，正在处理
            sendStreamStartMessage(messageProtobufReply, emitter, I18Consts.I18N_THINKING);

            String userID = messageProtobufQuery.getThread() != null ? 
                messageProtobufQuery.getThread().getUid() : "default-user-" + System.currentTimeMillis();
            String messageText = extractTextFromPrompt(prompt);
            
            CreateChatReq req = createChatRequest(llm, messageText, userID);
            
            Flowable<ChatEvent> resp = cozeApi.chat().stream(req);
            resp.subscribeOn(Schedulers.io())
                    .subscribe(
                            event -> {
                                if (isCompleted[0]) {
                                    return; // 如果已经完成，跳过处理
                                }
                                
                                try {
                                    if (ChatEventType.CONVERSATION_MESSAGE_DELTA.equals(event.getEvent()) && !isEmitterCompleted(emitter)) {
                                        if (event.getMessage() != null && event.getMessage().getContent() != null) {
                                            String textContent = event.getMessage().getContent();
                                            log.info("Coze API SSE response text: {}", textContent);
                                            sendStreamMessage(messageProtobufQuery, messageProtobufReply, emitter, textContent);
                                        }
                                    }
                                    if (ChatEventType.CONVERSATION_CHAT_COMPLETED.equals(event.getEvent())) {
                                        // 提取token使用情况
                                        if (event.getChat() != null && event.getChat().getUsage() != null) {
                                            long totalTokens = event.getChat().getUsage().getTokenCount();
                                            long promptTokens = (long) (totalTokens * 0.3);
                                            long completionTokens = totalTokens - promptTokens;
                                            tokenUsage[0] = new ChatTokenUsage(promptTokens, completionTokens, totalTokens);
                                        }
                                        success[0] = true;
                                        
                                        // 处理follow-up消息
                                        if (event.getMessage() != null && event.getMessage().getType() != null) {
                                            if (MessageType.FOLLOW_UP.equals(event.getMessage().getType()) && !isEmitterCompleted(emitter)) {
                                                sendStreamMessage(messageProtobufQuery, messageProtobufReply, emitter, 
                                                    "\n\n[Follow-up]: " + event.getMessage().getContent());
                                            }
                                        }
                                    }
                                    if (ChatEventType.DONE.equals(event.getEvent())) {
                                        if (!isCompleted[0]) {
                                            isCompleted[0] = true;
                                            success[0] = true;
                                        }
                                    }
                                } catch (IllegalStateException e) {
                                    log.warn("SseEmitter already completed, ignoring event: {}", event.getEvent());
                                    isCompleted[0] = true;
                                } catch (Exception e) {
                                    log.error("Coze API SSE error 1: ", e);
                                    handleSseError(e, messageProtobufQuery, messageProtobufReply, emitter);
                                    success[0] = false;
                                }
                            },
                            error -> {
                                log.error("Coze API SSE error 2: ", error);
                                handleSseError(error, messageProtobufQuery, messageProtobufReply, emitter);
                                success[0] = false;
                            },
                            () -> {
                                log.info("Coze API SSE complete");
                                if (!isCompleted[0]) {
                                    isCompleted[0] = true;
                                    // 发送流结束消息，包含token使用情况和prompt内容
                                    sendStreamEndMessage(messageProtobufQuery, messageProtobufReply, emitter,
                                            tokenUsage[0].getPromptTokens(), tokenUsage[0].getCompletionTokens(),
                                            tokenUsage[0].getTotalTokens(), fullPromptContent, LlmConsts.COZE,
                                            (llm != null && StringUtils.hasText(llm.getTextModel())) ? llm.getTextModel() : "coze-bot");
                                }
                                // 记录token使用情况
                                long responseTime = System.currentTimeMillis() - startTime;
                                String modelType = (llm != null && StringUtils.hasText(llm.getTextModel())) ? llm.getTextModel() : "coze-bot";
                                recordAiTokenUsage(robot, LlmConsts.COZE, modelType,
                                        tokenUsage[0].getPromptTokens(), tokenUsage[0].getCompletionTokens(), success[0], responseTime);
                                // 关闭API连接
                                cozeApi.shutdownExecutor();
                            });
        } catch (Exception e) {
            log.error("Error starting Coze stream", e);
            handleSseError(e, messageProtobufQuery, messageProtobufReply, emitter);
            success[0] = false;
            // 记录token使用情况
            long responseTime = System.currentTimeMillis() - startTime;
            String modelType = (llm != null && StringUtils.hasText(llm.getTextModel())) ? llm.getTextModel() : "coze-bot";
            recordAiTokenUsage(robot, LlmConsts.COZE, modelType,
                    tokenUsage[0].getPromptTokens(), tokenUsage[0].getCompletionTokens(), success[0], responseTime);
            // 关闭API连接
            if (cozeApi != null) {
                cozeApi.shutdownExecutor();
            }
        }
    }

}
