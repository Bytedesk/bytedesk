/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-13 13:41:56
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-28 11:45:48
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai;

import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.utils.JsonResult;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

/**
 * deepseek
 * https://docs.spring.io/spring-ai/reference/api/chat/deepseek-chat.html#chat-options
 * 参考：
 * https://github.com/spring-projects/spring-ai/blob/main/models/spring-ai-openai/src/test/java/org/springframework/ai/openai/chat/proxy/DeepSeekWithOpenAiChatModelIT.java
 */
@RestController
@RequestMapping("/springai/deepseek")
@RequiredArgsConstructor
@ConditionalOnProperty(name = "spring.ai.deepseek.chat.enabled", havingValue = "true", matchIfMissing = false)
public class SpringAIDeepseekController {
    
    // deepseek chat model
    // 在配置文件中修改：
    // spring.ai.openai.base-url=https://api.deepseek.com
    // spring.ai.openai.api-key=sk-xxx
    // spring.ai.openai.chat.options.model=deepseek-chat
    // spring.ai.openai.chat.options.temperature=0.7
    // The DeepSeek API doesn't support embeddings, so we need to disable it.
    // spring.ai.openai.embedding.enabled=false
    private final OpenAiChatModel deepSeekChatModel;

    // http://127.0.0.1:9003/springai/deepseek/ai/generate?message=hello
    @GetMapping("/ai/generate")
    public ResponseEntity<JsonResult<?>> generate(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        return ResponseEntity.ok(JsonResult.success(this.deepSeekChatModel.call(message)));
    }

    // http://127.0.0.1:9003/springai/deepseek/ai/generateStream?message=hello
    @GetMapping("/ai/generateStream")
    public Flux<ChatResponse> generateStream(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        Prompt prompt = new Prompt(new UserMessage(message));
        return this.deepSeekChatModel.stream(prompt);
    }
    
}
