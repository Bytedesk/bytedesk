/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-12 12:15:53
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-18 11:08:16
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.controller;

import java.util.Optional;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.bytedesk.core.utils.JsonResult;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * spring ai rag
 * https://docs.spring.io/spring-ai/reference/api/retrieval-augmented-generation.html
 */
@Slf4j
@RestController
@RequestMapping("/spring/ai")
@RequiredArgsConstructor
@ConditionalOnProperty(name = "spring.ai.ollama.chat.enabled", havingValue = "true")
public class SpringAIController {

        @Qualifier("defaultChatClient")
        private final Optional<ChatClient> defaultChatClient;

        // 流式生成
        // @GetMapping(value = "/generateStream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	// public Flux<ChatResponse> generateStream(
	// 		@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
	// 	return chatModel.stream(new Prompt(new UserMessage(message)));
	// }

        // http://127.0.0.1:9003/spring/ai/completion?message=hello&voice=agent
        // https://docs.spring.io/spring-ai/reference/api/chatclient.html
        @GetMapping("/completion")
        ResponseEntity<JsonResult<?>> completion(
                        @RequestParam(value = "message", defaultValue = "Tell me a joke") String message,
                        String voice) {

                String completion = this.defaultChatClient.get().prompt()
                                .system(sp -> sp.param("voice", voice))
                                .user(message)
                                .call()
                                .content();

                return ResponseEntity.ok(JsonResult.success(completion));
        }

        

}
