/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-11 13:45:49
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-11-12 14:25:02
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.provider.vendors.ollama;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.bytedesk.core.utils.JsonResult;
import com.bytedesk.core.utils.JsonResultCodeEnum;

import io.github.ollama4j.OllamaAPI;
import io.github.ollama4j.exceptions.OllamaBaseException;
import io.github.ollama4j.models.chat.OllamaChatMessageRole;
import io.github.ollama4j.models.chat.OllamaChatRequest;
import io.github.ollama4j.models.chat.OllamaChatRequestBuilder;
import io.github.ollama4j.models.chat.OllamaChatResult;
import io.github.ollama4j.models.generate.OllamaStreamHandler;
import io.github.ollama4j.models.response.OllamaAsyncResultStreamer;
import io.github.ollama4j.models.response.OllamaResult;
import io.github.ollama4j.utils.OptionsBuilder;
import lombok.extern.slf4j.Slf4j;

// https://ollama4j.github.io/ollama4j/apis-generate/generate/
@Slf4j
@RestController
@RequestMapping("/ollama4j/chat")
public class Ollama4jChatController {
    
    @Autowired
    @Qualifier("ollama4jApi")
    private OllamaAPI ollama4jApi;

    @Value("${spring.ai.ollama.chat.options.model}")
    private String ollamaDefaultModel;
    
    // 同步接口
    // http://127.0.0.1:9003/ollama4j/chat/sync?message=Tell%20me%20a%20j
    @RequestMapping("/sync")
    public ResponseEntity<?> getSyncAnswer(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        // 
        OllamaResult result;
        try {
            result = ollama4jApi.generate(ollamaDefaultModel, message, false, new OptionsBuilder().build());

            return ResponseEntity.ok(JsonResult.success(result.getResponse()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok(JsonResult.error());
    }

    // 同步接口
    // http://127.0.0.1:9003/ollama4j/chat/stream?message=Tell%20me%20a%2joke
    @RequestMapping(value = "/stream")
    public ResponseEntity<?> getSyncAnswerStream(
        @RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
            // define a stream handler (Consumer<String>)
            OllamaStreamHandler streamHandler = (content) -> {
                log.info("streamHandler: {}", content);
            };
            // 
            try {
                // Should be called using separate thread to gain non blocking streaming effect.
                OllamaResult result = ollama4jApi.generate(ollamaDefaultModel,
                    message, 
                    false,
                    new OptionsBuilder().build(), 
                    streamHandler);
                log.info("getSyncAnswerStream result: {}", result);
                return ResponseEntity.ok(JsonResult.success(result.getResponse()));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return ResponseEntity.ok(JsonResult.error());
    }

    // 使用SSE返回流结果的接口
    // http://127.0.0.1:9003/ollama4j/chat/stream-sse?message=Tell%20me%20a%20joke
    @RequestMapping(value = "/stream-sse")
    public SseEmitter getStreamAnswerSse(
        @RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        
        // 创建一个SseEmitter，设置超时时间（例如30分钟）
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        
        // 定义一个stream handler来发送SSE事件
        OllamaStreamHandler streamHandler = (content) -> {
            try {
                log.info("streamHandler: {}", content);
                // 使用SseEmitter发送内容
                emitter.send(SseEmitter.event().data(JsonResult.success(
                                        JsonResultCodeEnum.ROBOT_ANSWER_CONTINUE.getName(),
                                        JsonResultCodeEnum.ROBOT_ANSWER_CONTINUE.getValue(), content)));
                
            } catch (Exception e) {
                // 处理发送事件时的异常，例如客户端断开连接
                emitter.completeWithError(e);
            }
        };
        
        // 在新线程中调用generate方法以避免阻塞
        new Thread(() -> {
            try {
                // 发送开始事件
                emitter.send(SseEmitter.event().data(JsonResult.success(
                                        JsonResultCodeEnum.ROBOT_ANSWER_START.getName(),
                                        JsonResultCodeEnum.ROBOT_ANSWER_START.getValue(), 
                                        JsonResultCodeEnum.ROBOT_ANSWER_START.getName())));
                ollama4jApi.generate(ollamaDefaultModel, message, false, new OptionsBuilder().build(), streamHandler);
                // 发送完成事件
                emitter.send(SseEmitter.event().data(JsonResult.success(
                                JsonResultCodeEnum.ROBOT_ANSWER_END.getName(),
                                JsonResultCodeEnum.ROBOT_ANSWER_END.getValue(), 
                                JsonResultCodeEnum.ROBOT_ANSWER_END.getName())));
                // 完成SseEmitter
                emitter.complete();
            } catch (Exception e) {
                // 处理异常，并完成SseEmitter
                emitter.completeWithError(e);
            }
        }).start();
        
        // 返回SseEmitter以开始发送事件
        return emitter;
    }

    // 异步接口
    // http://127.0.0.1:9003/ollama4j/chat/async?message=Tell%20me%20a%2joke
    @GetMapping("/async")
    public ResponseEntity<?> getAsyncAnswer(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) throws InterruptedException {
        
        String prompt = message;
        OllamaAsyncResultStreamer streamer = ollama4jApi.generateAsync(ollamaDefaultModel, prompt, false);
        // Set the poll interval according to your needs. 
        // Smaller the poll interval, more frequently you receive the tokens.
        int pollIntervalMilliseconds = 1000;
        while (true) {
            String tokens = streamer.getStream().poll();
            log.info("getAsyncAnswer tokens {}", tokens);
            if (!streamer.isAlive()) {
                break;
            }
            Thread.sleep(pollIntervalMilliseconds);
        }
        log.info("Complete Response {}",streamer.getCompleteResponse());

        return ResponseEntity.ok(JsonResult.success(streamer.getCompleteResponse()));
    }
    

    // 添加-聊天上下文
    // https://ollama4j.github.io/ollama4j/apis-generate/chat
    // http://127.0.0.1:9003/ollama4j/chat/context?message=Tell%20me%20a%2joke
    @GetMapping("/context")
    public ResponseEntity<?> getChatWithContext(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) 
        throws OllamaBaseException, IOException, InterruptedException {
        OllamaChatRequestBuilder builder = OllamaChatRequestBuilder.getInstance(ollamaDefaultModel);
        // create first user question
        OllamaChatRequest requestModel = builder.withMessage(OllamaChatMessageRole.USER, "What is the capital of France?")
                .build();
        // start conversation with model
        OllamaChatResult chatResult = ollama4jApi.chat(requestModel);
        System.out.println("First answer: " + chatResult.getResponse());

        // create next userQuestion
        requestModel = builder.withMessages(chatResult.getChatHistory()).withMessage(OllamaChatMessageRole.USER, "And what is the second largest city?").build();
        // "continue" conversation with model
        chatResult = ollama4jApi.chat(requestModel);
        System.out.println("Second answer: " + chatResult.getResponse());
        System.out.println("Chat History: " + chatResult.getChatHistory());

        return ResponseEntity.ok(JsonResult.success(chatResult.getResponse()));
    }

}
