/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-19 09:39:15
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-16 09:48:24
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.zhipuai;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.bytedesk.ai.robot.RobotLlm;
import com.bytedesk.ai.robot.RobotProtobuf;
import com.bytedesk.ai.springai.service.BaseSpringAIService;
import com.bytedesk.core.constant.LlmConsts;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.MessageTypeEnum;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhipu.oapi.ClientV4;
import com.zhipu.oapi.Constants;
import com.zhipu.oapi.service.v4.deserialize.MessageDeserializeFactory;
import com.zhipu.oapi.service.v4.model.ChatCompletionRequest;
import com.zhipu.oapi.service.v4.model.ChatMessage;
import com.zhipu.oapi.service.v4.model.ChatMessageRole;
import com.zhipu.oapi.service.v4.model.ChatMeta;
import com.zhipu.oapi.service.v4.model.ModelApiResponse;
import com.zhipu.oapi.service.v4.model.ChatMessageAccumulator;
import com.zhipu.oapi.service.v4.model.ChatTool;
import com.zhipu.oapi.service.v4.model.ChatToolType;
import com.zhipu.oapi.service.v4.model.ChatFunction;
import com.zhipu.oapi.service.v4.model.QueryModelResultRequest;
import com.zhipu.oapi.service.v4.model.QueryModelResultResponse;
import com.zhipu.oapi.service.v4.model.WebSearch;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

/**
 * 智谱AI服务类
 * 使用 oapi-java-sdk 实现聊天功能，继承自BaseSpringAIService
 * https://github.com/MetaGLM/zhipuai-sdk-java-v4
 */
@Slf4j
@Service
@ConditionalOnProperty(prefix = "spring.ai.zhipuai.chat", name = "enabled", havingValue = "true", matchIfMissing = false)
public class ZhipuaiService extends BaseSpringAIService {

    @Autowired
    @Qualifier("zhipuaiChatClient")
    private ClientV4 client;

    @Autowired
    private ZhipuaiChatConfig zhipuaiChatConfig;

    /**
     * 构造函数
     */
    public ZhipuaiService() {
        super();
    }

    /**
     * 根据机器人配置创建动态的聊天选项
     */
    private ChatCompletionRequest createDynamicRequest(RobotLlm llm, String message, boolean stream) {
        List<ChatMessage> messages = new ArrayList<>();
        ChatMessage chatMessage = new ChatMessage(ChatMessageRole.USER.value(), message);
        messages.add(chatMessage);

        String requestId = String.format("zhipuai-%d", System.currentTimeMillis());
        
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model(llm != null && llm.getModel() != null ? llm.getModel() : zhipuaiChatConfig.getModel())
                .stream(stream)
                .invokeMethod(Constants.invokeMethod)
                .messages(messages)
                .requestId(requestId)
                .temperature(llm != null ? llm.getTemperature().floatValue() : (float) zhipuaiChatConfig.getTemperature())
                .build();

        return chatCompletionRequest;
    }

