/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-17 16:53:05
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-26 11:17:22
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.bytedesk.core.action.ActionTypeEnum;

/**
 * record for action and failed operations
 * 
 * @author jackning
 *
 */
@Target({ ElementType.PARAMETER, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ActionAnnotation {

    public String title() default "";

    public String action() default "";

    public String description() default "";

    public ActionTypeEnum type() default ActionTypeEnum.LOG;
}
