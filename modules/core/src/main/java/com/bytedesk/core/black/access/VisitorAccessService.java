/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-15 14:08:50
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-01-15 14:14:26
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.black.access;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class VisitorAccessService {

    private static final String BLOCKED_VISITOR_KEY = "bytedesk:blocked:visitor:";

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public boolean isAllowed(String visitorId) {
        return !Boolean.TRUE.equals(redisTemplate.hasKey(BLOCKED_VISITOR_KEY + visitorId));
    }

    public void blockVisitor(String visitorId) {
        redisTemplate.opsForValue().set(BLOCKED_VISITOR_KEY + visitorId, "blocked");
    }

    public void unblockVisitor(String visitorId) {
        redisTemplate.delete(BLOCKED_VISITOR_KEY + visitorId);
    }
} 