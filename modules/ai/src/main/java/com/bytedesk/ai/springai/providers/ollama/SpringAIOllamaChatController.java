 
/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-31 09:50:56
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-21 14:01:57
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.providers.ollama;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.converter.StructuredOutputConverter;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaChatOptions;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.bytedesk.ai.utils.output.ActorsFilms;
import com.bytedesk.core.config.properties.BytedeskProperties;
import com.bytedesk.core.utils.JsonResult;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

/**
 * Ollama接口
 */
@Slf4j
@RestController
@RequestMapping("/ollama")
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "spring.ai.ollama.chat", name = "enabled", havingValue = "true", matchIfMissing = false)
public class SpringAIOllamaChatController {

    private final BytedeskProperties bytedeskProperties;
    private final SpringAIOllamaChatService springAIOllamaService;

    private final ExecutorService executorService = Executors.newCachedThreadPool();

    // http://127.0.0.1:9003/ollama/format?actor=&baseUrl=http://127.0.0.1:11434&model=llama3
    // https://docs.spring.io/spring-ai/reference/api/structured-output-converter.html
    @GetMapping("/format")
    public ResponseEntity<JsonResult<?>> generate(
            @RequestParam(value = "actor", defaultValue = "Jeff Bridges") String actor,
            OllamaRequest request) {

        if (!bytedeskProperties.getDebug()) {
            return ResponseEntity.ok(JsonResult.error("Service is not available"));
        }

        var outputParser = new BeanOutputConverter<>(ActorsFilms.class);
        String format = outputParser.getFormat();
        log.info("format: {}", format);
        String userMessage = """
                Generate the filmography for the actor {actor}.
                {format}
                /no_think
                """;

        PromptTemplate promptTemplate = PromptTemplate.builder()
                .template(userMessage)
                .variables(Map.of("actor", actor, "format", format))
                .build();
        Prompt prompt = new Prompt(promptTemplate.createMessage());

        OllamaApi ollamaApi = OllamaApi.builder().baseUrl(request.getBaseUrl()).build();
        OllamaChatOptions options = OllamaChatOptions.builder().model(request.getModel()).build();
        OllamaChatModel model = OllamaChatModel.builder().ollamaApi(ollamaApi).defaultOptions(options).build();
        if (model == null) {
            return ResponseEntity.ok(JsonResult.error("Ollama service is not available"));
        }
        // 
        ChatResponse response = model.call(prompt);
        String content = response.getResult().getOutput().getText();
        ActorsFilms actorsFilms = outputParser.convert(content);
        log.info("actorsFilms: {}", actorsFilms);

        return ResponseEntity.ok(JsonResult.success(actorsFilms));
    }

    // structured output
    // http://127.0.0.1:9003/ollama/structured?message=&baseUrl=http://127.0.0.1:11434&model=llama3
    // https://docs.spring.io/spring-ai/reference/api/structured-output-converter.html
    @GetMapping("/structured")
    public ResponseEntity<JsonResult<?>> structured(
            @RequestParam(value = "message", defaultValue = "Tell me about the actor Jeff Bridges") String message,
            OllamaRequest request) {

        if (!bytedeskProperties.getDebug()) {
            return ResponseEntity.ok(JsonResult.error("Service is not available"));
        }

        StructuredOutputConverter<ActorsFilms> outputConverter = new BeanOutputConverter<>(ActorsFilms.class);
        String userInputTemplate = """
                Your response should be in JSON format.
                The data structure for the JSON should match this Java class: java.util.HashMap
                Do not include any explanations, only provide a RFC8259 compliant JSON response following this format without deviation.
                {message}
                {format}
                /no_think
                """;
        log.info("userInputTemplate: {}", userInputTemplate);

        Prompt prompt = new Prompt(
                PromptTemplate.builder()
                        .template(userInputTemplate)
                        .variables(Map.of("message", message, "format", outputConverter.getFormat()))
                        .build()
                        .createMessage());

        OllamaApi ollamaApi = OllamaApi.builder().baseUrl(request.getBaseUrl()).build();
        OllamaChatOptions options = OllamaChatOptions.builder().model(request.getModel()).build();
        OllamaChatModel model = OllamaChatModel.builder().ollamaApi(ollamaApi).defaultOptions(options).build();
        if (model == null) {
            return ResponseEntity.ok(JsonResult.error("Ollama service is not available"));
        }

        ChatResponse response = model.call(prompt);
        log.info("response: {}", response);
        return ResponseEntity.ok(JsonResult.success(response));
    }

