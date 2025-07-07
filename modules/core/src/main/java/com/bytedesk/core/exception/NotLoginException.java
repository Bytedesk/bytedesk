/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-26 12:35:08
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-07 11:25:50
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
 * Not Login Exception.
 */
public class NotLoginException extends BaseException {

    private static final long serialVersionUID = 1L;

    public NotLoginException(String message) {
        super(message);
    }

    public NotLoginException(String message, Throwable cause) {
        super(message, cause);
    }

    
}
