/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-02-26 11:45:58
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-02-26 12:02:57
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.auth;

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Component
public class AuthAspect {

    @Pointcut("execution(* com.bytedesk.core.auth.AuthController.*(..))")
    public void authLog() {
        log.debug("ActionAspect authLog");
    };

    @Before("authLog()")
    public void beforeAuthLog() {
        log.debug("ActionAspect beforeAuthLog");
    }

    @After("authLog()")
    public void afterAuthLog() {
        log.debug("ActionAspect afterAuthLog");
        // TODO: action log save to db
    }

}
