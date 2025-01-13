/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-08-31 10:01:05
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-01-13 16:09:26
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.message;

import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Component;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import jakarta.annotation.PostConstruct;

@Component
public class MessageCache {

     // 创建一个caffeineCache实例
    private Cache<String, MessageProtobuf> messageCache;

    @PostConstruct
    public void init() {
        // 初始化caffeineCache，设置缓存的最大大小、过期时间等参数
        messageCache = Caffeine.newBuilder()
                .maximumSize(10000) // 设置缓存的最大条目数
                .expireAfterWrite(10, TimeUnit.MINUTES) // 设置缓存条目的过期时间
                .build();
    }

    public void put(String key, MessageProtobuf message) {
        messageCache.put(key, message);
    }

    public MessageProtobuf get(String key) {
        return messageCache.getIfPresent(key);
    }
    
}
