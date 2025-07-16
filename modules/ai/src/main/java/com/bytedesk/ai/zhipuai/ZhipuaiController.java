/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-19 09:39:15
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-23 11:45:27
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import com.zhipu.oapi.service.v4.model.ChatFunction;
import com.zhipu.oapi.service.v4.model.ChatFunctionParameters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.io.File;

@Slf4j
@RestController
@RequestMapping("/zhipuai")
@RequiredArgsConstructor
public class ZhipuaiController {

    private final ZhipuaiService zhipuaiService;
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    /**
     * 同步调用
     * GET /zhipuai/sync?message=xxx
     */
    @GetMapping("/sync")
    public ResponseEntity<String> chatSync(@RequestParam("message") String message) {
        String result = zhipuaiService.chatSync(message);
        return ResponseEntity.ok(result);
    }

    /**
     * 流式调用
     * GET /zhipuai/stream?message=xxx
     */
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> chatStream(@RequestParam("message") String message) {
        return zhipuaiService.chatStream(message);
    }

    /**
     * SSE调用
     * GET /zhipuai/sse?message=xxx
     */
    @GetMapping(value = "/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter chatSse(@RequestParam("message") String message) {
        SseEmitter emitter = new SseEmitter(180_000L);
        executorService.execute(() -> {
            try {
                zhipuaiService.chatSSE(message, emitter);
            } catch (Exception e) {
                log.error("Error in SSE", e);
                emitter.completeWithError(e);
            }
        });
        emitter.onTimeout(emitter::complete);
        emitter.onCompletion(() -> log.info("SSE completed"));
        return emitter;
    }

    /**
     * 角色扮演
     * GET /zhipuai/roleplay?message=xxx&userInfo=...&botInfo=...&botName=...&userName=...
     */
    @GetMapping("/roleplay")
    public ResponseEntity<String> rolePlayChat(
            @RequestParam("message") String message,
            @RequestParam("userInfo") String userInfo,
            @RequestParam("botInfo") String botInfo,
            @RequestParam("botName") String botName,
            @RequestParam("userName") String userName) {
        String result = zhipuaiService.rolePlayChat(message, userInfo, botInfo, botName, userName);
        return ResponseEntity.ok(result);
    }

    /**
     * 健康检查
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        boolean healthy = zhipuaiService.isHealthy();
        return ResponseEntity.ok(healthy ? "ok" : "fail");
    }

    /**
     * Function Calling 同步调用
     * POST /zhipuai/function-call
     */
    @PostMapping("/function-call")
    public ResponseEntity<String> functionCallingChat(@RequestBody Map<String, Object> request) {
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
        return ResponseEntity.ok(result);
    }

