/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-01-20 10:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.aspect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * @NonNull 参数验证切面
 * 用于在运行时验证标记了 @NonNull 注解的参数
 */
@Slf4j
@Aspect
@Component
public class NonNullParameterValidationAspect {

    /**
     * 拦截所有 Service 层的方法调用
     * 检查标记了 @NonNull 注解的参数
     */
    @Before("execution(* com.bytedesk.service.*.service.*Service.*(..)) || " +
            "execution(* com.bytedesk.service.*.rest.*RestService.*(..))")
    public void validateNonNullParameters(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Object[] args = joinPoint.getArgs();
        Parameter[] parameters = method.getParameters();
        
        List<String> nullParameterNames = new ArrayList<>();
        
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            Object arg = args[i];
            
            // 检查是否有 @NonNull 注解
            if (parameter.isAnnotationPresent(NonNull.class)) {
                if (arg == null || (arg instanceof String && !StringUtils.hasText((String) arg))) {
                    nullParameterNames.add(parameter.getName());
                }
            }
        }
        
        // 如果发现 null 参数，抛出异常
        if (!nullParameterNames.isEmpty()) {
            String methodName = method.getDeclaringClass().getSimpleName() + "." + method.getName();
            String message = String.format("Method %s called with null/empty parameters: %s", 
                methodName, String.join(", ", nullParameterNames));
            
            log.error(message);
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * 拦截所有 Repository 层的方法调用
     * 检查标记了 @NonNull 注解的参数
     */
    @Before("execution(* com.bytedesk.service.*.repository.*Repository.*(..))")
    public void validateRepositoryNonNullParameters(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Object[] args = joinPoint.getArgs();
        Parameter[] parameters = method.getParameters();
        
        List<String> nullParameterNames = new ArrayList<>();
        
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            Object arg = args[i];
            
            // 检查是否有 @NonNull 注解
            if (parameter.isAnnotationPresent(NonNull.class)) {
                if (arg == null || (arg instanceof String && !StringUtils.hasText((String) arg))) {
                    nullParameterNames.add(parameter.getName());
                }
            }
        }
        
        // 如果发现 null 参数，抛出异常
        if (!nullParameterNames.isEmpty()) {
            String methodName = method.getDeclaringClass().getSimpleName() + "." + method.getName();
            String message = String.format("Repository method %s called with null/empty parameters: %s", 
                methodName, String.join(", ", nullParameterNames));
            
            log.error(message);
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * 通用方法：检查参数是否为 null 或空字符串
     */
    private boolean isNullOrEmpty(Object arg) {
        if (arg == null) {
            return true;
        }
        
        if (arg instanceof String) {
            return !StringUtils.hasText((String) arg);
        }
        
        return false;
    }

    /**
     * 获取参数名称（如果参数名不可用，则使用索引）
     */
    private String getParameterName(Parameter parameter, int index) {
        String name = parameter.getName();
        if (("arg" + index).equals(name)) {
            // 参数名不可用，使用索引
            return "parameter" + index;
        }
        return name;
    }
} 