    /**
     * 方式1：异步流式调用 - 实现BaseSpringAIService的抽象方法
     */
    @Override
    protected void processPromptWebsocket(Prompt prompt, RobotProtobuf robot, MessageProtobuf messageProtobufQuery,
            MessageProtobuf messageProtobufReply) {
        // 从robot中获取llm配置
        RobotLlm llm = robot.getLlm();
        String message = extractTextFromPrompt(prompt);

        long startTime = System.currentTimeMillis();
        final boolean[] success = {false};
        final TokenUsage[] tokenUsage = {new TokenUsage(0, 0, 0)};

        try {
            if (client == null) {
                sendMessageWebsocket(MessageTypeEnum.ERROR, "Zhipuai client is not available", messageProtobufReply);
                return;
            }

            ChatCompletionRequest chatCompletionRequest = createDynamicRequest(llm, message, true);
            
            ModelApiResponse response = client.invokeModelApi(chatCompletionRequest);
            
            if (response.isSuccess()) {
                response.getFlowable().subscribe(
                    accumulator -> {
                        try {
                            Object delta = accumulator.getDelta();
                            if (delta instanceof com.zhipu.oapi.service.v4.model.ChatMessage) {
                                Object content = ((com.zhipu.oapi.service.v4.model.ChatMessage) delta).getContent();
                                if (content != null) {
                                    sendMessageWebsocket(MessageTypeEnum.STREAM, content.toString(), messageProtobufReply);
                                }
                            }
                            success[0] = true;
                        } catch (Exception e) {
                            log.error("Error processing stream response", e);
                            success[0] = false;
                        }
                    },
                    error -> {
                        log.error("Zhipuai API error: ", error);
                        sendMessageWebsocket(MessageTypeEnum.ERROR, "服务暂时不可用，请稍后重试", messageProtobufReply);
                        success[0] = false;
                    },
                    () -> {
                        log.info("Zhipuai chat stream completed");
                        // 记录token使用情况
                        long responseTime = System.currentTimeMillis() - startTime;
                        String modelType = (llm != null && llm.getModel() != null) ? llm.getModel() : zhipuaiChatConfig.getModel();
                        recordAiTokenUsage(robot, LlmConsts.ZHIPUAI, modelType, 
                                tokenUsage[0].getPromptTokens(), tokenUsage[0].getCompletionTokens(), success[0], responseTime);
                    }
                );
            } else {
                log.error("Zhipuai API error: {}", response.getError());
                sendMessageWebsocket(MessageTypeEnum.ERROR, "服务暂时不可用，请稍后重试", messageProtobufReply);
            }
        } catch (Exception e) {
            log.error("Error in processPromptWebsocket", e);
            sendMessageWebsocket(MessageTypeEnum.ERROR, "服务暂时不可用，请稍后重试", messageProtobufReply);
        }
    }

    /**
     * 方式2：同步调用 - 实现BaseSpringAIService的抽象方法
     */
    @Override
    protected String processPromptSync(String message, RobotProtobuf robot) {
        long startTime = System.currentTimeMillis();
        boolean success = false;
        TokenUsage tokenUsage = new TokenUsage(0, 0, 0);
        
        try {
            if (client == null) {
                return "Zhipuai client is not available";
            }

            RobotLlm llm = robot != null ? robot.getLlm() : null;
            ChatCompletionRequest chatCompletionRequest = createDynamicRequest(llm, message, false);
            
            ModelApiResponse response = client.invokeModelApi(chatCompletionRequest);
            
            if (response.isSuccess() && response.getData() != null) {
                Object content = response.getData().getChoices().get(0).getMessage().getContent();
                success = true;
                return content != null ? content.toString() : null;
            } else {
                log.error("Zhipuai API error: {}", response.getError());
                return "Error: " + (response.getError() != null ? response.getError().getMessage() : "Unknown error");
            }
        } catch (Exception e) {
            log.error("Error in processPromptSync", e);
            return "Error: " + e.getMessage();
        } finally {
            // 记录token使用情况
            long responseTime = System.currentTimeMillis() - startTime;
            String modelType = (robot != null && robot.getLlm() != null && robot.getLlm().getModel() != null) 
                    ? robot.getLlm().getModel() : zhipuaiChatConfig.getModel();
            recordAiTokenUsage(robot, LlmConsts.ZHIPUAI, modelType, 
                    tokenUsage.getPromptTokens(), tokenUsage.getCompletionTokens(), success, responseTime);
        }
    }

