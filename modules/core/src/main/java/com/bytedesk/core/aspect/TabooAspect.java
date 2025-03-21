/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-22 18:28:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-11-22 18:28:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.aspect;

import java.lang.reflect.Parameter;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import com.bytedesk.core.annotation.TabooFilter;
import com.bytedesk.core.service.TabooService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Aspect for handling @TabooFilter annotations
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class TabooAspect {

    private final TabooService tabooService;

    /**
     * Intercept methods annotated with @TabooFilter
     * 
     * @param joinPoint the join point
     * @param annotation the annotation
     * @return the result of the method
     * @throws Throwable if an error occurs
     */
    @Around("@annotation(annotation)")
    public Object processTabooFilter(ProceedingJoinPoint joinPoint, TabooFilter annotation) throws Throwable {
        log.debug("处理敏感词过滤: {}", joinPoint.getSignature().toShortString());
        
        String paramName = annotation.value();
        boolean throwException = annotation.throwException();
        
        if (paramName.isEmpty()) {
            // 如果没有指定参数名，则尝试处理所有字符串参数
            return processAllStringParameters(joinPoint, throwException);
        } else {
            // 处理指定参数
            return processNamedParameter(joinPoint, paramName, throwException);
        }
    }
    
    /**
     * Process all String parameters
     * 
     * @param joinPoint the join point
     * @param throwException whether to throw exception
     * @return the result of the method
     * @throws Throwable if an error occurs
     */
    private Object processAllStringParameters(ProceedingJoinPoint joinPoint, boolean throwException) throws Throwable {
        Object[] args = joinPoint.getArgs();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Parameter[] parameters = signature.getMethod().getParameters();
        
        boolean modified = false;
        
        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof String) {
                String content = (String) args[i];
                
                // 处理参数上的注解
                TabooFilter paramAnnotation = parameters[i].getAnnotation(TabooFilter.class);
                boolean paramThrowException = paramAnnotation != null ? 
                        paramAnnotation.throwException() : throwException;
                
                String processed = tabooService.processContent(content, paramThrowException);
                if (!processed.equals(content)) {
                    args[i] = processed;
                    modified = true;
                }
            }
        }
        
        if (modified) {
            log.debug("敏感词已过滤，继续执行方法");
        }
        
        return joinPoint.proceed(args);
    }
    
    /**
     * Process a named parameter
     * 
     * @param joinPoint the join point
     * @param paramName the parameter name
     * @param throwException whether to throw exception
     * @return the result of the method
     * @throws Throwable if an error occurs
     */
    private Object processNamedParameter(ProceedingJoinPoint joinPoint, String paramName, boolean throwException) throws Throwable {
        Object[] args = joinPoint.getArgs();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] parameterNames = signature.getParameterNames();
        
        for (int i = 0; i < parameterNames.length; i++) {
            if (parameterNames[i].equals(paramName) && args[i] instanceof String) {
                String content = (String) args[i];
                String processed = tabooService.processContent(content, throwException);
                
                if (!processed.equals(content)) {
                    args[i] = processed;
                    log.debug("参数 {} 中的敏感词已过滤", paramName);
                }
                
                break;
            }
        }
        
        return joinPoint.proceed(args);
    }
} 