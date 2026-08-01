/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:17:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-12 22:36:33
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.starter.runner;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;

import com.bytedesk.core.config.properties.BytedeskProperties;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.utils.LicenseValidator;
import com.bytedesk.core.utils.LicenseValidator.LicenseInfo;

import lombok.extern.slf4j.Slf4j;

/**
 * @author bytedesk.com on 2019/4/21
 */
@Slf4j
@Component
public class InitDataRunner {

    @Value("${application.version}")
    private String version;
    
    @Value("${server.port}")
    private String port;

    private final BytedeskProperties bytedeskProperties;
    private final StringRedisTemplate stringRedisTemplate;

    public InitDataRunner(BytedeskProperties bytedeskProperties,
                          StringRedisTemplate stringRedisTemplate) {
        this.bytedeskProperties = bytedeskProperties;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    /**
     * 异步验证许可证有效性（本地 RSA 验签，不依赖远程服务）。
     * 验证结果写入 Redis 缓存，供 BytedeskPropertiesController 读取。
     */
    @Async
    public void validateLicenseOnStartup() {
        String licenseKey = bytedeskProperties.getOriginalAppkey();
        String cacheKey = BytedeskConsts.LICENSE_VALID_CACHE_PREFIX + licenseKey;
        try {
            LicenseInfo info = LicenseValidator.validateOnStartup(licenseKey);
            boolean valid = info != null && info.isValid();
            stringRedisTemplate.opsForValue().set(cacheKey, String.valueOf(valid));
            log.info("License startup validation complete: valid={}", valid);
        } catch (Exception e) {
            log.error("License startup validation error: {}", e.getMessage(), e);
            stringRedisTemplate.opsForValue().set(cacheKey, "false");
        }
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady(ApplicationReadyEvent event) {
        log.info("InitDataRunner executing...");
        log.info("bytedesk v{} started, http://127.0.0.1:{}", version, port);
        log.info("ApplicationReadyEvent received. Application is fully started and ready to serve on port {}", port);

        // 异步验证许可证
        validateLicenseOnStartup();
    }

    @EventListener(ContextClosedEvent.class)
    public void onContextClosed(ContextClosedEvent event) {
        log.warn("ContextClosedEvent received. Application context is shutting down.");
    }

}
