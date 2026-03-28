/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-08-21 12:26:02
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-21 12:37:21
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.zhipuai;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import ai.z.openapi.ZhipuAiClient;
import ai.z.openapi.core.Constants;
import ai.z.openapi.service.model.AsyncResultRetrieveParams;
import ai.z.openapi.service.model.ChatCompletionCreateParams;
import ai.z.openapi.service.model.ChatCompletionResponse;
import ai.z.openapi.service.model.ChatFunction;
import ai.z.openapi.service.model.ChatMessage;
import ai.z.openapi.service.model.ChatMessageRole;
import ai.z.openapi.service.model.ChatMeta;
import ai.z.openapi.service.model.ChatTool;
import ai.z.openapi.service.model.ChatToolType;
import ai.z.openapi.service.model.Delta;
import ai.z.openapi.service.model.QueryModelResultResponse;
import ai.z.openapi.service.model.WebSearch;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Slf4j
@Service
@ConditionalOnProperty(prefix = "spring.ai.zhipuai.chat", name = "enabled", havingValue = "true", matchIfMissing = false)
public class ZhipuaiChatService {

    private static final String CLIENT_UNAVAILABLE_MESSAGE = "Zhipuai client is not available";

    @Autowired(required = false)
    @Qualifier("zhipuAiClient")
    private ZhipuAiClient client;

    @Autowired
    private ZhipuaiChatConfig zhipuaiChatConfig;

    private boolean isClientUnavailable(String operation) {
        if (client != null) {
            return false;
        }

        log.warn("Skipping Zhipuai {} because chat client is unavailable. Check spring.ai.zhipuai.api-key configuration", operation);
        return true;
    }

