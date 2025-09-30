/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-17 11:02:08
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-01-17 17:10:38
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.black.event;

import org.springframework.context.ApplicationEvent;

import com.bytedesk.core.black.BlackEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class BlackDeleteEvent extends ApplicationEvent {
    
    private static final long serialVersionUID = 1L;

    private final BlackEntity blackEntity;

    public BlackDeleteEvent(Object source, BlackEntity blackEntity) {
        super(source);
        this.blackEntity = blackEntity;
    }
}
