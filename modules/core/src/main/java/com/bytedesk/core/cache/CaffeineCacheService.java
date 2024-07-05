/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-16 17:48:50
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-07-04 14:51:10
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.cache;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.message.MessageProtobuf;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
// import lombok.extern.slf4j.Slf4j;

// @Slf4j
@Service
// @AllArgsConstructor
public class CaffeineCacheService {
    // 
    // Parameter 1 of constructor in com.bytedesk.core.cache.CaffeineCacheService required a bean of type 'com.github.benmanes.caffeine.cache.Cache' that could not be found.
    // Consider defining a bean of type 'com.github.benmanes.caffeine.cache.Cache'
    // in your configuration.
    // public final BytedeskEventPublisher bytedeskEventPublisher;

    // 创建一个缓存实例，设置过期时间为5天
    Cache<String, List<String>> cache = Caffeine.newBuilder()
            .expireAfterWrite(5, TimeUnit.DAYS)
            .build(new CacheLoader<String, List<String>>() {
                @Override
                public List<String> load(String key) throws Exception {
                    // 当缓存中没有找到对应的键时，使用load方法初始化
                    return new ArrayList<>();
                }
            });

    // 假设我们使用"myList"作为缓存中的键
    String defaultPersistKey = "messageList";

    // 模拟 push 操作：向列表中添加元素
    public void pushForPersist(String messageJSON) {
        push(defaultPersistKey, messageJSON);
    }

    // 模拟 pop 操作：从列表中移除元素
    public List<String> getListForPersist() {
        return getList(defaultPersistKey);
    }

    // 
    // 模拟 push 操作：向列表中添加元素
    public void push(String listKey, String messageJSON) {
        List<String> cachedList = cache.getIfPresent(listKey);
        if (cachedList == null) {
            // 如果缓存中没有找到对应的键，则使用load方法初始化
            cachedList = new ArrayList<>();
        }
        cachedList.add(messageJSON);
        cache.put(listKey, cachedList);
    }

    public void pushGroup(String groupUid, String messageJSON) {
        // bytedeskEventPublisher.publishCaffeineCacheGroupEvent(groupUid, messageJSON);
    }

    // 模拟 pop 操作：从列表中移除元素
    // public String getFirst(String listKey) {
    //     List<String> cachedList = cache.getIfPresent(listKey);
    //     if (cachedList != null && !cachedList.isEmpty()) {
    //         String messageJSON = cachedList.remove(0);
    //         // log.info("Popped element: " + messageJSON);
    //         return messageJSON;
    //     }
    //     return null;
    // }

    public String removeMessage(String listKey, MessageProtobuf messageObject) {
        List<String> cachedList = cache.getIfPresent(listKey);
        if (cachedList != null && !cachedList.isEmpty()) {
            for (int i = 0; i < cachedList.size(); i++) {
                String element = cachedList.get(i);
                MessageProtobuf messageElement = JSON.parseObject(element, MessageProtobuf.class);
                // 回执消息内容中存储被回执消息的uid
                if (messageElement.getUid().equals(messageObject.getContent())) {
                    cachedList.remove(i);
                }
            }
            cache.put(listKey, cachedList);
        }
        return null;
    }

    public List<String> getList(String listKey) {
        List<String> cachedList = cache.getIfPresent(listKey);
        if (cachedList != null && !cachedList.isEmpty()) {
            // 只需要返回一次即可
            remove(listKey);
            return cachedList;
        }
        return null;
    }

    public void remove(String listKey) {
        cache.invalidate(listKey);
    }

    public List<String> getAllKeyList() {
        List<String> keys = new ArrayList<>();
        cache.asMap().keySet().forEach(key -> {
            if (key != defaultPersistKey) {
                keys.add(key);
            }
        });
        return keys;
    }

    public void clear() {
        cache.invalidateAll();
    }

}
