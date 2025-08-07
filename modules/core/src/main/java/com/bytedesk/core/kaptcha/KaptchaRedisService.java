/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-16 17:48:50
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-07 14:57:19
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.kaptcha;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import com.bytedesk.core.config.properties.BytedeskProperties;
import com.bytedesk.core.enums.ChannelEnum;
import com.bytedesk.core.redis.RedisConsts;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class KaptchaRedisService {
    
    // 验证码5分钟过期
    private static final long EXPIRE_TIME = 5; // 5分钟
    
    private final StringRedisTemplate stringRedisTemplate;

    private final BytedeskProperties bytedeskProperties;

    public void putKaptcha(String key, String value) {
        stringRedisTemplate.opsForValue().set(RedisConsts.BYTEDESK_REDIS_PREFIX + key, value, EXPIRE_TIME, TimeUnit.MINUTES);
    }

    public Boolean hasKaptcha(String key) {
        return stringRedisTemplate.hasKey(RedisConsts.BYTEDESK_REDIS_PREFIX + key);
    }

    public String getKaptcha(String key) {
        return stringRedisTemplate.opsForValue().get(RedisConsts.BYTEDESK_REDIS_PREFIX + key);
    }

    public Boolean checkKaptcha(String key, String value, @NonNull String channel) {
        // flutter手机端验证码暂时不做校验
        if (ChannelEnum.FLUTTER.name().equalsIgnoreCase(channel)) {
            return true;
        }
        // 如果禁用验证码，则直接返回true
        if (bytedeskProperties.isDisableCaptcha()) {
            return true;
        }
        // log.info("checkKaptcha key: " + key + ", value: " + value);
        String cachedValue = stringRedisTemplate.opsForValue().get(RedisConsts.BYTEDESK_REDIS_PREFIX + key);
        return cachedValue != null && cachedValue.equals(value);
    }

    public void removeKaptcha(String key) {
        stringRedisTemplate.delete(RedisConsts.BYTEDESK_REDIS_PREFIX + key);
    }

}
