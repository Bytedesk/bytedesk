/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-16 17:48:50
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-08-12 16:04:35
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.kaptcha;

import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
// import lombok.extern.slf4j.Slf4j;

/**
 * TODO: 用Redis替代
 */
// @Slf4j
@Service
// @AllArgsConstructor
public class KaptchaCacheService {
    
    // 验证码5分钟过期
    Cache<String, String> kaptchaCache = Caffeine.newBuilder().expireAfterWrite(5, TimeUnit.MINUTES).build();

    public void putKaptcha(String key, String value) {
        kaptchaCache.put(key, value);
    }

    public Boolean hasKaptcha(String key) {
        return kaptchaCache.getIfPresent(key) != null;
    }

    public String getKaptcha(String key) {
        return kaptchaCache.getIfPresent(key);
    }

    public Boolean checkKaptcha(String key, String value, String client) {
        // flutter手机端验证码暂时不做校验, TODO: 后续需要优化
        if (client != null && client.contains("flutter")) {
            return true;
        }

        String cachedValue = kaptchaCache.getIfPresent(key);
        return cachedValue != null && cachedValue.equals(value);
    }

    public void removeKaptcha(String key) {
        kaptchaCache.invalidate(key);
    }

}
