/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-05 11:03:50
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-11-05 11:08:12
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.push;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Service;

@Service
public class PushFilterService {
    
    // 用于存储每个IP最后发送验证码的时间的缓存
    private final ConcurrentHashMap<String, AtomicLong> ipLastSentTimeCache = new ConcurrentHashMap<>();

    // 验证码发送间隔阈值（单位：毫秒）
    private static final long VALIDATE_CODE_SEND_INTERVAL = 10 * 60 * 1000; // 10分钟

    // 检查是否可以发送验证码
    public Boolean canSendCode(String ip) {
        AtomicLong lastSentTime = ipLastSentTimeCache.getOrDefault(ip, new AtomicLong(0));
        long currentTime = System.currentTimeMillis();
        return (currentTime - lastSentTime.get()) >= VALIDATE_CODE_SEND_INTERVAL;
    }

    // 更新IP最后发送验证码的时间
    public void updateIpLastSentTime(String ip) {
        ipLastSentTimeCache.put(ip, new AtomicLong(System.currentTimeMillis()));
    }

    // 删除发送验证码的ip
    public void removeIpLastSentTime(String ip) {
        ipLastSentTimeCache.remove(ip);
    }

}
