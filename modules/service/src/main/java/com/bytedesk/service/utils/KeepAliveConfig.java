/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-04-24 12:28:19
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-24 12:34:33
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
// 添加到现有配置类或创建新的配置类

package com.bytedesk.service.utils;

import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class KeepAliveConfig {
    
    @PreDestroy
    public void onShutdown() {
        log.info("应用程序关闭，清理资源...");
        KeepAliveHelper.shutdown();
    }
}
