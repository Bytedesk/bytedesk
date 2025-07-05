/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-16 11:12:26
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-05 09:34:32
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.topic;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;

import jakarta.annotation.PostConstruct;

@Service
public class TopicCacheService {

    private String topicRequestCacheKey = "topicRequestList";

    private String clientIdCacheKey = "clientIdList";

    // 创建一个缓存实例，设置过期时间为1天
    private Cache<String, List<String>> topicRequestCache;

    @PostConstruct
    public void init() {
        // 初始化caffeineCache，设置缓存的最大大小、过期时间等参数
        topicRequestCache = Caffeine.newBuilder()
            .expireAfterWrite(1, TimeUnit.DAYS)
            .build(new CacheLoader<String, List<String>>() {
                @Override
                public List<String> load(String key) throws Exception {
                    // 当缓存中没有找到对应的键时，使用load方法初始化
                    return new ArrayList<>();
                }
            });
    }


    public void pushTopic(String topic, String userUid) {
        TopicRequest request = TopicRequest.builder()
                .topic(topic)
                .userUid(userUid)
                .build();
        pushRequest(request);
    }

    public void pushRequest(TopicRequest request) {
        // push(JSON.toJSONString(request));
        push(topicRequestCacheKey, request.toJson());
    }

    public void pushClientId(String clientId) {
        push(clientIdCacheKey, clientId);
    }

    public void removeClientId(String clientId) {
        remove(clientIdCacheKey, clientId);
    }

    // 模拟 push 操作：向列表中添加元素
    private void push(String listKey, String cacheValue) {
        List<String> cachedList = topicRequestCache.getIfPresent(listKey);
        if (cachedList == null) {
            // 如果缓存中没有找到对应的键，则使用load方法初始化
            cachedList = new ArrayList<>();
        }
        cachedList.add(cacheValue);
        topicRequestCache.put(listKey, cachedList);
    }

    // remove 操作：从列表中移除元素
    private void remove(String listKey, String cacheValue) {
        List<String> cachedList = topicRequestCache.getIfPresent(listKey);
        if (cachedList != null) {
            cachedList.remove(cacheValue);
            topicRequestCache.put(listKey, cachedList);
        }
    }

    // 模拟 pop 操作：从列表中移除元素
    public List<String> getTopicRequestList() {
        return getList(topicRequestCacheKey);
    }

    public List<String> getClientIdList() {
        return getList(clientIdCacheKey);
    }

    private List<String> getList(String listKey) {
        List<String> cachedList = topicRequestCache.getIfPresent(listKey);
        if (cachedList != null && !cachedList.isEmpty()) {
            // 只需要返回一次即可
            remove(listKey);
            return cachedList;
        }
        return null;
    }

    public void remove() {
        remove(topicRequestCacheKey);
    }

    private void remove(String listKey) {
        topicRequestCache.invalidate(listKey);
    }

    public void clear() {
        topicRequestCache.invalidateAll();
    }

}
