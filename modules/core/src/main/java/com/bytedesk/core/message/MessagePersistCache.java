/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-16 11:09:19
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-04 17:14:03
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

// import com.github.benmanes.caffeine.cache.Cache;
// import com.github.benmanes.caffeine.cache.CacheLoader;
// import com.github.benmanes.caffeine.cache.Caffeine;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class MessagePersistCache {

    private static final String DEFAULT_PERSIST_KEY = RedisConsts.BYTEDESK_REDIS_PREFIX + "messageList";
    private static final long EXPIRE_TIME = 1; // 1天
    private final StringRedisTemplate stringRedisTemplate;

    // 假设我们使用"myList"作为缓存中的键
    // String defaultPersistKey = "messageList";
    // 创建一个缓存实例，设置过期时间为5天
    // private Cache<String, List<String>> messageCache;

    @PostConstruct
    public void init() {
        // 初始化caffeineCache，设置缓存的最大大小、过期时间等参数
        // messageCache = Caffeine.newBuilder()
        //     .expireAfterWrite(1, TimeUnit.DAYS)
        //     .build(new CacheLoader<String, List<String>>() {
        //         @Override
        //         public List<String> load(String key) throws Exception {
        //             // 当缓存中没有找到对应的键时，使用load方法初始化
        //             return new ArrayList<>();
        //         }
        //     });
        // 初始化 Redis 配置
    }

    // 模拟 push 操作：向列表中添加元素
    public void pushForPersist(String messageJSON) {
        // push(defaultPersistKey, messageJSON);
        push(DEFAULT_PERSIST_KEY, messageJSON);
    }

    // 模拟 pop 操作：从列表中移除元素
    public List<String> getListForPersist() {
        // return getList(defaultPersistKey);
        return getList(DEFAULT_PERSIST_KEY);
    }

    // 模拟 push 操作：向列表中添加元素
    public void push(String listKey, String messageJSON) {
        // List<String> cachedList = messageCache.getIfPresent(listKey);
        // if (cachedList == null) {
        //     // 如果缓存中没有找到对应的键，则使用load方法初始化
        //     cachedList = new ArrayList<>();
        // }
        // cachedList.add(messageJSON);
        // messageCache.put(listKey, cachedList);
        stringRedisTemplate.opsForList().rightPush(listKey, messageJSON);
        stringRedisTemplate.expire(listKey, EXPIRE_TIME, TimeUnit.DAYS);
    }

    public void pushGroup(String groupUid, String messageJSON) {
        // bytedeskEventPublisher.publishCaffeineCacheGroupEvent(groupUid, messageJSON);
    }

    public List<String> getList(String listKey) {
        // List<String> cachedList = messageCache.getIfPresent(listKey);
        // if (cachedList != null && !cachedList.isEmpty()) {
        //     // 只需要返回一次即可
        //     remove(listKey);
        //     return cachedList;
        // }
        // return null;
        Long size = stringRedisTemplate.opsForList().size(listKey);
        if (size == null || size == 0) {
            return new ArrayList<>();
        }
        List<String> messages = stringRedisTemplate.opsForList().range(listKey, 0, size - 1);
        // 从缓存中删除列表内容
        clearCache(listKey);
        return messages;
    }

    // 清空缓存
    public void clearCache(String listKey) {
        stringRedisTemplate.delete(listKey);
    }

    // public void remove(String listKey) {
    //     messageCache.invalidate(listKey);
    // }

    // public void clear() {
    //     messageCache.invalidateAll();
    // }

}
