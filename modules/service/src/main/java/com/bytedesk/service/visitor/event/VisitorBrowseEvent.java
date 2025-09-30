/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-09 16:21:14
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-01-09 16:29:01
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.visitor.event;

import org.springframework.context.ApplicationEvent;

import com.bytedesk.service.visitor.VisitorRequest;

import lombok.Getter;

@Getter
public class VisitorBrowseEvent extends ApplicationEvent {
    
    private static final long serialVersionUID = 1L;

    private final VisitorRequest visitorRequest;

    public VisitorBrowseEvent(Object source, VisitorRequest visitorRequest) {
        super(source);
        this.visitorRequest = visitorRequest;
    }
}
