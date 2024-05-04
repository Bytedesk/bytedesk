/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-17 16:53:12
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-05-04 12:33:32
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.action;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 操作日志记录处理
 * 
 * @author jackning
 */
@Slf4j
@Async
@Aspect
@Component
@AllArgsConstructor
public class ActionLogAspect {

    private final ActionService actionService;

    /**
     * 处理请求前执行
     */
    @Before(value = "@annotation(controllerLog)")
    public void doBefore(JoinPoint joinPoint, ActionLogAnnotation controllerLog) {
        log.debug("actionLog before: model {}, action {}", controllerLog.title(), controllerLog.action());
    }

    /**
     * 处理完请求后执行
     *
     * @param joinPoint 切点
     */
    @AfterReturning(pointcut = "@annotation(controllerLog)", returning = "jsonResult")
    public void doAfterReturning(JoinPoint joinPoint, ActionLogAnnotation controllerLog, Object jsonResult) {
        log.debug("actionLog after returning: model {}, action {}, jsonResult {}", controllerLog.title(),
                controllerLog.action(), jsonResult);
        // handleLog(joinPoint, controllerLog, null, jsonResult);
        // TODO: 记录具体用户
        ActionRequest actionRequest = ActionRequest.builder()
                .title(controllerLog.title())
                .action(controllerLog.action())
                .description(controllerLog.description())
                .build();
        actionService.create(actionRequest);
    }

    /**
     * 拦截异常操作
     * 
     * @param joinPoint 切点
     * @param e 异常
     */
    @AfterThrowing(value = "@annotation(controllerLog)", throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint, ActionLogAnnotation controllerLog, Exception e) {
        log.info("actionLog after throwing: model {}, action {}", controllerLog.title(), controllerLog.action());
        // handleLog(joinPoint, controllerLog, e, null);
    }

    
    // protected void handleLog(final JoinPoint joinPoint, ActionLog controllerLog, final Exception e, Object jsonResult) {
    //     log.debug("actionLog handleLog: model {}, action {}", controllerLog.title(), controllerLog.action());
    //     // TODO: write to db
    // }


}
