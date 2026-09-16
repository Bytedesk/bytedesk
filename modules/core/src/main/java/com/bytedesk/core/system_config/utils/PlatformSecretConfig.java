/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-09-15 10:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM –
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 *  contact: 270580156@qq.com
 *  联系：270580156@qq.com
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved.
 */
package com.bytedesk.core.system_config.utils;

import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 将容器中的 Jasypt StringEncryptor（starter 提供）注入 PlatformSecretUtils 静态持有器。
 * 无实现时（如单模块测试环境）跳过，PlatformSecretUtils 以明文透传兼容运行。
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class PlatformSecretConfig {

    private final ObjectProvider<StringEncryptor> stringEncryptorProvider;

    @PostConstruct
    public void init() {
        StringEncryptor encryptor = stringEncryptorProvider.getIfAvailable();
        if (encryptor != null) {
            PlatformSecretUtils.init(encryptor);
            log.info("PlatformSecretUtils initialized with StringEncryptor: {}", encryptor.getClass().getSimpleName());
        } else {
            log.warn("No StringEncryptor bean available, provider secrets will be stored/read as plaintext");
        }
    }
}
