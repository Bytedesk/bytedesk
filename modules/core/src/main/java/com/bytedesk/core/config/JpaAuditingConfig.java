/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-07-03 12:30:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-03 12:30:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.config;

import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.util.Optional;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * JPA审计配置
 * 解决LocalDateTime到ZonedDateTime的转换问题
 */
@Configuration
@EnableJpaAuditing(dateTimeProviderRef = "dateTimeProvider")
public class JpaAuditingConfig {

    /**
     * 配置日期时间提供者，使用ZonedDateTime，指定Asia/Shanghai时区
     */
    @Bean
    public DateTimeProvider dateTimeProvider() {
        return () -> Optional.of(ZonedDateTime.now(ZoneId.of("Asia/Shanghai")));
    }

    /**
     * 配置审计者提供者（如果需要的话）
     */
    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> Optional.of("system");
    }
} 