    /**
     * 方式3：SSE方式调用 - 实现BaseSpringAIService的抽象方法
     */
    @Override
    protected void processPromptSse(Prompt prompt, RobotProtobuf robot, MessageProtobuf messageProtobufQuery,
            MessageProtobuf messageProtobufReply, SseEmitter emitter) {
        // 从robot中获取llm配置
        RobotLlm llm = robot.getLlm();
        String message = extractTextFromPrompt(prompt);
        
        // 发送起始消息
        sendStreamStartMessage(messageProtobufReply, emitter, "正在思考中...");

        long startTime = System.currentTimeMillis();
        final boolean[] success = {false};
        final TokenUsage[] tokenUsage = {new TokenUsage(0, 0, 0)};

        try {
            if (client == null) {
                handleSseError(new Exception("Zhipuai client is not available"), messageProtobufQuery, messageProtobufReply, emitter);
                return;
            }

            ChatCompletionRequest chatCompletionRequest = createDynamicRequest(llm, message, true);
            
            ModelApiResponse response = client.invokeModelApi(chatCompletionRequest);
            
            if (response.isSuccess()) {
                // AtomicBoolean isFirst = new AtomicBoolean(true);
                response.getFlowable()
                        .doOnNext(accumulator -> {
                            try {
                                Object delta = accumulator.getDelta();
                                if (delta instanceof com.zhipu.oapi.service.v4.model.ChatMessage) {
                                    Object content = ((com.zhipu.oapi.service.v4.model.ChatMessage) delta).getContent();
                                    if (content != null) {
                                        sendStreamMessage(messageProtobufQuery, messageProtobufReply, emitter, content.toString());
                                    }
                                }
                                success[0] = true;
                            } catch (Exception e) {
                                log.error("Error sending SSE data", e);
                                success[0] = false;
                            }
                        })
                        .doOnComplete(() -> {
                            try {
                                sendStreamEndMessage(messageProtobufQuery, messageProtobufReply, emitter, 
                                        tokenUsage[0].getPromptTokens(), tokenUsage[0].getCompletionTokens(), tokenUsage[0].getTotalTokens());
                            } catch (Exception e) {
                                log.error("Error completing SSE", e);
                            }
                        })
                        .doOnError(error -> {
                            log.error("Error in SSE stream", error);
                            handleSseError(error, messageProtobufQuery, messageProtobufReply, emitter);
                            success[0] = false;
                        })
                        .subscribe();
            } else {
                log.error("Zhipuai API error: {}", response.getError());
                handleSseError(new Exception(response.getError() != null ? response.getError().getMessage() : "Unknown error"), 
                        messageProtobufQuery, messageProtobufReply, emitter);
            }
        } catch (Exception e) {
            log.error("Error in processPromptSse", e);
            handleSseError(e, messageProtobufQuery, messageProtobufReply, emitter);
        } finally {
            // 记录token使用情况
            long responseTime = System.currentTimeMillis() - startTime;
            String modelType = (llm != null && llm.getModel() != null) ? llm.getModel() : zhipuaiChatConfig.getModel();
            recordAiTokenUsage(robot, LlmConsts.ZHIPUAI, modelType, 
                    tokenUsage[0].getPromptTokens(), tokenUsage[0].getCompletionTokens(), success[0], responseTime);
        }
    }

    /**
     * 从Prompt中提取文本内容
     */
    private String extractTextFromPrompt(Prompt prompt) {
        if (prompt == null || prompt.getInstructions() == null) {
            return "";
        }
        // Spring AI Prompt的getInstructions()返回的是List<Message>
        // 我们需要找到UserMessage并提取其内容
        for (org.springframework.ai.chat.messages.Message message : prompt.getInstructions()) {
            if (message instanceof org.springframework.ai.chat.messages.UserMessage) {
                return ((org.springframework.ai.chat.messages.UserMessage) message).getText();
            }
        }
        return "";
    }

    /**
     * 角色扮演聊天
     */
    public String rolePlayChat(String message, String userInfo, String botInfo, String botName, String userName) {
        try {
            if (client == null) {
                return "Zhipuai client is not available";
            }

            List<ChatMessage> messages = new ArrayList<>();
            ChatMessage chatMessage = new ChatMessage(ChatMessageRole.USER.value(), message);
            messages.add(chatMessage);

            ChatMeta meta = new ChatMeta();
            meta.setUser_info(userInfo);
            meta.setBot_info(botInfo);
            meta.setBot_name(botName);
            meta.setUser_name(userName);

            String requestId = String.format("roleplay-%d", System.currentTimeMillis());
            
            ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                    .model(Constants.ModelCharGLM3)
                    .stream(Boolean.FALSE)
                    .invokeMethod(Constants.invokeMethod)
                    .messages(messages)
                    .meta(meta)
                    .requestId(requestId)
                    .build();
            
            ModelApiResponse response = client.invokeModelApi(chatCompletionRequest);
            
            if (response.isSuccess() && response.getData() != null) {
                Object content = response.getData().getChoices().get(0).getMessage().getContent();
                return content != null ? content.toString() : null;
            } else {
                log.error("Zhipuai API error: {}", response.getError());
                return "Error: " + (response.getError() != null ? response.getError().getMessage() : "Unknown error");
            }
        } catch (Exception e) {
            log.error("Error in rolePlayChat", e);
            return "Error: " + e.getMessage();
        }
    }

