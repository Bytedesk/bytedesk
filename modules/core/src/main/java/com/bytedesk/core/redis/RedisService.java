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
        try {
            String key = RedisConsts.MESSAGE_UNREAD_PREFIX + messageUid;
            Boolean result = redisTemplate.delete(key);
            if (Boolean.TRUE.equals(result)) {
                // 成功删除
            } else {
                // 键不存在，这是正常的
            }
        } catch (Exception e) {
            // Redis 操作失败，记录日志但不抛出异常
            // 避免因为 Redis 问题影响主要业务逻辑
        }
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
    
    /**
     * 检查自动回复消息是否已处理（用于去重）
     * @param messageUid 消息UID
     * @return true if already processed, false otherwise
     */
    public boolean isAutoReplyProcessed(String messageUid) {
        if (messageUid == null || messageUid.trim().isEmpty()) {
            return false;
        }
        String key = RedisConsts.AUTO_REPLY_PROCESSED_PREFIX + messageUid;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
    
    /**
     * 设置自动回复消息已处理标记（用于去重）
     * @param messageUid 消息UID
     * @param ttl 过期时间（秒），建议设置为24小时
     */
    public void setAutoReplyProcessed(String messageUid, long ttl) {
        if (messageUid == null || messageUid.trim().isEmpty()) {
            return;
        }
        String key = RedisConsts.AUTO_REPLY_PROCESSED_PREFIX + messageUid;
        redisTemplate.opsForValue().set(key, RedisConsts.AUTO_REPLY_PROCESSED_VALUE);
        redisTemplate.expire(key, ttl, TimeUnit.SECONDS);
    }
    
    /**
     * 删除自动回复消息已处理标记
     * @param messageUid 消息UID
     */
    public void removeAutoReplyProcessed(String messageUid) {
        if (messageUid == null || messageUid.trim().isEmpty()) {
            return;
        }
        try {
            String key = RedisConsts.AUTO_REPLY_PROCESSED_PREFIX + messageUid;
            Boolean result = redisTemplate.delete(key);
            if (Boolean.TRUE.equals(result)) {
                // 成功删除
            } else {
                // 键不存在，这是正常的
            }
        } catch (Exception e) {
            // Redis 操作失败，记录日志但不抛出异常
            // 避免因为 Redis 问题影响主要业务逻辑
        }
    }
    
    /**
     * 获取自动回复消息已处理标记的值
     * @param messageUid 消息UID
     * @return 处理标记值，如果不存在返回null
     */
    public String getAutoReplyProcessed(String messageUid) {
        if (messageUid == null || messageUid.trim().isEmpty()) {
            return null;
        }
        String key = RedisConsts.AUTO_REPLY_PROCESSED_PREFIX + messageUid;
        return redisTemplate.opsForValue().get(key);
    }
    
}
