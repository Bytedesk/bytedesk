/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-31 09:50:56
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-22 18:07:02
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.provider.vendors.ollama;

import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.utils.JsonResult;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

// 检测Ollama是否可用，打开网址：http://localhost:11434/
// https://docs.spring.io/spring-ai/reference/api/chat/ollama-chat.html
@Slf4j
@RestController
@RequestMapping("/ollama")
public class OllamaChatController {

    @Autowired
    private OllamaChatModel ollamaChatModel;

    // http://127.0.0.1:9003/ollama/generate?message=Tell%20me%20a%20joke
    @GetMapping("/generate")
    public ResponseEntity<?> generate(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        ChatResponse response = ollamaChatModel.call(
        new Prompt(
            message,
            OllamaOptions.builder()
                .model("qwen:0.5b")
                .temperature(0.4)
                .build()
        ));
        // return response;
        String content = response.getResult().getOutput().getContent();
        return ResponseEntity.ok(JsonResult.success(content));
    }

    // http://127.0.0.1:9003/ollama/stream?message=Tell%20me%20a%20joke
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<ChatResponse> generateStream(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        Prompt prompt = new Prompt(new UserMessage(message));
        return ollamaChatModel.stream(prompt);
    }


}
