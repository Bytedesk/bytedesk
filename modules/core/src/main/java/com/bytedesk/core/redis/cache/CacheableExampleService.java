/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-05-28 23:15:02
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-28 23:15:02
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.redis.cache;

import java.util.concurrent.TimeUnit;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

/**
 * 缓存使用示例服务
 * 展示@Cacheable, @CachePut和@CacheEvict注解的用法
 */
@Slf4j
@Service
@CacheConfig(cacheNames = "userCache") // 指定默认缓存空间
public class CacheableExampleService {

    /**
     * 使用@Cacheable进行缓存
     * 如果缓存中有数据则直接返回，不执行方法体
     * 如果缓存中没有数据，则执行方法体并将结果缓存起来
     * 
     * @param id 用户ID
     * @return 用户信息
     */
    @Cacheable(key = "#id", unless = "#result == null")
    public String getUserInfo(String id) {
        log.info("获取用户信息，未命中缓存，执行方法体: {}", id);
        // 模拟数据库查询等耗时操作
        try {
            TimeUnit.MILLISECONDS.sleep(300);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return "User:" + id + ", time:" + System.currentTimeMillis();
    }

    /**
     * 使用@CachePut更新缓存
     * 无论如何都会执行方法体，并将执行结果更新到缓存中
     * 
     * @param id 用户ID
     * @param info 新的用户信息
     * @return 更新后的用户信息
     */
    @CachePut(key = "#id")
    public String updateUserInfo(String id, String info) {
        log.info("更新用户信息并刷新缓存: {}", id);
        // 模拟更新操作
        return info + ", updatedAt:" + System.currentTimeMillis();
    }

    /**
     * 使用@CacheEvict删除缓存
     * 
     * @param id 用户ID
     */
    @CacheEvict(key = "#id")
    public void deleteUserInfo(String id) {
        log.info("删除用户信息并清除缓存: {}", id);
        // 模拟删除操作
    }

    /**
     * 清空所有缓存
     */
    @CacheEvict(allEntries = true)
    public void clearAllUserCache() {
        log.info("清除所有用户缓存");
    }
}
