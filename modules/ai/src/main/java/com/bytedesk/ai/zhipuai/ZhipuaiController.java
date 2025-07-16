/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-19 09:39:15
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-16 13:48:48
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.zhipuai;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import com.zhipu.oapi.service.v4.model.ChatFunction;
import com.zhipu.oapi.service.v4.model.ChatFunctionParameters;
import com.bytedesk.core.config.properties.BytedeskProperties;
import com.bytedesk.core.utils.JsonResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.io.File;

/**
 * 智谱AI接口 - 使用 oapi-java-sdk
 * https://open.bigmodel.cn/dev/api#sdk_install
 * https://github.com/MetaGLM/zhipuai-sdk-java-v4
 */
@Slf4j
@RestController
@RequestMapping("/zhipuai")
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "spring.ai.zhipuai.chat", name = "enabled", havingValue = "true", matchIfMissing = false)
public class ZhipuaiController {

    private final BytedeskProperties bytedeskProperties;
    private final ZhipuaiService zhipuaiService;
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    /**
     * 同步调用 - 使用新的统一接口
     * GET http://127.0.0.1:9003/zhipuai/sync?message=hello
     */
    @GetMapping("/sync")
    public ResponseEntity<JsonResult<?>> chatSync(@RequestParam("message") String message) {
        if (!bytedeskProperties.getDebug()) {
            return ResponseEntity.ok(JsonResult.error("Zhipuai service is not available"));
        }
        
        try {
            String result = zhipuaiService.processPromptSync(message, null, "");
            return ResponseEntity.ok(JsonResult.success(result));
        } catch (Exception e) {
            log.error("Error in chat sync", e);
            return ResponseEntity.ok(JsonResult.error("Error: " + e.getMessage()));
        }
    }

    /**
     * 流式调用 - 使用新的统一接口
     * GET http://127.0.0.1:9003/zhipuai/stream?message=hello
     */
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> chatStream(@RequestParam("message") String message) {
        if (!bytedeskProperties.getDebug()) {
            return Flux.empty();
        }
        
        // 由于新的接口使用SSE，这里返回一个简单的流
        return Flux.just("Streaming is now handled via SSE endpoint: /zhipuai/sse");
    }

    /**
     * SSE调用 - 使用新的统一接口
     * GET http://127.0.0.1:9003/zhipuai/sse?message=hello
     */
    @GetMapping(value = "/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter chatSse(@RequestParam("message") String message) {
        if (!bytedeskProperties.getDebug()) {
            return null;
        }
        
        SseEmitter emitter = new SseEmitter(180_000L); // 3分钟超时
        
        executorService.execute(() -> {
            try {
                // 创建简单的消息对象用于测试
                // 这里需要创建MessageProtobuf对象，但为了简化，我们直接调用服务
                // 在实际使用中，应该通过SpringAIServiceRegistry调用
                String result = zhipuaiService.processPromptSync(message, null, "");
                emitter.send("data: " + result + "\n\n");
                emitter.complete();
            } catch (Exception e) {
                log.error("Error in SSE", e);
                emitter.completeWithError(e);
            }
        });
        
        // 添加超时和完成时的回调
        emitter.onTimeout(() -> {
            log.warn("SSE connection timed out");
            emitter.complete();
        });
        
        emitter.onCompletion(() -> {
            log.info("SSE connection completed");
        });
        
        return emitter;
    }

    /**
     * 角色扮演
     * GET http://127.0.0.1:9003/zhipuai/roleplay?message=hello&userInfo=...&botInfo=...&botName=...&userName=...
     */
    @GetMapping("/roleplay")
    public ResponseEntity<JsonResult<?>> rolePlayChat(
            @RequestParam("message") String message,
            @RequestParam("userInfo") String userInfo,
            @RequestParam("botInfo") String botInfo,
            @RequestParam("botName") String botName,
            @RequestParam("userName") String userName) {
        
        if (!bytedeskProperties.getDebug()) {
            return ResponseEntity.ok(JsonResult.error("Zhipuai service is not available"));
        }
        
        try {
            String result = zhipuaiService.rolePlayChat(message, userInfo, botInfo, botName, userName);
            return ResponseEntity.ok(JsonResult.success(result));
        } catch (Exception e) {
            log.error("Error in role play chat", e);
            return ResponseEntity.ok(JsonResult.error("Error: " + e.getMessage()));
        }
    }

