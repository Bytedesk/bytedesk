/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-26 12:35:08
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-07 11:24:25
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
 * Resource not found
 */
public class NotFoundException extends BaseException {

    private static final long serialVersionUID = 1L;

    public NotFoundException(String message) {
        super(message);
        //TODO Auto-generated constructor stub
    }

    public ForbiddenException(String message, Throwable cause) {
        super(message, cause);
    }

    
}
