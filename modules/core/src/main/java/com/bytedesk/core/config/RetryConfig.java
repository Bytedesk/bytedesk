/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-04-11 11:10:19
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-11 11:15:09
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.springframework.retry.annotation.EnableRetry;

/**
 * 启用Spring Retry重试机制的配置类
 */
@Configuration
@EnableRetry
@Description("Retry Configuration - Retry mechanism configuration for handling transient failures")
public class RetryConfig {
    // 仅用于启用Spring重试功能的配置类
    // 具体重试配置通过@Retryable注解在方法上实现
}
