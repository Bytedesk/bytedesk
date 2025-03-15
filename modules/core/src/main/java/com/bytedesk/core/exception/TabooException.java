/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-03-15 16:30:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-15 16:30:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.exception;

/**
 * 敏感词异常
 */
public class TabooException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;

    public TabooException(String message) {
        super(message);
    }
    
    public TabooException(String message, Throwable cause) {
        super(message, cause);
    }
}