    /**
     * Function Calling 流式调用
     * POST /zhipuai/function-call/stream
     */
    @PostMapping(value = "/function-call/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> functionCallingChatStream(@RequestBody Map<String, Object> request) {
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
     * GET /zhipuai/weather?city=北京
     */
    @GetMapping("/weather")
    public ResponseEntity<String> weatherFunctionCall(@RequestParam("city") String city) {
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
        return ResponseEntity.ok(result);
    }

    /**
     * 示例：查询航班信息的 Function Calling
     * GET /zhipuai/flight?from=成都&to=北京
     */
    @GetMapping("/flight")
    public ResponseEntity<String> flightFunctionCall(
            @RequestParam("from") String from,
            @RequestParam("to") String to) {
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
        return ResponseEntity.ok(result);
    }

    /**
     * 图像生成
     * POST /zhipuai/image
     */
    @PostMapping("/image")
    public ResponseEntity<String> generateImage(@RequestBody Map<String, String> request) {
        String prompt = request.get("prompt");
        String requestId = request.get("requestId");
        
        if (prompt == null || prompt.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Prompt is required");
        }
        
        String result = zhipuaiService.generateImage(prompt, requestId);
        return ResponseEntity.ok(result);
    }

    /**
     * 向量嵌入
     * POST /zhipuai/embedding
     */
    @PostMapping("/embedding")
    public ResponseEntity<List<Double>> getEmbedding(@RequestBody Map<String, String> request) {
        String text = request.get("text");
        
        if (text == null || text.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(new ArrayList<>());
        }
        
        List<Double> result = zhipuaiService.getEmbedding(text);
        return ResponseEntity.ok(result);
    }

    /**
     * 批量向量嵌入
     * POST /zhipuai/embeddings
     */
    @PostMapping("/embeddings")
    public ResponseEntity<List<List<Double>>> getEmbeddings(@RequestBody Map<String, Object> request) {
        @SuppressWarnings("unchecked")
        List<String> texts = (List<String>) request.get("texts");
        
        if (texts == null || texts.isEmpty()) {
            return ResponseEntity.badRequest().body(new ArrayList<>());
        }
        
        List<List<Double>> result = zhipuaiService.getEmbeddings(texts);
        return ResponseEntity.ok(result);
    }

    /**
     * 语音合成
     * POST /zhipuai/speech
     */
    @PostMapping("/speech")
    public ResponseEntity<String> generateSpeech(@RequestBody Map<String, String> request) {
        String text = request.get("text");
        String voice = request.get("voice");
        String responseFormat = request.get("responseFormat");
        
        if (text == null || text.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Text is required");
        }
        
        File result = zhipuaiService.generateSpeech(text, voice, responseFormat);
        if (result != null) {
            return ResponseEntity.ok("Speech generated: " + result.getAbsolutePath());
        } else {
            return ResponseEntity.ok("Failed to generate speech");
        }
    }

    /**
     * 自定义语音合成
     * POST /zhipuai/speech/custom
     */
    @PostMapping("/speech/custom")
    public ResponseEntity<String> generateCustomSpeech(@RequestBody Map<String, Object> request) {
        String text = (String) request.get("text");
        String voiceText = (String) request.get("voiceText");
        String voiceDataPath = (String) request.get("voiceDataPath");
        String responseFormat = (String) request.get("responseFormat");
        
        if (text == null || text.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Text is required");
        }
        
        if (voiceDataPath == null || voiceDataPath.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Voice data path is required");
        }
        
        File voiceData = new File(voiceDataPath);
        if (!voiceData.exists()) {
            return ResponseEntity.badRequest().body("Voice data file not found");
        }
        
        File result = zhipuaiService.generateCustomSpeech(text, voiceText, voiceData, responseFormat);
        if (result != null) {
            return ResponseEntity.ok("Custom speech generated: " + result.getAbsolutePath());
        } else {
            return ResponseEntity.ok("Failed to generate custom speech");
        }
    }

    /**
     * 文件上传
     * POST /zhipuai/file/upload
     */
    @PostMapping("/file/upload")
    public ResponseEntity<String> uploadFile(@RequestBody Map<String, String> request) {
        String filePath = request.get("filePath");
        String purpose = request.get("purpose");
        
        if (filePath == null || filePath.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("File path is required");
        }
        
        if (purpose == null || purpose.trim().isEmpty()) {
            purpose = "fine-tune";
        }
        
        String result = zhipuaiService.uploadFile(filePath, purpose);
        return ResponseEntity.ok(result);
    }

    /**
     * 查询文件列表
     * GET /zhipuai/files
     */
    @GetMapping("/files")
    public ResponseEntity<List<Map<String, Object>>> queryFiles() {
        List<Map<String, Object>> result = zhipuaiService.queryFiles();
        return ResponseEntity.ok(result);
    }

    /**
     * 下载文件
     * POST /zhipuai/file/download
     */
    @PostMapping("/file/download")
    public ResponseEntity<String> downloadFile(@RequestBody Map<String, String> request) {
        String fileId = request.get("fileId");
        String outputPath = request.get("outputPath");
        
        if (fileId == null || fileId.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("File ID is required");
        }
        
        if (outputPath == null || outputPath.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Output path is required");
        }
        
        File result = zhipuaiService.downloadFile(fileId, outputPath);
        if (result != null) {
            return ResponseEntity.ok("File downloaded: " + result.getAbsolutePath());
        } else {
            return ResponseEntity.ok("Failed to download file");
        }
    }

    /**
     * 创建微调任务
     * POST /zhipuai/finetune/create
     */
    @PostMapping("/finetune/create")
    public ResponseEntity<String> createFineTuningJob(@RequestBody Map<String, String> request) {
        String model = request.get("model");
        String trainingFile = request.get("trainingFile");
        
        if (model == null || model.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Model is required");
        }
        
        if (trainingFile == null || trainingFile.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Training file is required");
        }
        
        String result = zhipuaiService.createFineTuningJob(model, trainingFile);
        return ResponseEntity.ok(result);
    }

    /**
     * 查询微调任务
     * GET /zhipuai/finetune/{jobId}
     */
    @GetMapping("/finetune/{jobId}")
    public ResponseEntity<Map<String, Object>> queryFineTuningJob(@PathVariable String jobId) {
        Map<String, Object> result = zhipuaiService.queryFineTuningJob(jobId);
        return ResponseEntity.ok(result);
    }

    /**
     * 异步聊天
     * POST /zhipuai/async
     */
    @PostMapping("/async")
    public ResponseEntity<String> chatAsync(@RequestBody Map<String, String> request) {
        String message = request.get("message");
        
        if (message == null || message.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Message is required");
        }
        
        String result = zhipuaiService.chatAsync(message);
        return ResponseEntity.ok(result);
    }

    /**
     * 带Web搜索的聊天
     * POST /zhipuai/websearch
     */
    @PostMapping("/websearch")
    public ResponseEntity<String> chatWithWebSearch(@RequestBody Map<String, String> request) {
        String message = request.get("message");
        String searchQuery = request.get("searchQuery");
        
        if (message == null || message.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Message is required");
        }
        
        if (searchQuery == null || searchQuery.trim().isEmpty()) {
            searchQuery = message; // 如果没有指定搜索查询，使用消息作为搜索查询
        }
        
        String result = zhipuaiService.chatWithWebSearch(message, searchQuery);
        return ResponseEntity.ok(result);
    }

    /**
     * 语音模型聊天
     * POST /zhipuai/voice
     */
    @PostMapping("/voice")
    public ResponseEntity<String> chatWithVoice(@RequestBody Map<String, String> request) {
        String message = request.get("message");
        
        if (message == null || message.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Message is required");
        }
        
        String result = zhipuaiService.chatWithVoice(message);
        return ResponseEntity.ok(result);
    }
} 