    /**
     * 方式1：同步调用
     * http://127.0.0.1:9003/api/v1/ollama/chat/sync?message=hello&amp;amp;amp;apiUrl=&amp;model=llama3
     */
    @GetMapping("/chat/sync")
    public ResponseEntity<JsonResult<?>> chatSync(OllamaRequest request) {
        if (!bytedeskProperties.getDebug()) {
            return ResponseEntity.ok(JsonResult.error("Service is not available"));
        }
        // 
        OllamaApi ollamaApi = OllamaApi.builder().baseUrl(request.getBaseUrl()).build();
        OllamaChatOptions options = OllamaChatOptions.builder().model(request.getModel()).build();
        OllamaChatModel model = OllamaChatModel.builder().ollamaApi(ollamaApi).defaultOptions(options).build();
        if (model == null) {
            return ResponseEntity.ok(JsonResult.error("Ollama service is not available"));
        }
        var response = model.call(request.getMessage());
        if (response == null) {
            return ResponseEntity.ok(JsonResult.error("Ollama service is not available"));
        }
        return ResponseEntity.ok(JsonResult.success(response));
    }

    /**
     * 方式2：异步流式调用
     * http://127.0.0.1:9003/api/v1/ollama/chat/stream?message=hello&amp;amp;amp;apiUrl=&amp;model=llama3
     */
    @GetMapping("/chat/stream")
    public Flux<ChatResponse> chatStream(OllamaRequest request) {

        if (!bytedeskProperties.getDebug()) {
            return Flux.empty();
        }

        Prompt prompt = new Prompt(new UserMessage(request.getMessage()));
        // 
        OllamaApi ollamaApi = OllamaApi.builder().baseUrl(request.getBaseUrl()).build();
        OllamaChatOptions options = OllamaChatOptions.builder().model(request.getModel()).build();
        OllamaChatModel model = OllamaChatModel.builder().ollamaApi(ollamaApi).defaultOptions(options).build();
        if (model != null) {
            return model.stream(prompt);
        } else {
            return Flux.empty();
        }
    }

    /**
     * 方式3：SSE调用
     * http://127.0.0.1:9003/api/v1/ollama/chat/sse?message=hello&amp;amp;amp;apiUrl=&amp;model=llama3
     */
    @GetMapping(value = "/chat/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter chatSSE(OllamaRequest request) {

        SseEmitter emitter = new SseEmitter(180_000L); // 3分钟超时

        if (!bytedeskProperties.getDebug()) {
            try {
                emitter.send(SseEmitter.event().name("error").data("Service is not available"));
            } catch (Exception e) {
                log.debug("Unable to send error event", e);
            }
            emitter.complete();
            return emitter;
        }

        executorService.execute(() -> {
            try {
                // springAIOllamaService.processPromptSSE(message, emitter);
            } catch (Exception e) {
                log.error("Error processing SSE request", e);
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
     * 自定义模型参数的调用示例
     * http://127.0.0.1:9003/api/v1/ollama/chat/custom?message=hello&amp;amp;amp;apiUrl=&amp;model=llama3
     */
    @GetMapping("/chat/custom")
    public ResponseEntity<JsonResult<?>> chatCustom(OllamaRequest request) {

        if (!bytedeskProperties.getDebug()) {
            return ResponseEntity.ok(JsonResult.error("Service is not available"));
        }

        OllamaApi ollamaApi = OllamaApi.builder().baseUrl(request.getBaseUrl()).build();
        OllamaChatOptions options = OllamaChatOptions.builder().model(request.getModel()).build();
        OllamaChatModel model = OllamaChatModel.builder().ollamaApi(ollamaApi).defaultOptions(options).build();
        if (model == null) {
            return ResponseEntity.ok(JsonResult.error("Ollama service is not available"));
        }

        try {
            ChatResponse response = model.call(new Prompt(request.getMessage()));

            String result = response.getResult().getOutput().getText();
            return ResponseEntity.ok(JsonResult.success(result));
        } catch (Exception e) {
            return ResponseEntity.ok(JsonResult.error(e.getMessage()));
        }
    }

    // 检查嵌入模型是否已经存在
    // http://127.0.0.1:9003/api/v1/ollama4j/embedding-model/exists
    @GetMapping("/embedding-model/exists")
    public ResponseEntity<?> isEmbeddingModelExists(OllamaRequest request) {
        if (!bytedeskProperties.getDebug()) {
            return ResponseEntity.ok(JsonResult.error("Service is not available"));
        }
        boolean exists = springAIOllamaService.isModelExists(request);
        log.info("Embedding model exists: {}, {}", request.getModel(), exists);
        // 
        return ResponseEntity.ok(JsonResult.success(exists));
    }

    // 在 Bean 销毁时关闭线程池
    public void destroy() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}
