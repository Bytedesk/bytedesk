/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-05 14:51:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-10-29 18:26:44
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
public class KnowledgebaseAspect {

    /**
     * 处理会话请求前执行
     */
    @Before(value = "@annotation(knowledgebaseAnnotation)")
    public void doBefore(JoinPoint joinPoint, KnowledgebaseAnnotation knowledgebaseAnnotation) {
        log.debug("KnowledgebaseAspect before: model {}, ", knowledgebaseAnnotation.title());
        // 获取方法签名
    }

    /**
     * 处理完请求后执行
     *
     * @param joinPoint 切点
     */
    @AfterReturning(pointcut = "@annotation(knowledgebaseAnnotation)", returning = "jsonResult")
    public void doAfterReturning(JoinPoint joinPoint, KnowledgebaseAnnotation knowledgebaseAnnotation, Object jsonResult) {
        log.debug("KnowledgebaseAspect after returning: title {}, jsonResult {}", knowledgebaseAnnotation.title(), jsonResult);

    }


    @Pointcut("execution(* com.bytedesk.kbase.knowledge_base.KnowledgebaseRouter.*(..))")
    public void knowledgebaseLog() {};

    @Before("knowledgebaseLog()")
    public void beforeKnowledgebaseLog() {
        log.debug("KnowledgebaseAspect beforeKnowledgebaseLog");
    }

    @After("knowledgebaseLog()")
    public void afterKnowledgebaseLog() {
        log.debug("KnowledgebaseAspect afterKnowledgebaseLog");
    }

    @Around("knowledgebaseLog()")
    public Object aroundKnowledgebaseLog(ProceedingJoinPoint joinPoint) throws Throwable {
        log.debug("KnowledgebaseAspect aroundKnowledgebaseLog before");
        // 
        long start = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long executionTime = System.currentTimeMillis() - start;
        log.debug("KnowledgebaseAspect aroundKnowledgebaseLog {} executed in {} ms", joinPoint.getSignature(), executionTime);
        // 

        return result;
    }



}
