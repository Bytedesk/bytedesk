/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-04-12 18:10:10
 * @Description: 任务调度器配置
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
@EnableScheduling
@Description("Scheduler Configuration - Task scheduling configuration for enabling @Scheduled annotations")
public class SchedulerConfig {

    @Bean
    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(5);
        scheduler.setThreadNamePrefix("BytedeskScheduler-");
        scheduler.initialize();
        return scheduler;
    }
}
