/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-05-24 12:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-24 12:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
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

/**
 * 用于标记需要国际化的 API 操作
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface I18nApi {
    
    /**
     * API 操作的 i18n 键前缀
     */
    String value() default "";
    
    /**
     * API 操作摘要的 i18n 键
     */
    String summary() default "";
    
    /**
     * API 操作描述的 i18n 键
     */
    String description() default "";
}
