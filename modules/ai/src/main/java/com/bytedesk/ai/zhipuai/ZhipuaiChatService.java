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

import com.zhipu.oapi.ClientV4;
import com.zhipu.oapi.Constants;
import com.zhipu.oapi.service.v4.model.ChatCompletionRequest;
import com.zhipu.oapi.service.v4.model.ChatFunction;
import com.zhipu.oapi.service.v4.model.ChatMessage;
import com.zhipu.oapi.service.v4.model.ChatMessageAccumulator;
import com.zhipu.oapi.service.v4.model.ChatMessageRole;
import com.zhipu.oapi.service.v4.model.ChatMeta;
import com.zhipu.oapi.service.v4.model.ChatTool;
import com.zhipu.oapi.service.v4.model.ChatToolType;
import com.zhipu.oapi.service.v4.model.ModelApiResponse;
import com.zhipu.oapi.service.v4.model.ModelData;
import com.zhipu.oapi.service.v4.model.QueryModelResultRequest;
import com.zhipu.oapi.service.v4.model.QueryModelResultResponse;
import com.zhipu.oapi.service.v4.model.WebSearch;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Slf4j
@Service
@ConditionalOnProperty(prefix = "spring.ai.zhipuai.chat", name = "enabled", havingValue = "true", matchIfMissing = false)
public class ZhipuaiChatService {

    @Autowired
    @Qualifier("zhipuaiChatClient")
    private ClientV4 client;

    @Autowired
    private ZhipuaiChatConfig zhipuaiChatConfig;

