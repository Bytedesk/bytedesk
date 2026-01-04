/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-08-23 11:37:33
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-08-23 11:37:36
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.redis.cache;

import org.springframework.cache.Cache;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.data.redis.serializer.SerializationException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RedisCacheErrorHandler implements CacheErrorHandler {

    @Override
    public void handleCacheGetError(RuntimeException exception, Cache cache, Object key) {
        // 反序列化失败通常意味着缓存里存在旧结构数据，直接 evict 让系统自愈
        log.warn("Failure getting from cache: {}, key={}, exception={}", cache.getName(), key, exception.toString());
        if (exception instanceof SerializationException
                || (exception.getMessage() != null && exception.getMessage().contains("Could not read JSON"))) {
            try {
                cache.evict(key);
                log.warn("Evicted bad cache entry: {}, key={}", cache.getName(), key);
            } catch (Exception evictEx) {
                log.warn("Failed to evict bad cache entry: {}, key={}, msg={}", cache.getName(), key, evictEx.getMessage());
            }
        }
    }

    @Override
    public void handleCachePutError(RuntimeException exception, Cache cache, Object key, Object value) {
        log.warn("Failure putting into cache: {}, key={}, exception={}", cache.getName(), key, exception.toString());
    }

    @Override
    public void handleCacheEvictError(RuntimeException exception, Cache cache, Object key) {
        log.warn("Failure evicting from cache: {}, key={}, exception={}", cache.getName(), key, exception.toString());
    }

    @Override
    public void handleCacheClearError(RuntimeException exception, Cache cache) {
        log.warn("Failure clearing cache: {}, exception={}", cache.getName(), exception.toString());
    }
}