    /**
     * 健康检查
     */
    @GetMapping("/health")
    public ResponseEntity<JsonResult<?>> health() {
        if (!bytedeskProperties.getDebug()) {
            return ResponseEntity.ok(JsonResult.error("Zhipuai service is not available"));
        }
        
        try {
            boolean healthy = zhipuaiService.isHealthy();
            return ResponseEntity.ok(JsonResult.success(healthy ? "ok" : "fail"));
        } catch (Exception e) {
            log.error("Error in health check", e);
            return ResponseEntity.ok(JsonResult.error("Error: " + e.getMessage()));
        }
    }

    /**
     * Function Calling 同步调用
     * POST http://127.0.0.1:9003/zhipuai/function-call
     */
    @PostMapping("/function-call")
    public ResponseEntity<JsonResult<?>> functionCallingChat(@RequestBody Map<String, Object> request) {
        if (!bytedeskProperties.getDebug()) {
            return ResponseEntity.ok(JsonResult.error("Zhipuai service is not available"));
        }
        
        try {
            String message = (String) request.get("message");
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> functionsData = (List<Map<String, Object>>) request.get("functions");
            
            List<ChatFunction> functions = new ArrayList<>();
            for (Map<String, Object> funcData : functionsData) {
                ChatFunction function = ChatFunction.builder()
                        .name((String) funcData.get("name"))
                        .description((String) funcData.get("description"))
                        .parameters(createFunctionParameters((Map<String, Object>) funcData.get("parameters")))
                        .build();
                functions.add(function);
            }
            
            String result = zhipuaiService.functionCallingChat(message, functions);
            return ResponseEntity.ok(JsonResult.success(result));
        } catch (Exception e) {
            log.error("Error in function calling chat", e);
            return ResponseEntity.ok(JsonResult.error("Error: " + e.getMessage()));
        }
    }

    /**
     * Function Calling 流式调用
     * POST http://127.0.0.1:9003/zhipuai/function-call/stream
     */
    @PostMapping(value = "/function-call/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> functionCallingChatStream(@RequestBody Map<String, Object> request) {
        if (!bytedeskProperties.getDebug()) {
            return Flux.empty();
        }
        
        try {
            String message = (String) request.get("message");
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> functionsData = (List<Map<String, Object>>) request.get("functions");
            
            List<ChatFunction> functions = new ArrayList<>();
            for (Map<String, Object> funcData : functionsData) {
                ChatFunction function = ChatFunction.builder()
                        .name((String) funcData.get("name"))
                        .description((String) funcData.get("description"))
                        .parameters(createFunctionParameters((Map<String, Object>) funcData.get("parameters")))
                        .build();
                functions.add(function);
            }
            
            return zhipuaiService.functionCallingChatStream(message, functions);
        } catch (Exception e) {
            log.error("Error in function calling chat stream", e);
            return Flux.just("Error: " + e.getMessage());
        }
    }

    /**
     * 创建函数参数
     */
    private ChatFunctionParameters createFunctionParameters(Map<String, Object> paramsData) {
        ChatFunctionParameters parameters = new ChatFunctionParameters();
        parameters.setType((String) paramsData.get("type"));
        
        @SuppressWarnings("unchecked")
        Map<String, Object> properties = (Map<String, Object>) paramsData.get("properties");
        if (properties != null) {
            parameters.setProperties(properties);
        }
        
        @SuppressWarnings("unchecked")
        List<String> required = (List<String>) paramsData.get("required");
        if (required != null) {
            parameters.setRequired(required);
        }
        
        return parameters;
    }

