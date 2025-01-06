/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-09 11:58:06
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-05-04 12:54:59
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.apilimit;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

import org.springframework.core.annotation.AliasFor;

/**
 * https://blog.csdn.net/MICHAELKING1/article/details/106058874
 * 
 * @author dyz
 * @version 1.0
 * @date 2020/5/11 15:40 api rate limiter
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(value = RetentionPolicy.RUNTIME)
@Documented
public @interface ApiRateLimiter {

    // 
    int NOT_LIMITED = 0;
 
    /**
     * qps
     */
    @AliasFor("qps") double value() default NOT_LIMITED;
 
    /**
     * qps
     */
    @AliasFor("value") double qps() default NOT_LIMITED;
 
    /**
     * 超时时长
     */
    int timeout() default 0;
 
    /**
     * 超时时间单位
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;
}