    /**
     * Function Calling 聊天
     */
    public String functionCallingChat(String message, List<ChatFunction> functions) {
        return functionCallingChat(message, null, null, functions);
    }

    /**
     * Function Calling 聊天（带自定义参数）
     */
    public String functionCallingChat(String message, String model, Double temperature, List<ChatFunction> functions) {
        try {
            if (client == null) {
                return "Zhipuai client is not available";
            }

            List<ChatMessage> messages = new ArrayList<>();
            ChatMessage chatMessage = new ChatMessage(ChatMessageRole.USER.value(), message);
            messages.add(chatMessage);

            List<ChatTool> chatToolList = new ArrayList<>();
            for (ChatFunction function : functions) {
                ChatTool chatTool = new ChatTool();
                chatTool.setType(ChatToolType.FUNCTION.value());
                chatTool.setFunction(function);
                chatToolList.add(chatTool);
            }

            String requestId = String.format("function-%d", System.currentTimeMillis());
            
            ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                    .model(model != null ? model : zhipuaiChatConfig.getModel())
                    .stream(Boolean.FALSE)
                    .invokeMethod(Constants.invokeMethod)
                    .messages(messages)
                    .requestId(requestId)
                    .temperature(temperature != null ? temperature.floatValue() : (float) zhipuaiChatConfig.getTemperature())
                    .tools(chatToolList)
                    .toolChoice("auto")
                    .build();
            
            ModelApiResponse response = client.invokeModelApi(chatCompletionRequest);
            
            if (response.isSuccess() && response.getData() != null) {
                Object content = response.getData().getChoices().get(0).getMessage().getContent();
                return content != null ? content.toString() : null;
            } else {
                log.error("Zhipuai API error: {}", response.getError());
                return "Error: " + (response.getError() != null ? response.getError().getMessage() : "Unknown error");
            }
        } catch (Exception e) {
            log.error("Error in functionCallingChat", e);
            return "Error: " + e.getMessage();
        }
    }

    /**
     * 流式 Function Calling 聊天
     */
    public Flux<String> functionCallingChatStream(String message, List<ChatFunction> functions) {
        return functionCallingChatStream(message, null, null, functions);
    }

    /**
     * 流式 Function Calling 聊天（带自定义参数）
     */
    public Flux<String> functionCallingChatStream(String message, String model, Double temperature, List<ChatFunction> functions) {
        try {
            if (client == null) {
                return Flux.just("Zhipuai client is not available");
            }

            List<ChatMessage> messages = new ArrayList<>();
            ChatMessage chatMessage = new ChatMessage(ChatMessageRole.USER.value(), message);
            messages.add(chatMessage);

            List<ChatTool> chatToolList = new ArrayList<>();
            for (ChatFunction function : functions) {
                ChatTool chatTool = new ChatTool();
                chatTool.setType(ChatToolType.FUNCTION.value());
                chatTool.setFunction(function);
                chatToolList.add(chatTool);
            }

            String requestId = String.format("function-stream-%d", System.currentTimeMillis());
            
            ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                    .model(model != null ? model : zhipuaiChatConfig.getModel())
                    .stream(Boolean.TRUE)
                    .messages(messages)
                    .requestId(requestId)
                    .temperature(temperature != null ? temperature.floatValue() : (float) zhipuaiChatConfig.getTemperature())
                    .tools(chatToolList)
                    .toolChoice("auto")
                    .build();
            
            ModelApiResponse response = client.invokeModelApi(chatCompletionRequest);
            
            if (response.isSuccess()) {
                return Flux.from(response.getFlowable().map(accumulator -> {
                    Object delta = accumulator.getDelta();
                    if (delta instanceof com.zhipu.oapi.service.v4.model.ChatMessage) {
                        Object content = ((com.zhipu.oapi.service.v4.model.ChatMessage) delta).getContent();
                        return content != null ? content.toString() : "";
                    } else if (delta != null) {
                        return delta.toString();
                    }
                    return "";
                }));
            } else {
                log.error("Zhipuai API error: {}", response.getError());
                return Flux.just("Error: " + (response.getError() != null ? response.getError().getMessage() : "Unknown error"));
            }
        } catch (Exception e) {
            log.error("Error in functionCallingChatStream", e);
            return Flux.just("Error: " + e.getMessage());
        }
    }

