/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-12 12:15:53
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-13 10:12:23
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
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.utils.JsonResult;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/spring/ai")
@RequiredArgsConstructor
public class SpringAIController {

    private final ChatClient chatClient;

    private final VectorStore vectorStore;

    private final ChatModel chatModel;

    // http://127.0.0.1:9003/spring/ai/completion?message=hello&voice=agent
    // https://docs.spring.io/spring-ai/reference/api/chatclient.html
    @GetMapping("/completion")
    ResponseEntity<JsonResult<?>> completion(
            @RequestParam(value = "message", defaultValue = "Tell me a joke") String message, String voice) {

        String completion = this.chatClient.prompt()
                .system(sp -> sp.param("voice", voice))
                .user(message)
                .call()
                .content();
        
        return ResponseEntity.ok(JsonResult.success(completion));
    }

    // rag 
    // http://127.0.0.1:9003/spring/ai/rag?message=什么时间考试？
    @GetMapping("/rag")
    ResponseEntity<JsonResult<?>> rag(
            @RequestParam(value = "message", defaultValue = "什么时间考试？") String message) {

        ChatResponse response = ChatClient.builder(chatModel)
                .build().prompt()
                .advisors(new QuestionAnswerAdvisor(vectorStore))
                .user(message)
                .call()
                .chatResponse();
        return ResponseEntity.ok(JsonResult.success(response));
    }

}