    /**
     * 示例：查询天气的 Function Calling
     * GET http://127.0.0.1:9003/zhipuai/weather?city=北京
     */
    @GetMapping("/weather")
    public ResponseEntity<JsonResult<?>> weatherFunctionCall(@RequestParam("city") String city) {
        if (!bytedeskProperties.getDebug()) {
            return ResponseEntity.ok(JsonResult.error("Zhipuai service is not available"));
        }
        
        try {
            List<ChatFunction> functions = new ArrayList<>();
            
            // 定义天气查询函数
            Map<String, Object> properties = new HashMap<>();
            properties.put("city", new HashMap<String, Object>() {{
                put("type", "string");
                put("description", "城市名称");
            }});
            
            ChatFunctionParameters parameters = new ChatFunctionParameters();
            parameters.setType("object");
            parameters.setProperties(properties);
            parameters.setRequired(List.of("city"));
            
            ChatFunction weatherFunction = ChatFunction.builder()
                    .name("get_weather")
                    .description("获取指定城市的天气信息")
                    .parameters(parameters)
                    .build();
            
            functions.add(weatherFunction);
            
            String message = "请告诉我" + city + "的天气情况";
            String result = zhipuaiService.functionCallingChat(message, functions);
            return ResponseEntity.ok(JsonResult.success(result));
        } catch (Exception e) {
            log.error("Error in weather function call", e);
            return ResponseEntity.ok(JsonResult.error("Error: " + e.getMessage()));
        }
    }

    /**
     * 示例：查询航班信息的 Function Calling
     * GET http://127.0.0.1:9003/zhipuai/flight?from=成都&to=北京
     */
    @GetMapping("/flight")
    public ResponseEntity<JsonResult<?>> flightFunctionCall(
            @RequestParam("from") String from,
            @RequestParam("to") String to) {
        if (!bytedeskProperties.getDebug()) {
            return ResponseEntity.ok(JsonResult.error("Zhipuai service is not available"));
        }
        
        try {
            List<ChatFunction> functions = new ArrayList<>();
            
            // 定义航班查询函数
            Map<String, Object> properties = new HashMap<>();
            properties.put("departure", new HashMap<String, Object>() {{
                put("type", "string");
                put("description", "出发城市");
            }});
            properties.put("destination", new HashMap<String, Object>() {{
                put("type", "string");
                put("description", "目的地城市");
            }});
            
            ChatFunctionParameters parameters = new ChatFunctionParameters();
            parameters.setType("object");
            parameters.setProperties(properties);
            parameters.setRequired(List.of("departure", "destination"));
            
            ChatFunction flightFunction = ChatFunction.builder()
                    .name("query_flight_prices")
                    .description("查询航班价格信息")
                    .parameters(parameters)
                    .build();
            
            functions.add(flightFunction);
            
            String message = "请查询从" + from + "到" + to + "的航班价格";
            String result = zhipuaiService.functionCallingChat(message, functions);
            return ResponseEntity.ok(JsonResult.success(result));
        } catch (Exception e) {
            log.error("Error in flight function call", e);
            return ResponseEntity.ok(JsonResult.error("Error: " + e.getMessage()));
        }
    }

    /**
     * 图像生成
     * POST http://127.0.0.1:9003/zhipuai/image
     */
    @PostMapping("/image")
    public ResponseEntity<JsonResult<?>> generateImage(@RequestBody Map<String, String> request) {
        if (!bytedeskProperties.getDebug()) {
            return ResponseEntity.ok(JsonResult.error("Zhipuai service is not available"));
        }
        
        try {
            String prompt = request.get("prompt");
            String requestId = request.get("requestId");
            
            if (prompt == null || prompt.trim().isEmpty()) {
                return ResponseEntity.ok(JsonResult.error("Prompt is required"));
            }
            
            String result = zhipuaiService.generateImage(prompt, requestId);
            return ResponseEntity.ok(JsonResult.success(result));
        } catch (Exception e) {
            log.error("Error in image generation", e);
            return ResponseEntity.ok(JsonResult.error("Error: " + e.getMessage()));
        }
    }

    /**
     * 向量嵌入
     * POST http://127.0.0.1:9003/zhipuai/embedding
     */
    @PostMapping("/embedding")
    public ResponseEntity<JsonResult<?>> getEmbedding(@RequestBody Map<String, String> request) {
        if (!bytedeskProperties.getDebug()) {
            return ResponseEntity.ok(JsonResult.error("Zhipuai service is not available"));
        }
        
        try {
            String text = request.get("text");
            
            if (text == null || text.trim().isEmpty()) {
                return ResponseEntity.ok(JsonResult.error("Text is required"));
            }
            
            List<Double> result = zhipuaiService.getEmbedding(text);
            return ResponseEntity.ok(JsonResult.success(result));
        } catch (Exception e) {
            log.error("Error in embedding", e);
            return ResponseEntity.ok(JsonResult.error("Error: " + e.getMessage()));
        }
    }