    /**
     * 角色扮演聊天
     */
    public String rolePlayChat(String message, String userInfo, String botInfo, String botName, String userName) {
        // 添加请求日志
        log.info("Zhipuai API role play request - message length: {}, userInfo: {}, botInfo: {}, botName: {}, userName: {}", 
                message.length(), userInfo, botInfo, botName, userName);
        
        long startTime = System.currentTimeMillis();
        
        // 使用默认client进行角色扮演聊天
        ClientV4 chatClient = client;
        
        try {
            if (client == null) {
                log.error("Zhipuai API client is null");
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
            
            log.info("Zhipuai API role play invoking model with requestId: {}", requestId);
            ModelApiResponse response = chatClient.invokeModelApi(chatCompletionRequest);
            
            if (response.isSuccess() && response.getData() != null) {
                log.info("Zhipuai API role play response success");
                
                // 提取token使用情况
                // TokenUsage tokenUsage = extractZhipuaiTokenUsage(response);
                // log.info("Zhipuai API role play tokenUsage: {}", tokenUsage);
                
                Object content = response.getData().getChoices().get(0).getMessage().getContent();
                return content != null ? content.toString() : null;
            } else {
                log.error("Zhipuai API role play error: {}", response.getError());
                return "Error: " + (response.getError() != null ? response.getError().getMessage() : "Unknown error");
            }
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
        // 添加请求日志
        log.info("Zhipuai API function calling request - message length: {}, model: {}, temperature: {}, functions count: {}", 
                message.length(), model, temperature, functions != null ? functions.size() : 0);
        
        long startTime = System.currentTimeMillis();
        
        // 使用默认client进行函数调用聊天
        ClientV4 chatClient = client;
        
        try {

            List<ChatMessage> messages = new ArrayList<>();
            ChatMessage chatMessage = new ChatMessage(ChatMessageRole.USER.value(), message);
            messages.add(chatMessage);

            List<ChatTool> chatToolList = new ArrayList<>();
            if (functions != null) {
                for (ChatFunction function : functions) {
                    ChatTool chatTool = new ChatTool();
                    chatTool.setType(ChatToolType.FUNCTION.value());
                    chatTool.setFunction(function);
                    chatToolList.add(chatTool);
                }
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
            
            log.info("Zhipuai API function calling invoking model with requestId: {}", requestId);
            ModelApiResponse response = chatClient.invokeModelApi(chatCompletionRequest);
            
            if (response.isSuccess() && response.getData() != null) {
                log.info("Zhipuai API function calling response success");
                
                // 提取token使用情况
                // TokenUsage tokenUsage = extractZhipuaiTokenUsage(response);
                // log.info("Zhipuai API function calling tokenUsage: {}", tokenUsage);
                
                Object content = response.getData().getChoices().get(0).getMessage().getContent();
                return content != null ? content.toString() : null;
            } else {
                log.error("Zhipuai API function calling error: {}", response.getError());
                return "Error: " + (response.getError() != null ? response.getError().getMessage() : "Unknown error");
            }
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
        // 使用默认client进行流式函数调用聊天
        ClientV4 chatClient = client;
        
        try {

            List<ChatMessage> messages = new ArrayList<>();
            ChatMessage chatMessage = new ChatMessage(ChatMessageRole.USER.value(), message);
            messages.add(chatMessage);

            List<ChatTool> chatToolList = new ArrayList<>();
            if (functions != null) {
                for (ChatFunction function : functions) {
                    ChatTool chatTool = new ChatTool();
                    chatTool.setType(ChatToolType.FUNCTION.value());
                    chatTool.setFunction(function);
                    chatToolList.add(chatTool);
                }
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
            
            ModelApiResponse response = chatClient.invokeModelApi(chatCompletionRequest);
            
            if (response.isSuccess()) {
                return Flux.from(mapStreamToAccumulator(response.getFlowable()).map(accumulator -> {
                    log.info("Zhipuai API function calling accumulator received: {}", accumulator);
                    log.info("Zhipuai API function calling accumulator class: {}", accumulator.getClass().getName());
                    
                    Object delta = accumulator.getDelta();
                    log.info("Zhipuai API function calling delta: {}", delta);
                    log.info("Zhipuai API function calling delta class: {}", delta != null ? delta.getClass().getName() : "null");
                    
                    // 处理tool_calls（如果有的话）
                    if (delta != null && delta instanceof com.zhipu.oapi.service.v4.model.ChatMessage) {
                        com.zhipu.oapi.service.v4.model.ChatMessage deltaMessage = (com.zhipu.oapi.service.v4.model.ChatMessage) delta;
                        if (deltaMessage.getTool_calls() != null) {
                            log.info("Zhipuai API function calling tool_calls: {}", deltaMessage.getTool_calls());
                        }
                    }
                    
                    if (delta instanceof com.zhipu.oapi.service.v4.model.ChatMessage) {
                        Object content = ((com.zhipu.oapi.service.v4.model.ChatMessage) delta).getContent();
                        log.info("Zhipuai API function calling content: {}", content);
                        return content != null ? content.toString() : "";
                    } else if (delta != null) {
                        String deltaStr = delta.toString();
                        log.info("Zhipuai API function calling delta as string: {}", deltaStr);
                        
                        // 尝试从JSON字符串中提取content字段
                        // String extractedContent = extractContentFromDeltaString(deltaStr);
                        // if (extractedContent != null && !extractedContent.isEmpty()) {
                        //     log.info("Zhipuai API function calling extracted content: {}", extractedContent);
                        //     return extractedContent;
                        // } else if (!isEmptyAssistantMessage(deltaStr)) {
                        //     return deltaStr;
                        // } else {
                        //     return "";
                        // }
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
        // 添加请求日志
        log.info("Zhipuai API async request - message length: {}", message.length());
        
        long startTime = System.currentTimeMillis();
        
        // 使用默认client进行异步聊天
        ClientV4 chatClient = client;
        
        try {

            List<ChatMessage> messages = new ArrayList<>();
            ChatMessage chatMessage = new ChatMessage(ChatMessageRole.USER.value(), message);
            messages.add(chatMessage);

            ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                    .model(zhipuaiChatConfig.getModel())
                    .stream(Boolean.FALSE)
                    .invokeMethod(Constants.invokeMethodAsync)
                    .messages(messages)
                    .build();
            
            log.info("Zhipuai API async invoking model");
            ModelApiResponse response = chatClient.invokeModelApi(chatCompletionRequest);
            
            if (response.isSuccess() && response.getData() != null) {
                String taskId = response.getData().getId();
                log.info("Zhipuai API async task created with taskId: {}", taskId);
                
                // 轮询获取结果
                return pollAsyncResult(taskId);
            } else {
                log.error("Zhipuai API async error: {}", response.getError());
                return "Error: " + (response.getError() != null ? response.getError().getMessage() : "Unknown error");
            }
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
        
        try {
            int maxAttempts = 30; // 最多轮询30次
            int attempt = 0;
            
            while (attempt < maxAttempts) {
                log.debug("Zhipuai API polling attempt {}/{} for taskId: {}", attempt + 1, maxAttempts, taskId);
                
                QueryModelResultRequest request = new QueryModelResultRequest();
                request.setTaskId(taskId);
                
                QueryModelResultResponse response = client.queryModelResult(request);
                
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
        // 添加请求日志
        log.info("Zhipuai API web search request - message length: {}, searchQuery: {}", 
                message.length(), searchQuery);
        
        long startTime = System.currentTimeMillis();
        
        // 使用默认client进行网络搜索聊天
        ClientV4 chatClient = client;
        
        try {

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
            
            log.info("Zhipuai API web search invoking model with requestId: {}", requestId);
            ModelApiResponse response = chatClient.invokeModelApi(chatCompletionRequest);
            
            if (response.isSuccess() && response.getData() != null) {
                log.info("Zhipuai API web search response success");
                
                // 提取token使用情况
                // TokenUsage tokenUsage = extractZhipuaiTokenUsage(response);
                // log.info("Zhipuai API web search tokenUsage: {}", tokenUsage);
                
                Object content = response.getData().getChoices().get(0).getMessage().getContent();
                return content != null ? content.toString() : null;
            } else {
                log.error("Zhipuai API web search error: {}", response.getError());
                return "Error: " + (response.getError() != null ? response.getError().getMessage() : "Unknown error");
            }
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


    /**
     * 将流式响应转换为Accumulator，参考官方示例
     */
    private io.reactivex.Flowable<ChatMessageAccumulator> mapStreamToAccumulator(io.reactivex.Flowable<ModelData> flowable) {
        return flowable.map(chunk -> {
            return new ChatMessageAccumulator(
                chunk.getChoices().get(0).getDelta(), 
                null, 
                chunk.getChoices().get(0), 
                chunk.getUsage(), 
                chunk.getCreated(), 
                chunk.getId()
            );
        });
    }

    /**
     * 测试流式响应功能
     * 用于调试流式响应问题
     */
    public void testStreamResponse() {
        // 使用默认client进行测试
        // ClientV4 chatClient = client;
        
        try {
            log.info("Zhipuai API testing stream response...");
            
            // // 创建一个简单的测试请求
            // String testMessage = "Hello, this is a test message for stream response.";
            // ChatCompletionRequest chatCompletionRequest = createDynamicRequest(null, testMessage, true);
            
            // log.info("Zhipuai API making stream test call with message: {}", testMessage);
            
            // // 调用API
            // ModelApiResponse response = chatClient.invokeModelApi(chatCompletionRequest);
            
            // log.info("Zhipuai API stream test response success: {}", response.isSuccess());
            
            // if (response.isSuccess()) {
            //     log.info("Zhipuai API stream test starting flowable processing");
                
            //     final int[] messageCount = {0};
                
            //     // 使用AtomicBoolean来标记是否是第一个消息，参考官方示例
            //     // java.util.concurrent.atomic.AtomicBoolean isFirst = new java.util.concurrent.atomic.AtomicBoolean(true);
                
            //     mapStreamToAccumulator(response.getFlowable())
            //             .doOnNext(accumulator -> {
            //                 messageCount[0]++;
            //                 log.info("Zhipuai API stream test message #{}: accumulator={}", messageCount[0], accumulator);
            //                 log.info("Zhipuai API stream test message #{}: accumulator class={}", messageCount[0], accumulator.getClass().getName());
                            
            //                 Object delta = accumulator.getDelta();
            //                 log.info("Zhipuai API stream test message #{}: delta={}", messageCount[0], delta);
            //                 log.info("Zhipuai API stream test message #{}: delta class={}", messageCount[0], delta != null ? delta.getClass().getName() : "null");
                            
            //                 if (delta instanceof com.zhipu.oapi.service.v4.model.ChatMessage) {
            //                     Object content = ((com.zhipu.oapi.service.v4.model.ChatMessage) delta).getContent();
            //                     log.info("Zhipuai API stream test message #{}: content={}", messageCount[0], content);
            //                 } else {
            //                     log.info("Zhipuai API stream test message #{}: delta is not ChatMessage", messageCount[0]);
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
