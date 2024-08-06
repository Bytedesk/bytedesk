/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-16 11:12:26
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-08-05 11:31:56
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
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

import com.alibaba.fastjson2.JSON;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;

@Service
public class TopicCacheService {

    // 创建一个缓存实例，设置过期时间为5天
    Cache<String, List<String>> topicCache = Caffeine.newBuilder()
            .expireAfterWrite(5, TimeUnit.DAYS)
            .build(new CacheLoader<String, List<String>>() {
                @Override
                public List<String> load(String key) throws Exception {
                    // 当缓存中没有找到对应的键时，使用load方法初始化
                    return new ArrayList<>();
                }
            });

    // 假设我们使用"myList"作为缓存中的键
    String defaultCacheKey = "topicList";

    // 模拟 push 操作：向列表中添加元素
    public void push(String messageJSON) {
        push(defaultCacheKey, messageJSON);
    }

    public void pushRequest(TopicRequest request) {
        push(JSON.toJSONString(request));
    }

    // 模拟 push 操作：向列表中添加元素
    public void push(String listKey, String messageJSON) {
        List<String> cachedList = topicCache.getIfPresent(listKey);
        if (cachedList == null) {
            // 如果缓存中没有找到对应的键，则使用load方法初始化
            cachedList = new ArrayList<>();
        }
        cachedList.add(messageJSON);
        topicCache.put(listKey, cachedList);
    }

    // 模拟 pop 操作：从列表中移除元素
    public List<String> getList() {
        return getList(defaultCacheKey);
    }

    public List<String> getList(String listKey) {
        List<String> cachedList = topicCache.getIfPresent(listKey);
        if (cachedList != null && !cachedList.isEmpty()) {
            // 只需要返回一次即可
            remove(listKey);
            return cachedList;
        }
        return null;
    }

    public void remove() {
        remove(defaultCacheKey);
    }

    public void remove(String listKey) {
        topicCache.invalidate(listKey);
    }

    public void clear() {
        topicCache.invalidateAll();
    }

}