    /**
     * 批量向量嵌入
     * POST http://127.0.0.1:9003/zhipuai/embeddings
     */
    @PostMapping("/embeddings")
    public ResponseEntity<JsonResult<?>> getEmbeddings(@RequestBody Map<String, Object> request) {
        if (!bytedeskProperties.getDebug()) {
            return ResponseEntity.ok(JsonResult.error("Zhipuai service is not available"));
        }
        
        try {
            @SuppressWarnings("unchecked")
            List<String> texts = (List<String>) request.get("texts");
            
            if (texts == null || texts.isEmpty()) {
                return ResponseEntity.ok(JsonResult.error("Texts are required"));
            }
            
            List<List<Double>> result = zhipuaiService.getEmbeddings(texts);
            return ResponseEntity.ok(JsonResult.success(result));
        } catch (Exception e) {
            log.error("Error in embeddings", e);
            return ResponseEntity.ok(JsonResult.error("Error: " + e.getMessage()));
        }
    }

    /**
     * 语音合成
     * POST http://127.0.0.1:9003/zhipuai/speech
     */
    @PostMapping("/speech")
    public ResponseEntity<JsonResult<?>> generateSpeech(@RequestBody Map<String, String> request) {
        if (!bytedeskProperties.getDebug()) {
            return ResponseEntity.ok(JsonResult.error("Zhipuai service is not available"));
        }
        
        try {
            String text = request.get("text");
            String voice = request.get("voice");
            String responseFormat = request.get("responseFormat");
            
            if (text == null || text.trim().isEmpty()) {
                return ResponseEntity.ok(JsonResult.error("Text is required"));
            }
            
            File result = zhipuaiService.generateSpeech(text, voice, responseFormat);
            if (result != null) {
                return ResponseEntity.ok(JsonResult.success("Speech generated: " + result.getAbsolutePath()));
            } else {
                return ResponseEntity.ok(JsonResult.error("Failed to generate speech"));
            }
        } catch (Exception e) {
            log.error("Error in speech generation", e);
            return ResponseEntity.ok(JsonResult.error("Error: " + e.getMessage()));
        }
    }

    /**
     * 自定义语音合成
     * POST http://127.0.0.1:9003/zhipuai/speech/custom
     */
    @PostMapping("/speech/custom")
    public ResponseEntity<JsonResult<?>> generateCustomSpeech(@RequestBody Map<String, Object> request) {
        if (!bytedeskProperties.getDebug()) {
            return ResponseEntity.ok(JsonResult.error("Zhipuai service is not available"));
        }
        
        try {
            String text = (String) request.get("text");
            String voiceText = (String) request.get("voiceText");
            String voiceDataPath = (String) request.get("voiceDataPath");
            String responseFormat = (String) request.get("responseFormat");
            
            if (text == null || text.trim().isEmpty()) {
                return ResponseEntity.ok(JsonResult.error("Text is required"));
            }
            
            if (voiceDataPath == null || voiceDataPath.trim().isEmpty()) {
                return ResponseEntity.ok(JsonResult.error("Voice data path is required"));
            }
            
            File voiceData = new File(voiceDataPath);
            if (!voiceData.exists()) {
                return ResponseEntity.ok(JsonResult.error("Voice data file not found"));
            }
            
            File result = zhipuaiService.generateCustomSpeech(text, voiceText, voiceData, responseFormat);
            if (result != null) {
                return ResponseEntity.ok(JsonResult.success("Custom speech generated: " + result.getAbsolutePath()));
            } else {
                return ResponseEntity.ok(JsonResult.error("Failed to generate custom speech"));
            }
        } catch (Exception e) {
            log.error("Error in custom speech generation", e);
            return ResponseEntity.ok(JsonResult.error("Error: " + e.getMessage()));
        }
    }

    /**
     * 文件上传
     * POST http://127.0.0.1:9003/zhipuai/file/upload
     */
    @PostMapping("/file/upload")
    public ResponseEntity<JsonResult<?>> uploadFile(@RequestBody Map<String, String> request) {
        if (!bytedeskProperties.getDebug()) {
            return ResponseEntity.ok(JsonResult.error("Zhipuai service is not available"));
        }
        
        try {
            String filePath = request.get("filePath");
            String purpose = request.get("purpose");
            
            if (filePath == null || filePath.trim().isEmpty()) {
                return ResponseEntity.ok(JsonResult.error("File path is required"));
            }
            
            if (purpose == null || purpose.trim().isEmpty()) {
                purpose = "fine-tune";
            }
            
            String result = zhipuaiService.uploadFile(filePath, purpose);
            return ResponseEntity.ok(JsonResult.success(result));
        } catch (Exception e) {
            log.error("Error in file upload", e);
            return ResponseEntity.ok(JsonResult.error("Error: " + e.getMessage()));
        }
    }

