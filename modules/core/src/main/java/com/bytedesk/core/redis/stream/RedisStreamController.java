/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-08-23 10:21:21
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-08-23 10:51:40
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.redis.stream;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.redis.RedisEvent;
import com.bytedesk.core.utils.JsonResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/redis/stream/")
@AllArgsConstructor
@Tag(name = "Redis Stream Management", description = "Redis stream management APIs for event streaming")
public class RedisStreamController {

    private final RedisStreamService redisStreamProducer;

    // http://127.0.0.1:9003/redis/stream/test?content=123&type=text
    @Operation(summary = "Test Redis Stream", description = "Test Redis stream functionality with custom content and type")
    @GetMapping("/test")
    public ResponseEntity<?> test(
            @RequestParam(required = false, defaultValue = "text") String type,
            @RequestParam(required = false, defaultValue = "123") String content) {

        RedisEvent redisEvent = RedisEvent.builder()
                .type(type)
                .content(content)
                .build();
        redisStreamProducer.produce(redisEvent);

        return ResponseEntity.ok(JsonResult.success("test"));
    }

}
