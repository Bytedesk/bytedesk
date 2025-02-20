/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-20 12:04:43
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-20 12:33:08
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.ai.springai.tool_calling.DateTimeTools;
import com.bytedesk.core.utils.JsonResult;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 工具调用
 * https://docs.spring.io/spring-ai/reference/api/tools.html
 * 大模型工具支持情况
 * https://docs.spring.io/spring-ai/reference/api/chat/comparison.html
 */
@Slf4j
@RestController
@RequestMapping("/tools")
@RequiredArgsConstructor
public class SpringAiToolsController {

    // private final ChatClient defaultChatClient;
    private final ChatClient zhipuaiChatClient;

    // ollama deepseek-r1 does not support tools: registry.ollama.ai/library/deepseek-r1:latest does not support tools
    // private final ChatClient ollamaChatClient;

    // private final ChatClient dashScopeChatClient; // not support tools

    // private final ChatClient deepSeekChatClient;

    // http://127.0.0.1:9003/tools/time?message=
    // get current date and time
    @GetMapping("/time")
    public ResponseEntity<JsonResult<?>> time(
            @RequestParam(value = "message", defaultValue = "What day is tomorrow?") String message) {

        String response = zhipuaiChatClient
                .prompt(message)
                .tools(new DateTimeTools())
                .call()
                .content();
        // FIXME: I can't provide the exact day for tomorrow without knowing your
        // current timezone. If you provide your timezone, I can calculate and tell you
        // what day tomorrow is.
        log.info("response: {}", response);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    // set alarm
    // http://127.0.0.1:9003/tools/alarm?message=
    @GetMapping("/alarm")
    public ResponseEntity<JsonResult<?>> alarm(
            @RequestParam(value = "message", defaultValue = "Can you set an alarm 10 minutes from now?") String message) {

        String response = zhipuaiChatClient
                .prompt(message)
                .tools(new DateTimeTools())
                .call()
                .content();
        // The alarm has been set for 10 minutes from now.
        log.info("response: {}", response);

        return ResponseEntity.ok(JsonResult.success(response));
    }
}
