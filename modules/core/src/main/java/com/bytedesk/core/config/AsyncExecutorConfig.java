/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-17 10:35:11
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-04-17 10:54:09
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.config;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.support.TaskExecutorAdapter;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;

import lombok.extern.slf4j.Slf4j;

/**
 * async executor config
 * https://www.baeldung.com/spring-async
 * https://spring.io/guides/gs/async-method
 */
@Slf4j
@EnableAsync
@Configuration
public class AsyncExecutorConfig implements AsyncConfigurer {

    @Bean(name = "virtualAsyncExecutor", destroyMethod = "close")
    public ExecutorService virtualAsyncExecutor() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }
    
    /**
     * 定义 applicationTaskExecutor bean
     * Spring Boot 3.5.x 不再自动创建此 bean，但 Flowable 等框架需要它
     * 
     * @return AsyncTaskExecutor
     */
    @Bean(name = "applicationTaskExecutor")
    public AsyncTaskExecutor applicationTaskExecutor() {
        return new TaskExecutorAdapter(virtualAsyncExecutor());
    }

    @Override
    public Executor getAsyncExecutor() {
        return applicationTaskExecutor();
    }

    /**
     * @Async 方法内事件监听器抛出 Error（如 protobuf 代码不一致导致的 NoSuchMethodError）时，
     * 会被包装为 UndeclaredThrowableException 并传播到异步线程。默认的
     * SimpleAsyncUncaughtExceptionHandler 每次都会打印完整堆栈，此处兜底记录一行摘要日志，
     * 具体异常已在各事件监听器内部拦截处理
     */
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (throwable, method, params) -> {
            Throwable cause = NestedExceptionUtils.getMostSpecificCause(throwable);
            log.error("async method uncaught exception: method={}, rootCause={}: {}",
                    method.getDeclaringClass().getSimpleName() + "." + method.getName(),
                    cause.getClass().getName(), cause.getMessage());
        };
    }
}
