/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-16 11:09:19
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-15 18:18:47
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.robot_message;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.redis.RedisConsts;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@AllArgsConstructor
@Slf4j
public class RobotMessageCache {

    private static final String DEFAULT_PERSIST_KEY = RedisConsts.BYTEDESK_REDIS_PREFIX + "robotMessageList";
    private static final long EXPIRE_TIME = 1; // 1天
    private final StringRedisTemplate stringRedisTemplate;

    @PostConstruct
    public void init() {
    }

    public void pushRequest(RobotMessageRequest request) {
        push(DEFAULT_PERSIST_KEY, JSON.toJSONString(request));
    }

    // 模拟 pop 操作：从列表中移除元素
    public List<String> getListForPersist() {
        try {
            return getList(DEFAULT_PERSIST_KEY);
        } catch (Exception e) {
            log.error("Failed to get robot message list from cache: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    // 模拟 push 操作：向列表中添加元素
    private void push(String listKey, String messageJSON) {
        stringRedisTemplate.opsForList().rightPush(listKey, messageJSON);
        stringRedisTemplate.expire(listKey, EXPIRE_TIME, TimeUnit.DAYS);
    }

    private List<String> getList(String listKey) {
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

}
