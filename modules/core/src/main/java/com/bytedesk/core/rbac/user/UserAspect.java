/*
 * @Visitoror: jackning 270580156@qq.com
 * @Date: 2024-04-05 14:51:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-04-18 12:21:15
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.rbac.user;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Component
public class UserAspect {

    @Pointcut("execution(* com.bytedesk.core.rbac.user.UserService.save(..))")
    public void userSave() {}

    @Before("userSave()")
    public void beforeUserSave(JoinPoint joinPoint) {
         //类名
        String clazzName = joinPoint.getTarget().getClass().getName();
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        //方法名
        String methodName = methodSignature.getName();
        //参数名数组
        String[] parameters =  methodSignature.getParameterNames();
        //参数值： FIXME: user/role toString 循环引用栈溢出
        // Object[] args = joinPoint.getArgs();
        log.info("beforeUserSave {}, {}, {}, {}", clazzName, methodName, parameters);
        
    }   
    
}