    /**
     * 查询文件列表
     * GET http://127.0.0.1:9003/zhipuai/files
     */
    @GetMapping("/files")
    public ResponseEntity<JsonResult<?>> queryFiles() {
        if (!bytedeskProperties.getDebug()) {
            return ResponseEntity.ok(JsonResult.error("Zhipuai service is not available"));
        }
        
        try {
            List<Map<String, Object>> result = zhipuaiService.queryFiles();
            return ResponseEntity.ok(JsonResult.success(result));
        } catch (Exception e) {
            log.error("Error in query files", e);
            return ResponseEntity.ok(JsonResult.error("Error: " + e.getMessage()));
        }
    }

    /**
     * 下载文件
     * POST http://127.0.0.1:9003/zhipuai/file/download
     */
    @PostMapping("/file/download")
    public ResponseEntity<JsonResult<?>> downloadFile(@RequestBody Map<String, String> request) {
        if (!bytedeskProperties.getDebug()) {
            return ResponseEntity.ok(JsonResult.error("Zhipuai service is not available"));
        }
        
        try {
            String fileId = request.get("fileId");
            String outputPath = request.get("outputPath");
            
            if (fileId == null || fileId.trim().isEmpty()) {
                return ResponseEntity.ok(JsonResult.error("File ID is required"));
            }
            
            if (outputPath == null || outputPath.trim().isEmpty()) {
                return ResponseEntity.ok(JsonResult.error("Output path is required"));
            }
            
            File result = zhipuaiService.downloadFile(fileId, outputPath);
            if (result != null) {
                return ResponseEntity.ok(JsonResult.success("File downloaded: " + result.getAbsolutePath()));
            } else {
                return ResponseEntity.ok(JsonResult.error("Failed to download file"));
            }
        } catch (Exception e) {
            log.error("Error in file download", e);
            return ResponseEntity.ok(JsonResult.error("Error: " + e.getMessage()));
        }
    }

    /**
     * 创建微调任务
     * POST http://127.0.0.1:9003/zhipuai/finetune/create
     */
    @PostMapping("/finetune/create")
    public ResponseEntity<JsonResult<?>> createFineTuningJob(@RequestBody Map<String, String> request) {
        if (!bytedeskProperties.getDebug()) {
            return ResponseEntity.ok(JsonResult.error("Zhipuai service is not available"));
        }
        
        try {
            String model = request.get("model");
            String trainingFile = request.get("trainingFile");
            
            if (model == null || model.trim().isEmpty()) {
                return ResponseEntity.ok(JsonResult.error("Model is required"));
            }
            
            if (trainingFile == null || trainingFile.trim().isEmpty()) {
                return ResponseEntity.ok(JsonResult.error("Training file is required"));
            }
            
            String result = zhipuaiService.createFineTuningJob(model, trainingFile);
            return ResponseEntity.ok(JsonResult.success(result));
        } catch (Exception e) {
            log.error("Error in create fine-tuning job", e);
            return ResponseEntity.ok(JsonResult.error("Error: " + e.getMessage()));
        }
    }

    /**
     * 查询微调任务
     * GET http://127.0.0.1:9003/zhipuai/finetune/{jobId}
     */
    @GetMapping("/finetune/{jobId}")
    public ResponseEntity<JsonResult<?>> queryFineTuningJob(@PathVariable String jobId) {
        if (!bytedeskProperties.getDebug()) {
            return ResponseEntity.ok(JsonResult.error("Zhipuai service is not available"));
        }
        
        try {
            Map<String, Object> result = zhipuaiService.queryFineTuningJob(jobId);
            return ResponseEntity.ok(JsonResult.success(result));
        } catch (Exception e) {
            log.error("Error in query fine-tuning job", e);
            return ResponseEntity.ok(JsonResult.error("Error: " + e.getMessage()));
        }
    }

