/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-10-15 10:57:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-10-15 11:01:08
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.redis.queue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisQueueService {
    
    @Autowired
    private StringRedisTemplate redisTemplate;

    // 入队操作
    public void push(String queueKey, String value) {
        redisTemplate.opsForList().rightPush(queueKey, value);
    }

    // 出队操作
    public String pop(String queueKey) {
        return (String) redisTemplate.opsForList().leftPop(queueKey);
    }

    // 显示队列排队数量
    public Long getQueueSize(String queueKey) {
        return redisTemplate.opsForList().size(queueKey);
    }

}
