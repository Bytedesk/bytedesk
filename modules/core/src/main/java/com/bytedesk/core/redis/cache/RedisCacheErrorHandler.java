/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-08-23 11:37:33
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-08-23 11:37:36
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.redis.cache;

import org.springframework.cache.Cache;
import org.springframework.cache.interceptor.CacheErrorHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RedisCacheErrorHandler implements CacheErrorHandler {

    @Override
    public void handleCacheGetError(RuntimeException exception, Cache cache, Object key) {
        log.info("Failure getting from cache: " + cache.getName() + ", exception: " + exception.toString());
    }

    @Override
    public void handleCachePutError(RuntimeException exception, Cache cache, Object key, Object value) {
        log.info("Failure putting into cache: " + cache.getName() + ", exception: " + exception.toString());
    }

    @Override
    public void handleCacheEvictError(RuntimeException exception, Cache cache, Object key) {
        log.info("Failure evicting from cache: " + cache.getName() + ", exception: " + exception.toString());
    }

    @Override
    public void handleCacheClearError(RuntimeException exception, Cache cache) {
        log.info("Failure clearing cache: " + cache.getName() + ", exception: " + exception.toString());
    }
}
