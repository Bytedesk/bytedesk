/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-08-22 17:41:22
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-10 16:36:15
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
    
    /**
     * 检查消息是否已存在（用于去重）
     * @param messageUid 消息UID
     * @return true if exists, false otherwise
     */
    public boolean isMessageExists(String messageUid) {
        if (messageUid == null || messageUid.trim().isEmpty()) {
            return false;
        }
        String key = RedisConsts.MESSAGE_UNREAD_PREFIX + messageUid;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
    
    /**
     * 设置消息存在标记（用于去重）
     * @param messageUid 消息UID
     * @param ttl 过期时间（秒），建议设置为24小时
     */
    public void setMessageExists(String messageUid, long ttl) {
        if (messageUid == null || messageUid.trim().isEmpty()) {
            return;
        }
        String key = RedisConsts.MESSAGE_UNREAD_PREFIX + messageUid;
        redisTemplate.opsForValue().set(key, RedisConsts.MESSAGE_UNREAD_EXISTS_VALUE);
        redisTemplate.expire(key, ttl, TimeUnit.SECONDS);
    }
    
    /**
     * 删除消息存在标记
     * @param messageUid 消息UID
     */
    public void removeMessageExists(String messageUid) {
        if (messageUid == null || messageUid.trim().isEmpty()) {
            return;
        }
        String key = RedisConsts.MESSAGE_UNREAD_PREFIX + messageUid;
        redisTemplate.delete(key);
    }
    
    /**
     * 获取消息存在标记的值
     * @param messageUid 消息UID
     * @return 消息标记值，如果不存在返回null
     */
    public String getMessageExists(String messageUid) {
        if (messageUid == null || messageUid.trim().isEmpty()) {
            return null;
        }
        String key = RedisConsts.MESSAGE_UNREAD_PREFIX + messageUid;
        return redisTemplate.opsForValue().get(key);
    }
    
}
