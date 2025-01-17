/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-05 14:51:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-01-17 21:07:06
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
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
import org.springframework.stereotype.Component;
import com.bytedesk.core.black.BlackRestService;
import com.bytedesk.core.black.BlackEntity;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * filter blocked visitor
 */
@Slf4j
@Aspect
@Component
@AllArgsConstructor
public class VisitorAspect {

    // private final IpService ipService;
    private final BlackRestService blackRestService;

    /**
     * 处理会话请求前执行
     */
    @Before(value = "@annotation(visitorAnnotation)")
    public void doBefore(JoinPoint joinPoint, VisitorAnnotation visitorAnnotation) {
        log.debug("VisitorAspect before: model {}", visitorAnnotation.title());
        // 获取方法签名
        // MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        // 获取方法参数列表
        Object[] args = joinPoint.getArgs();
        
        String uid = null;
        String orgUid = null;

        // 遍历参数
        for (int i = 0; i < args.length; i++) {
            // String paramName = signature.getParameterNames()[i];
            Object paramValue = args[i];
            
            // 如果参数是 VisitorRequest 类型
            if (paramValue instanceof VisitorRequest) {
                VisitorRequest visitorRequest = (VisitorRequest) paramValue;
                uid = visitorRequest.getUid();
                orgUid = visitorRequest.getOrgUid();
                log.debug("Found VisitorRequest - uid: {}, orgUid: {}", uid, orgUid);
                
                // 检查黑名单
                if (uid != null && orgUid != null) {
                    Optional<BlackEntity> blackOpt = blackRestService.findByVisitorUidAndOrgUid(uid, orgUid);
                    if (blackOpt.isPresent()) {
                        BlackEntity black = blackOpt.get();
                        if (black.getEndTime() == null || black.getEndTime().isAfter(LocalDateTime.now())) {
                            throw new RuntimeException("Access denied for visitor: " + uid + " in org: " + orgUid);
                        } else {
                            log.debug("Found VisitorRequest - Visitor out of end time");
                        }
                    } else {
                        log.debug("Found VisitorRequest - Visitor not in black list");
                    }
                } else {
                    log.debug("Found VisitorRequest - uid or orgUid is null");
                }
                break;
            }
        }

        // 处理 IP 相关逻辑
        // ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        // if (attributes != null) {
        //     HttpServletRequest request = attributes.getRequest();
        //     String ip = IpUtils.getIp(request);
        //     String ipLocation = ipService.getIpLocation(ip);
        //     log.info("IP: {}, Location: {}", ip, ipLocation);
        //     // TODO: 检查 IP 是否被封禁
        // }

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
