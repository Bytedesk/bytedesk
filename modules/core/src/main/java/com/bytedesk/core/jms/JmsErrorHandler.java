/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-03-15 16:35:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-15 16:25:40
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.jms;

import org.springframework.stereotype.Component;
import org.springframework.util.ErrorHandler;

import com.bytedesk.core.exception.TabooException;

import lombok.extern.slf4j.Slf4j;

/**
 * JMS 错误处理器
 */
@Slf4j
@Component
public class JmsErrorHandler implements ErrorHandler {

    @Override
    public void handleError(Throwable t) {
        if (t.getCause() instanceof TabooException) {
            // 处理敏感词异常
            log.warn("JMS处理中发现敏感词: {}", t.getCause().getMessage());
        } else if (t.getCause() instanceof IllegalArgumentException && 
                  t.getCause().getMessage() != null && 
                  t.getCause().getMessage().contains("敏感词")) {
            // 处理敏感词异常（旧版本兼容）
            log.warn("JMS处理中发现敏感词(IllegalArgumentException): {}", t.getCause().getMessage());
        } else {
            // 处理其他异常
            log.error("JMS处理异常:", t);
        }
    }
}
