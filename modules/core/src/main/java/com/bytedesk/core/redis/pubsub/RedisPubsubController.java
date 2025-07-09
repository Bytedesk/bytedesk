/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-15 17:14:16
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-08-23 21:09:41
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.redis.pubsub;

import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.utils.JsonResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

/**
 * Spring Data Redis - Pub/Sub Messaging
 * https://docs.spring.io/spring-data/redis/reference/redis/pubsub.html
 * https://redis.io/docs/latest/develop/interact/pubsub/
 */
@Slf4j
@RestController
@RequestMapping("/redis/pubsub")
@Tag(name = "Redis PubSub Management", description = "Redis publish/subscribe messaging APIs")
public class RedisPubsubController {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    // @Autowired
    // private RedisTemplate<String, Object> redisTemplate;

    /**
     * http://127.0.0.1:9003/redis/pubsub/send?message=hello
     * 
     * @param message
     * @return
     */
    @Operation(summary = "Publish Text Message", description = "Publish a text message to Redis pub/sub channel")
    @GetMapping("/send")
    public JsonResult<String> publishText(@RequestParam String message) {
        log.info("redisPubsub redisMessageSend: {}", message);
        stringRedisTemplate.convertAndSend(
                RedisPubsubConst.BYTEDESK_PUBSUB_CHANNEL_STRING,
                message);
        //
        return new JsonResult<>("send string", 200, message);
    }

    /**
     * http://127.0.0.1:9003/redis/pubsub/send/object?message=hello
     * 
     * @param message
     * @return
     */
    // @GetMapping("/send/object")
    // public JsonResult<String> publishObject(@RequestParam String message) {
    //     log.info("redisPubsub send: {}", message);
    //     //
    //     RedisPubsubMessage messageDto = RedisPubsubMessage.builder()
    //             .type("text")
    //             .fileUrl("pubsub").fileUid("").kbUid("")
    //             .build();
    //     redisTemplate.convertAndSend(RedisPubsubConst.BYTEDESK_PUBSUB_CHANNEL_OBJECT, messageDto);

    //     return new JsonResult<>("send object", 200, message);
    // }

}
