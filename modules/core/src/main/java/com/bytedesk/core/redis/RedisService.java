/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-08-22 17:41:22
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-08-23 10:13:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.redis;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

// https://redis.io/docs/latest/develop/data-types/streams/
@Service
public class RedisService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;
  
    public void push(String key, String value, long ttl) {
        // redisTemplate.opsForValue().get(key); // Returns the associated value
        redisTemplate.opsForValue().set(key, value); // Stores the key-value pair
        redisTemplate.expire(key, ttl, TimeUnit.SECONDS); // Key is expired after specified time
    }
    
}