    /**
     * 异步聊天
     * POST http://127.0.0.1:9003/zhipuai/async
     */
    @PostMapping("/async")
    public ResponseEntity<JsonResult<?>> chatAsync(@RequestBody Map<String, String> request) {
        if (!bytedeskProperties.getDebug()) {
            return ResponseEntity.ok(JsonResult.error("Zhipuai service is not available"));
        }
        
        try {
            String message = request.get("message");
            
            if (message == null || message.trim().isEmpty()) {
                return ResponseEntity.ok(JsonResult.error("Message is required"));
            }
            
            String result = zhipuaiService.chatAsync(message);
            return ResponseEntity.ok(JsonResult.success(result));
        } catch (Exception e) {
            log.error("Error in async chat", e);
            return ResponseEntity.ok(JsonResult.error("Error: " + e.getMessage()));
        }
    }

    /**
     * 带Web搜索的聊天
     * POST http://127.0.0.1:9003/zhipuai/websearch
     */
    @PostMapping("/websearch")
    public ResponseEntity<JsonResult<?>> chatWithWebSearch(@RequestBody Map<String, String> request) {
        if (!bytedeskProperties.getDebug()) {
            return ResponseEntity.ok(JsonResult.error("Zhipuai service is not available"));
        }
        
        try {
            String message = request.get("message");
            String searchQuery = request.get("searchQuery");
            
            if (message == null || message.trim().isEmpty()) {
                return ResponseEntity.ok(JsonResult.error("Message is required"));
            }
            
            if (searchQuery == null || searchQuery.trim().isEmpty()) {
                searchQuery = message; // 如果没有指定搜索查询，使用消息作为搜索查询
            }
            
            String result = zhipuaiService.chatWithWebSearch(message, searchQuery);
            return ResponseEntity.ok(JsonResult.success(result));
        } catch (Exception e) {
            log.error("Error in web search chat", e);
            return ResponseEntity.ok(JsonResult.error("Error: " + e.getMessage()));
        }
    }

    /**
     * 语音模型聊天
     * POST http://127.0.0.1:9003/zhipuai/voice
     */
    @PostMapping("/voice")
    public ResponseEntity<JsonResult<?>> chatWithVoice(@RequestBody Map<String, String> request) {
        if (!bytedeskProperties.getDebug()) {
            return ResponseEntity.ok(JsonResult.error("Zhipuai service is not available"));
        }
        
        try {
            String message = request.get("message");
            
            if (message == null || message.trim().isEmpty()) {
                return ResponseEntity.ok(JsonResult.error("Message is required"));
            }
            
            String result = zhipuaiService.chatWithVoice(message);
            return ResponseEntity.ok(JsonResult.success(result));
        } catch (Exception e) {
            log.error("Error in voice chat", e);
            return ResponseEntity.ok(JsonResult.error("Error: " + e.getMessage()));
        }
    }

    /**
     * 测试流式响应功能
     * GET http://127.0.0.1:9003/zhipuai/test-stream
     */
    @GetMapping("/test-stream")
    public ResponseEntity<JsonResult<?>> testStreamResponse() {
        if (!bytedeskProperties.getDebug()) {
            return ResponseEntity.ok(JsonResult.error("Zhipuai service is not available"));
        }
        
        try {
            zhipuaiService.testStreamResponse();
            return ResponseEntity.ok(JsonResult.success("Stream response test completed. Check logs for details."));
        } catch (Exception e) {
            log.error("Error testing stream response", e);
            return ResponseEntity.ok(JsonResult.error("Error testing stream response: " + e.getMessage()));
        }
    }

    /**
     * 简单流式测试 - 完全按照官方示例代码实现
     * GET http://127.0.0.1:9003/zhipuai/test-simple-stream
     */
    @GetMapping("/test-simple-stream")
    public ResponseEntity<JsonResult<?>> testSimpleStream() {
        if (!bytedeskProperties.getDebug()) {
            return ResponseEntity.ok(JsonResult.error("Zhipuai service is not available"));
        }
        
        try {
            zhipuaiService.testSimpleStream();
            return ResponseEntity.ok(JsonResult.success("Simple stream test completed. Check logs for details."));
        } catch (Exception e) {
            log.error("Error testing simple stream", e);
            return ResponseEntity.ok(JsonResult.error("Error testing simple stream: " + e.getMessage()));
        }
    }

    /**
     * 在 Bean 销毁时关闭线程池
     */
    public void destroy() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
} 