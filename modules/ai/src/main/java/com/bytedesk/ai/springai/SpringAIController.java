/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-12 12:15:53
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-17 13:43:28
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
// import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.utils.JsonResult;

import lombok.RequiredArgsConstructor;

/**
 * spring ai rag
 * https://docs.spring.io/spring-ai/reference/api/retrieval-augmented-generation.html
 */
@RestController
@RequestMapping("/spring/ai")
@RequiredArgsConstructor
public class SpringAiController {

//     @Qualifier("defaultChatClient")
    private final ChatClient defaultChatClient;

    private final VectorStore vectorStore;

    private final ChatModel chatModel;

    // http://127.0.0.1:9003/spring/ai/completion?message=hello&voice=agent
    // https://docs.spring.io/spring-ai/reference/api/chatclient.html
    @GetMapping("/completion")
    ResponseEntity<JsonResult<?>> completion(
            @RequestParam(value = "message", defaultValue = "Tell me a joke") String message, String voice) {

        String completion = this.defaultChatClient.prompt()
                .system(sp -> sp.param("voice", voice))
                .user(message)
                .call()
                .content();

        return ResponseEntity.ok(JsonResult.success(completion));
    }

    // rag
    // https://docs.spring.io/spring-ai/reference/api/retrieval-augmented-generation.html#_questionansweradvisor
    // http://127.0.0.1:9003/spring/ai/rag?message=什么时间考试？
    @GetMapping("/rag")
    ResponseEntity<JsonResult<?>> rag(
            @RequestParam(value = "message", defaultValue = "什么时间考试？") String message) {
        // 创建qaAdvisor
        var qaAdvisor = new QuestionAnswerAdvisor(
                this.vectorStore,
                SearchRequest.builder()
                        .similarityThreshold(0.8d)
                        .topK(6)
                        .build());
        // 使用chatClient
        ChatResponse response = ChatClient.builder(chatModel)
                .build()
                .prompt()
                .advisors(qaAdvisor)
                .user(message)
                .call()
                .chatResponse();
        return ResponseEntity.ok(JsonResult.success(response));
    }

    // filter
    // http://127.0.0.1:9003/spring/ai/filter?message=什么时间考试？
    @GetMapping("/filter")
    ResponseEntity<JsonResult<?>> filter(
            @RequestParam(value = "message", defaultValue = "什么时间考试？") String message) {

        ChatClient chatClient = ChatClient.builder(chatModel)
                .defaultAdvisors(new QuestionAnswerAdvisor(vectorStore, SearchRequest.builder().build()))
                .build();

        // Update filter expression at runtime
        String content = chatClient.prompt()
                .user(message)
                .advisors(a -> a.param(QuestionAnswerAdvisor.FILTER_EXPRESSION, "type == 'Spring'"))
                .call()
                .content();

        return ResponseEntity.ok(JsonResult.success(content));
    }


}
