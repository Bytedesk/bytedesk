/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-31 09:50:56
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-05-31 11:12:19
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.ollama;

import java.util.List;
import java.util.Map;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.utils.JsonResult;

import lombok.AllArgsConstructor;
import reactor.core.publisher.Flux;

// https://docs.spring.io/spring-ai/reference/api/chat/ollama-chat.html
@RestController
@RequestMapping("/visitor/api/v1/ai/ollama")
@AllArgsConstructor
public class OllamaController {

    private final OllamaEmbeddingModel ollamaEmbeddingModel;

    private final ChatClient chatClient;

    private final OllamaChatModel chatModel;

    // public OllamaController(EmbeddingModel embeddingModel, ChatClient.Builder
    // chatClientbBuilder) {
    // this.embeddingModel = embeddingModel;
    // this.chatClient = chatClientbBuilder.build();
    // }

    // http://127.0.0.1:9003/visitor/api/v1/ai/ollama/chat?input=hello
    @GetMapping("/chat")
    public ResponseEntity<?> generation(@RequestParam("input") String input) {
        String content = this.chatClient.prompt()
                .user(input)
                .call()
                .content();
        return ResponseEntity.ok(JsonResult.success(content));
    }

    // http://127.0.0.1:9003/visitor/api/v1/ai/ollama/simple
    @GetMapping("/simple")
    public Map<String, String> completion(
            @RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        return Map.of("completion", chatClient.prompt().user(message).call().content());
    }

    // http://127.0.0.1:9003/visitor/api/v1/ai/ollama/generate
    @GetMapping("/generate")
    public Map<?, ?> generate(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        return Map.of("generation", chatModel.call(message));
    }

    // http://127.0.0.1:9003/visitor/api/v1/ai/ollama/generateStream
    @GetMapping("/generateStream")
    public Flux<ChatResponse> generateStream(
            @RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        Prompt prompt = new Prompt(new UserMessage(message));
        return chatModel.stream(prompt);
    }

    /*
     * http://127.0.0.1:9003/visitor/api/v1/ai/ollama/embedding
     */
    @GetMapping("/embedding")
    public Map<?, ?> embed(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        // EmbeddingResponse embeddingResponse =
        // embeddingModel.embedForResponse(List.of(message));
        //
        EmbeddingResponse embeddingResponse = ollamaEmbeddingModel
                .call(new EmbeddingRequest(List.of("Hello World", "World is big and salvation is near"),
                        OllamaOptions.create().withModel("qwen:7b")));

        return Map.of("embedding", embeddingResponse);
    }
}
