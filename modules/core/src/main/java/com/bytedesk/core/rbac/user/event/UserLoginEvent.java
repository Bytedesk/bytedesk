/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-09-25 10:32:39
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-08 15:42:51
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.rbac.user.event;

import org.springframework.context.ApplicationEvent;

import com.bytedesk.core.rbac.user.UserEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class UserLoginEvent extends ApplicationEvent {
    
    private static final long serialVersionUID = 1L;

    private UserEntity user;

    public UserLoginEvent(Object source, UserEntity user) {
        super(source);
        this.user = user;
    }
}
