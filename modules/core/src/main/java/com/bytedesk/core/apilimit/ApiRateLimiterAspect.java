/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-09 11:59:04
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-05-04 13:02:47
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.apilimit;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import com.bytedesk.core.utils.JsonResult;

import lombok.extern.slf4j.Slf4j;
import com.google.common.util.concurrent.RateLimiter;


/**
 * https://springdoc.cn/spring/core.html#aop
 * https://blog.csdn.net/MICHAELKING1/article/details/106058874
 */
@Slf4j
@Aspect
@Component
public class ApiRateLimiterAspect {

    private static final ConcurrentMap<String, RateLimiter> RATE_LIMITER_CACHE = new ConcurrentHashMap<>();

    @Pointcut("@annotation(com.bytedesk.core.apilimit.ApiRateLimiter)")
    public void apiRateLimit() {}

    @Around("apiRateLimit()")
    public Object pointcut(ProceedingJoinPoint point) throws Throwable {
        //
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        // 通过 AnnotationUtils.findAnnotation 获取 RateLimiter 注解
        ApiRateLimiter apiRateLimiter = AnnotationUtils.findAnnotation(method, ApiRateLimiter.class);
        //
        boolean proceed = true;
        if (apiRateLimiter != null && apiRateLimiter.qps() > ApiRateLimiter.NOT_LIMITED) {
            double qps = apiRateLimiter.qps();
            if (RATE_LIMITER_CACHE.get(method.getName()) == null) {
                // 初始化 QPS
                RATE_LIMITER_CACHE.put(method.getName(), RateLimiter.create(qps));
            }
            log.debug("api {}, {}", method.getName(), RATE_LIMITER_CACHE.get(method.getName()).getRate());
            // 尝试获取令牌
            if (RATE_LIMITER_CACHE.get(method.getName()) != null && !RATE_LIMITER_CACHE.get(method.getName())
                    .tryAcquire(apiRateLimiter.timeout(), apiRateLimiter.timeUnit())) {
                proceed = false;
                log.debug("api out of limit, please try later");
            }
        }
        // 
        if (proceed) {
            return point.proceed();
        }
        //
        return JsonResult.error("api out of limit, please try later");
    }
    
}
