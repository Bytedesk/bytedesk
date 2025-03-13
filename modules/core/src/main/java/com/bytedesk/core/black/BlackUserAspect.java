/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-17 16:53:12
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-13 09:57:48
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.black;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import com.bytedesk.core.annotation.BlackUserFilter;
// import com.bytedesk.core.message.IMessageSendService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 黑名单用户过滤
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
public class BlackUserAspect {

    // private final IMessageSendService messageSendService;

    /**
     * 处理请求前执行
     */
    @Before(value = "@annotation(blackUserFilter)")
    public void doBefore(JoinPoint joinPoint, BlackUserFilter blackUserFilter) {
        log.debug("blackUserLog before: model {}, action {}", blackUserFilter.title(), blackUserFilter.action());
        // 获取方法参数
        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            if (arg instanceof String) {
                // String message = (String) arg;
                
            }
        }
    }

    /**
     * 处理完请求后执行
     *
     * @param joinPoint 切点
     */
    @AfterReturning(pointcut = "@annotation(blackUserFilter)", returning = "jsonResult")
    public void doAfterReturning(JoinPoint joinPoint, BlackUserFilter blackUserFilter, Object jsonResult) {
        log.debug("blackUserLog after returning: title {}, action {}", blackUserFilter.title(), blackUserFilter.action());
        //
    }

    /**
     * 拦截异常操作
     * 
     * @param joinPoint 切点
     * @param e         异常
     */
    @AfterThrowing(value = "@annotation(blackUserFilter)", throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint, BlackUserFilter blackUserFilter, Exception e) {
        log.info("blackUserLog after throwing: model {}, action {}", blackUserFilter.title(), blackUserFilter.action());
        // handleLog(joinPoint, blackUserFilter, e, null);
    }

    /**
     * 
     * @param message 消息内容
     * @return 是否包含黑名单用户
     */
    // private boolean containsSensitiveWords(String message) {
    //     log.info("containsSensitiveWords message {}", message);
    //     // 实现黑名单用户过滤逻辑
    //     // 例如：可以从数据库或配置文件中加载黑名单用户列表，然后进行匹配
    //     // 这里简单示例，假设黑名单用户为 "黑名单用户"
    //     return message.contains("黑名单用户");
    // }

}
