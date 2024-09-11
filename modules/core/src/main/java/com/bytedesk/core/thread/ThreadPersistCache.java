/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-08-31 10:43:14
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-08-31 11:03:26
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.thread;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Component;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;

import jakarta.annotation.PostConstruct;

@Component
public class ThreadPersistCache {
    
    // 假设我们使用"myList"作为缓存中的键
    String defaultPersistKey = "threadList";

    // 创建一个缓存实例，设置过期时间为5天
    private Cache<String, List<Thread>> threadCache;

    @PostConstruct
    public void init() {
        // 初始化caffeinecache，设置缓存的最大大小、过期时间等参数
        threadCache = Caffeine.newBuilder()
            .expireAfterWrite(1, TimeUnit.DAYS)
            .build(new CacheLoader<String, List<Thread>>() {
                @Override
                public List<Thread> load(String key) throws Exception {
                    // 当缓存中没有找到对应的键时，使用load方法初始化
                    return new ArrayList<>();
                }
            });
    }

    // 模拟 push 操作：向列表中添加元素
    public void pushForPersist(Thread thread) {
        // 通过thread.uid判断defaultPersistKey中是否已经存在 则替换掉，不存在，则插入
        String uid = thread.getUid();
        List<Thread> cachedList = threadCache.getIfPresent(defaultPersistKey);
        if (cachedList == null) {
            cachedList = new ArrayList<>();
        }

        boolean found = false;
        for (int i = 0; i < cachedList.size(); i++) {
            if (cachedList.get(i).getUid().equals(uid)) {
                found = true;
                cachedList.set(i, thread); // 替换已存在的Thread对象
                break;
            }
        }

        if (!found) {
            cachedList.add(thread);
        }
        threadCache.put(defaultPersistKey, cachedList);
        // push(defaultPersistKey, thread);
    }

    // 模拟 pop 操作：从列表中移除元素
    public List<Thread> getListForPersist() {
        return getList(defaultPersistKey);
    }

    // 模拟 push 操作：向列表中添加元素
    public void push(String listKey, Thread thread) {
        List<Thread> cachedList = threadCache.getIfPresent(listKey);
        if (cachedList == null) {
            // 如果缓存中没有找到对应的键，则使用load方法初始化
            cachedList = new ArrayList<>();
        }
        cachedList.add(thread);
        threadCache.put(listKey, cachedList);
    }

    public List<Thread> getList(String listKey) {
        List<Thread> cachedList = threadCache.getIfPresent(listKey);
        if (cachedList != null && !cachedList.isEmpty()) {
            // 只需要返回一次即可
            remove(listKey);
            return cachedList;
        }
        return null;
    }

    public void remove(String listKey) {
        threadCache.invalidate(listKey);
    }

    public void clear() {
        threadCache.invalidateAll();
    }

}
