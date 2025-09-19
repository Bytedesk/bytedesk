/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-05 11:03:50
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-19 10:06:46
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.push;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.bytedesk.core.constant.RedisConsts;

import java.util.concurrent.TimeUnit;

@Service
public class PushFilterService {
    

    // 验证码发送间隔阈值（单位：秒）
    private static final long VALIDATE_CODE_SEND_INTERVAL_SECONDS = 10 * 60; // 10分钟

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    // 检查是否可以发送验证码
    public Boolean canSendCode(String ip) {
        String key = RedisConsts.PUSH_CODE_IP_PREFIX + ip;
        Boolean exists = stringRedisTemplate.hasKey(key);
        return exists == null || !exists;
    }

    // 更新IP最后发送验证码的时间
    public void updateIpLastSentTime(String ip) {
        String key = RedisConsts.PUSH_CODE_IP_PREFIX + ip;
        stringRedisTemplate.opsForValue().set(key, "1", VALIDATE_CODE_SEND_INTERVAL_SECONDS, TimeUnit.SECONDS);
    }


    // 删除发送验证码的ip
    public void removeIpLastSentTime(String ip) {
        String key = RedisConsts.PUSH_CODE_IP_PREFIX + ip;
        stringRedisTemplate.delete(key);
    }

}
