/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-17 16:53:12
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-11-23 10:48:52
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.action.aop;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
// import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
// import org.aspectj.lang.annotation.Before;
// import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.bytedesk.core.action.ActionRequest;
import com.bytedesk.core.action.ActionRestService;
import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.base.BaseRequest;
import com.bytedesk.core.ip.IpService;
import com.bytedesk.core.ip.IpUtils;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 操作日志记录处理
 * 
 * 注意：不能在类上注解@Async，否则会获取不到 HttpServletRequest，attributes为空。
 * 如果不需要HttpServletRequest，可以添加@Async注解
 * 
 * @author jackning
 */
@Slf4j
@Aspect
@Component
@AllArgsConstructor
public class ActionAspect {

    private final ActionRestService actionService;

    private final IpService ipService;

    /**
     * 处理完请求后执行
     *
     * @param joinPoint 切点
     */
    @AfterReturning(pointcut = "@annotation(actionAnnotation)", returning = "jsonResult")
    public void doAfterReturning(JoinPoint joinPoint, ActionAnnotation actionAnnotation, Object jsonResult) {
        // log.debug("actionLog after returning: title {}, action {}", actionAnnotation.title(), actionAnnotation.action());
        //
        ActionRequest actionRequest = ActionRequest.builder()
                .title(actionAnnotation.title())
                .action(actionAnnotation.action())
                .description(actionAnnotation.description())
                .type(actionAnnotation.type().name())
                .channel(resolveChannel(joinPoint.getArgs()))
                .build();
        // 
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            // 注意：不能在类上注解@Async，否则会获取不到 HttpServletRequest，attributes为空
            HttpServletRequest request = attributes.getRequest();
            // String ipAddress = request.getRemoteAddr();
            String ip = IpUtils.getClientIp(request);
            String ipLocation = ipService.getIpLocation(ip);
            // log.info("ipAddress {}, ip {}, ipLocation {}", ipAddress, ip, ipLocation);
            actionRequest.setIp(ip);
            actionRequest.setIpLocation(ipLocation);
            // 接下来的操作...
        } else {
            // 处理非Web请求情况，比如记录日志或者直接返回
        }
        // 
        actionService.create(actionRequest);
    }

    /**
     * 从请求参数中解析客户端渠道（如登录请求体中的 channel=WEB_ADMIN），
     * 用于操作日志/登录通知中标识来源端。无法解析时返回 null，不阻断日志记录。
     */
    String resolveChannel(Object[] args) {
        if (args == null) {
            return null;
        }
        for (Object arg : args) {
            if (arg == null) {
                continue;
            }
            if (arg instanceof BaseRequest baseRequest) {
                return StringUtils.hasText(baseRequest.getChannel()) ? baseRequest.getChannel().trim() : null;
            }
            // 兼容未继承 BaseRequest 但含 channel 字段的请求 DTO（如各 OAuth 登录请求体）
            try {
                Method getter = arg.getClass().getMethod("getChannel");
                if (getter.getReturnType() == String.class) {
                    Object value = getter.invoke(arg);
                    if (value instanceof String channelValue && StringUtils.hasText(channelValue)) {
                        return channelValue.trim();
                    }
                    return null;
                }
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                // 无 channel 字段，继续检查下一个参数
            }
        }
        return null;
    }

}
