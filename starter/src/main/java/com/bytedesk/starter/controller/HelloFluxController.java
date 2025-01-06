/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-12 10:36:12
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-07 10:43:27
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.starter.controller;

import java.time.Duration;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/flux")
public class HelloFluxController {

    // router中优先运行
    // http://localhost:9003/flux/
    @GetMapping({"", "/"})
    public String index() {
        return "Hello Bytedesk AI Flux";
    }

    // http://localhost:9003/flux/chat
    @Operation(
            summary = "流式获取回答",
            description = "以流的形式返回",
            responses = {
                @ApiResponse(responseCode = "200", description = "Get Stream Chat")
            })
    @GetMapping(value = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> getStreamAnswer() {
        // 创建一个包含流数据的Flux
        return Flux.interval(Duration.ofMillis(100))
                .map(sequence -> "Data " + sequence)
                .take(50)
                .log();
    }

    
}