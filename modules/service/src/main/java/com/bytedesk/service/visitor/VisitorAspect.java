/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-05 14:51:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-19 17:43:04
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.visitor;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.bytedesk.core.ip.IpService;
import com.bytedesk.core.ip.IpUtils;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * filter blocked visitor
 */
@Slf4j
@Aspect
@Component
@AllArgsConstructor
public class VisitorAspect {

    private final IpService ipService;

    /**
     * 处理会话请求前执行
     */
    @Before(value = "@annotation(visitorAnnotation)")
    public void doBefore(JoinPoint joinPoint, VisitorAnnotation visitorAnnotation) {
        log.debug("VisitorAspect before: model {}, ", visitorAnnotation.title());
        // 
        // 获取方法签名
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        // 获取方法参数列表
        Object[] args = joinPoint.getArgs();
        // 遍历参数
        for (int i = 0; i < args.length; i++) {
            // 获取参数名
            String paramName = signature.getParameterNames()[i];
            // 获取参数值
            Object paramValue = args[i];
            // 参数名: authRequest, 参数值: AuthRequest(username=admin@email.com, password=admin,
            // mobile=null, email=null, code=null, platform=bytedesk)
            log.debug("TODO: 参数名: {}, 参数值: {}", paramName, paramValue);
        }
        // 
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            // 注意：不能在类上注解@Async，否则会获取不到 HttpServletRequest，attributes为空
            HttpServletRequest request = attributes.getRequest();
            String ipAddress = request.getRemoteAddr();
            String ip = IpUtils.getIp(request);
            String ipLocation = ipService.getIpLocation(ip);
            log.info("ipAddress {}, ip {}, ipLocation {}", ipAddress, ip, ipLocation);
            // 接下来的操作...
        }

        // TODO: 是否黑名单用户

        // TODO: ip是否被封禁

        // TODO: 检查付费情况，是否过期，是否试用到期等
        
    }

    /**
     * 处理完请求后执行
     *
     * @param joinPoint 切点
     */
    @AfterReturning(pointcut = "@annotation(visitorAnnotation)", returning = "jsonResult")
    public void doAfterReturning(JoinPoint joinPoint, VisitorAnnotation visitorAnnotation, Object jsonResult) {
        log.debug("VisitorFilterAspect after returning: title {}", visitorAnnotation.title());
        // handleLog(joinPoint, visitorAnnotation, null, jsonResult);

    }

    // @Pointcut("execution(*
    // com.bytedesk.service.visitor.VisitorController.*(..))")
    // public void visitorLog() {};

    // @Before("visitorLog()")
    // public void beforeVisitorLog() {
    // // TODO: 拦截骚扰用户 + 被禁ip/ip段
    // log.debug("VisitorAspect TODO: 拦截骚扰用户 + 被禁ip/ip段");
    // }

    // @After("visitorLog()")
    // public void afterVisitorLog() {
    // log.debug("VisitorAspect afterVisitorLog");
    // // TODO: action log save to db
    // }

    // @Around("visitorLog()")
    // public Object aroundVisitorLog(ProceedingJoinPoint joinPoint) throws
    // Throwable {
    // log.debug("VisitorAspect aroundVisitorLog before");

    // long start = System.currentTimeMillis();

    // // body
    // Object result = joinPoint.proceed();

    // long executionTime = System.currentTimeMillis() - start;

    // log.debug("{} executed in {} ms", joinPoint.getSignature(), executionTime);

    // log.debug("VisitorAspect aroundVisitorLog after");

    // return result;
    // }

}