    /**
     * 图像生成 - 暂不支持，需要等待SDK更新
     */
    public String generateImage(String prompt) {
        return "Image generation is not supported in current SDK version";
    }

    /**
     * 图像生成（带请求ID）- 暂不支持，需要等待SDK更新
     */
    public String generateImage(String prompt, String requestId) {
        return "Image generation is not supported in current SDK version";
    }

    /**
     * 向量嵌入 - 暂不支持，需要等待SDK更新
     */
    public List<Double> getEmbedding(String text) {
        log.warn("Embedding is not supported in current SDK version");
        return new ArrayList<>();
    }

    /**
     * 批量向量嵌入 - 暂不支持，需要等待SDK更新
     */
    public List<List<Double>> getEmbeddings(List<String> texts) {
        log.warn("Embeddings is not supported in current SDK version");
        return new ArrayList<>();
    }

    /**
     * 语音合成 - 暂不支持，需要等待SDK更新
     */
    public File generateSpeech(String text, String voice, String responseFormat) {
        log.warn("Speech synthesis is not supported in current SDK version");
        return null;
    }

    /**
     * 自定义语音合成 - 暂不支持，需要等待SDK更新
     */
    public File generateCustomSpeech(String text, String voiceText, File voiceData, String responseFormat) {
        log.warn("Custom speech synthesis is not supported in current SDK version");
        return null;
    }

    /**
     * 文件上传 - 暂不支持，需要等待SDK更新
     */
    public String uploadFile(String filePath, String purpose) {
        return "File upload is not supported in current SDK version";
    }

    /**
     * 查询文件列表 - 暂不支持，需要等待SDK更新
     */
    public List<Map<String, Object>> queryFiles() {
        log.warn("File query is not supported in current SDK version");
        return new ArrayList<>();
    }

    /**
     * 下载文件内容 - 暂不支持，需要等待SDK更新
     */
    public File downloadFile(String fileId, String outputPath) {
        log.warn("File download is not supported in current SDK version");
        return null;
    }

    /**
     * 创建微调任务 - 暂不支持，需要等待SDK更新
     */
    public String createFineTuningJob(String model, String trainingFile) {
        return "Fine-tuning is not supported in current SDK version";
    }

    /**
     * 查询微调任务 - 暂不支持，需要等待SDK更新
     */
    public Map<String, Object> queryFineTuningJob(String jobId) {
        log.warn("Fine-tuning query is not supported in current SDK version");
        return new HashMap<>();
    }

    /**
     * 异步聊天
     */
    public String chatAsync(String message) {
        try {
            if (client == null) {
                return "Zhipuai client is not available";
            }

            List<ChatMessage> messages = new ArrayList<>();
            ChatMessage chatMessage = new ChatMessage(ChatMessageRole.USER.value(), message);
            messages.add(chatMessage);

            ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                    .model(zhipuaiChatConfig.getModel())
                    .stream(Boolean.FALSE)
                    .invokeMethod(Constants.invokeMethodAsync)
                    .messages(messages)
                    .build();
            
            ModelApiResponse response = client.invokeModelApi(chatCompletionRequest);
            
            if (response.isSuccess() && response.getData() != null) {
                String taskId = response.getData().getId();
                
                // 轮询获取结果
                return pollAsyncResult(taskId);
            } else {
                log.error("Zhipuai API error: {}", response.getError());
                return "Error: " + (response.getError() != null ? response.getError().getMessage() : "Unknown error");
            }
        } catch (Exception e) {
            log.error("Error in chatAsync", e);
            return "Error: " + e.getMessage();
        }
    }

