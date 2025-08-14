/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-16 11:09:19
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-14 23:04:33
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.message;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import com.bytedesk.core.redis.RedisConsts;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 消息持久化缓存服务
 * 使用Redis List结构存储消息，支持消息的推入和批量获取
 * 
 * @author jackning
 * @since 2024-07-16
 */
@Slf4j
@Component
@AllArgsConstructor
public class MessagePersistCache {

    private static final String DEFAULT_PERSIST_KEY = RedisConsts.BYTEDESK_REDIS_PREFIX + "messageList";
    private static final long EXPIRE_TIME_DAYS = 1;
    
    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 初始化Redis配置
     */
    @PostConstruct
    public void init() {
        log.info("MessagePersistCache initialized with Redis");
    }

    /**
     * 向默认持久化列表推送消息
     * 
     * @param messageJSON 消息JSON字符串
     */
    public void pushForPersist(String messageJSON) {
        push(DEFAULT_PERSIST_KEY, messageJSON);
    }

    /**
     * 从默认持久化列表获取所有消息
     * 
     * @return 消息列表，如果列表为空则返回空列表
     */
    public List<String> getListForPersist() {
        return getList(DEFAULT_PERSIST_KEY);
    }

    /**
     * 向指定列表推送消息
     * 
     * @param listKey 列表键名
     * @param messageJSON 消息JSON字符串
     */
    public void push(String listKey, String messageJSON) {
        try {
            stringRedisTemplate.opsForList().rightPush(listKey, messageJSON);
            stringRedisTemplate.expire(listKey, EXPIRE_TIME_DAYS, TimeUnit.DAYS);
            log.debug("Message pushed to list: {}, message: {}", listKey, messageJSON);
        } catch (Exception e) {
            log.error("Failed to push message to list: {}", listKey, e);
        }
    }

    /**
     * 获取指定列表的所有消息并清空列表
     * 
     * @param listKey 列表键名
     * @return 消息列表，如果列表为空则返回空列表
     */
    public List<String> getList(String listKey) {
        try {
            Long size = stringRedisTemplate.opsForList().size(listKey);
            if (size == null || size == 0) {
                return new ArrayList<>();
            }
            
            List<String> messages = stringRedisTemplate.opsForList().range(listKey, 0, size - 1);
            if (messages != null) {
                // 获取消息后清空缓存
                clearCache(listKey);
                log.debug("Retrieved {} messages from list: {}", messages.size(), listKey);
                return messages;
            }
            
            return new ArrayList<>();
        } catch (Exception e) {
            log.error("Failed to get messages from list: {}", listKey, e);
            return new ArrayList<>();
        }
    }

    /**
     * 清空指定列表的缓存
     * 
     * @param listKey 列表键名
     */
    public void clearCache(String listKey) {
        try {
            stringRedisTemplate.delete(listKey);
            log.debug("Cache cleared for list: {}", listKey);
        } catch (Exception e) {
            log.error("Failed to clear cache for list: {}", listKey, e);
        }
    }
}
