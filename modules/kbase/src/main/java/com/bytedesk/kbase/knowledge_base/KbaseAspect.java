/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-05 14:51:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-24 17:13:04
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.knowledge_base;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 */
@Slf4j
@Aspect
@Component
@AllArgsConstructor
public class KbaseAspect {

    /**
     * 处理会话请求前执行
     */
    @Before(value = "@annotation(kbaseAnnotation)")
    public void doBefore(JoinPoint joinPoint, KbaseAnnotation kbaseAnnotation) {
        log.debug("KbaseAspect before: model {}, ", kbaseAnnotation.title());
        // 获取方法签名
    }

    /**
     * 处理完请求后执行
     *
     * @param joinPoint 切点
     */
    @AfterReturning(pointcut = "@annotation(kbaseAnnotation)", returning = "jsonResult")
    public void doAfterReturning(JoinPoint joinPoint, KbaseAnnotation kbaseAnnotation, Object jsonResult) {
        log.debug("KbaseAspect after returning: title {}, jsonResult {}", kbaseAnnotation.title(), jsonResult);

    }


    @Pointcut("execution(* com.bytedesk.kbase.knowledge_base.KbaseRouter.*(..))")
    public void kbaseLog() {};

    @Before("kbaseLog()")
    public void beforeKbaseLog() {
        log.debug("KbaseAspect beforeKbaseLog");
    }

    @After("kbaseLog()")
    public void afterKbaseLog() {
        log.debug("KbaseAspect afterKbaseLog");
    }

    @Around("kbaseLog()")
    public Object aroundKbaseLog(ProceedingJoinPoint joinPoint) throws Throwable {
        log.debug("KbaseAspect aroundKbaseLog before");
        // 
        long start = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long executionTime = System.currentTimeMillis() - start;
        log.debug("KbaseAspect aroundKbaseLog {} executed in {} ms", joinPoint.getSignature(), executionTime);
        // 

        return result;
    }



}