    /**
     * 角色扮演聊天
     */
    public String rolePlayChat(String message, String userInfo, String botInfo, String botName, String userName) {
        log.info("Zhipuai API role play request - message length: {}, userInfo: {}, botInfo: {}, botName: {}, userName: {}",
                message.length(), userInfo, botInfo, botName, userName);

        long startTime = System.currentTimeMillis();

        if (isClientUnavailable("rolePlayChat")) {
            return CLIENT_UNAVAILABLE_MESSAGE;
        }

        try {
            List<ChatMessage> messages = new ArrayList<>();
            ChatMessage chatMessage = new ChatMessage(ChatMessageRole.USER.value(), message);
            messages.add(chatMessage);

            ChatMeta meta = ChatMeta.builder()
                    .userInfo(userInfo)
                    .botInfo(botInfo)
                    .botName(botName)
                    .userName(userName)
                    .build();

            String requestId = String.format("roleplay-%d", System.currentTimeMillis());

            ChatCompletionCreateParams chatCompletionRequest = ChatCompletionCreateParams.builder()
                    .model(Constants.ModelCharGLM3)
                    .stream(Boolean.FALSE)
                    .messages(messages)
                    .meta(meta)
                    .requestId(requestId)
                    .build();

            log.info("Zhipuai API role play invoking model with requestId: {}", requestId);
            ChatCompletionResponse response = client.chat().createChatCompletion(chatCompletionRequest);

            if (response.isSuccess() && response.getData() != null && response.getData().getChoices() != null
                    && !response.getData().getChoices().isEmpty()) {
                log.info("Zhipuai API role play response success");
            }

            log.error("Zhipuai API role play error: {}", response.getError());
            return extractResponseContent(response);
        } catch (Exception e) {
            log.error("Zhipuai API role play error: ", e);
            return "Error: " + e.getMessage();
        } finally {
            long responseTime = System.currentTimeMillis() - startTime;
            log.info("Zhipuai API role play completed in {}ms", responseTime);
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
        log.info("Zhipuai API function calling request - message length: {}, model: {}, temperature: {}, functions count: {}",
                message.length(), model, temperature, functions != null ? functions.size() : 0);

        long startTime = System.currentTimeMillis();

        if (isClientUnavailable("functionCallingChat")) {
            return CLIENT_UNAVAILABLE_MESSAGE;
        }

        try {

            ChatCompletionResponse response = client.chat().createChatCompletion(
                    buildFunctionCallingRequest(message, model, temperature, functions, false));

            if (response.isSuccess() && response.getData() != null && response.getData().getChoices() != null
                    && !response.getData().getChoices().isEmpty()) {
                log.info("Zhipuai API function calling response success");
            }

            log.error("Zhipuai API function calling error: {}", response.getError());
            return extractResponseContent(response);
        } catch (Exception e) {
            log.error("Zhipuai API function calling error: ", e);
            return "Error: " + e.getMessage();
        } finally {
            long responseTime = System.currentTimeMillis() - startTime;
            log.info("Zhipuai API function calling completed in {}ms", responseTime);
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
        if (isClientUnavailable("functionCallingChatStream")) {
            return Flux.just(CLIENT_UNAVAILABLE_MESSAGE);
        }

        try {

            ChatCompletionResponse response = client.chat().createChatCompletion(
                    buildFunctionCallingRequest(message, model, temperature, functions, true));

            if (response.isSuccess() && response.getFlowable() != null) {
                return Flux.from(response.getFlowable())
                        .map(modelData -> {
                            if (modelData.getChoices() == null || modelData.getChoices().isEmpty()) {
                                return "";
                            }

                            Delta delta = modelData.getChoices().get(0).getDelta();
                            if (delta != null && delta.getTool_calls() != null && !delta.getTool_calls().isEmpty()) {
                                log.info("Zhipuai API function calling tool_calls: {}", delta.getTool_calls());
                            }
                            return extractDeltaContent(delta);
                        })
                        .filter(chunk -> chunk != null && !chunk.isEmpty());
            }

            log.error("Zhipuai API error: {}", response.getError());
            return Flux.just(buildErrorMessage(response));
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
        log.warn("Custom voice synthesis is not supported in current SDK version");
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
        log.info("Zhipuai API async request - message length: {}", message.length());

        long startTime = System.currentTimeMillis();

        if (isClientUnavailable("chatAsync")) {
            return CLIENT_UNAVAILABLE_MESSAGE;
        }

        try {
            List<ChatMessage> messages = new ArrayList<>();
            ChatMessage chatMessage = new ChatMessage(ChatMessageRole.USER.value(), message);
            messages.add(chatMessage);

            ChatCompletionCreateParams chatCompletionRequest = ChatCompletionCreateParams.builder()
                    .model(zhipuaiChatConfig.getModel())
                    .stream(Boolean.FALSE)
                    .messages(messages)
                    .build();

            log.info("Zhipuai API async invoking model");
            ChatCompletionResponse response = client.chat().asyncChatCompletion(chatCompletionRequest);

            if (response.isSuccess() && response.getData() != null) {
                String taskId = response.getData().getId();
                log.info("Zhipuai API async task created with taskId: {}", taskId);

                return pollAsyncResult(taskId);
            }

            log.error("Zhipuai API async error: {}", response.getError());
            return buildErrorMessage(response);
        } catch (Exception e) {
            log.error("Zhipuai API async error: ", e);
            return "Error: " + e.getMessage();
        } finally {
            long responseTime = System.currentTimeMillis() - startTime;
            log.info("Zhipuai API async completed in {}ms", responseTime);
        }
    }

    /**
     * 轮询异步结果
     */
    private String pollAsyncResult(String taskId) {
        log.info("Zhipuai API starting async result polling for taskId: {}", taskId);

        if (isClientUnavailable("pollAsyncResult")) {
            return CLIENT_UNAVAILABLE_MESSAGE;
        }
        
        try {
            int maxAttempts = 30; // 最多轮询30次
            int attempt = 0;

            while (attempt < maxAttempts) {
                log.debug("Zhipuai API polling attempt {}/{} for taskId: {}", attempt + 1, maxAttempts, taskId);

                QueryModelResultResponse response = client.chat().retrieveAsyncResult(
                        AsyncResultRetrieveParams.builder().taskId(taskId).build());

                if (response.isSuccess() && response.getData() != null) {
                    Object taskStatus = response.getData().getTaskStatus();
                    log.debug("Zhipuai API task status: {} for taskId: {}", taskStatus, taskId);

                    if ("SUCCESS".equals(taskStatus.toString())) {
                        log.info("Zhipuai API async task completed successfully for taskId: {}", taskId);
                        Object content = response.getData().getChoices().get(0).getMessage().getContent();
                        return content != null ? content.toString() : null;
                    } else if ("FAILED".equals(taskStatus.toString())) {
                        log.error("Zhipuai API async task failed for taskId: {}", taskId);
                        return "Task failed";
                    }
                } else {
                    log.warn("Zhipuai API async polling response not successful for taskId: {}", taskId);
                }

                attempt++;
                Thread.sleep(2000); // 等待2秒后重试
            }

            log.error("Zhipuai API async task timeout after {} attempts for taskId: {}", maxAttempts, taskId);
            return "Task timeout after " + maxAttempts + " attempts";
        } catch (Exception e) {
            log.error("Zhipuai API error polling async result for taskId: {}", taskId, e);
            return "Error: " + e.getMessage();
        }
    }

    /**
     * 带Web搜索的聊天
     */
    public String chatWithWebSearch(String message, String searchQuery) {
        log.info("Zhipuai API web search request - message length: {}, searchQuery: {}",
                message.length(), searchQuery);

        long startTime = System.currentTimeMillis();

        if (isClientUnavailable("chatWithWebSearch")) {
            return CLIENT_UNAVAILABLE_MESSAGE;
        }

        try {
            List<ChatMessage> messages = new ArrayList<>();
            ChatMessage chatMessage = new ChatMessage(ChatMessageRole.USER.value(), message);
            messages.add(chatMessage);

            List<ChatTool> chatToolList = new ArrayList<>();

            // 添加Web搜索工具
            ChatTool webSearchTool = new ChatTool();
            webSearchTool.setType(ChatToolType.WEB_SEARCH.value());
            WebSearch webSearch = WebSearch.builder()
                    .searchQuery(searchQuery)
                    .searchResult(Boolean.TRUE)
                    .enable(Boolean.TRUE)
                    .build();
            webSearchTool.setWebSearch(webSearch);
            chatToolList.add(webSearchTool);

            String requestId = String.format("websearch-%d", System.currentTimeMillis());

            ChatCompletionCreateParams chatCompletionRequest = ChatCompletionCreateParams.builder()
                    .model(zhipuaiChatConfig.getModel())
                    .stream(Boolean.FALSE)
                    .messages(messages)
                    .requestId(requestId)
                    .tools(chatToolList)
                    .toolChoice("auto")
                    .build();

            log.info("Zhipuai API web search invoking model with requestId: {}", requestId);
            ChatCompletionResponse response = client.chat().createChatCompletion(chatCompletionRequest);

            if (response.isSuccess() && response.getData() != null && response.getData().getChoices() != null
                    && !response.getData().getChoices().isEmpty()) {
                log.info("Zhipuai API web search response success");
            }

            log.error("Zhipuai API web search error: {}", response.getError());
            return extractResponseContent(response);
        } catch (Exception e) {
            log.error("Zhipuai API web search error: ", e);
            return "Error: " + e.getMessage();
        } finally {
            long responseTime = System.currentTimeMillis() - startTime;
            log.info("Zhipuai API web search completed in {}ms", responseTime);
        }
    }

    /**
     * 语音模型聊天 - 暂不支持，需要等待SDK更新
     */
    public String chatWithVoice(String message) {
        log.warn("Voice chat is not supported in current SDK version");
        return "Voice chat is not supported in current SDK version";
    }


    private ChatCompletionCreateParams buildFunctionCallingRequest(String message, String model, Double temperature,
            List<ChatFunction> functions, boolean stream) {
        List<ChatMessage> messages = new ArrayList<>();
        messages.add(new ChatMessage(ChatMessageRole.USER.value(), message));

        List<ChatTool> chatToolList = new ArrayList<>();
        if (functions != null) {
            for (ChatFunction function : functions) {
                ChatTool chatTool = new ChatTool();
                chatTool.setType(ChatToolType.FUNCTION.value());
                chatTool.setFunction(function);
                chatToolList.add(chatTool);
            }
        }

        return ChatCompletionCreateParams.builder()
                .model(model != null ? model : zhipuaiChatConfig.getModel())
                .stream(stream)
                .messages(messages)
                .requestId(String.format(stream ? "function-stream-%d" : "function-%d", System.currentTimeMillis()))
                .temperature(temperature != null ? temperature.floatValue() : (float) zhipuaiChatConfig.getTemperature())
                .topP((float) zhipuaiChatConfig.getTopP())
                .maxTokens(zhipuaiChatConfig.getMaxTokens())
                .tools(chatToolList)
                .toolChoice("auto")
                .build();
    }

    private String extractResponseContent(ChatCompletionResponse response) {
        if (response != null && response.isSuccess() && response.getData() != null && response.getData().getChoices() != null
                && !response.getData().getChoices().isEmpty() && response.getData().getChoices().get(0).getMessage() != null) {
            Object content = response.getData().getChoices().get(0).getMessage().getContent();
            return content != null ? content.toString() : null;
        }
        return buildErrorMessage(response);
    }

    private String buildErrorMessage(ChatCompletionResponse response) {
        if (response == null) {
            return "Error: Empty response";
        }
        if (response.getError() != null && response.getError().getMessage() != null) {
            return "Error: " + response.getError().getMessage();
        }
        if (response.getMsg() != null && !response.getMsg().isBlank()) {
            return "Error: " + response.getMsg();
        }
        return "Error: Unknown error";
    }

    private String extractDeltaContent(Delta delta) {
        if (delta == null) {
            return "";
        }
        if (delta.getContent() != null && !delta.getContent().isBlank()) {
            return delta.getContent();
        }
        if (delta.getReasoningContent() != null && !delta.getReasoningContent().isBlank()) {
            return delta.getReasoningContent();
        }
        return "";
    }

    /**
     * 测试流式响应功能
     * 用于调试流式响应问题
     */
    public void testStreamResponse() {
        if (isClientUnavailable("testStreamResponse")) {
            return;
        }

        try {
            log.info("Zhipuai API testing stream response...");

            // // 创建一个简单的测试请求
            // String testMessage = "Hello, this is a test message for stream response.";
            // ChatCompletionCreateParams chatCompletionRequest = ChatCompletionCreateParams.builder()
            //         .model(zhipuaiChatConfig.getModel())
            //         .messages(List.of(new ChatMessage(ChatMessageRole.USER.value(), testMessage)))
            //         .stream(Boolean.TRUE)
            //         .build();

            // log.info("Zhipuai API making stream test call with message: {}", testMessage);

            // // 调用API
            // ChatCompletionResponse response = client.chat().createChatCompletion(chatCompletionRequest);

            // log.info("Zhipuai API stream test response success: {}", response.isSuccess());

            // if (response.isSuccess()) {
            //     log.info("Zhipuai API stream test starting flowable processing");
            //
            //     final int[] messageCount = {0};
            //
            //     response.getFlowable()
            //             .doOnNext(modelData -> {
            //                 messageCount[0]++;
            //                 Delta delta = modelData.getChoices().get(0).getDelta();
            //                 log.info("Zhipuai API stream test message #{}: delta={}", messageCount[0], delta);
            //                 if (delta != null) {
            //                     log.info("Zhipuai API stream test message #{}: content={}", messageCount[0], delta.getContent());
            //                 } else {
            //                     log.info("Zhipuai API stream test message #{}: delta is null", messageCount[0]);
            //                 }
            //             })
            //             .doOnComplete(() -> {
            //                 log.info("Zhipuai API stream test completed, total messages: {}", messageCount[0]);
            //             })
            //             .doOnError(error -> {
            //                 log.error("Zhipuai API stream test error: ", error);
            //             })
            //             .subscribe();
            // } else {
            //     log.error("Zhipuai API stream test failed: {}", response.getError());
            // }

        } catch (Exception e) {
            log.error("Zhipuai API test stream response error", e);
        }
    }


    
}