    /**
     * 轮询异步结果
     */
    private String pollAsyncResult(String taskId) {
        try {
            int maxAttempts = 30; // 最多轮询30次
            int attempt = 0;
            
            while (attempt < maxAttempts) {
                QueryModelResultRequest request = new QueryModelResultRequest();
                request.setTaskId(taskId);
                
                QueryModelResultResponse response = client.queryModelResult(request);
                
                if (response.isSuccess() && response.getData() != null) {
                    if ("SUCCESS".equals(response.getData().getTaskStatus())) {
                        Object content = response.getData().getChoices().get(0).getMessage().getContent();
                        return content != null ? content.toString() : null;
                    } else if ("FAILED".equals(response.getData().getTaskStatus())) {
                        return "Task failed";
                    }
                }
                
                attempt++;
                Thread.sleep(2000); // 等待2秒后重试
            }
            
            return "Task timeout after " + maxAttempts + " attempts";
        } catch (Exception e) {
            log.error("Error polling async result", e);
            return "Error: " + e.getMessage();
        }
    }

    /**
     * 带Web搜索的聊天
     */
    public String chatWithWebSearch(String message, String searchQuery) {
        try {
            if (client == null) {
                return "Zhipuai client is not available";
            }

            List<ChatMessage> messages = new ArrayList<>();
            ChatMessage chatMessage = new ChatMessage(ChatMessageRole.USER.value(), message);
            messages.add(chatMessage);

            List<ChatTool> chatToolList = new ArrayList<>();
            
            // 添加Web搜索工具
            ChatTool webSearchTool = new ChatTool();
            webSearchTool.setType(ChatToolType.WEB_SEARCH.value());
            WebSearch webSearch = new WebSearch();
            webSearch.setSearch_query(searchQuery);
            webSearch.setSearch_result(true);
            webSearch.setEnable(true);
            webSearchTool.setWeb_search(webSearch);
            chatToolList.add(webSearchTool);

            String requestId = String.format("websearch-%d", System.currentTimeMillis());
            
            ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                    .model(zhipuaiChatConfig.getModel())
                    .stream(Boolean.FALSE)
                    .invokeMethod(Constants.invokeMethod)
                    .messages(messages)
                    .requestId(requestId)
                    .tools(chatToolList)
                    .toolChoice("auto")
                    .build();
            
            ModelApiResponse response = client.invokeModelApi(chatCompletionRequest);
            
            if (response.isSuccess() && response.getData() != null) {
                Object content = response.getData().getChoices().get(0).getMessage().getContent();
                return content != null ? content.toString() : null;
            } else {
                log.error("Zhipuai API error: {}", response.getError());
                return "Error: " + (response.getError() != null ? response.getError().getMessage() : "Unknown error");
            }
        } catch (Exception e) {
            log.error("Error in chatWithWebSearch", e);
            return "Error: " + e.getMessage();
        }
    }

    /**
     * 语音模型聊天 - 暂不支持，需要等待SDK更新
     */
    public String chatWithVoice(String message) {
        log.warn("Voice chat is not supported in current SDK version");
        return "Voice chat is not supported in current SDK version";
    }

    /**
     * 将流式响应转换为Flux
     */
    private Flux<String> mapStreamToFlux(io.reactivex.Flowable<ChatMessageAccumulator> flowable) {
        return Flux.from(flowable.map(accumulator -> {
            if (accumulator.getDelta() != null && accumulator.getDelta().getContent() != null) {
                return accumulator.getDelta().getContent();
            }
            return "";
        }));
    }

    /**
     * 检查服务健康状态
     */
    public boolean isHealthy() {
        try {
            if (client == null) {
                return false;
            }
            
            // 发送一个简单的测试请求
            String response = processPromptSync("Hello", null);
            return response != null && !response.startsWith("Error");
        } catch (Exception e) {
            log.error("Health check failed", e);
            return false;
        }
    }
} 